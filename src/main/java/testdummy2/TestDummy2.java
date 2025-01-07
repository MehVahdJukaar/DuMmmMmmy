package testdummy2;

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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import testdummy2.entity.EntityDpsFloatingNumber;
import testdummy2.entity.EntityDummy;
import testdummy2.entity.EntityFloatingNumber;
import testdummy2.item.ItemDummy;
import testdummy2.proxy.CommonProxy;

import java.text.DecimalFormat;

@Mod(modid = TestDummy2.MODID, version = TestDummy2.VERSION, name = TestDummy2.NAME, dependencies = "required-after:fermiumbooter")
public class TestDummy2 {
    public static final String MODID = "testdummy2";
    public static final String VERSION = "2.0.1";
    public static final String NAME = "TestDummy2";
    public static final Logger log = LogManager.getLogger();
    public static final DecimalFormat df = new DecimalFormat("#.##");


    @SidedProxy(clientSide = "testdummy2.proxy.ClientProxy", serverSide = "testdummy2.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(MODID)
    public static TestDummy2 instance;

    public static Item itemDummy = new ItemDummy();

    public TestDummy2() {
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
}