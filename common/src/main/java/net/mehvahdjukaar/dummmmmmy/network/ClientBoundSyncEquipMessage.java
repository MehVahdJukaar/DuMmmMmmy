package net.mehvahdjukaar.dummmmmmy.network;

import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ClientBoundSyncEquipMessage implements Message {
    private final int entityID;
    private final int slotId;
    private final ItemStack itemstack;

    public ClientBoundSyncEquipMessage(FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.slotId = buf.readInt();
        this.itemstack = buf.readItem();
    }

    public ClientBoundSyncEquipMessage(int entityId, int slotId, @Nonnull ItemStack itemstack) {
        this.entityID = entityId;
        this.slotId = slotId;
        this.itemstack = itemstack.copy();
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeInt(slotId);
        buf.writeItem(itemstack);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
        if (entity instanceof TargetDummyEntity dummy) {
            dummy.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, this.slotId), this.itemstack);
        }
    }
}
