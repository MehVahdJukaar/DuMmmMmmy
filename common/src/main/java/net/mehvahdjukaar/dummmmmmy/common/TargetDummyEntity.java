
package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.configs.CommonConfigs;
import net.mehvahdjukaar.dummmmmmy.network.ClientBoundDamageNumberMessage;
import net.mehvahdjukaar.dummmmmmy.network.ClientBoundUpdateAnimationMessage;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class TargetDummyEntity extends Mob {

    private static final int SHIELD_COOLDOWN = 100;
    private static final int HEALTH_RECHARGE_TIME = 160;

    private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(TargetDummyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BOSS = SynchedEntityData.defineId(TargetDummyEntity.class, EntityDataSerializers.BOOLEAN);

    // used to calculate the whole damage in one tick, in case there are multiple sources
    private int lastTickActuallyDamaged;
    // currently, recording damage taken
    private float totalDamageTakenInCombat;
    private float totalHealingTakenInCombat;
    //has just been hit by critical? server side
    private final List<CritRecord> critRecordsThisTick = new ArrayList<>();
    private DummyMobType mobType = DummyMobType.UNDEFINED;

    private DamageSource currentDamageSource = null;
    private boolean unbreakable = false;
    private final PlayersTracker playersTracker = new PlayersTracker();
    private int healthRechargeTimer = 0;
    private float lastHealth;

    //client values

    private float prevAnimationPosition = 0;
    private float animationPosition;
    //position of damage number in the semicircle
    private int damageNumberPos = 0;

    // used to have an independent start for the animation, otherwise the phase of the animation depends ont he damage dealt
    private float shakeAmount = 0;
    private float prevShakeAmount = 0;
    private int shieldCooldown = 0;


    public TargetDummyEntity(EntityType<TargetDummyEntity> type, Level world) {
        super(type, world);
        this.xpReward = 0;
        this.setCanPickUpLoot(false);
        Arrays.fill(this.armorDropChances, 1.1f);
        this.playersTracker.showHealthBar(false);
    }

    public TargetDummyEntity(Level world) {
        this(Dummmmmmy.TARGET_DUMMY.get(), world);
    }

    public float getShake(float partialTicks) {
        return Mth.lerp(partialTicks, prevShakeAmount, shakeAmount);
    }

    public float getAnimationPosition(float partialTicks) {
        return Mth.lerp(partialTicks, prevAnimationPosition, animationPosition);
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(SHEARED, sheared);
    }

    public boolean isBoss() {
        return this.entityData.get(BOSS);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (dataAccessor == BOSS) {
            this.playersTracker.showHealthBar(this.isBoss());
        }
    }

    public void setBoss(boolean boss) {
        this.entityData.set(BOSS, boss);
        this.playersTracker.showHealthBar(boss);
    }

    public boolean canScare() {
        return this.mobType == DummyMobType.SCARECROW;
    }

    public boolean canAttract() {
        return this.mobType == DummyMobType.DECOY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.HEAD) {
            this.mobType = DummyMobType.get(stack);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHEARED, false);
        builder.define(BOSS, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sheared", this.isSheared());
        tag.putInt("HealthRechargeTimer", this.healthRechargeTimer);
        if (this.unbreakable) tag.putBoolean("Unbreakable", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean("Sheared"));
        this.healthRechargeTimer = tag.getInt("HealthRechargeTimer");
        if (tag.contains("Unbreakable")) {
            this.unbreakable = tag.getBoolean("Unbreakable");
        }
        this.mobType = DummyMobType.get(this.getItemBySlot(EquipmentSlot.HEAD));
        this.setBoss(this.getItemBySlot(EquipmentSlot.OFFHAND).getItem() instanceof BannerItem);
        this.lastHealth = this.getHealth();
    }

    // dress it up! :D
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        boolean success = false;
        if (!player.isSpectator() && player.getAbilities().mayBuild) {
            ItemStack itemstack = player.getItemInHand(hand);
            EquipmentSlot equipmentSlot = getEquipmentSlotForItem(itemstack);

            Item item = itemstack.getItem();

            //special items
            if (DummyMobType.get(itemstack) != DummyMobType.UNDEFINED ||
                    ForgeHelper.canEquipItem(this, itemstack, EquipmentSlot.HEAD)) {
                equipmentSlot = EquipmentSlot.HEAD;
            }

            // empty hand -> unequip
            Level level = player.level();
            if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                equipmentSlot = this.getClickedSlot(vec);
                if (equipmentSlot == null) {
                    if (hasItemInSlot(EquipmentSlot.MAINHAND)) {
                        equipmentSlot = EquipmentSlot.MAINHAND;
                    } else equipmentSlot = EquipmentSlot.OFFHAND;
                }
                if (this.hasItemInSlot(equipmentSlot)) {
                    if (level.isClientSide) return InteractionResult.CONSUME;
                    this.swapItem(player, equipmentSlot, ItemStack.EMPTY, hand);
                    success = true;

                }
            }
            // armor item in hand -> equip/swap
            else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                if (level.isClientSide) return InteractionResult.CONSUME;
                this.swapItem(player, equipmentSlot, itemstack, hand);
                success = true;

            } else if (itemstack.getItem() instanceof ShieldItem) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
                if (level.isClientSide) return InteractionResult.CONSUME;
                this.swapItem(player, EquipmentSlot.MAINHAND, itemstack, hand);

                success = true;
            }
            //remove sack
            else if (item instanceof ShearsItem) {
                if (!this.isSheared()) {
                    player.playSound(SoundEvents.SNOW_GOLEM_SHEAR, 1.0F, 1.0F);
                    if (level.isClientSide) return InteractionResult.CONSUME;
                    this.setSheared(true);
                    return InteractionResult.SUCCESS;
                }
            } else if (item instanceof BannerItem) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 1.0F);
                if (level.isClientSide) return InteractionResult.CONSUME;
                this.swapItem(player, EquipmentSlot.OFFHAND, itemstack, hand);
                this.setBoss(true);
                return InteractionResult.SUCCESS;
            }

            if (success) return InteractionResult.SUCCESS;

        }
        return InteractionResult.PASS;
    }

    private void swapItem(Player player, EquipmentSlot slot, ItemStack armor, InteractionHand hand) {
        ItemStack oldArmor = this.getItemBySlot(slot);
        player.setItemInHand(hand, oldArmor.copy());
        this.setItemSlotAndDropWhenKilled(slot, armor);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        //clears boss bar when killed
        this.setBoss(false);
    }

    @Nullable
    private EquipmentSlot getClickedSlot(Vec3 vec3) {
        EquipmentSlot equipmentSlot = null;
        double d0 = vec3.y;
        EquipmentSlot slot = EquipmentSlot.FEET;
        if (d0 >= 0.1D && d0 < 0.1D + (0.45D) && this.hasItemInSlot(slot)) {
            equipmentSlot = EquipmentSlot.FEET;
        } else if (d0 >= 0.9D + (0.0D) && d0 < 0.9D + (0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
            equipmentSlot = EquipmentSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + (0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
            equipmentSlot = EquipmentSlot.LEGS;
        } else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
            equipmentSlot = EquipmentSlot.HEAD;
        }
        return equipmentSlot;
    }

    @Override
    public void dropEquipment() {
        dropPreservedEquipment();
        this.spawnAtLocation(getPickResult(), 1);
    }

    // same as super just spawns higher
    @Override
    public Set<EquipmentSlot> dropPreservedEquipment(Predicate<ItemStack> predicate) {
        Set<EquipmentSlot> set = new HashSet<>();

        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            if (!itemStack.isEmpty()) {
                if (!predicate.test(itemStack)) {
                    set.add(equipmentSlot);
                } else {
                    double d = this.getEquipmentDropChance(equipmentSlot);
                    if (d > 1.0) {
                        this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                        this.spawnAtLocation(itemStack, 1);
                    }
                }
            }
        }


        return set;
    }

    public void dismantle(boolean drops) {
        Level level = this.level();
        if (!level.isClientSide && this.isAlive()) {
            if (drops) this.dropEquipment();

            this.playSound(this.getDeathSound(), 1.0F, 1.0F);

            ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (this.getBbWidth() / 4.0F),
                    (this.getBbHeight() / 4.0F), (this.getBbWidth() / 4.0F), 0.05D);

            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    private void setRecharging() {
        this.healthRechargeTimer = HEALTH_RECHARGE_TIME;
        CombatTracker tracker = this.getCombatTracker();
        //tracker.inCombat = false;
        tracker.entries.clear();
        playersTracker.finishFight();
        this.level().broadcastEntityEvent(this, (byte) 32);
    }

    public float getRechargingAnimation(float partialTicks) {
        return Math.max(0, Mth.lerp(partialTicks, this.healthRechargeTimer, this.healthRechargeTimer - 1) / HEALTH_RECHARGE_TIME);
    }

    public boolean isRecharging() {
        return this.healthRechargeTimer > 0;
    }

    public boolean hasInfiniteHealth() {
        return !(this.isBoss() && playersTracker.hasPlayers());
    }

    @Override
    @NotNull
    public ItemStack getPickResult() {
        ItemStack itemStack = new ItemStack(Dummmmmmy.DUMMY_ITEM.get());
        if (this.hasCustomName()) {
            itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        }
        return itemStack;
    }

    @Override
    public void kill() {
        this.dismantle(true);
    }

    @Override
    public boolean isBlocking() {
        return shieldCooldown == 0 && !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    @Override
    protected void blockUsingShield(LivingEntity attacker) {
        super.blockUsingShield(attacker);
        // same as player
        if (attacker.canDisableShield()) {
            this.disableShield();
        } else {
            this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
        }
    }

    //same as player
    private void disableShield() {
        this.shieldCooldown = SHIELD_COOLDOWN;
        this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
        this.level().broadcastEntityEvent(this, (byte) 30);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 30) {
            this.shieldCooldown = SHIELD_COOLDOWN;
        }
        if (id == 32) {
            this.healthRechargeTimer = HEALTH_RECHARGE_TIME;
        }
        super.handleEntityEvent(id);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return isRecharging() ||
                super.isInvulnerableTo(source) || source == this.damageSources().drown() ||
                source == this.damageSources().inWall();
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        //not immune to void damage, immune to drown, wall
        if (source == this.damageSources().fellOutOfWorld()) {
            this.remove(RemovalReason.KILLED);
            return true;
        }
        //workaround for wither boss, otherwise it would keep targeting the test double forever
        if (source.getDirectEntity() instanceof WitherBoss || source.getEntity() instanceof WitherBoss) {
            this.dismantle(true);
            return true;
        }
        // dismantling + adding players to dps message list
        if (source.getEntity() instanceof Player player) {
            if (player instanceof ServerPlayer sp) {
                playersTracker.track(sp);
            }
            // shift-left-click with empty hand dismantles
            if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && !this.unbreakable && player.getAbilities().mayBuild) {
                dismantle(!player.isCreative());
                return false;
            }
        }
        //pets
        if (source.getEntity() instanceof TamableAnimal animal && animal.isTame()) {
            if (animal.getOwner() instanceof ServerPlayer player) {
                playersTracker.track(player);
            }
        }

        if (level().isClientSide) return false;
        //for recursion
        var old = currentDamageSource;
        this.currentDamageSource = source;
        if (!this.critRecordsThisTick.isEmpty()) {
            CritRecord critRecord = this.critRecordsThisTick.get(this.critRecordsThisTick.size() - 1);
            if (critRecord.canCompleteWith(source)) {
                critRecord.addSource(source);
            }
        }
        boolean result = super.hurt(source, damage);
        this.currentDamageSource = old;
        //set to zero to disable a red glow that happens when hurt
        this.hurtTime = 0;


        return result;
    }

    //all damaging stuff will inevitably call this function.
    //intercepting to block damage and show it
    //this is only called server side
    @Override
    public void setHealth(float newHealth) {
        if (newHealth == this.getMaxHealth()) {
            super.setHealth(newHealth);
        } else {
            Level level = this.level();

            if (level.isClientSide) return;
            float damage = this.getHealth() - newHealth;
            DamageSource actualSource = getActualDamageSource(damage);

            if (actualSource != null || damage < 0) {
                onActuallyDamagedOrTrueDamageDetected(damage, actualSource);
            }
            this.lastTickActuallyDamaged = this.tickCount;
            if (!hasInfiniteHealth()) {
                doSetHealth(newHealth);

                if (newHealth <= 0) {
                    setRecharging();
                }
            }
        }
    }

    private void doSetHealth(float newHealth) {
        super.setHealth(newHealth);
        this.lastHealth = getHealth();
        this.playersTracker.updateHealth();
    }

    private @Nullable DamageSource getActualDamageSource(float damage) {
        DamageSource actualSource = null;
        //Accounts for forge event modifying damage... I think. On fabric this isn't set yet
        if (PlatHelper.getPlatform().isForge()) {
            CombatEntry currentCombatEntry = getLastEntry();
            //Is same damage as current one. Sanity-check, I guess
            if (currentCombatEntry != null && getCombatTracker().lastDamageTime == this.tickCount
                //idk why but some rounding errors could occur. we still want to sanity check this i think? or not
                //&& DoubleMath.fuzzyEquals(damage, currentCombatEntry.damage(), 0.0001)
            ) {
                actualSource = currentCombatEntry.source();
                if (Math.abs(damage - currentCombatEntry.damage()) > 0.0001) {
                    int error = 0;
                }
            }
        } else actualSource = currentDamageSource;
        return actualSource;
    }

    @Override
    public boolean isDeadOrDying() {
        // can never die from health
        return false;
    }

    private void onActuallyDamagedOrTrueDamageDetected(float damage, @Nullable DamageSource actualSource) {
        if (this.isRecharging()) return;
        if (damage < 0) {
            //keep showing heals to players
            playersTracker.getPlayers().forEach(playersTracker::track);
        }
        showDamageAndAnimationsToClients(damage, actualSource);
        if (damage < 0) {
            this.totalHealingTakenInCombat -= damage;
        } else {
            this.totalDamageTakenInCombat += damage;
        }
        updateTargetBlock(damage);
        if (level() instanceof ServerLevel sl && damage > 0) {
            float xp = CommonConfigs.DROP_XP.get().floatValue() * damage;
            if (xp > 0) {
                ExperienceOrb.award(sl, this.position().add(0, 0.5, 0), Mth.floor(xp));
            }
        }
    }

    @Nullable
    public CombatEntry getLastEntry() {
        CombatTracker tracker = this.getCombatTracker();
        if (tracker.entries.isEmpty()) {
            return null;
        }
        return tracker.entries.get(tracker.entries.size() - 1);
    }

    private void showDamageAndAnimationsToClients(float damage, @Nullable DamageSource source) {

        //if damage is in the same tick, it gets added otherwise we reset
        // this is also used server side to track added damage
        if (this.lastTickActuallyDamaged != this.tickCount) {
            this.animationPosition = 0;
        }
        if (damage > 0) {
            this.animationPosition = Math.min(this.animationPosition + damage, 60f);

            //custom update packet to send animation position
            NetworkHelper.sendToAllClientPlayersTrackingEntity(this,
                    new ClientBoundUpdateAnimationMessage(this.getId(), this.animationPosition));
        }
        if (playersTracker.hasPlayers()) {
            CritRecord critRec = null;
            for (int j = critRecordsThisTick.size() - 1; j >= 0; j--) {
                var c = critRecordsThisTick.get(j);
                if (c.matches(source)) {
                    critRec = c;
                    break;
                }
            }

            for (var p : this.playersTracker.getPlayers()) {
                NetworkHelper.sendToClientPlayer(p,
                        new ClientBoundDamageNumberMessage(this.getId(), damage, source, critRec, level()));
            }
            if (critRec != null) {
                this.critRecordsThisTick.remove(critRec);
            }
        }
    }

    private void updateTargetBlock(float damage) {
        BlockPos pos = this.getOnPos();
        BlockState state = this.getBlockStateOn();
        if (state.getBlock() instanceof TargetBlock) {
            Level level = level();
            if (!level.getBlockTicks().hasScheduledTick(pos, state.getBlock())) {
                int power = (int) Mth.clamp((damage / this.getHealth()) * 15, 1, 15);
                level.setBlock(pos, state.setValue(BlockStateProperties.POWER, power), 3);
                level.scheduleTick(pos, state.getBlock(), 20);
            }
        }
    }

    @Override
    protected void updateControlFlags() {
    }

    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(0, this.getEyeHeight() - 1, 0);
    }

    @Override
    public void aiStep() {
        // for fire and powder snow. Better call this to match what a normal entity does
        super.aiStep();
    }


    @Override
    public void tick() {
        if (this.shieldCooldown > 0) {
            this.shieldCooldown--;
        }
        this.critRecordsThisTick.clear();

        Level level = this.level();
        BlockPos onPos = this.getOnPos();
        float health = this.getHealth();

        if (!level.isClientSide) {

            //show true damage that has bypassed hurt method
            if (lastTickActuallyDamaged + 1 == this.tickCount && !level.isClientSide) {
                float trueDamage = lastHealth - health;
                if (trueDamage > 0) {
                    if (hasInfiniteHealth()) {
                        // unfortunately we cant show true damage when in boss mode
                        this.heal(trueDamage);
                    }
                    onActuallyDamagedOrTrueDamageDetected(trueDamage, null);
                }
            }

            //check if on stable ground. used for automation
            if (level.getGameTime() % 20L == 0L) {
                if (level.isEmptyBlock(onPos)) {
                    this.dismantle(true);
                    return;
                }
            }
        }

        if (healthRechargeTimer != 0) {
            healthRechargeTimer--;
            this.doSetHealth(health + this.getMaxHealth() / HEALTH_RECHARGE_TIME);
            if (healthRechargeTimer == 0) {
                playersTracker.clear();
                this.totalHealingTakenInCombat = 0;
                this.totalDamageTakenInCombat = 0;
            }
        }

        this.setNoGravity(true);
        // for some raeson this is not done in aiStep
        BlockState onState = level.getBlockState(onPos);
        onState.getBlock().stepOn(level, onPos, onState, this);


        super.tick();


        if (level.isClientSide) {
            //set to zero to disable the red glow that happens when hurt
            this.hurtTime = 0; //this.maxHurtTime;
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
            displayCombatMessages();
        }
    }

    private void displayCombatMessages() {
        // DPS!
        CombatTracker tracker = this.getCombatTracker();

        //TODO: move dps mode logic to client
        //am i being attacked?
        if (tracker.inCombat) {
            float combatDuration = tracker.getCombatDuration();
            this.playersTracker.update(combatDuration, this.totalDamageTakenInCombat, this.totalHealingTakenInCombat);
        } else {
            totalDamageTakenInCombat = 0;
            if (!playersTracker.hasPlayers()) {
                totalHealingTakenInCombat = 0;
            }
        }
    }

    @Override
    public void setDeltaMovement(Vec3 motionIn) {
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
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
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
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
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    public @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    // these 2 mimic what tags do but depending on entity state
    @Override
    public boolean isInvertedHealAndHarm() {
        return this.getType().is(EntityTypeTags.INVERTED_HEALING_AND_HARM) ||
                this.mobType.isInvertedHealAndHarm();
    }

    // mimic tag behavior
    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if ((effectInstance.is(MobEffects.REGENERATION) || effectInstance.is(MobEffects.POISON))
                && this.mobType.ignoresPoisonAndRegen()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.playersTracker.healthBar.setName(this.getDisplayName());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.playersTracker.unTrack(serverPlayer);
    }


    public @NotNull DummyMobType getMobType() {
        return this.mobType;
    }

    public static AttributeSupplier.Builder makeAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.MAX_HEALTH, CommonConfigs.BOSS_HEALTH.get())
                .add(Attributes.ARMOR, 0D)
                .add(Attributes.ATTACK_DAMAGE, 0D)
                .add(Attributes.FLYING_SPEED, 0D);
    }

    public void updateAnimation(float shake) {
        this.animationPosition = shake;
    }

    public void moist(Entity attacker, float critModifier) {
        this.critRecordsThisTick.add(new CritRecord(attacker, critModifier));
    }

    public int getNextNumberPos() {
        return damageNumberPos++;
    }

    private void showDpsMessageTo(ServerPlayer player, float combatDuration, float dps, float hps, boolean outOfCompat) {
        CommonConfigs.DpsMode dpsMode = CommonConfigs.DYNAMIC_DPS.get();

        if (dpsMode != CommonConfigs.DpsMode.OFF && combatDuration > 0) {
            boolean showMessage;
            if (dpsMode == CommonConfigs.DpsMode.DYNAMIC) {
                showMessage = this.lastTickActuallyDamaged + 1 == this.tickCount;
            } else showMessage = outOfCompat;

            //here is to visually show dps on a status message
            if (showMessage && player.distanceTo(this) < 64) {
                Component message;
                Component dpsMessage = Component.translatable("message.dummmmmmy.dps",
                        Dummmmmmy.DF2.format(dps));
                Component hpsMessage = Component.translatable("message.dummmmmmy.hps",
                        Dummmmmmy.DF2.format(hps));

                if (dps > 0 && hps > 0) {
                    message = Component.translatable("message.dummmmmmy.double",
                            this.getDisplayName(),
                            dpsMessage,
                            hpsMessage);
                } else if (dps > 0) {
                    message = Component.translatable("message.dummmmmmy.single",
                            this.getDisplayName(),
                            dpsMessage);
                } else if (hps > 0) {
                    message = Component.translatable("message.dummmmmmy.single",
                            this.getDisplayName(),
                            hpsMessage);
                } else return;

                player.displayClientMessage(message, true);

            }
        }
    }

    private class PlayersTracker {

        private final Map<ServerPlayer, Integer> currentlyAttacking = new HashMap<>();
        private final ServerBossEvent healthBar = new ServerBossEvent(getDisplayName(), CommonConfigs.BOSS_HEALTH_COLOR.get(),
                BossEvent.BossBarOverlay.NOTCHED_12);

        public void showHealthBar(boolean on) {
            healthBar.setVisible(on);
            if (!on) {
                healthBar.removeAllPlayers();
            }
        }

        public void updateHealth() {
            healthBar.setProgress(getHealth() / getMaxHealth());
        }

        public void track(ServerPlayer serverPlayer) {
            currentlyAttacking.put(serverPlayer, 300); //needs to match combat tracker. This works the same but per player instead of per receiving entity
            healthBar.addPlayer(serverPlayer);
        }

        public void unTrack(ServerPlayer serverPlayer) {
            currentlyAttacking.remove(serverPlayer);
            if (!TargetDummyEntity.this.isRecharging()) healthBar.removePlayer(serverPlayer);
        }

        public void update(float combatDuration, float totalDamageTakenInCombat, float totalHealingTakenInCombat) {
            List<ServerPlayer> removedPlayers = new ArrayList<>();

            float seconds = combatDuration / 20f + 1;
            float dps = totalDamageTakenInCombat / seconds;
            float hps = totalHealingTakenInCombat / seconds;

            for (var e : currentlyAttacking.entrySet()) {
                ServerPlayer p = e.getKey();
                int timer = e.getValue() - 1;
                currentlyAttacking.replace(p, timer);
                boolean outOfCombat = false;
                if (timer <= 0) {
                    removedPlayers.add(p);
                    outOfCombat = true;
                }
                showDpsMessageTo(p, combatDuration, dps, hps, outOfCombat);
            }
            removedPlayers.forEach(this::unTrack);
        }


        public boolean hasPlayers() {
            return !currentlyAttacking.isEmpty();
        }

        public Collection<ServerPlayer> getPlayers() {
            return currentlyAttacking.keySet();
        }

        public void clear() {
            currentlyAttacking.clear();
            healthBar.removeAllPlayers();
        }

        public void finishFight() {
            currentlyAttacking.replaceAll((p, i) -> 0);
        }
    }

}
