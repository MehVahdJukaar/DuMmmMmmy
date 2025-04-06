package testdummy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import testdummy.entity.EntityDpsFloatingNumber;
import testdummy.entity.EntityDummy;
import testdummy.entity.EntityFloatingNumber;
import testdummy.item.ItemDummy;
import testdummy.proxy.CommonProxy;

import java.text.DecimalFormat;

@Mod(modid = TestDummy.MODID, version = TestDummy.VERSION, name = TestDummy.NAME)
public class TestDummy {
    public static final String MODID = "testdummy";
    public static final String VERSION = "2.0.5";
    public static final String NAME = "TestDummy";
    public static final Logger log = LogManager.getLogger();
    public static final DecimalFormat df = new DecimalFormat("#.##");

    @SidedProxy(clientSide = "testdummy.proxy.ClientProxy", serverSide = "testdummy.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(MODID)
    public static TestDummy instance;

    public static Item itemDummy = new ItemDummy();

    public TestDummy() {
        //log.info("Please don't hurt me. :S");
        //EntityArrow.ARROW_TARGETS = Predicates.and(Arrays.asList(new Predicate[]{EntitySelectors.NOT_SPECTATING, entity -> entity.canBeCollidedWith(), entity -> (entity.isEntityAlive() || entity instanceof EntityDummy)}));
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "dummy"), EntityDummy.class, "Dummy", 0, instance, 128, 10, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "floating_number"), EntityFloatingNumber.class, "FloatingNumber", 1, instance, 128, 1, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "floating_number_dps"), EntityDpsFloatingNumber.class, "FloatingNumberDPS", 2, instance, 128, 1, false);
        proxy.preinit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @SubscribeEvent
    public void register(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(itemDummy);
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID, "dummy"), null,
                new ItemStack(itemDummy),
                " B ", "HWH", " P ",
                'B', Item.getItemFromBlock(Blocks.HAY_BLOCK),
                'H', Items.WHEAT,
                'W', new ItemStack(Blocks.WOOL, 0, 32767),
                'P', "plankWood");
    }

    @SubscribeEvent
    public void onMissingEntityMappings(RegistryEvent.MissingMappings<EntityEntry> event) {
        for (RegistryEvent.MissingMappings.Mapping<EntityEntry> mapping : event.getAllMappings())
            if (mapping.key.getNamespace().equals("testdummy2"))
                switch (mapping.key.getPath()) {
                    case "dummy": mapping.remap(EntityRegistry.getEntry(EntityDummy.class)); break;
                    case "floating_number": mapping.remap(EntityRegistry.getEntry(EntityFloatingNumber.class)); break;
                    case "floating_number_dps": mapping.remap(EntityRegistry.getEntry(EntityDpsFloatingNumber.class)); break;
                }
    }

    @SubscribeEvent
    public void onMissingItemMappings(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings())
            if (mapping.key.getNamespace().equals("testdummy2") && mapping.key.getPath().equals("dummy"))
                mapping.remap(TestDummy.itemDummy);
    }
}