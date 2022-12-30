
package net.mehvahdjukaar.dummmmmmy.entity;

import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.mehvahdjukaar.moonlight.api.entity.IExtraClientSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.*;


public class DummyNumberEntity extends Entity implements IExtraClientSpawnData {
    protected static final int MAX_AGE = 35;
    public static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));

    private final Collection<UUID> targetPlayers = new HashSet<>();
    public int age;
    private float number = 69420;
    protected float speed = 1;
    public float dy = 0;
    public float prevDy = 0;
    public TargetDummyEntity.DamageType color = TargetDummyEntity.DamageType.GENERIC;
    public float dx = 0;
    public float prevDx = 0;
    private float speedX = 0;
    public float fadeout = -1;
    public float prevFadeout = -1;
    private int type = -1; //used for location in array

    public DummyNumberEntity(EntityType<DummyNumberEntity> type, Level world) {
        super(type, world);
    }

    public DummyNumberEntity(float number, TargetDummyEntity.DamageType color, int type, Level world, Collection<UUID> playersThatCanSee) {
        this(ModRegistry.DUMMY_NUMBER.get(), world);
        this.number = number;
        this.color = color;
        this.type = type;
        this.targetPlayers.clear();
        this.targetPlayers.addAll(playersThatCanSee);
    }

    public boolean canPlayerSee(@Nullable Player p) {
        return p == null || this.targetPlayers.contains(p.getUUID());
    }

    //have to give him some attributes or server will throw errors
    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.number);
        buffer.writeEnum(this.color);
        buffer.writeInt(this.type);
        buffer.writeInt(this.targetPlayers.size());
        this.targetPlayers.forEach(buffer::writeUUID);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.number = buffer.readFloat();
        this.color = buffer.readEnum(TargetDummyEntity.DamageType.class);
        int i = buffer.readInt();
        if (i != -1) {
            this.speedX = POSITIONS.get(i % POSITIONS.size());
        } else {
            //this.speedx = (this.rand.nextFloat() - 0.5f) / 2f;
            this.speedX = POSITIONS.get(this.random.nextInt(POSITIONS.size()));
        }
        int size = buffer.readInt();
        this.targetPlayers.clear();
        for (int j = 0; j < size; j++) {
            this.targetPlayers.add(buffer.readUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.number = compound.getFloat("Number");
        this.color = TargetDummyEntity.DamageType.values()[compound.getInt("Type")];
        this.age = compound.getInt("Age");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Number", this.number);
        compound.putInt("Type", this.color.ordinal());
        compound.putInt("Age", this.age);
    }

    protected void defineSynchedData() {
        // this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        if (this.age++ > MAX_AGE || this.getY() < -64.0D) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.level.isClientSide) {
            float length = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (MAX_AGE - length) ? ((float) MAX_AGE - this.age) / length : 1;


            // this.forceSetPosition(this.getPosX(), this.getPosY() + (this.speed / 2),
            // this.getPosZ());
            this.prevDy = this.dy;
            this.dy += this.speed;
            this.prevDx = this.dx;
            this.dx += this.speedX;
            // this.speed / 500d;
            //spawn numbers in a sort of ellipse centered on his torso
            if (Math.sqrt(Math.pow(this.dx * 1.5, 2) + Math.pow(this.dy - 1, 2)) < 1.9 - 1) {

                speed = speed / 2;
            } else {
                speed = 0;
                speedX = 0;
            }
        }
    }

    public float getNumber() {
        return this.number;
    }

    @Override
    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

}
