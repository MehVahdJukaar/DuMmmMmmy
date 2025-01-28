package net.mehvahdjukaar.dummmmmmy.network;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.CritRecord;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.dummmmmmy.configs.CritMode;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ClientBoundDamageNumberMessage implements Message {
    private final int entityID;
    private final float damageAmount;
    private final ResourceLocation damageType;
    private final boolean isCrit;
    private final float critMult;

    public ClientBoundDamageNumberMessage(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.damageAmount = buf.readFloat();
        this.damageType = buf.readResourceLocation();
        this.isCrit = buf.readBoolean();
        this.critMult = isCrit ? buf.readFloat() : 0;
    }

    public ClientBoundDamageNumberMessage(LivingEntity e, float damage, DamageSource source, @Nullable CritRecord critical) {
        this(e.getId(), damage, encodeDamage(e.level(), source), critical != null, critical == null ? 0 : critical.getMultiplier());
    }

    public static ResourceLocation encodeDamage(Level level, DamageSource source) {
        if (source == null) return Dummmmmmy.TRUE_DAMAGE;
        //if (critical) return Dummmmmmy.CRITICAL_DAMAGE;
        DamageType damageType = source.type();
        if (damageType == null) throw new AssertionError("Damage source has null type. How?: " + source);
        var id = source.typeHolder().unwrapKey();
        if (id.isEmpty())
            throw new AssertionError("Damage type not found in registry. This is a bug from that mod that added it!: " + damageType);
        return id.get().location();
    }

    protected ClientBoundDamageNumberMessage(int id, float damage, ResourceLocation damageType, boolean isCrit, float critMult) {
        this.entityID = id;
        this.damageAmount = damage;
        this.damageType = damageType;
        this.isCrit = isCrit;
        this.critMult = critMult;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeFloat(this.damageAmount);
        buf.writeResourceLocation(this.damageType);
        buf.writeBoolean(this.isCrit);
        if (isCrit) buf.writeFloat(this.critMult);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (entity instanceof TargetDummyEntity dummy) {
            if (ClientConfigs.DAMAGE_NUMBERS.get()) {
                int i = dummy.getNextNumberPos();
                spawnParticle(entity, i);
            }
            if (ClientConfigs.HAY_PARTICLES.get()) {
                spawnHay(entity);
            }
        } else if (entity != null) {
            spawnParticle(entity, 0);
        }
    }

    private void spawnParticle(Entity entity, int animationPos) {
        ResourceLocation type = damageType;
        float mult = 0;
        CritMode critMode = ClientConfigs.CRIT_MODE.get();
        if (critMode != CritMode.OFF && isCrit) {
            type = Dummmmmmy.CRITICAL_DAMAGE;
            if (critMode == CritMode.COLOR_AND_MULTIPLIER) {
                mult = critMult;
            }
        }
        double z = CritMode.encodeIntFloatToDouble(animationPos, mult);
        int color = ClientConfigs.getDamageColor(type);

        entity.level().addParticle(Dummmmmmy.NUMBER_PARTICLE.get(),
                entity.getX(), entity.getY() + 1, entity.getZ(), damageAmount, color, z);
    }


    private void spawnHay(Entity entity) {
        var random = entity.level().random;
        int amount = (int) (1 + Mth.map(this.damageAmount, 0, 40, 0, 10));
        amount = Mth.clamp(amount, 0, 10);
        for (int i = 0; i < amount; i++) {
            Vec3 pos = new Vec3(entity.getRandomX(0.5),
                    entity.getY() + 0.75 + random.nextFloat() * 0.85,
                    entity.getRandomZ(0.5));
            Vec3 speed = getOutwardSpeed(pos.subtract(entity.position()), random);
            entity.level().addParticle(Dummmmmmy.HAY_PARTICLE.get(), pos.x, pos.y, pos.z,
                    speed.x, random.nextFloat() * 0.04,
                    speed.z);
        }
    }

    public static Vec3 getOutwardSpeed(Vec3 position, RandomSource random) {

        // Normalize the vector to get the direction
        Vec3 direction = position.normalize();

        // Apply random rotation variation
        float randomLen = 0.02f + random.nextFloat() * 0.04f;
        float angleVariation = (float) (random.nextGaussian() * 0.3f); // variation up to ±22.5 degrees
        float sin = Mth.sin(angleVariation);
        float cos = Mth.cos(angleVariation);

        double newX = direction.x * cos - direction.z * sin;
        double newY = direction.x * sin + direction.z * cos;

        return new Vec3(newX * randomLen, 0, newY * randomLen);
    }
}

