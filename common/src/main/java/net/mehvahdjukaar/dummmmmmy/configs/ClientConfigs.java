package net.mehvahdjukaar.dummmmmmy.configs;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ClientConfigs {

    public static void init() {
    }

    public static final ConfigSpec SPEC;


    public static final Supplier<Double> ANIMATION_INTENSITY;
    public static final Supplier<Boolean> SHOW_HEARTHS;
    public static final Supplier<SkinType> SKIN;
    public static final Supplier<Boolean> DAMAGE_NUMBERS;

    public static final Supplier<Integer> DAMAGE_GENERIC;
    public static final Supplier<Integer> DAMAGE_CRIT;
    public static final Supplier<Integer> DAMAGE_DRAGON;
    public static final Supplier<Integer> DAMAGE_WITHER;
    public static final Supplier<Integer> DAMAGE_EXPLOSION;
    public static final Supplier<Integer> DAMAGE_IND_MAGIC;
    public static final Supplier<Integer> DAMAGE_TRIDENT;
    public static final Supplier<Integer> DAMAGE_MAGIC;
    public static final Supplier<Integer> DAMAGE_FIRE;
    public static final Supplier<Integer> DAMAGE_LIGHTNING;
    public static final Supplier<Integer> DAMAGE_CACTUS;
    public static final Supplier<Integer> DAMAGE_TRUE;

    private static final int COLOR_GENERIC = 0xffffff;
    private static final int COLOR_CRIT = 0xff0000;
    private static final int COLOR_DRAGON = 0xE600FF;
    private static final int COLOR_WITHER = 0x666666;
    private static final int COLOR_EXPLOSION = 0xFFBB29;
    private static final int COLOR_IND_MAGIC = 0x844CE7;
    private static final int COLOR_MAGIC = 0x33B1FF;
    private static final int COLOR_TRIDENT = 0x00FF9D;
    private static final int COLOR_FIRE = 0xFF7700;
    private static final int COLOR_LIGHTNING = 0xFFF200;
    private static final int COLOR_CACTUS = 0x0FA209;
    private static final int COLOR_TRUE = 0x910038;

    static {
        ConfigBuilder builder = ConfigBuilder.create(Dummmmmmy.res("client"), ConfigType.CLIENT);


        builder.comment("lots of cosmetic stuff in here");

        builder.push("visuals");
        ANIMATION_INTENSITY = builder.comment("How much the dummy swings in degrees with respect to the damage dealt. default=0.75")
                .define("animation_intensity", 0.75, 0.0, 2.0);
        SHOW_HEARTHS = builder.comment("Show hearths instead of damage dealt? (1 hearth = two damage)")
                .define("show_hearths", false);
        DAMAGE_NUMBERS = builder.comment("Show damage numbers on entity")
                .define("damage_numbers", true);

        SKIN = builder.comment("Skin used by the dummy").define("texture", SkinType.DEFAULT);

        builder.push("damage_number_colors").comment("hex color for various damage sources");
        DAMAGE_GENERIC = builder.defineColor("genetic", COLOR_GENERIC);
        DAMAGE_CRIT = builder.defineColor("crit", COLOR_CRIT);
        DAMAGE_DRAGON = builder.defineColor("dragon_breath", COLOR_DRAGON);
        DAMAGE_WITHER = builder.defineColor("wither", COLOR_WITHER);
        DAMAGE_EXPLOSION = builder.defineColor("explosion", COLOR_EXPLOSION);
        DAMAGE_IND_MAGIC = builder.defineColor("magic_indirect", COLOR_IND_MAGIC);
        DAMAGE_MAGIC = builder.defineColor("magic", COLOR_MAGIC);
        DAMAGE_TRIDENT = builder.defineColor("trident", COLOR_TRIDENT);
        DAMAGE_FIRE = builder.defineColor("fire", COLOR_FIRE);
        DAMAGE_LIGHTNING = builder.defineColor("lightning", COLOR_LIGHTNING);
        DAMAGE_CACTUS = builder.defineColor("cactus", COLOR_CACTUS);
        DAMAGE_TRUE = builder.defineColor("true_damage", COLOR_TRUE);

        builder.pop();

        builder.pop();


        SPEC = builder.buildAndRegister();
    }

    public enum SkinType {
        DEFAULT("dummy", "dummy_h"),
        ORIGINAL("dummy_1", "dummy_1"),
        DUNGEONS("dummy_3", "dummy_3_h"),
        ALTERNATIVE("dummy_2", "dummy_2_h");

        private final ResourceLocation texture;
        private final ResourceLocation shearedTexture;

        SkinType(String name, String shearedName) {
            texture = new ResourceLocation(Dummmmmmy.MOD_ID + ":textures/" + name + ".png");
            shearedTexture = new ResourceLocation(Dummmmmmy.MOD_ID + ":textures/" + shearedName + ".png");
        }

        public ResourceLocation getSkin(Boolean sheared) {
            return sheared ? shearedTexture : texture;
        }
    }


}
