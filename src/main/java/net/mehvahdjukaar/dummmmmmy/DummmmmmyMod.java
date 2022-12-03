
//Authors: bonusboni, mehvahdjukaar
package net.mehvahdjukaar.dummmmmmy;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.setup.ClientHandler;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.mehvahdjukaar.dummmmmmy.setup.ModSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(DummmmmmyMod.MOD_ID)
public class DummmmmmyMod {

    public static final String MOD_ID = "dummmmmmy";

    public static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
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

	/*
    private static void testStuff() {
		RecipeManager manager;
		var recipes = manager.getAllRecipesFor(RecipeType.CRAFTING);
        var map = new HashMap<Item, List<Object2IntArrayMap<Item>>>();
		for(var r : recipes){
			ItemStack stack = r.getResultItem();
			if(seemsLikeWood(stack)) {
				int count = stack.getCount();
				var list = map.computeIfAbsent(stack.getItem(), k -> new ArrayList<>());
				var m = new Object2IntArrayMap<Item>();
				for (var i : r.getIngredients()) {
					int c = m.getOrDefault(i, 0);
					m.put(i.getItems()[0].getItem(), c + count);
				}
				list.add(m);
			}
		}
		var fast = new HashMap<Item, Integer>();
		for(var e : map.entrySet()){
			Item result = e.getKey();
			for(var l : e.getValue()){
				//this is one recipe

			}

		}

        var iterator = recipes.listIterator();
        while (iterator.hasNext()) {
            var r = iterator.next();

        }

    }
*/
	private static boolean seemsLikeWood(ItemStack stack) {
		return true;
	}
}