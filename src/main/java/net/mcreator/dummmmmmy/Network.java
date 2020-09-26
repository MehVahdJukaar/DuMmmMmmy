/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class DummmmmmyModElements.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser - New... and make sure to make the class
 * outside net.mcreator.dummmmmmy as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/
package net.mcreator.dummmmmmy;

import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;

import net.mcreator.dummmmmmy.entity.TargetDummyEntity;

import java.util.function.Supplier;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;
import net.minecraft.inventory.EquipmentSlotType;

@DummmmmmyModElements.ModElement.Tag
public class Network extends DummmmmmyModElements.ModElement {
	/**
	 * Do not remove this constructor
	 */
	public Network(DummmmmmyModElements instance) {
		super(instance, 2);
	}

	@Override
	public void initElements() {
			//Networking.registerMessages();
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		Networking.registerMessages();
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {
			//Networking.registerMessages();
	}
	public static class myMessage {
	};

	public static class PacketDamageNumber extends myMessage {
		private int entityID;
		private float damage;
		private float shake;
		public PacketDamageNumber(PacketBuffer buf) {
			this.entityID = buf.readInt();
			this.damage = buf.readFloat();
			this.shake = buf.readFloat();
		}

		public PacketDamageNumber(int id, float damage, float shakeAmount) {
			this.entityID = id;
			this.damage = damage;
			this.shake = shakeAmount;
		}

		public void toBytes(PacketBuffer buf) {
			buf.writeInt(this.entityID);
			buf.writeFloat(this.damage);
			buf.writeFloat(this.shake);
		}

		public void handle(Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				Entity entity = Minecraft.getInstance().world.getEntityByID(this.entityID);
				if (entity != null && entity instanceof TargetDummyEntity.CustomEntity) {
					TargetDummyEntity.CustomEntity dummy = (TargetDummyEntity.CustomEntity) entity;
					dummy.setShake(this.shake);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}



	public static class PacketSyncEquip extends myMessage {
		private int entityID;
		private int slotId;
		private ItemStack itemstack;
		public PacketSyncEquip(PacketBuffer buf) {
			this.entityID = buf.readInt();
		    this.slotId = buf.readInt();

		    this.itemstack = buf.readItemStack();
		}

		public PacketSyncEquip(int entityId, int slotId, @Nonnull ItemStack itemstack) {
			this.entityID = entityId;
		    this.slotId = slotId;
		    this.itemstack = itemstack.copy();
		}

		public void toBytes(PacketBuffer buf) {
			buf.writeInt(this.entityID);
		    buf.writeInt(slotId);  
		    buf.writeItemStack(itemstack);
		}

		public void handle(Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				Entity entity = Minecraft.getInstance().world.getEntityByID(this.entityID);
				if (entity != null && entity instanceof TargetDummyEntity.CustomEntity) {
					TargetDummyEntity.CustomEntity dummy = (TargetDummyEntity.CustomEntity) entity;
					dummy.setItemStackToSlot(EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.ARMOR, this.slotId), this.itemstack);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}







	public static class Networking {
		public static SimpleChannel INSTANCE;
		private static int ID = 0;
		public static int nextID() {
			return ID++;
		}

		public static void registerMessages() {
			INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("lightupmyenvironment:mychannel"), () -> "1.0", s -> true, s -> true);
			
			INSTANCE.registerMessage(nextID(), PacketDamageNumber.class, PacketDamageNumber::toBytes, PacketDamageNumber::new,
					PacketDamageNumber::handle);


			INSTANCE.registerMessage(nextID(), PacketSyncEquip.class, PacketSyncEquip::toBytes, PacketSyncEquip::new,
					PacketSyncEquip::handle);

		}
	}

	
	// I'm so bad with this, I know. should work fine though.. I hope
	public static void sendToAllNear(double x, double y, double z, double radius, DimensionType dimension, myMessage message) {
		MinecraftServer mcserv = ServerLifecycleHooks.getCurrentServer();
		if (mcserv != null && dimension != null) {
			PlayerList players = mcserv.getPlayerList();
			players.sendToAllNearExcept((PlayerEntity) null, x, y, z, radius, dimension,
					Networking.INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT));
		}
	}
}
