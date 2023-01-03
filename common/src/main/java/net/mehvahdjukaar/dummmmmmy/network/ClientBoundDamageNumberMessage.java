package net.mehvahdjukaar.dummmmmmy.network;

import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ClientBoundDamageNumberMessage implements Message {
    private final int entityID;
    private final float damage;
    private final int damageType;

    public ClientBoundDamageNumberMessage(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.damage = buf.readFloat();
        this.damageType = buf.readInt();
    }

    public ClientBoundDamageNumberMessage(int id, float damage, int damageType) {
        this.entityID = id;
        this.damage = damage;
        this.damageType = damageType;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeFloat(this.damage);
        buf.writeInt(this.damageType);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (entity instanceof TargetDummyEntity dummy) {
            dummy.updateClientDamage(damage, damageType);
        }
    }
}

