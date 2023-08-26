package net.mehvahdjukaar.dummmmmmy.network;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.configs.ClientConfigs;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class ClientBoundDamageNumberMessage implements Message {
    private final int entityID;
    private final float damage;
    private final ResourceLocation damageType;

    public ClientBoundDamageNumberMessage(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.damage = buf.readFloat();
        this.damageType = buf.readResourceLocation();
    }

    public ClientBoundDamageNumberMessage(int id, float damage, DamageSource source, boolean critical) {
        this(id, damage, encodeDamage(source, critical));
    }

    public static ResourceLocation encodeDamage(DamageSource source,  boolean critical) {
        if (source == null) return Dummmmmmy.TRUE_DAMAGE;
        if (critical) return Dummmmmmy.CRITICAL_DAMAGE;
        return Utils.hackyGetRegistry(Registries.DAMAGE_TYPE).getKey(source.type());
    }

    protected ClientBoundDamageNumberMessage(int id, float damage, ResourceLocation damageType) {
        this.entityID = id;
        this.damage = damage;
        this.damageType = damageType;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeFloat(this.damage);
        buf.writeResourceLocation(this.damageType);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (entity instanceof TargetDummyEntity dummy) {
            dummy.spawnDamageParticle(damage, damageType);
        } else {
            //when is this even used?
            int color = ClientConfigs.getDamageColor(damageType);
            entity.level().addParticle(Dummmmmmy.NUMBER_PARTICLE.get(),
                    entity.getX(), entity.getY() + 1, entity.getZ(), damage, color, 0);
        }
    }
}

