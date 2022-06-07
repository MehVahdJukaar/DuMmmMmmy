
package net.mehvahdjukaar.dummmmmmy.entity;

import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.common.NetworkHandler;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TargetDummyEntity extends Mob {

    private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(TargetDummyEntity.class, EntityDataSerializers.BOOLEAN);

    //client values
    public float prevAnimationPosition = 0;
    // used to have an independent start for the animation, otherwise the phase of the animation depends ont he damage dealt
    public float shakeAmount = 0;
    public float prevShakeAmount = 0;

    // used to calculate the whole damage in one tick, in case there are multiple sources
    private int lastTickActuallyDamaged;
    // currently, recording damage taken
    public float totalDamageTakenInCombat;
    //has just been hit by critical?
    public boolean critical = false;
    public MobAttribute mobType = MobAttribute.UNDEFINED;
    //position of damage number in the semicircle
    private int damageNumberPos = 0;
    //needed because it's private, and we aren't calling le tick
    private final NonNullList<ItemStack> lastArmorItems = NonNullList.withSize(4, ItemStack.EMPTY);

    private final Map<ServerPlayer, Integer> currentlyAttacking = new HashMap<>();
    private DamageSource currentDamageSource = null;


    public TargetDummyEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModRegistry.TARGET_DUMMY.get(), world);
    }

    public TargetDummyEntity(EntityType<TargetDummyEntity> type, Level world) {
        super(type, world);
    }

    public TargetDummyEntity(Level world) {
        this(ModRegistry.TARGET_DUMMY.get(), world);
        this.xpReward = 0;
        Arrays.fill(this.armorDropChances, 1.1f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHEARED, false);
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(SHEARED, sheared);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Type", this.mobType.ordinal());
        tag.putInt("NumberPos", this.damageNumberPos);
        tag.putBoolean("Sheared", this.isSheared());

        this.applyEquipmentModifiers();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.mobType = MobAttribute.values()[tag.getInt("Type")];
        this.damageNumberPos = tag.getInt("NumberPos");
        this.setSheared(tag.getBoolean("Sheared"));
    }

    @Override
    public void setYBodyRot(float pOffset) {
        float r = this.getYRot();
        this.yRotO = r;
        this.yBodyRotO = this.yBodyRot = r;
    }

    @Override
    public void setYHeadRot(float pRotation) {
        float r = this.getYRot();
        this.yRotO = r;
        this.yHeadRotO = this.yHeadRot = r;
    }

    @Override
    protected InteractionResult mobInteract(Player p_21472_, InteractionHand p_21473_) {
        return super.mobInteract(p_21472_, p_21473_);
    }

    // dress it up! :D
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        boolean inventoryChanged = false;
        if (!player.isSpectator() && player.getAbilities().mayBuild) {
            ItemStack itemstack = player.getItemInHand(hand);
            EquipmentSlot equipmentSlot = getEquipmentSlotForItem(itemstack);

            Item item = itemstack.getItem();

            //special items
            if (item instanceof BannerItem || this.isPumpkin(item) || item.canEquip(itemstack, EquipmentSlot.HEAD, this)) {
                equipmentSlot = EquipmentSlot.HEAD;
            }


            // empty hand -> unequip
            if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                equipmentSlot = this.getClickedSlot(vec);
                if (this.hasItemInSlot(equipmentSlot)) {
                    if (player.level.isClientSide) return InteractionResult.CONSUME;
                    this.unequipArmor(player, equipmentSlot, itemstack, hand);
                    inventoryChanged = true;

                }
            }
            // armor item in hand -> equip/swap
            else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                if (player.level.isClientSide) return InteractionResult.CONSUME;
                this.equipArmor(player, equipmentSlot, itemstack, hand);
                inventoryChanged = true;

            }
            //remove sack
            else if (item instanceof ShearsItem) {
                if (!this.isSheared()) {
                    level.playSound(player, this, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (player.level.isClientSide) return InteractionResult.CONSUME;
                    this.setSheared(true);
                    return InteractionResult.SUCCESS;
                }
            }


            if (inventoryChanged) {
                this.setLastArmorItem(equipmentSlot, itemstack);
                if (!this.level.isClientSide) {
                    NetworkHandler.sendToAllTracking(this, (ServerLevel) this.level, new NetworkHandler.PacketSyncEquip(this.getId(), equipmentSlot.getIndex(), this.getItemBySlot(equipmentSlot)));
                }
                //this.applyEquipmentModifiers();
                return InteractionResult.SUCCESS;
            }


        }
        return InteractionResult.PASS;
    }

    private void unequipArmor(Player player, EquipmentSlot slot, ItemStack stack, InteractionHand hand) {
        // set slot to stack which is empty stack
        ItemStack itemstack = this.getItemBySlot(slot);
        ItemStack itemstack2 = itemstack.copy();

        player.setItemInHand(hand, itemstack2);
        this.setItemSlot(slot, stack);

        //this.applyEquipmentModifiers();
        //now done here^
        this.getAttributes().removeAttributeModifiers(itemstack2.getAttributeModifiers(slot));
        //clear mob type
        if (slot == EquipmentSlot.HEAD) this.mobType = MobAttribute.UNDEFINED;

    }

    private void equipArmor(Player player, EquipmentSlot slot, ItemStack stack, InteractionHand hand) {
        ItemStack currentItem = this.getItemBySlot(slot);
        ItemStack newItem = stack.copy();
        newItem.setCount(1);

        player.setItemInHand(hand, ItemUtils.createFilledResult(stack.copy(), player, currentItem, player.isCreative()));

        this.equipEventAndSound(newItem);
        this.setItemSlot(slot, newItem);

        //this.applyEquipmentModifiers();
        //now done here^
        this.getAttributes().addTransientAttributeModifiers(newItem.getAttributeModifiers(slot));
        if (slot == EquipmentSlot.HEAD) {
            //add mob type
            if (this.isUndeadSkull(newItem)) this.mobType = MobAttribute.UNDEAD;
            else if (newItem.getItem() == Items.TURTLE_HELMET) this.mobType = MobAttribute.WATER;
            else if (newItem.getItem() == Items.DRAGON_HEAD) this.mobType = MobAttribute.ARTHROPOD;
            else if (ItemStack.matches(newItem, Raid.getLeaderBannerInstance())) this.mobType = MobAttribute.ILLAGER;
            else if (this.isPumpkin(newItem.getItem())) this.mobType = MobAttribute.SCARECROW;
            else this.mobType = MobAttribute.UNDEFINED;
        }
    }

    private boolean isPumpkin(Item item) {
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            String name = item.getRegistryName().getPath();
            return block instanceof CarvedPumpkinBlock || name.contains("pumpkin") || name.contains("jack_o");
        }
        return false;
    }

    private boolean isUndeadSkull(ItemStack itemstack) {
        Item i = itemstack.getItem();
        return i == Items.WITHER_SKELETON_SKULL ||
                i == Items.SKELETON_SKULL ||
                i == Items.ZOMBIE_HEAD;
    }

    public boolean isScarecrow() {
        return this.mobType == MobAttribute.SCARECROW;
    }

    private EquipmentSlot getClickedSlot(Vec3 p_190772_1_) {
        EquipmentSlot equipmentslottype = EquipmentSlot.MAINHAND;
        double d0 = p_190772_1_.y;
        EquipmentSlot slot = EquipmentSlot.FEET;
        if (d0 >= 0.1D && d0 < 0.1D + (0.45D) && this.hasItemInSlot(slot)) {
            equipmentslottype = EquipmentSlot.FEET;
        } else if (d0 >= 0.9D + (0.0D) && d0 < 0.9D + (0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
            equipmentslottype = EquipmentSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
            equipmentslottype = EquipmentSlot.LEGS;
        } else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
            equipmentslottype = EquipmentSlot.HEAD;
        }
        return equipmentslottype;
    }


    private void setLastArmorItem(EquipmentSlot type, ItemStack stack) {
        this.lastArmorItems.set(type.getIndex(), stack);
    }

    public void applyEquipmentModifiers() {
        //living entity code here. apparently every entity does this check every tick.
        //trying instead to run it only when needed instead
        if (!this.level.isClientSide) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemstack;
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                    itemstack = this.lastArmorItems.get(equipmentSlot.getIndex());

                    ItemStack slot = this.getItemBySlot(equipmentSlot);
                    if (!ItemStack.matches(slot, itemstack)) {
                        if (!slot.equals(itemstack, true))
                            //packets are already handled by livingEntity detectEquipmentChange
                            //send packet
                            //Network.sendToAllTracking(this.world,this, new Network.PacketSyncEquip(this.getEntityId(), equipmentslottype.getIndex(), itemstack));
                            MinecraftForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(this, equipmentSlot, itemstack, slot));
                        if (!itemstack.isEmpty()) {
                            this.getAttributes().removeAttributeModifiers(itemstack.getAttributeModifiers(equipmentSlot));
                        }
                        if (!slot.isEmpty()) {
                            this.getAttributes().addTransientAttributeModifiers(slot.getAttributeModifiers(equipmentSlot));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void dropEquipment() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack armor = getItemBySlot(slot);
            if (!armor.isEmpty()) {
                this.spawnAtLocation(armor, 1.0f);
            }
        }
    }

    public void dismantle(boolean drops) {
        if (!this.level.isClientSide) {
            if (drops) {
                this.dropEquipment();
                this.spawnAtLocation(ModRegistry.DUMMY_ITEM.get(), 1);
            }
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getDeathSound(),
                    this.getSoundSource(), 1.0F, 1.0F);

            ((ServerLevel) this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (this.getBbWidth() / 4.0F),
                    (this.getBbHeight() / 4.0F), (this.getBbWidth() / 4.0F), 0.05D);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void kill() {
        this.dismantle(true);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(ModRegistry.DUMMY_ITEM.get());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN || source == DamageSource.IN_WALL;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        //not immune to void damage, immune to drown, wall
        if (source == DamageSource.OUT_OF_WORLD) {
            this.remove(RemovalReason.KILLED);
            return true;
        }
        //workaround for wither boss, otherwise it would keep targeting the dummy forever
        if (source.getDirectEntity() instanceof WitherBoss || source.getEntity() instanceof WitherBoss) {
            this.dismantle(true);
            return true;
        }
        // dismantling + adding players to dps message list
        if (source.getEntity() instanceof Player player) {
            if (player instanceof ServerPlayer sp) {
                currentlyAttacking.put(sp, Configs.cachedServer.MAX_COMBAT_INTERVAL);
            }
            // shift-leftclick with empty hand dismantles
            if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {
                dismantle(!player.isCreative());
                return false;
            }
        }
        this.currentDamageSource = source;
        boolean result = super.hurt(source, damage);
        this.currentDamageSource = null;
        //set to 0 to disable red glow that happens when hurt
        this.hurtTime = 0;

        return result;
    }

    //all damaging stuff will inevitably call this function. intercepting to block damage and show it
    @Override
    public void setHealth(float newHealth) {
        if (newHealth == this.getMaxHealth()) {
            super.setHealth(newHealth);
        } else {

            float damage = this.getHealth() - newHealth;
            if (damage > 0) {


                //if damage is in the same tick it gets added
                if (this.lastTickActuallyDamaged != this.tickCount) {
                    this.animationPosition = 0;
                }
                this.animationPosition = Math.min(this.animationPosition + damage, 60f);
                this.lastTickActuallyDamaged = this.tickCount;

                if (!this.level.isClientSide) {

                    CombatEntry e = this.getCombatTracker().getLastEntry();
                    DamageSource s = null;
                    //is same damage as
                    if(currentDamageSource != null) {
                        if (e != null && e.getTime() == this.tickCount && Math.abs(damage - e.getDamage()) < 0.001) {
                            s = e.getSource();
                        }
                    }

                    this.showDamageDealt(damage, getDamageType(s));
                    this.critical = false;
                    //this.currentDamageSource = null;
                }
            }
        }
    }

    private void showDamageDealt(float damage, DamageType type) {
        //custom update packet
        NetworkHandler.sendToAllTracking(this, (ServerLevel) this.level, new NetworkHandler.PacketDamageNumber(this.getId(), this.animationPosition));

        if (Configs.cachedServer.DAMAGE_NUMBERS) {
            // damage numebrssss
            DummyNumberEntity number = new DummyNumberEntity(damage, type, this.damageNumberPos++, this.level, this.currentlyAttacking.keySet().stream().map(Entity::getUUID).collect(Collectors.toSet()));
            number.moveTo(this.getX(), this.getY() + 1, this.getZ(), 0.0F, 0.0F);
            this.level.addFreshEntity(number);
        }

        this.totalDamageTakenInCombat += damage;
    }

    @Override
    public void tick() {

        //show true damage that has bypassed hurt method
        if (lastTickActuallyDamaged + 1 == this.tickCount && !this.level.isClientSide) {
            float trueDamage = this.getMaxHealth() - this.getHealth();
            if (trueDamage > 0) {
                this.heal(trueDamage);
                this.showDamageDealt(trueDamage, DamageType.TRUE);
            }
        }

        BlockPos onPos = this.getOnPos();

        //check if on stable ground. used for automation
        if (this.level.getGameTime() % 20L == 0L && !this.level.isClientSide) {
            if (level.isEmptyBlock(onPos)) {
                this.dismantle(true);
                return;
            }
        }

        this.setNoGravity(true);
        BlockState onState = this.level.getBlockState(onPos);
        onState.getBlock().stepOn(this.level, onPos, onState, this);

        //used for fire damage, poison damage etc.
        //so you can damage it like any mob

        this.baseTick();


        this.level.getProfiler().push("travel");
        this.travel(new Vec3(this.xxa, this.yya, this.zza));
        this.level.getProfiler().pop();


        this.level.getProfiler().push("push");
        this.pushEntities();
        this.level.getProfiler().pop();
        //end living tick stuff


        if (this.level.isClientSide) {
            //set to 0 to disable red glow that happens when hurt
            this.hurtTime = 0;//this.maxHurtTime;
            this.prevShakeAmount = this.shakeAmount;
            this.prevAnimationPosition = this.animationPosition;
            //client animation
            if (this.animationPosition > 0) {

                this.shakeAmount++;
                this.animationPosition -= 0.8f;
                if (this.animationPosition <= 0) {
                    this.shakeAmount = 0;
                    this.animationPosition = 0;
                }
            }

        } else {
            // DPS!
            CombatTracker tracker = this.getCombatTracker();


            //am i being attacked?
            if (tracker.isInCombat() && this.totalDamageTakenInCombat > 0) {

                float combatDuration = tracker.getCombatDuration();
                Configs.DpsMode dpsMode = Configs.cached.DYNAMIC_DPS;
                if (dpsMode != Configs.DpsMode.OFF && combatDuration > 0) {

                    boolean dynamic = dpsMode == Configs.DpsMode.DYNAMIC;
                    float seconds = combatDuration / 20f + 1;
                    float dps = totalDamageTakenInCombat / seconds;
                    List<ServerPlayer> outOfCombat = new ArrayList<>();

                    for (ServerPlayer p : this.currentlyAttacking.keySet()) {
                        int timer = this.currentlyAttacking.get(p) - 1;
                        this.currentlyAttacking.replace(p, timer);


                        boolean showMessage = dynamic && this.lastTickActuallyDamaged + 1 == this.tickCount;
                        if (timer <= 0) {
                            outOfCombat.add(p);
                            if (!dynamic) showMessage = true;
                        }
                        //here is to visually show dps on status message
                        if (showMessage && p.distanceTo(this) < 64) {
                            p.displayClientMessage(new TranslatableComponent("message.dummmmmmy.dps",
                                    this.getDisplayName(),
                                    new DecimalFormat("#.##").format(dps)), true);
                        }
                    }

                    outOfCombat.forEach(currentlyAttacking::remove);
                }
            } else {
                this.currentlyAttacking.clear();
                this.totalDamageTakenInCombat = 0;
            }
        }
    }

    @Override
    public void setDeltaMovement(Vec3 motionIn) {
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void markHurt() {
    }

    @Override
    public boolean isPickable() {
        return super.isPickable();
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public @NotNull MobType getMobType() {
        return this.mobType.get();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.MAX_HEALTH, 40D)
                .add(Attributes.ARMOR, 0D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.FLYING_SPEED, 0D);
    }


    private enum MobAttribute {
        UNDEFINED,
        UNDEAD,
        WATER,
        ILLAGER,
        ARTHROPOD,
        SCARECROW;

        public MobType get() {
            return switch (this) {
                case UNDEFINED, SCARECROW -> MobType.UNDEFINED;
                case UNDEAD -> MobType.UNDEAD;
                case WATER -> MobType.WATER;
                case ILLAGER -> MobType.ILLAGER;
                case ARTHROPOD -> MobType.ARTHROPOD;
            };
        }
    }

    public enum DamageType {
        GENERIC,
        CRIT,
        DRAGON,
        WITHER,
        EXPLOSION,
        MAGIC,
        IND_MAGIC,
        TRIDENT,
        FIRE,
        LIGHTNING,
        CACTUS,
        TRUE;

        //only client
        public int getColor() {
            return switch (this) {
                case CRIT -> Configs.cached.DAMAGE_CRIT;
                case FIRE -> Configs.cached.DAMAGE_FIRE;
                case MAGIC -> Configs.cached.DAMAGE_MAGIC;
                case CACTUS -> Configs.cached.DAMAGE_CACTUS;
                case DRAGON -> Configs.cached.DAMAGE_DRAGON;
                case WITHER -> Configs.cached.DAMAGE_WITHER;
                case GENERIC -> Configs.cached.DAMAGE_GENERIC;
                case TRIDENT -> Configs.cached.DAMAGE_TRIDENT;
                case EXPLOSION -> Configs.cached.DAMAGE_EXPLOSION;
                case IND_MAGIC -> Configs.cached.DAMAGE_IND_MAGIC;
                case LIGHTNING -> Configs.cached.DAMAGE_LIGHTNING;
                case TRUE -> Configs.cached.DAMAGE_TRUE;
            };
        }
    }

    public DamageType getDamageType(DamageSource source) {
        if (source == null) return DamageType.TRUE;
        if (this.critical) return DamageType.CRIT;
        if (source == DamageSource.DRAGON_BREATH) return DamageType.DRAGON;
        if (source == DamageSource.WITHER) return DamageType.WITHER;
        if (source.msgId.equals("explosion") || source.msgId.equals("explosion.player") || source.isExplosion())
            return DamageType.EXPLOSION;
        if (source.msgId.equals("indirectMagic")) return DamageType.IND_MAGIC;
        if (source.msgId.equals("trident")) return DamageType.TRIDENT;
        if (source == DamageSource.HOT_FLOOR || source == DamageSource.LAVA || source == DamageSource.ON_FIRE
                || source == DamageSource.IN_FIRE || source.isFire()) return DamageType.FIRE;
        if (source == DamageSource.MAGIC || source.isMagic()) {
            //would really like to detect poison damage, but I don't think there's a simple way
            return DamageType.MAGIC;
        }
        if (source == DamageSource.LIGHTNING_BOLT) return DamageType.LIGHTNING;

        if (source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH) return DamageType.CACTUS;
        return DamageType.GENERIC;
    }

}