
//Authors: bonusboni, mehvahdjukaar
package net.mehvahdjukaar.dummmmmmy;

import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.setup.ClientHandler;
import net.mehvahdjukaar.dummmmmmy.setup.ModSetup;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DummmmmmyMod.MOD_ID)
public class DummmmmmyMod {

	public static final String MOD_ID = "dummmmmmy";

	public static final Logger LOGGER = LogManager.getLogger();

	public static ResourceLocation res(String name){
		return new ResourceLocation(MOD_ID,name);
	}

	public DummmmmmyMod() {

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configs.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.SERVER_CONFIG);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Configs::reloadConfigsEvent);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();


		ModRegistry.init(bus);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> bus.register(ClientHandler.class));

		bus.addListener(ModSetup::init);

	}
}