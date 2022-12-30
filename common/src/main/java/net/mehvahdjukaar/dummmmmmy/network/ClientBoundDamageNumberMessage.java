package net.mehvahdjukaar.dummmmmmy.network;

import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class ClientBoundDamageNumberMessage implements Message {
    private final int entityID;
    //private final float damage;
    private final float shake;

    public ClientBoundDamageNumberMessage(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        //this.damage = buf.readFloat();
        this.shake = buf.readFloat();
    }

    public ClientBoundDamageNumberMessage(int id, float shakeAmount) {
        this.entityID = id;
        //this.damage = damage;
        this.shake = shakeAmount;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        //buf.writeFloat(this.damage);
        buf.writeFloat(this.shake);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (entity instanceof TargetDummyEntity dummy) {
            dummy.animationPosition = shake;
        }
    }
}

