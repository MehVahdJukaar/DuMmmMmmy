
package net.mcreator.dummmmmmy.entity;

import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.IPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.block.BlockState;

import net.mcreator.dummmmmmy.DummmmmmyModElements;

import java.util.Random;

import java.text.DecimalFormat;

import com.mojang.blaze3d.matrix.MatrixStack;

@DummmmmmyModElements.ModElement.Tag
public class DummyNumberEntity extends DummmmmmyModElements.ModElement {
	public static EntityType entity = null;
	public DummyNumberEntity(DummmmmmyModElements instance) {
		super(instance, 5);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@Override
	public void initElements() {
		entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, EntityClassification.MONSTER).setShouldReceiveVelocityUpdates(true)
				.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new).size(0.6f, 1.8f)).build("dummy_number")
						.setRegistryName("dummy_number");
		elements.entities.add(() -> entity);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void init(FMLCommonSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> new CustomRender(renderManager));
	}
	public static class CustomEntity extends Entity implements IEntityAdditionalSpawnData {
		protected int age;
		public float number = 69420;
		protected float speed = 1;
		public float dy = 0;
		public float prevDy = 0;
		public int color = 0xffffffff;
		public float dx = 0;
		public float prevDx = 0;
		public float speedx = 0;
		protected final Random rand = new Random();
		public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
			this(entity, world);
		}

		public CustomEntity(EntityType<CustomEntity> type, World world) {
			super(type, world);
		}

		public CustomEntity(float number, int color, World world) {
			super(entity, world);
			this.number = number;
			this.color = color;
		}

		@Override
		public AxisAlignedBB getBoundingBox() {
			return new AxisAlignedBB(new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ()));
		}

		@Override
		public void writeSpawnData(PacketBuffer buffer) {
			buffer.writeFloat(this.number);
			buffer.writeInt(this.color);
		}

		@Override
		public void readSpawnData(PacketBuffer additionalData) {
			this.number = additionalData.readFloat();
			this.color = additionalData.readInt();
			this.speedx = (this.rand.nextFloat() - 0.5f) / 2f;
		}

		public void readAdditional(CompoundNBT compound) {
			// super.readAdditional(compound);
			this.number = compound.getFloat("number");
			this.color = compound.getInt("color");
			this.age = compound.getInt("age");
		}

		public void writeAdditional(CompoundNBT compound) {
			// super.writeAdditional(compound);
			compound.putFloat("number", this.number);
			compound.putInt("color", this.color);
			compound.putInt("age", this.age);
		}

		protected void registerData() {
			// this.getDataManager().register(ITEM, ItemStack.EMPTY);
		}

		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		public BlockPos pos() {
			return new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ());
		}

		@Override
		public void tick() {
			if (this.age++ > 40 || this.getPosY() < -64.0D) {
				this.remove();
			}
			// this.forceSetPosition(this.getPosX(), this.getPosY() + (this.speed / 2),
			// this.getPosZ());
			this.prevDy = this.dy;
			this.dy += this.speed;
			this.prevDx = this.dx;
			this.dx += this.speedx;
			// this.speed / 500d;
			if (MathHelper.abs(this.dx) + this.dy < 1.9) {
				speed = speed / 2;
			} else {
				speed = 0;
				speedx = 0;
			}
		}

		public void reSet(float number) {
			this.number = number;
			this.age = 0;
		}

		public float getNumber() {
			return this.number;
		}

		@Override
		public boolean onLivingFall(float l, float d) {
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@Override
		public boolean isPushedByWater() {
			return false;
		}

		@Override
		public boolean canBeCollidedWith() {
			return false;
		}

		@Override
		protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		}

		@Override
		public void setNoGravity(boolean ignored) {
			super.setNoGravity(true);
		}
	}

	public static class CustomRender extends EntityRenderer<CustomEntity> {
		private static DecimalFormat df = new DecimalFormat("#.##");
		public CustomRender(EntityRendererManager renderManager) {
			super(renderManager);
		}

		@Override
		public void render(CustomEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
				int packedLightIn) {
			FontRenderer fontrenderer = this.renderManager.getFontRenderer();
			matrixStackIn.push();
			// translate towards player
			PlayerEntity player = Minecraft.getInstance().player;
			Vec3d v = (player.getPositionVec().subtract(entityIn.getPositionVec())).normalize();
			matrixStackIn.translate(v.getX(), v.getY(), v.getZ());
			// animation
			matrixStackIn.translate(0, MathHelper.lerp(partialTicks, entityIn.prevDy, entityIn.dy), 0);
			// rotate towards camera
			double d = Math.sqrt(this.renderManager.getDistanceToCamera(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ()));
			float scale = (float) (0.006 * d);
			matrixStackIn.rotate(this.renderManager.getCameraOrientation());
			// matrixStackIn.translate(0, 0, -1);
			// animation
			matrixStackIn.translate(MathHelper.lerp(partialTicks, entityIn.prevDx, entityIn.dx), 0, 0);
			// scale depending on distance so size remains the same
			matrixStackIn.scale(-scale, -scale, scale);
			matrixStackIn.translate(0, -d / 10d, 0);
			String s = df.format(entityIn.getNumber());
			// center string
			matrixStackIn.translate((-fontrenderer.getStringWidth(s) / 2f) + 0.5f, 0, 0);
			fontrenderer.renderString(s, 0, 0, entityIn.color, true, matrixStackIn.getLast().getMatrix(), bufferIn, false, 0, packedLightIn);
			// matrixStackIn.translate(fontrenderer.getStringWidth(s) / 2, 0, 0);
			matrixStackIn.pop();
		}

		@Override
		public ResourceLocation getEntityTexture(CustomEntity entity) {
			return null;
		}
	}
}
