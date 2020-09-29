
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
import net.mcreator.dummmmmmy.Config;

import java.util.Random;

import java.text.DecimalFormat;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.sun.org.apache.bcel.internal.generic.FADD;
import com.sun.org.apache.bcel.internal.generic.FADD;
import java.math.MathContext;

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
		protected static final int MAXAGE=40;
		public int age;
		public float number = 69420;
		protected float speed = 1;
		public float dy = 0;
		public float prevDy = 0;
		public int color = 0xffffffff;
		public float dx = 0;
		public float prevDx = 0;
		public float speedx = 0;
		public float fadeout=-1;
		private int type =-1;
		protected final Random rand = new Random();
		public  List<Float> list = new ArrayList<Float>(Arrays.asList(0f,-0.25f,0.12f,-0.12f,0.25f));
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
		public CustomEntity(float number, int color, int type, World world) {
			this(number,  color,  world);
			this.type=type;
		}


		@Override
		public AxisAlignedBB getBoundingBox() {
			return new AxisAlignedBB(new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ()));
		}

		@Override
		public void writeSpawnData(PacketBuffer buffer) {
			buffer.writeFloat(this.number);
			buffer.writeInt(this.color);
			buffer.writeInt(this.type);
		}

		@Override
		public void readSpawnData(PacketBuffer additionalData) {
			this.number = additionalData.readFloat();
			this.color = additionalData.readInt();
			int i = additionalData.readInt();
            if(i!=-1){
            	this.speedx=list.get(i%list.size());
            }
            else{
				//this.speedx = (this.rand.nextFloat() - 0.5f) / 2f;
				this.speedx = list.get(this.rand.nextInt(list.size()));
            }
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
			if (this.age++ > MAXAGE || this.getPosY() < -64.0D) {
				this.remove();
			}

			float lenght=6;
			this.fadeout = this.age>(MAXAGE-10)? (float)((float)10f-(this.age-30))/10f : 1;

			
			// this.forceSetPosition(this.getPosX(), this.getPosY() + (this.speed / 2),
			// this.getPosZ());
			this.prevDy = this.dy;
			this.dy += this.speed;
			this.prevDx = this.dx;
			this.dx += this.speedx;
			// this.speed / 500d;
			//spawn numbers in a sort of elliple centered on his torso
			if (Math.sqrt(Math.pow(this.dx*1.5,2) + Math.pow(this.dy-1,2)) < 1.9-1) { 
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
			
			
			//TODO:put this inan entity function
			float fadeout =entityIn.fadeout;
			
			float defscale = 0.006f;
			float scale = (float) (defscale * d);
			matrixStackIn.rotate(this.renderManager.getCameraOrientation());
			// matrixStackIn.translate(0, 0, -1);
			// animation
			matrixStackIn.translate(MathHelper.lerp(partialTicks, entityIn.prevDx, entityIn.dx),0, 0);
			// scale depending on distance so size remains the same
			matrixStackIn.scale(-scale, -scale, scale);
			matrixStackIn.translate(0, (4d*(1-fadeout)) , 0);
			matrixStackIn.scale(fadeout, fadeout, fadeout);
			matrixStackIn.translate(0,  -d / 10d, 0);

			float number = Config.Configs.SHOW_HEARTHS.get()? entityIn.getNumber()/2f : entityIn.getNumber();
			String s = df.format(number);
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
