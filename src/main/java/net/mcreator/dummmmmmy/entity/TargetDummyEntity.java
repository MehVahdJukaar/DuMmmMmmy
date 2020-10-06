
package net.mcreator.dummmmmmy.entity;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Hand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.IPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;

import net.mcreator.dummmmmmy.entity.DummyNumberEntity;

import net.mcreator.dummmmmmy.Config;
import net.mcreator.dummmmmmy.item.TargetDummyPlacerItem;
import net.mcreator.dummmmmmy.Network;
import net.mcreator.dummmmmmy.DummmmmmyModElements;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.text.DecimalFormat;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraft.item.Item;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.item.Items;
import net.minecraft.item.BannerItem;
import net.minecraft.world.raid.Raid;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.item.ShearsItem;
import net.minecraft.network.play.server.SEntityEquipmentPacket;



@DummmmmmyModElements.ModElement.Tag
public class TargetDummyEntity extends DummmmmmyModElements.ModElement {
	public static EntityType entity = null;
	public TargetDummyEntity(DummmmmmyModElements instance) {
		super(instance, 119);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@Override
	public void initElements() {
		entity = (EntityType.Builder.<CustomEntity>create(CustomEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
				.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CustomEntity::new).size(0.6f, 2f)).build("target_dummy")
						.setRegistryName("target_dummy");
		elements.entities.add(() -> entity);
	}



	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(entity, renderManager -> {
			BipedRenderer customRender = new BipedRenderer(renderManager, new ModelDummy(), 0f) {
				// shadow size
				@Override
				public ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation(Config.Configs.getSkin(entity));
				}
			};
			customRender.addLayer(new LayerDummyArmor(customRender, new BipedModel(0.5f), new BipedModel(1)));
			return customRender;
		});
	}


	
	public static class CustomEntity extends MobEntity implements IEntityAdditionalSpawnData {


		public float shake;
		public float shakeAnimation; // used to have an independent start for the animation, otherwhise the phase of
										// the animation depends ont he damage dealt
										// used to calculate the whole damage in one tick, in case there are multiple
										// sources
		public float lastDamage;
		public int lastDamageTick;
		public int firstDamageTick; // indicates when we started taking damage and if != 0 it also means that we are
									// currently recording damage taken
		public float damageTaken;
		public boolean critical = false; //has just been hit by critical?
		public int mobType =0; //0=undefined, 1=undead, 2=water, 3= illager
		private List<ServerPlayerEntity> currentlyAttacking = new ArrayList<>();//players to which display dps message
		private int mynumberpos =0;
		public boolean sheared;
		//public DummyNumberEntity.CustomEntity myLittleNumber;
		private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);

		public CustomEntity(FMLPlayMessages.SpawnEntity packet, World world) {
			this(entity, world);
		}

		public CustomEntity(EntityType<CustomEntity> type, World world) {
			super(type, world);
			experienceValue = 0;
			setNoAI(true);
			Arrays.fill(this.inventoryArmorDropChances, 1.1f);
            this.sheared=false;
	
		}

		public CustomEntity(World world) {
			this(entity, world);
		}

		public void setShake(float s) {
			this.shake = s;

		}
	
		public void hitByCritical(){
			this.critical=true;	
		}

		public void updateOnLoadClient() {
			float r = this.rotationYaw;
			this.prevRotationYawHead = this.rotationYawHead = r;
			this.prevRotationYaw = r;
			this.prevRenderYawOffset = this.renderYawOffset = r;
					
		}
	    public void updateOnLoadServer(){
	    	this.applyEquipmentModifiers();	
	    }

		// dress it up! :D

		@Override
		public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
			boolean invchanged = false;
			if (player.isSpectator()) {
				return ActionResultType.PASS;
			} else {
				ItemStack itemstack = player.getHeldItem(hand);
				EquipmentSlotType equipmentslottype = this.getSlotForItemStack(itemstack);
				// empty hand -> unequip
				if (itemstack.isEmpty() && hand == Hand.MAIN_HAND) {
					equipmentslottype = this.getClickedSlot(vec);
					if (this.hasItemInSlot(equipmentslottype)) {
						this.unequipArmor(player, equipmentslottype, itemstack, hand);
						invchanged=true;

					}
				}
				//equip banner
				else if(itemstack.getItem() instanceof BannerItem){	
					equipmentslottype = EquipmentSlotType.HEAD;		
					this.equipArmor(player, equipmentslottype, itemstack, hand);
					invchanged=true;


				}
				// armor item in hand -> equip/swap
				else if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.ARMOR) {
					this.equipArmor(player, equipmentslottype, itemstack, hand);
					invchanged=true;
					
				}
				//remove sack
				else if (itemstack.getItem() instanceof ShearsItem){
					if(!this.sheared){
						this.sheared=true;
						if(!this.world.isRemote){
							Network.sendToAllTracking(this.world, this,
								new Network.PacketChangeSkin(this.getEntityId(), true));
						}
						return ActionResultType.SUCCESS;
					
						
					}
				}


				if(invchanged){
					Network.sendToAllTracking(this.world,this, new Network.PacketSyncEquip(this.getEntityId(), equipmentslottype.getIndex(), itemstack));
					this.applyEquipmentModifiers();
					return ActionResultType.SUCCESS;
				}

				
				return ActionResultType.PASS;
			}
		}

		private void unequipArmor(PlayerEntity player, EquipmentSlotType slot, ItemStack stack, Hand hand) {
			// set slot to stack which is empty stack
			ItemStack itemstack = this.getItemStackFromSlot(slot);
			ItemStack itemstack2 = itemstack.copy();
			player.setHeldItem(hand, itemstack2);
			this.setItemStackToSlot(slot, stack);

			this.applyEquipmentModifiers();
			//now done here^
			//this.getAttributes().removeAttributeModifiers(itemstack2.getAttributeModifiers(slot));
			//clear mob type
			if(slot==EquipmentSlotType.HEAD)this.mobType=0;
			
		}

		private void equipArmor(PlayerEntity player, EquipmentSlotType slot, ItemStack stack, Hand hand) {
			ItemStack itemstack = this.getItemStackFromSlot(slot);
			ItemStack itemstack2 = stack.copy();
			itemstack2.setCount(1);
			if (!player.isCreative()) {
				stack.shrink(1);
				if (!itemstack.isEmpty()) {
					// give item to player hand, inventory or drop it
					if (stack.isEmpty()) {
						player.setHeldItem(hand, itemstack);
					} else if (!player.inventory.addItemStackToInventory(itemstack)) {
						if (!getEntityWorld().isRemote) {
							this.entityDropItem(itemstack, 1.0f);
						}
					}
				}
			}
			this.playEquipSound(itemstack2);
			this.setItemStackToSlot(slot, itemstack2);
			
			this.applyEquipmentModifiers();
			//now done here^
			//this.getAttributes().applyAttributeModifiers(itemstack2.getAttributeModifiers(slot));
			//add mob type
			if(this.isUndeadSkull(itemstack2)){ 
				this.mobType=1;
			}
			else if(this.isTurtleHelmet(itemstack2)){
				this.mobType=2;
			}
			else if(ItemStack.areItemStacksEqual(itemstack2, Raid.createIllagerBanner())){
				this.mobType=3;
			}
			else this.mobType=0;
		}
  

		private boolean isTurtleHelmet(ItemStack itemstack){
			return (itemstack.getItem() == new ItemStack(Items.TURTLE_HELMET, (int) 1).getItem());
		}

		private boolean isUndeadSkull(ItemStack itemstack){
			Item i =itemstack.getItem();
			if(i == new ItemStack(Blocks.ZOMBIE_HEAD, (int) (1)).getItem() ||
				i == new ItemStack(Blocks.SKELETON_SKULL, (int) (1)).getItem() ||
				i== new ItemStack(Blocks.WITHER_SKELETON_SKULL, (int) (1)).getItem() ){
				return true;
			}	
			return false;
		}

		private EquipmentSlotType getClickedSlot(Vec3d p_190772_1_) {
			EquipmentSlotType equipmentslottype = EquipmentSlotType.MAINHAND;
			boolean flag = false;
			double d0 = flag ? p_190772_1_.y * 2.0D : p_190772_1_.y;
			EquipmentSlotType equipmentslottype1 = EquipmentSlotType.FEET;
			if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(equipmentslottype1)) {
				equipmentslottype = EquipmentSlotType.FEET;
			} else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EquipmentSlotType.CHEST)) {
				equipmentslottype = EquipmentSlotType.CHEST;
			} else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EquipmentSlotType.LEGS)) {
				equipmentslottype = EquipmentSlotType.LEGS;
			} else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlotType.HEAD)) {
				equipmentslottype = EquipmentSlotType.HEAD;
			}
			return equipmentslottype;
		}

		private void playBrokenSound() {
			this.world.playSound((PlayerEntity) null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK,
					this.getSoundCategory(), 1.0F, 1.0F);
		}

		private void playParticles() {
			if (this.world instanceof ServerWorld) {
				((ServerWorld) getEntityWorld()).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()),
						this.getPosX(), this.getPosYHeight(0.6666666666666666D), this.getPosZ(), 10, (double) (this.getWidth() / 4.0F),
						(double) (this.getHeight() / 4.0F), (double) (this.getWidth() / 4.0F), 0.05D);
			}
		}

		public void dropInventory() {
			for (EquipmentSlotType slot : EquipmentSlotType.values()) {
				if (slot.getSlotType() != EquipmentSlotType.Group.ARMOR) {
					continue;
				}
				ItemStack armor = getItemStackFromSlot(slot);
				if (!armor.isEmpty()) {
					this.entityDropItem(armor, 1.0f);
				}
			}
		}

		public void dismantle(boolean drops) {
			if (!getEntityWorld().isRemote) {
				if (drops) {
					dropInventory();
					this.entityDropItem(TargetDummyPlacerItem.block, 1);
				}
				this.playBrokenSound();
				this.playParticles();
			}
			this.remove();
			this.removed=true;
		}
			
		
		@Override
	   public void onKillCommand() {
	      this.dismantle(true);
	   }
	   
	   @Override
	   public boolean canBreatheUnderwater() {
	      return true;
	   }

		public int getColorFromDamageSource(DamageSource source){

			if(this.critical) return 0xff0000;
			if (source == DamageSource.DRAGON_BREATH) return 0xFF00FF;
			if (source ==DamageSource.WITHER)return  0x666666;
			if(source.damageType.equals("explosion")||source.damageType.equals("explosion.player"))return 0xFFCC33;
			if(source.damageType.equals("indirectMagic"))return 0x990033;
			if(source.damageType.equals("trident"))return 0x00FFCC;
			if(source==DamageSource.GENERIC)return 0xffffff;
			
			if(source == DamageSource.MAGIC){
				//would really like to detect poisn damage but i don't think there's simple way

				return 0x3399FF;
			}
			if (source ==DamageSource.HOT_FLOOR||source ==DamageSource.LAVA||source ==DamageSource.ON_FIRE||source ==DamageSource.IN_FIRE) return 0xFF9900;
			if (source == DamageSource.LIGHTNING_BOLT) return 0xFFFF00;


			if (source ==DamageSource.CACTUS || source ==DamageSource.SWEET_BERRY_BUSH)return  0x006600;
			
			return 0xffffff;
		}


	   
		@Override
		public boolean attackEntityFrom(DamageSource source, float damage) {

      		if (this.isInvulnerableTo(source)) return false;


			//super.attackEntityFrom(source, damage);

			//not immune to void damage, immune to drown, wall
            if(source == DamageSource.OUT_OF_WORLD){ this.remove();return true;};
            if(source == DamageSource.DROWN || source == DamageSource.IN_WALL) return false;
            //workaround for wither boss, otherwise it would keep targeting the dummy forever
            if(source.getImmediateSource() instanceof WitherEntity|| source.getTrueSource() instanceof WitherEntity){
            	this.dismantle(true);
            	return true;
            
            }

            //lots of living entity code here
            if (source.isFireDamage() && this.isPotionActive(Effects.FIRE_RESISTANCE)) return false;

	       if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
            this.getItemStackFromSlot(EquipmentSlotType.HEAD).damageItem((int)(damage * 4.0F + this.rand.nextFloat() * damage * 2.0F), this, (p_213341_0_) -> {
               p_213341_0_.sendBreakAnimation(EquipmentSlotType.HEAD);
            });
            damage *= 0.75F;
         	}
	     
            

			// dismantling + adding players to dps message list
			if (source.damageType.equals("player") || source.getTrueSource() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) source.getTrueSource();
				if (!world.isRemote) {
					ServerPlayerEntity sp = (ServerPlayerEntity) player;
					if (!this.currentlyAttacking.contains(sp)) {
						this.currentlyAttacking.add(sp);
					}

				}
				// shift-leftclick with empty hand dismantles
				if (player.isSneaking() && player.getHeldItemMainhand().isEmpty()) {
					dismantle(!player.isCreative());
					return false;
				}
			}


			//vanilla livingentity code down here

			//check if i'm on invulnerability frame
			if ((float)this.hurtResistantTime > 10.0F) {
							
	           	//check if i recieved damage greater that previous. if not do nothing cause bigger damage overrides smaller
	            
	            //currently instat damage tipped arrows do not work cause of this vanilla code. (pot damage overrides arrow since thay happen in same tick...) 
	            //Awesome game desing right here mojang!
	            if (damage <= this.lastDamage) {
	               return false;
	               
	            }
				//if true deal that damage minus the one i just inflicted.
				
				float ld = this.lastDamage;
				this.lastDamage = damage;
	            damage = damage - ld;
	            
	         } else {
	         	//if i'm not on invulnerability frame deal normal damage and reset cooldowns
	 
	            this.lastDamage = damage;
	            this.hurtResistantTime = 20;
	            this.maxHurtTime = 10;

	            //don't know what this does. probably sends a packet of some sort. seems to be related to red overlay so I disbled
	            //this.world.setEntityState(this, (byte)2);
	         }

	        //set to 0 to disable red glow that happens when hurt
	        this.hurtTime = 0;//this.maxHurtTime;


			
			// calculate the ACTUAL damage done after armor n stuff
			
			if (!world.isRemote) {
				damage = ForgeHooks.onLivingHurt(this, source, damage);
				if (damage > 0) {
					damage = this.applyArmorCalculations(source, damage);
					damage = this.applyPotionDamageCalculations(source, damage);
					float f1 = damage;
					damage = Math.max(damage - this.getAbsorptionAmount(), 0.0F);
					this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - damage));				
				}
			}
			// magic code ^


			// damage in the same tick, add it
			if (lastDamageTick == this.ticksExisted) {
				lastDamage += damage;
				shake += damage ;
				shake = Math.min(shake, 60f);
			} else {
				// OUCH :(
				shake =  Math.min(damage, 60f);
				lastDamage = damage;
				lastDamageTick = this.ticksExisted;
			}

			
			if (!this.world.isRemote) {
				//custom update packet
				/*Network.sendToAllNear(this.getPosX(), this.getPosY(), this.getPosZ(), 128, this.world.getDimension().getType(),
							new Network.PacketDamageNumber(this.getEntityId(), damage, shake));
				*/
				Network.sendToAllTracking(this.world, this, new Network.PacketDamageNumber(this.getEntityId(), damage, shake));
				
				// damage numebrssss
				int color = getColorFromDamageSource(source);
				DummyNumberEntity.CustomEntity number = new DummyNumberEntity.CustomEntity(damage, color, this.mynumberpos++, this.world);
				number.setLocationAndAngles(this.getPosX(), this.getPosY()+1, this.getPosZ(), 0.0F, 0.0F);
				this.world.addEntity(number);

				this.critical=false;

						

				this.damageTaken += damage;
				if (firstDamageTick == 0) {
					firstDamageTick = this.ticksExisted;
				}
			}
			return true;
		}



		public void applyEquipmentModifiers(){
			//living entity code here. aparently every entity does this check every tick.
			//trying insted to run it only when needed instead
			if (!this.world.isRemote){		
		         for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
		            ItemStack itemstack;
		            switch(equipmentslottype.getSlotType()) {
		            case ARMOR:
		               itemstack = this.inventoryArmor.get(equipmentslottype.getIndex());
		               break;
		            default:
		               continue;
		            }
		
		            ItemStack itemstack1 = this.getItemStackFromSlot(equipmentslottype);
		            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
		               if (!itemstack1.equals(itemstack, true))

		              // ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SEntityEquipmentPacket(this.getEntityId(), equipmentslottype, itemstack1));
		               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent(this, equipmentslottype, itemstack, itemstack1));
		               if (!itemstack.isEmpty()) {
		                  this.getAttributes().removeAttributeModifiers(itemstack.getAttributeModifiers(equipmentslottype));
		               }
		
		               if (!itemstack1.isEmpty()) {
		                  this.getAttributes().applyAttributeModifiers(itemstack1.getAttributeModifiers(equipmentslottype));
		               }
		            }
		         }
			}	
		}
		 
		@Override
		public void tick() {

			//check if on stable ground. used for automation
			if (this.world != null && this.world.getGameTime() % 20L == 0L) {
				if (world.isAirBlock(this.getOnPosition())) {
					this.dismantle(true);
				}
			}

			
			//used for fire damage, poison damage etc.
			//so you can damage it like any mob
			//for some reason instant damage 2 arrows show their damage twice
			this.baseTick();

	        //set to 0 to disable red glow that happens when hurt
	        this.hurtTime = 0;//this.maxHurtTime;  
			
			if (shake > 0) {

				shakeAnimation++;
				shake -= 0.8f;
				if (shake <= 0) {
					shakeAnimation = 0;
					shake = 0;
					
				}
			}
			//used only for dragon head mouth
			this.prevLimbSwingAmount=0;
			this.limbSwingAmount=0; 
			this.limbSwing=shake; 


			// DPS!
			//&& this.ticksExisted - lastDamageTick >60 for static

			//am i being attacked?
			if (!getEntityWorld().isRemote && this.damageTaken > 0) { 

				boolean isdynamic = Config.Configs.DYNAMIC_DPS.get();
				boolean flag = isdynamic? (this.ticksExisted == lastDamageTick+1) : (this.ticksExisted - lastDamageTick) >60;
				

				//only show damage after second damage tick
				if (flag && firstDamageTick < lastDamageTick) {

					// it's not actual DPS but "damage per tick scaled to seconds".. but meh.
					float seconds = (lastDamageTick - firstDamageTick) / 20f + 1;
					float dps = damageTaken / seconds;
					for (int i = 0; i < this.currentlyAttacking.size(); i++) {
						this.currentlyAttacking.get(i).sendStatusMessage(new StringTextComponent("Target Dummy: " + new DecimalFormat("#.##").format(dps)+" DPS"), true);
				
					}
					

				}
				//out of combat. reset variables
 				if(this.ticksExisted - lastDamageTick >60){
 					this.currentlyAttacking.clear();
					this.damageTaken = 0;
					this.firstDamageTick = 0;
 				}
			}
		}


		@Override
		protected boolean isMovementBlocked() {
			return true;
		}

		// used for arrows. may want to change this, but then again 
		// armor stands are alive so...
		//can cause problems with wither
		@Override
		public boolean isAlive() {
			return true;
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		public boolean canBeCollidedWith() {
			return true;
		}

		//called when entity is first spawned/loaded
		@Override
		public void writeSpawnData(PacketBuffer buffer) {
			buffer.writeFloat(this.shake);
			buffer.writeBoolean(this.sheared);
			//hijacking this method to do some other server calculations. ther's probably an event just for this but I havent found ti
			this.updateOnLoadServer();

		}

		//called when entity is first spawned/loaded
		@Override
		public void readSpawnData(PacketBuffer additionalData) {
			this.shake = additionalData.readFloat();
			this.sheared = additionalData.readBoolean();	
			//and this as well to do some other client calculations
			this.updateOnLoadClient();
			
		}


		@Override
		public void writeAdditional(CompoundNBT tag) {
			super.writeAdditional(tag);
			tag.putFloat("shake", this.shake);
			tag.putInt("type", this.mobType);
			tag.putInt("damage number pos", this.mynumberpos);
			tag.putBoolean("sheared", this.sheared);
		}

		@Override
		public void readAdditional(CompoundNBT tag) {
			super.readAdditional(tag);
			this.shake = tag.getFloat("shake");
			this.mobType = tag.getInt("type");
			this.mynumberpos = tag.getInt("damage number pos");
			this.sheared = tag.getBoolean("sheared");
		}


		@Override
		public IPacket<?> createSpawnPacket() {
			return NetworkHooks.getEntitySpawningPacket(this);
		}

		@Override
		public CreatureAttribute getCreatureAttribute() {
			switch (this.mobType){
				default:
				case 0:
					return CreatureAttribute.UNDEFINED;
				case 1:
					return CreatureAttribute.UNDEAD;
				case 2:
					return CreatureAttribute.WATER;
				case 3:
					return CreatureAttribute.ILLAGER;
			}
		}

		@Override
		public boolean canDespawn(double distanceToClosestPlayer) {
			return false;
		}

		protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
			super.dropSpecialItems(source, looting, recentlyHitIn);
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.armor_stand.hit"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.armor_stand.break"));
		}

		@Override
		public boolean onLivingFall(float l, float d) {
			return false;
		}

		@Override
		protected void registerAttributes() {
			super.registerAttributes();
			if (this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
			if (this.getAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
			if (this.getAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0);
			if (this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0);
			if (this.getAttribute(SharedMonsterAttributes.FLYING_SPEED) == null)
				this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0);
		}

		@Override
		protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		}

		@Override
		public void setNoGravity(boolean ignored) {
			super.setNoGravity(true);
		}


	}

	public class ModelDummy extends BipedModel<LivingEntity> {
		public ModelRenderer standPlate;
		public ModelRenderer newhead;
		public ModelRenderer newhead2;
		private float r = 0;
		private float r2 = 0;
		public ModelDummy() {
			this(0, 0f);
		}

		public ModelDummy(float size, float yOffsetIn) {
			this(size, yOffsetIn, 64, 64, 0);
		}

		public ModelDummy(float size, float yOffsetIn, int xw, int yw, float legOffset) {
			super(size, yOffsetIn, xw, yw);
			this.bipedRightArm = new ModelRenderer(this, 40, 16);
			this.bipedRightArm.addBox(-3.0F, 1.0F, -2.0F, 4, 8, 4.0F, size+0.01f);
			this.bipedRightArm.setRotationPoint(-2.5F, -22.0F + yOffsetIn, 0.0F); // +2
			this.bipedLeftArm = new ModelRenderer(this, 40, 16);
			this.bipedLeftArm.mirror = true;
			this.bipedLeftArm.addBox(-1.0F, 1.0F, -2.0F, 4, 8, 4.0F, size+0.01f);
			this.bipedLeftArm.setRotationPoint(2.5F, -22.0F + yOffsetIn, 0.0F);
			// left leg == stand
			this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
			this.bipedLeftLeg.addBox(-2.0F, -12.0F, -2.0F, 4, 12, 4, size+legOffset);
			this.bipedLeftLeg.setRotationPoint(0F, 24.0F + yOffsetIn, 0.0F);
			this.bipedRightLeg = new ModelRenderer(this, 0, 0);
			this.standPlate = new ModelRenderer(this, 0, 32);
			this.standPlate.addBox(-7.0F, 12F, -7.0F, 14, 1, 14, size);
			this.standPlate.setRotationPoint(0F, 11F + yOffsetIn, 0.0F);
			this.bipedBody = new ModelRenderer(this, 16, 16);
			// armor overlay size is slightly larger for leggins to prevent clipping armor
			this.bipedBody.addBox(-4.0F, -24.0F, -2.0F, 8, 12, 4, size ); // -24
			this.bipedBody.setRotationPoint(0.0F, 24.0F + yOffsetIn, 0.0F);
			this.bipedBody.addChild(this.bipedRightArm);
			this.bipedBody.addChild(this.bipedLeftArm);
			/*
			 * this.bipedHead = new ModelRenderer(this, 0, 0); this.bipedHead.addBox(-4.0F,
			 * -8.0F+ yOffsetIn, -4.0F, 8.0F, 8.0F, 8.0F, size);
			 * this.bipedHead.setRotationPoint(0.0F, 0.0F , 0.0F);
			 * 
			 */
			// don't know why but changing head rotation point does nothing. made newhead instead
			this.newhead = new ModelRenderer(this, 0, 0);
			this.newhead.setRotationPoint(0.0F, 24.0F + yOffsetIn, 0.0F);
			
			this.newhead2 = new ModelRenderer(this, 0, 0);
			this.newhead2.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, size);
			this.newhead2.setRotationPoint(0.0F, -24.0F + yOffsetIn, 0.0F);
			this.newhead.addChild(this.newhead2);
			
			this.bipedHeadwear = new ModelRenderer(this, 32, 0);
			this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, size + 0.5F);
			this.bipedHeadwear.setRotationPoint(0.0F, -24.0F + yOffsetIn, 0.0F);
			this.newhead.addChild(this.bipedHeadwear);
		}
		
		@Override
		public ModelRenderer getModelHead() {
			// head parameters
			double hx = -4f;
			double hy = -8f;
			double hz = -4f;
			double hs = 8f;
			double hrx = 0f;
			double hry = -24f;
			double hrz = 0f;
			// can't find a bette solution for skull heads... hardcoding it is
			// same parameters and rotation as newhead2. hopefully won't get called often
			Vec3d v = new Vec3d(hx, hy + hry, hz).rotatePitch(-r / 2).add(0, -hry - 0.99, 0);
			Vec3d v2 = new Vec3d(hrx, hry, hrz).rotatePitch(-r / 2).add(0, -hry - 0.99, 0);
			ModelRenderer skullhead = new ModelRenderer(this, 0, 0);
			skullhead.addBox((float) v.getX(), (float) v.getY(), (float) v.getZ(), 8f, 8f, 8f, 1f);
			skullhead.setRotationPoint((float) v2.getX(), (float) v2.getY(), (float) v2.getZ());
			skullhead.rotateAngleX = -r + r / 2;
			skullhead.rotateAngleZ = r2;
			return skullhead;
		}

		/**
		 * Sets the models various rotation angles then renders the model.
		 */
		@Override
		public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green,
				float blue, float alpha) {
			matrixStackIn.push();

			this.standPlate.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			matrixStackIn.translate(0, -0.0625, 0);
			this.newhead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.bipedLeftLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.bipedBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

			matrixStackIn.pop();
		}

		/**
		 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used
		 * for animating the movement of arms and legs, where par1 represents the
		 * time(so that arms and legs swing back and forth) and par2 represents how
		 * "far" arms and legs can swing at most.
		 */
		@Override
		public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
				float headPitch) {
			// this.bipedHead.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
			// this.bipedHead.rotateAngleX = headPitch / (180F / (float) Math.PI);
			//this.newhead.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
			//this.newhead.rotateAngleX = headPitch / (180F / (float) Math.PI);

			this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
			this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
			this.bipedRightArm.rotateAngleZ = 0.0F;
			this.bipedLeftArm.rotateAngleZ = 0.0F;
			this.bipedLeftArm.rotateAngleX = 0;
			this.bipedRightArm.rotateAngleX = 0;
			this.bipedRightArm.rotateAngleY = 0.0F;
			this.bipedLeftArm.rotateAngleY = 0.0F;
			this.bipedBody.rotateAngleX = 0.0F;
			this.bipedHead.rotationPointY = 0.0F;
			this.bipedHeadwear.rotationPointY = 0.0F;
			// un-rotate the stand plate so it's aligned to the block grid
			this.standPlate.rotateAngleY = -((CustomEntity) entityIn).rotationYaw / (180F / (float) Math.PI);
			this.bipedRightArm.rotateAngleZ = (float) Math.PI / 2f;
			this.bipedLeftArm.rotateAngleZ = -(float) Math.PI / 2f;

			float phase = ((CustomEntity) entityIn).shakeAnimation;
			float shake = Math.min((float)(((CustomEntity) entityIn).shake * Config.Configs.ANIMATION_INTENSITY.get()), 40f);
			this.r = 0;
			this.r2 = 0;
			//float r3=0;

			if (shake > 0) {
				r = (float) -(MathHelper.sin(phase) * Math.PI / 100f * shake);
				r2 = (float) (MathHelper.sin(phase) * Math.PI / 20f);
				//r3 = (float) -(MathHelper.sin(phase/2) * Math.PI / 100f * shake);
	
			}
			float n = 1.5f;
			this.bipedLeftArm.rotateAngleX = r * n;
			this.bipedRightArm.rotateAngleX = r  * n;
			this.bipedLeftLeg.rotateAngleX = r / 2; // z instead of x because it's rotated 90°
			this.bipedBody.rotateAngleX = r / 2;
			// this.bipedHead.rotateAngleX = -r;
			// this.bipedHead.rotateAngleZ = r2;
			// this.newhead.setRotationPoint(0.0F, 0.0F , 0.0F);
			this.newhead2.rotateAngleX =-r; //-r
			//
			this.newhead2.rotateAngleZ = r2; //r2
			// this.newhead.setRotationPoint(0F, 24.0F + 0, 0.0F);
			this.newhead.rotateAngleX = r / 2;
			// add
			this.bipedHeadwear.rotateAngleX = -r;
			this.bipedHeadwear.rotateAngleZ = r2;
		}
	}

	public class LayerDummyArmor extends BipedArmorLayer<LivingEntity, BipedModel<LivingEntity>, BipedModel<LivingEntity>> {
		public LayerDummyArmor(IEntityRenderer<LivingEntity, BipedModel<LivingEntity>> p_i50936_1_, BipedModel<LivingEntity> p_i50936_2_,
				BipedModel<LivingEntity> p_i50936_3_) {
			super(p_i50936_1_, new ModelDummy(0.5F, 0, 64, 32, -0.01f), new ModelDummy(1.0F, 0, 64, 32, -0.01f));
			((ModelDummy) modelArmor).standPlate.showModel = false;
			((ModelDummy) modelLeggings).standPlate.showModel = false;
			((ModelDummy) modelLeggings).newhead.showModel = false;
			((ModelDummy) modelArmor).newhead.showModel = false;
		}

		@Override
		protected void setModelSlotVisible(BipedModel<LivingEntity> modelIn, EquipmentSlotType slotIn) {
			this.setModelVisible(modelIn);
			switch (slotIn) {
				case HEAD :
					modelIn.bipedHead.showModel = true;
					((ModelDummy) modelIn).newhead.showModel = true;
					modelIn.bipedHeadwear.showModel = true;
					break;
				case CHEST :
					modelIn.bipedBody.showModel = true;
					modelIn.bipedRightArm.showModel = true;
					modelIn.bipedLeftArm.showModel = true;
					((ModelDummy) modelIn).newhead.showModel = false;
					break;
				case LEGS :
					modelIn.bipedBody.showModel = true;
					modelIn.bipedRightLeg.showModel = true;
					modelIn.bipedLeftLeg.showModel = true;
					((ModelDummy) modelIn).newhead.showModel = false;
					break;
				case FEET :
					modelIn.bipedRightLeg.showModel = false;
					modelIn.bipedLeftLeg.showModel = true;
					modelIn.bipedBody.showModel = false;
					((ModelDummy) modelIn).newhead.showModel = false;
			}
		}
	}
}
