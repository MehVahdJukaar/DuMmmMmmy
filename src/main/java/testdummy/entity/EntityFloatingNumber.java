package testdummy.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFloatingNumber extends Entity implements IEntityAdditionalSpawnData {
    public float damage;
    protected int age;
    protected int speed;

    public EntityFloatingNumber(World world) {
        super(world);
    }

    public EntityFloatingNumber(World world, float damage, double x, double y, double z) {
        super(world);
        this.damage = damage;
        this.lastTickPosX = this.posX = x;
        this.lastTickPosY = this.posY = y;
        this.lastTickPosZ = this.posZ = z;
    }

    protected void entityInit() {
        this.age = 0;
        this.speed = 500;
    }

    public void onEntityUpdate() {
        if (this.age++ > 50) {
            setDead();
        }
        this.posY += this.speed / 500.0D;
        if (this.speed > 1) {
            this.speed /= 2;
        } else if (this.speed == 1) {
            this.speed = 0;
        }
    }

    public void move(MoverType type, double x, double y, double z) {
    }

    public void reSet(float damage) {
        this.damage = damage;
        this.age = 0;
    }

    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(this.damage);
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.damage = additionalData.readFloat();
    }
}