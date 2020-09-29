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

import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;



@DummmmmmyModElements.ModElement.Tag
public class Config extends DummmmmmyModElements.ModElement {
	/**
	 * Do not remove this constructor
	 */
	public Config(DummmmmmyModElements instance) {
		super(instance, 9);
	}

	@Override
	public void initElements() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configs.CLIENT_CONFIG);
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
	
	
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {
	}


	@EventBusSubscriber
	public  static class Configs{
		public static ForgeConfigSpec CLIENT_CONFIG;
		public static ForgeConfigSpec.DoubleValue ANIMATION_INTENSITY;
		public static ForgeConfigSpec.BooleanValue SHOW_HEARTHS;
		public static ForgeConfigSpec.BooleanValue DYNAMIC_DPS;
		static {
	
	        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	
	        CLIENT_BUILDER.comment("lots of cosmetic stuff in here").push("visuals");
	        ANIMATION_INTENSITY = CLIENT_BUILDER.comment("How much the dummy swings in degrees with respect to the damage dealt").defineInRange("animationIntensity", 0.75, 0.0, 2.0);
	       	SHOW_HEARTHS = CLIENT_BUILDER.comment("Show hearths instead of damage dealt? (1 hearth = two damage)").define("showHearths", false);
	        DYNAMIC_DPS = CLIENT_BUILDER.comment("Does dps message update dynamically or will it only appear after each parse? ").define("dynamicDPS", true);
	        
	        
	        
	        CLIENT_BUILDER.pop();
	

	
	        CLIENT_CONFIG = CLIENT_BUILDER.build();
    	}

		
	}


	
}
