package testdummy.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import testdummy.entity.EntityDummy;

import javax.annotation.Nonnull;

public class SyncEquipmentMessage implements IMessage {
    private int entityID;
    private int slotId;
    private ItemStack itemstack;

    public SyncEquipmentMessage() {
    }

    public SyncEquipmentMessage(int entityId, int slotId, @Nonnull ItemStack itemstack) {
        this.entityID = entityId;
        this.slotId = slotId;
        this.itemstack = itemstack.copy();
    }

    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.slotId = buf.readInt();
        this.itemstack = ByteBufUtils.readItemStack(buf);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeInt(this.slotId);
        ByteBufUtils.writeItemStack(buf, this.itemstack);
    }

    public static class MessageHandlerClient
            implements IMessageHandler<SyncEquipmentMessage, IMessage> {
        public SyncEquipmentMessage onMessage(final SyncEquipmentMessage message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                Entity entity = (Minecraft.getMinecraft()).world.getEntityByID(message.entityID);
                if (entity instanceof EntityDummy) {
                    EntityEquipmentSlot slot = EntityEquipmentSlot.values()[message.slotId];
                    entity.setItemStackToSlot(slot, message.itemstack);
                }
            });
            return null;
        }
    }
}