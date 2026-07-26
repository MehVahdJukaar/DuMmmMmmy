package testdummy;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
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

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;

@Mod(modid = TestDummy.MODID, version = TestDummy.VERSION, name = TestDummy.NAME)
public class TestDummy {
    public static final String MODID = "testdummy";
    public static final String VERSION = "2.0.7";
    public static final String NAME = "TestDummy";
    public static final Logger log = LogManager.getLogger(NAME);
    public static final DecimalFormat df = new DecimalFormat("#.##");

    @SidedProxy(clientSide = "testdummy.proxy.ClientProxy", serverSide = "testdummy.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(MODID)
    public static TestDummy instance;

    public static Item itemDummy = new ItemDummy();

    public TestDummy() {
        modifyArrowTargetSelector();
    }

    @SuppressWarnings("unchecked")
    public static void modifyArrowTargetSelector() {
        try {
            //All of this just to replace EntityArrow.ARROW_TARGETS sub predicate IS_ALIVE with one that doesn't check Dummies
            Field field = EntityArrow.class.getDeclaredField("field_184553_f"); //ARROW_TARGETS
            field.setAccessible(true);
            Predicate<Entity> arrow_targets = (Predicate<Entity>) field.get(null);

            Class<?> and_predicate_class = null;
            for (Class<?> clazz : Predicates.class.getDeclaredClasses()) {
                if (clazz.getSimpleName().equals("AndPredicate")) {
                    and_predicate_class = clazz;
                    break;
                }
            }
            if (and_predicate_class == null) return;
            if (and_predicate_class.isAssignableFrom(arrow_targets.getClass())) {
                Field components_field = and_predicate_class.getDeclaredField("components");
                components_field.setAccessible(true);
                List<Predicate<Entity>> components = (List<Predicate<Entity>>) components_field.get(arrow_targets);

                int idx = 0;
                for (Predicate<Entity> pred : components) {
                    if (pred.equals(EntitySelectors.IS_ALIVE)) {
                        components.set(idx, Predicates.or(pred, Predicates.instanceOf(EntityDummy.class)));
                        break;
                    }
                    idx++;
                }
            }
        } catch (Exception e) {
            TestDummy.log.warn("Failed to modify arrows. You will be unable to target dummies with arrows.");
        }
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