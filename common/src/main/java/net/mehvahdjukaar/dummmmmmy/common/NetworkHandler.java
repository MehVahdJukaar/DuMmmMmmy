
package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static int nextID() {
        return ID++;
    }


    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DummmmmmyMod.MOD_ID, "dummychannel"), () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

        INSTANCE.registerMessage(nextID(), PacketDamageNumber.class, PacketDamageNumber::toBytes, PacketDamageNumber::new,
                PacketDamageNumber::handle);

        INSTANCE.registerMessage(nextID(), PacketSyncEquip.class, PacketSyncEquip::toBytes, PacketSyncEquip::new,
                PacketSyncEquip::handle);

    }


    private interface Message {
    }

    public static void sendToAllTracking(Entity entity, ServerLevel world, Message message) {
        world.getChunkSource().broadcast(entity, INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT));
    }

    public static class PacketDamageNumber implements Message {
        private final int entityID;
        //private final float damage;
        private final float shake;

        public PacketDamageNumber(FriendlyByteBuf buf) {
            this.entityID = buf.readInt();
            //this.damage = buf.readFloat();
            this.shake = buf.readFloat();
        }

        public PacketDamageNumber(int id, float shakeAmount) {
            this.entityID = id;
            //this.damage = damage;
            this.shake = shakeAmount;
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeInt(this.entityID);
            //buf.writeFloat(this.damage);
            buf.writeFloat(this.shake);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
                if (entity instanceof TargetDummyEntity dummy) {
                    dummy.animationPosition = shake;
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }


    public static class PacketSyncEquip implements Message {
        private final int entityID;
        private final int slotId;
        private final ItemStack itemstack;

        public PacketSyncEquip(FriendlyByteBuf buf) {
            this.entityID = buf.readInt();
            this.slotId = buf.readInt();

            this.itemstack = buf.readItem();
        }

        public PacketSyncEquip(int entityId, int slotId, @Nonnull ItemStack itemstack) {
            this.entityID = entityId;
            this.slotId = slotId;
            this.itemstack = itemstack.copy();
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeInt(this.entityID);
            buf.writeInt(slotId);
            buf.writeItem(itemstack);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
                if (entity instanceof TargetDummyEntity dummy) {
                    dummy.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, this.slotId), this.itemstack);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
