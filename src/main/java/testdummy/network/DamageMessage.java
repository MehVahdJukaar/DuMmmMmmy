package testdummy.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import testdummy.TestDummy;
import testdummy.entity.EntityDummy;
import testdummy.entity.EntityFloatingNumber;

public class DamageMessage implements IMessage {
    public float damage;
    public float shakeAmount;
    public int entityID;
    public int nrID;

    public DamageMessage() {
    }

    public DamageMessage(float damage, float shakeAmount, EntityDummy entity, EntityFloatingNumber e2) {
        this.damage = damage;
        this.shakeAmount = shakeAmount;
        this.entityID = entity.getEntityId();
        this.nrID = (e2 != null) ? e2.getEntityId() : -1;
    }

    public void fromBytes(ByteBuf buf) {
        this.damage = buf.readFloat();
        this.shakeAmount = buf.readFloat();
        this.entityID = buf.readInt();
        this.nrID = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.damage);
        buf.writeFloat(this.shakeAmount);
        buf.writeInt(this.entityID);
        buf.writeInt(this.nrID);
    }

    public static class MessageHandlerClient implements IMessageHandler<DamageMessage, IMessage> {

        public DamageMessage onMessage(final DamageMessage message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
                public void run() {
                    Entity entity = (Minecraft.getMinecraft()).world.getEntityByID(message.entityID);
                    if (entity instanceof EntityDummy) {
                        EntityDummy dummy = (EntityDummy) entity;
                        dummy.shake = message.shakeAmount;
                        dummy.setCustomNameTag(String.valueOf(TestDummy.df.format(message.damage)));
                    }
                    if (message.nrID > 0) {
                        entity = (Minecraft.getMinecraft()).world.getEntityByID(message.nrID);
                        if (entity instanceof EntityFloatingNumber) {
                            ((EntityFloatingNumber) entity).reSet(message.damage);
                        }
                    }
                }
            });
            return null;
        }
    }
}