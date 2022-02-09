
package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Collections;
import java.util.List;

public class Configs {

    public static void reloadConfigsEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() == CLIENT_CONFIG)
            cached.refresh();
        else if (event.getConfig().getSpec() == SERVER_CONFIG) {
            cachedServer.refresh();
        }
    }

    private static String col2String(int color) {
        return Integer.toHexString(color);
    }
/*
    private static final Color = new Color(0xffffff);
    private static final Color COLOR_CRIT = new Color(0xff0000);
    private static final Color COLOR_DRAGON = new Color(0xE600FF);
    private static final Color COLOR_WITHER = new Color(0x666666);
    private static final Color COLOR_EXPLOSION = new Color(0xFFBB29);
    private static final Color COLOR_IND_MAGIC = new Color(0x844CE7);
    private static final Color COLOR_MAGIC = new Color(0x33B1FF);
    private static final Color COLOR_TRIDENT = new Color(0x00FF9D);
    private static final Color COLOR_FIRE = new Color(0xFF7700);
    private static final Color COLOR_LIGHTNING = new Color(0xFFF200);
    private static final Color COLOR_CACTUS = new Color(0x0FA209);
    private static final Color COLOR_TRUE = new Color(0x910038);
*/

    private static final int COLOR_GENERIC =  0xffffff;
    private static final int COLOR_CRIT = 0xff0000;
    private static final int COLOR_DRAGON = 0xE600FF;
    private static final int COLOR_WITHER = 0x666666;
    private static final int COLOR_EXPLOSION = 0xFFBB29;
    private static final int COLOR_IND_MAGIC = 0x844CE7;
    private static final int COLOR_MAGIC = 0x33B1FF;
    private static final int COLOR_TRIDENT = 0x00FF9D;
    private static final int COLOR_FIRE = 0xFF7700;
    private static final int COLOR_LIGHTNING = 0xFFF200;
    private static final int COLOR_CACTUS =0x0FA209;
    private static final int COLOR_TRUE = 0x910038;

    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.DoubleValue ANIMATION_INTENSITY;
    public static ForgeConfigSpec.BooleanValue SHOW_HEARTHS;
    public static ForgeConfigSpec.EnumValue<DpsMode> DYNAMIC_DPS;
    public static ForgeConfigSpec.ConfigValue<SkinType> SKIN;

    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_GENERIC;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_CRIT;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_DRAGON;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_WITHER;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_EXPLOSION;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_IND_MAGIC;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_TRIDENT;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_MAGIC;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_FIRE;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_LIGHTNING;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_CACTUS;
    public static ForgeConfigSpec.ConfigValue<String> DAMAGE_TRUE;

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue DAMAGE_NUMBERS;
    public static ForgeConfigSpec.BooleanValue DAMAGE_EQUIPMENT;
    public static ForgeConfigSpec.IntValue RADIUS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;
    public static ForgeConfigSpec.IntValue MAX_COMBAT_INTERVAL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("lots of cosmetic stuff in here").push("visuals");
        ANIMATION_INTENSITY = builder.comment("How much the dummy swings in degrees with respect to the damage dealt. default=0.75").defineInRange("animationIntensity", 0.75, 0.0, 2.0);
        SHOW_HEARTHS = builder.comment("Show hearths instead of damage dealt? (1 hearth = two damage)").define("showHearths", false);
        DYNAMIC_DPS = builder.comment("Does dps message update dynamically or will it only appear after each parse? ")
                .defineEnum("DPS_mode", DpsMode.DYNAMIC);


        SKIN = builder.comment("Skin used by the dummy").defineEnum("texture", SkinType.DEFAULT);

        builder.push("damage_number_colors").comment("hex color for various damage sources");
        DAMAGE_GENERIC = builder.define("genetic", col2String(COLOR_GENERIC), Configs::isValidHex);
        DAMAGE_CRIT = builder.define("crit", col2String(COLOR_CRIT), Configs::isValidHex);
        DAMAGE_DRAGON = builder.define("dragon_breath", col2String(COLOR_DRAGON), Configs::isValidHex);
        DAMAGE_WITHER = builder.define("wither", col2String(COLOR_WITHER), Configs::isValidHex);
        DAMAGE_EXPLOSION = builder.define("explosion", col2String(COLOR_EXPLOSION), Configs::isValidHex);
        DAMAGE_IND_MAGIC = builder.define("magic_indirect", col2String(COLOR_IND_MAGIC), Configs::isValidHex);
        DAMAGE_MAGIC = builder.define("magic", col2String(COLOR_MAGIC), Configs::isValidHex);
        DAMAGE_TRIDENT = builder.define("trident", col2String(COLOR_TRIDENT), Configs::isValidHex);
        DAMAGE_FIRE = builder.define("fire", col2String(COLOR_FIRE), Configs::isValidHex);
        DAMAGE_LIGHTNING = builder.define("lightning", col2String(COLOR_LIGHTNING), Configs::isValidHex);
        DAMAGE_CACTUS = builder.define("cactus", col2String(COLOR_CACTUS), Configs::isValidHex);
        DAMAGE_TRUE = builder.define("true_damage", col2String(COLOR_TRUE), Configs::isValidHex);

        builder.pop();

        builder.pop();

        CLIENT_CONFIG = builder.build();


        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

        serverBuilder.push("scarecrow").comment("Equip a dummy with a pumpkin to make hit act as a scarecrow");

        WHITELIST = serverBuilder.comment("All animal entities will be scared. add here additional ones that are not included").defineList("mobs_whitelist", Collections.singletonList(""), s -> true);
        BLACKLIST = serverBuilder.comment("Animal entities that will not be scared").defineList("mobs_blacklist", Collections.singletonList(""), s -> true);

        RADIUS = serverBuilder.comment("Scaring radius").defineInRange("scare_radius", 12, 0, 100);

        serverBuilder.pop();
        DAMAGE_NUMBERS = serverBuilder.comment("Enable and disable damage numbers")
                .define("damage_numbers", true);

        DAMAGE_EQUIPMENT = serverBuilder.comment("Enable this to prevent your equipment from getting damaged when attacking the dummy")
                .define("disable_equipment_damage", true);

        MAX_COMBAT_INTERVAL = serverBuilder.comment("Time in ticks that it takes for a dummy to be considered out of combat after having recieved damage")
                .defineInRange("maximum_out_of_combat_interval", 6*20, 20, 1000);

        SERVER_CONFIG = serverBuilder.build();
    }

    private static int parseHex(String s) {
        int hex = 0xffffff;
        try {
            hex = Integer.parseInt(s.replace("0x", ""), 16);
        } catch (Exception e) {
            DummmmmmyMod.LOGGER.warn("failed to parse damage source color from config");
        }
        return hex;
    }

    private static boolean isValidHex(Object s) {
        try {
            Integer.parseInt(((String) s).replace("0x", ""), 16);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static class cachedServer {

        public static List<? extends String> WHITELIST;
        public static List<? extends String> BLACKLIST;
        public static int RADIUS;
        public static boolean DAMAGE_EQUIPMENT;
        public static boolean DAMAGE_NUMBERS;
        public static int MAX_COMBAT_INTERVAL;

        public static void refresh() {

            RADIUS = Configs.RADIUS.get();
            WHITELIST = Configs.WHITELIST.get();
            BLACKLIST = Configs.BLACKLIST.get();
            DAMAGE_EQUIPMENT = Configs.DAMAGE_EQUIPMENT.get();
            DAMAGE_NUMBERS = Configs.DAMAGE_NUMBERS.get();
            MAX_COMBAT_INTERVAL = Configs.MAX_COMBAT_INTERVAL.get();
        }
    }


    public static class cached {
        public static double ANIMATION_INTENSITY;
        public static boolean SHOW_HEARTHS;
        public static DpsMode DYNAMIC_DPS;
        public static SkinType SKIN;

        public static int DAMAGE_GENERIC;
        public static int DAMAGE_CRIT;
        public static int DAMAGE_DRAGON;
        public static int DAMAGE_WITHER;
        public static int DAMAGE_EXPLOSION;
        public static int DAMAGE_IND_MAGIC;
        public static int DAMAGE_TRIDENT;
        public static int DAMAGE_MAGIC;
        public static int DAMAGE_FIRE;
        public static int DAMAGE_LIGHTNING;
        public static int DAMAGE_CACTUS;
        public static int DAMAGE_TRUE;



        public static void refresh() {
            ANIMATION_INTENSITY = Configs.ANIMATION_INTENSITY.get();
            SHOW_HEARTHS = Configs.SHOW_HEARTHS.get();
            try {
                DYNAMIC_DPS = Configs.DYNAMIC_DPS.get();
            }
            catch (Exception e){
                DYNAMIC_DPS = DpsMode.DYNAMIC;
            }
            try {
                SKIN = Configs.SKIN.get();
            }catch (Exception e){
                SKIN = SkinType.DEFAULT;
            }

            DAMAGE_GENERIC = parseHex(Configs.DAMAGE_GENERIC.get());
            DAMAGE_CRIT = parseHex(Configs.DAMAGE_CRIT.get());
            DAMAGE_DRAGON = parseHex(Configs.DAMAGE_DRAGON.get());
            DAMAGE_WITHER = parseHex(Configs.DAMAGE_WITHER.get());
            DAMAGE_EXPLOSION = parseHex(Configs.DAMAGE_EXPLOSION.get());
            DAMAGE_IND_MAGIC = parseHex(Configs.DAMAGE_IND_MAGIC.get());
            DAMAGE_TRIDENT = parseHex(Configs.DAMAGE_TRIDENT.get());
            DAMAGE_MAGIC = parseHex(Configs.DAMAGE_MAGIC.get());
            DAMAGE_FIRE = parseHex(Configs.DAMAGE_FIRE.get());
            DAMAGE_LIGHTNING = parseHex(Configs.DAMAGE_LIGHTNING.get());
            DAMAGE_CACTUS = parseHex(Configs.DAMAGE_CACTUS.get());
            DAMAGE_TRUE = parseHex(Configs.DAMAGE_TRUE.get());


        }
    }

    public enum SkinType {
        DEFAULT("dummy","dummy_h"),
        ORIGINAL("dummy_1","dummy_1"),
        DUNGEONS("dummy_3","dummy_3_h"),
        ALTERNATIVE("dummy_2","dummy_2_h");

        private final ResourceLocation texture;
        private final ResourceLocation shearedTexture;

        SkinType(String name, String shearedName) {
            texture = new ResourceLocation(DummmmmmyMod.MOD_ID + ":textures/" + name + ".png");
            shearedTexture = new ResourceLocation(DummmmmmyMod.MOD_ID + ":textures/" + shearedName + ".png");
        }

        public ResourceLocation getSkin(Boolean sheared) {
            return sheared ? shearedTexture : texture;
        }
    }

    public enum DpsMode{
        DYNAMIC,
        STATIC,
        OFF
    }

}
