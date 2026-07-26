package net.mehvahdjukaar.dummmmmmy.integration.platform;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.core.client.config.MoonlightConfigSelectScreen;
import net.minecraft.resources.ResourceLocation;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MoonlightConfigSelectScreen.create(Dummmmmmy.MOD_ID, parent,
                ResourceLocation.withDefaultNamespace("textures/block/hay_block_side.png"));
    }
}
