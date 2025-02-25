package net.mehvahdjukaar.dummmmmmy.network;

import com.google.common.base.Preconditions;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.CritRecord;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.dummmmmmy.configs.CritMode;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public record ClientBoundDamageNumberMessage
        (int entityID, float damageAmount, Holder<DamageType> damageType, boolean isCrit, float critMult)
        implements Message {

    public static final CustomPacketPayload.TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundDamageNumberMessage> TYPE =
            Message.makeType(Dummmmmmy.res("s2c_damage_number"), ClientBoundDamageNumberMessage::of);


    public static ClientBoundDamageNumberMessage of(RegistryFriendlyByteBuf buf) {
        var entityID = buf.readInt();
        var damageAmount = buf.readFloat();
        var damageType = DamageType.STREAM_CODEC.decode(buf);
        var isCrit = buf.readBoolean();
        var critMult = isCrit ? buf.readFloat() : 0;
        return new ClientBoundDamageNumberMessage(entityID, damageAmount, damageType, isCrit, critMult);
    }

    public ClientBoundDamageNumberMessage(int id, float damage, @Nullable DamageSource source, @Nullable CritRecord critical, Level level) {
        this(id, damage, encodeDamage(source, level), critical != null, critical == null ? 0 : critical.getMultiplier());
    }

    public static Holder<DamageType> encodeDamage(@Nullable DamageSource source, Level level) {
        if (source == null) return Dummmmmmy.TRUE_DAMAGE.getHolder(level);
        //if (critical) return Dummmmmmy.CRITICAL_DAMAGE;
        var damageType = source.typeHolder();
        return Preconditions.checkNotNull(damageType);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeFloat(this.damageAmount);
        DamageType.STREAM_CODEC.encode(buf, damageType);
        buf.writeBoolean(this.isCrit);
        if (isCrit) buf.writeFloat(this.critMult);
    }

    @Override
    public void handle(Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (entity instanceof TargetDummyEntity dummy) {
            if (ClientConfigs.DAMAGE_NUMBERS.get()) {
                int i = dummy.getNextNumberPos();
                spawnNumber(entity, i);
            }
            if (ClientConfigs.HAY_PARTICLES.get()) {
                spawnHay(entity);
            }
        } else if (entity != null) {
            spawnNumber(entity, 0);
        }
    }

    private void spawnHay(Entity entity) {
        var random = entity.getRandom();
        int amount = (int) (1 + Mth.map(this.damageAmount, 0, 40, 0, 10));
        amount  = Math.min(amount, 10);
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
        float sin =  Mth.sin(angleVariation);
        float cos =  Mth.cos(angleVariation);

        double newX = direction.x * cos - direction.z * sin;
        double newY = direction.x * sin + direction.z * cos;

        return new Vec3(newX*randomLen,0, newY*randomLen);
    }

    private void spawnNumber(Entity entity, int animationPos) {
        var type = damageType;
        float mult = 0;
        CritMode critMode = ClientConfigs.CRIT_MODE.get();
        if (critMode != CritMode.OFF && isCrit) {
            type = Dummmmmmy.CRITICAL_DAMAGE.getHolder(entity);
            if (critMode == CritMode.COLOR_AND_MULTIPLIER) {
                mult = critMult;
            }
        }
        double z = CritMode.encodeIntFloatToDouble(animationPos, mult);
        int color = ClientConfigs.getDamageColor(type);

        entity.level().addParticle(Dummmmmmy.NUMBER_PARTICLE.get(),
                entity.getX(), entity.getY() + 1, entity.getZ(), damageAmount, color, z);
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }
}

