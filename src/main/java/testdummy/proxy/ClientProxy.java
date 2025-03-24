package testdummy.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import testdummy.TestDummy;
import testdummy.client.RenderDummy;
import testdummy.client.RenderFloatingNumber;
import testdummy.entity.EntityDummy;
import testdummy.entity.EntityFloatingNumber;

public class ClientProxy
        extends CommonProxy {
    public void preinit() {
        MinecraftForge.EVENT_BUS.register(this);
        RenderingRegistry.registerEntityRenderingHandler(EntityDummy.class, RenderDummy::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFloatingNumber.class, RenderFloatingNumber::new);
    }

    @SubscribeEvent
    public void models(ModelRegistryEvent e) {
        String loc = "testdummy:dummy";
        ModelLoader.setCustomModelResourceLocation(TestDummy.itemDummy, 0, new ModelResourceLocation(loc, "inventory"));
    }
}