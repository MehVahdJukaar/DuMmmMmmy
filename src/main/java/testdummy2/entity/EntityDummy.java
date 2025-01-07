package testdummy2.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import testdummy2.TestDummy2;
import testdummy2.handlers.ConfigHandler;
import testdummy2.network.DamageMessage;
import testdummy2.network.SyncEquipmentMessage;

import java.util.Arrays;
import java.util.UUID;

public class EntityDummy extends EntityLiving implements IEntityAdditionalSpawnData {
    public float shake;
    public float shakeAnimation;

    private int lastDamageTick;
    private int firstDamageTick;

    private int damageCounter;
    private float damageTaken;
    private float maxDamage;

    private EntityFloatingNumber myLittleNumber;
    private float customRotation;
    private EntityPlayer lastAttacker;

    private final static float defaultHealth = 10000F;
    private final static UUID healthUUID = new UUID(-8110296273408798866L,-8643467302095110506L);

    public EntityDummy(World world) {
        super(world);
        Arrays.fill(this.inventoryArmorDropChances, 1.1F);
    }

    protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(new AttributeModifier(healthUUID,"DummyHealth",defaultHealth-20F,0));
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(healthUUID,"DummyHealth",defaultHealth-20F,0));
    }

    public void setPlacementRotation(Vec3d lookVector, int side) {
        int r;
        switch (side) {
            case 0:
            case 1:
                r = (int) (Math.atan2(lookVector.z, lookVector.x) * 360.0D / 6.283185307179586D);
                r += 90;
                break;
            case 2:
                r = 180;
                break;
            case 4:
                r = 90;
                break;
            case 5:
                r = 270;
                break;
            case 3:
            default:
                r = 0;
        }

        this.customRotation = r;
        setCustomRotationStuff();
    }

    private void setCustomRotationStuff() {
        float r = this.customRotation;
        this.prevRotationYawHead = this.rotationYawHead = r;
        this.prevRotationYaw = this.rotationYaw = r;
        this.prevRenderYawOffset = this.renderYawOffset = r;
        this.randomYawVelocity = 0.0F;
    }

    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (itemStack.isEmpty()) {
            return removeArmor(player);
        }
        return equipArmor(player, itemStack);
    }

    private boolean equipArmor(EntityPlayer player, ItemStack itemStack) {
        Item item = itemStack.getItem();
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                if (item.isValidArmor(itemStack, slot, player)) {
                    ItemStack armor = getItemStackFromSlot(slot);
                    if (!armor.isEmpty() && !this.world.isRemote) {
                        entityDropItem(armor, 1.0F);
                    }
                    armor = itemStack.copy();
                    armor.setCount(1);
                    if (!this.world.isRemote) {
                        TestDummy2.proxy.network.sendToAllAround(new SyncEquipmentMessage(getEntityId(), slot.ordinal(), armor), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 20.0D));
                    }
                    setItemStackToSlot(slot, armor);
                    getAttributeMap().applyAttributeModifiers(armor.getAttributeModifiers(slot));
                    itemStack.shrink(1);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean removeArmor(EntityPlayer player) {
        if (!player.isSneaking()) {
            return false;
        }
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                ItemStack armor = getItemStackFromSlot(slot);
                if (!armor.isEmpty()) {
                    if (!this.world.isRemote) {
                        if (!player.capabilities.isCreativeMode) {
                            entityDropItem(armor, 1.0F);
                        }
                        TestDummy2.proxy.network.sendToAllAround(new SyncEquipmentMessage(getEntityId(), slot.ordinal(), ItemStack.EMPTY), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 20.0D));
                    }
                    setItemStackToSlot(slot, ItemStack.EMPTY);
                    getAttributeMap().removeAttributeModifiers(armor.getAttributeModifiers(slot));
                    return true;
                }
            }
        }
        return false;
    }

    public void dismantle() {
        if (!this.world.isRemote) {
            dropEquipment(true, 999);
            dropItem(TestDummy2.itemDummy, 1);
        }
        setDead();
    }

    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (this.world.isRemote) return false;
        if (this.isEntityInvulnerable(source)) return false;

        if(source.getTrueSource()==null) return false;
        if (!(source.getTrueSource() instanceof EntityPlayer)) return false;

        EntityPlayer player = (EntityPlayer) source.getTrueSource();
        lastAttacker = player;
        if (player.isSneaking() && player.getHeldItemMainhand().isEmpty()) {
            dismantle();
            return false;
        }

        //Don't count fire or other side dmgs (potions) that happened since last hit
        this.setHealth(this.getMaxHealth());

        //Vanilla dmg calc
        if(ConfigHandler.server.useIframes) {
            if ((float) this.hurtResistantTime > (float) this.maxHurtResistantTime / 2.0F) {
                if (damage <= this.lastDamage)
                    return false;
                //Intended behavior:
                //1. Use for further damage calc only the part that is more than last dmg (difference currDmg-lastDmg)
                //2. Set lastDamage to the full damage, not just the currently used difference
                //Intention by MC is probably that a third even stronger attack during iframes will only deal the difference to the second attack, not the first one.
                float fullDamage = damage;          //0. save full damage in tmp variable
                damage = damage - this.lastDamage;  //1. only the difference to last highest attack is being used for further calculations
                this.lastDamage = fullDamage;       //2. save full damage of the attack in lastDamage
            } else {
                this.lastDamage = damage;
                this.hurtResistantTime = this.maxHurtResistantTime;
            }
        }

        if(ConfigHandler.server.useLivingHurtEvent) {
            damage = ForgeHooks.onLivingHurt(this, source, damage);
            if (damage <= 0)
                return false;
        }
        if(ConfigHandler.server.useArmorCalc)
            damage = applyArmorCalculations(source, damage);
        if(ConfigHandler.server.useResistanceCalc)
            damage = applyPotionDamageCalculations(source, damage);
        if(ConfigHandler.server.useLivingDamageEvent)
            damage = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, source, damage);

        //Compat for mods like SME reducing health outside the usual pathways
        if(ConfigHandler.server.useNoEventDmg) {
            float healthLost = this.getMaxHealth() - this.getHealth();
            damage += healthLost;
        }
        this.setHealth(this.getMaxHealth());

        this.shake = Math.min(damage, 30.0F);
        this.lastDamageTick = this.ticksExisted;

        this.hurtTime = this.maxHurtTime = 10;

        if(ConfigHandler.server.showHearts)
            damage /= 2F;

        if (this.myLittleNumber != null && !this.myLittleNumber.isDead) {
            this.myLittleNumber.setDead();
        }

        EntityFloatingNumber number = new EntityFloatingNumber(this.world, damage, this.posX, this.posY + 2.0D, this.posZ);
        this.myLittleNumber = number;
        this.world.spawnEntity(number);
        TestDummy2.proxy.network.sendToAllAround(new DamageMessage(damage, this.shake, this, this.myLittleNumber), new NetworkRegistry.TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 20.0D));
        this.maxDamage = Math.max(damage,this.maxDamage);
        this.damageTaken += damage;
        this.damageCounter++;
        if (this.firstDamageTick == 0) {
            this.firstDamageTick = this.ticksExisted;
        }

        return true;
    }

    public void onUpdate() {
        super.onUpdate();

        if (this.shake > 0.0F) {
            this.shakeAnimation++;
            this.shake -= 0.8F;
            if (this.shake <= 0.0F) {
                this.shakeAnimation = 0.0F;
                this.shake = 0.0F;
            }
        }

        if (this.damageTaken > 0.0F && this.ticksExisted - this.lastDamageTick > ConfigHandler.server.ticksUntilDPS) {
            this.extinguish();
            if(this.world.isRemote) return;

            if (this.firstDamageTick < this.lastDamageTick) createFloatingDPSNumber();
            this.damageTaken = 0F;
            this.damageCounter = 0;
            this.maxDamage = 0F;
            this.firstDamageTick = 0;
        }

    }

    private void createFloatingDPSNumber() {
        float seconds = (this.lastDamageTick - this.firstDamageTick) / 20.0F;
        seconds *= this.damageCounter / (this.damageCounter - 1F);   //Correction for small amount of hits (last hit's window goes until start of theoretical next hit)
        float dps = this.damageTaken / seconds;
        float avg = this.damageTaken / this.damageCounter;
        float hps = this.damageCounter / seconds;
        float outputValue;
        switch(ConfigHandler.server.outputMessage){
            case "AVG": outputValue = avg; break;
            case "HPS": outputValue = hps; break;
            case "MAX": outputValue = this.maxDamage; break;
            case "DPS":
            default: outputValue = dps;
        }
        this.world.spawnEntity(new EntityDpsFloatingNumber(this.world, outputValue, this.posX, this.posY + 3.0D, this.posZ));

        if(ConfigHandler.server.sendMsgInChat) {
            String msg = "DPS " + TextFormatting.GRAY + TestDummy2.df.format(dps) + TextFormatting.WHITE +
                    ", HPS " + TextFormatting.GRAY + TestDummy2.df.format(hps) + TextFormatting.WHITE +
                    ", AVG " + TextFormatting.GRAY + TestDummy2.df.format(avg) + TextFormatting.WHITE +
                    ",MAX " + TextFormatting.GRAY + TestDummy2.df.format(this.maxDamage);
            this.lastAttacker.sendMessage(new TextComponentString(msg));
        }
    }

    protected boolean isMovementBlocked() {
        return true;
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        motionX = motionY = motionZ = 0F;
        //cancel movement fully
    }

    protected boolean canDespawn() {
        return false;
    }

    public boolean isEntityAlive() {
        return false;
    }

    public boolean canBePushed() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(this.customRotation);
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.customRotation = additionalData.readFloat();
        setCustomRotationStuff();
    }

    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setFloat("customRotation", this.customRotation);
        tag.setFloat("shake", this.shake);
    }

    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.customRotation = tag.getFloat("customRotation");
        this.shake = tag.getFloat("shake");
    }
}