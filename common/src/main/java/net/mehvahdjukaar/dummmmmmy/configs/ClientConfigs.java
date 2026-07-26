package net.mehvahdjukaar.dummmmmmy.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.mehvahdjukaar.dummmmmmy.Dummmmmmy.*;

public class ClientConfigs {

    public static void init() {
    }

    public static final ModConfigHolder SPEC;


    public static final Supplier<Double> ANIMATION_INTENSITY;
    public static final Supplier<Boolean> SHOW_HEARTHS;
    public static final Supplier<SkinType> SKIN;
    public static final Supplier<Boolean> DAMAGE_NUMBERS;
    public static final Supplier<Boolean> LIT_UP_PARTICLES;
    public static final Supplier<CritMode> CRIT_MODE;
    public static final Supplier<Boolean> CRIT_BOLD;
    public static final Supplier<Boolean> HAY_PARTICLES;

    public static final Supplier<Map<IdOrTagPredicate, Integer>> DAMAGE_TO_COLORS;

    private static final int COLOR_GENERIC = 0xffffff;
    private static final int COLOR_CRIT = 0xff0000;
    private static final int COLOR_DRAGON = 0xE600FF;
    private static final int COLOR_WITHER = 0x666666;
    private static final int COLOR_EXPLOSION = 0xFFBB29;
    private static final int COLOR_IND_MAGIC = 0x844CE7;
    private static final int COLOR_WATER = 0x1898E3;
    private static final int COLOR_FREEZING = 0x09D2FF;
    private static final int COLOR_TRIDENT = 0x00FF9D;
    private static final int COLOR_FIRE = 0xFF7700;
    private static final int COLOR_LIGHTNING = 0xFFF200;
    private static final int COLOR_CACTUS = 0x0FA209;
    private static final int COLOR_TRUE = 0x910038;
    private static final int COLOR_WARDEN = 0x074550;
    private static final int COLOR_WIND = 0xBEF3FF;
    private static final int COLOR_BLEED = 0x810A0A;


    static {

        ConfigBuilder builder = ConfigBuilder.create(Dummmmmmy.res("client"), ConfigType.CLIENT);

        builder.icon("target_dummy").push("visuals");
        ANIMATION_INTENSITY = builder.comment("How much the dummy swings in degrees with respect to the damage dealt")
                .defineSlider("animation_intensity", 0.75, 0.0, 2.0);
        SKIN = builder.comment("Skin used by the dummy")
                .define("texture", SkinType.DEFAULT);
        HAY_PARTICLES = builder.icon("minecraft:hay_block")
                .comment("Show hay particles when dealing damage")
                .feature("hay_particles");
        builder.pop();

        builder.icon("minecraft:iron_sword").push("damage_numbers");
        DAMAGE_NUMBERS = builder.comment("Floating damage numbers popping off the dummy every time it's hit")
                .mainFeature();
        SHOW_HEARTHS = builder.comment("Show hearths instead of damage dealt? (1 hearth = two damage)")
                .define("show_hearths", false);
        LIT_UP_PARTICLES = builder.comment("Display the numbers fullbright, so they stay readable in the dark")
                .define("full_bright", true);
        CRIT_MODE = builder.comment("How crits should be shown")
                .define("crit_mode", CritMode.COLOR_AND_MULTIPLIER);
        CRIT_BOLD = builder.comment("Make critical hit damage numbers bold")
                .define("crit_bold", false);

        // insertion ordered: getDamageColor returns the first key that matches, and the specific ids below have to win
        // over the broader tags
        Map<IdOrTagPredicate, Integer> map = new LinkedHashMap<>();
        map.put(new IdPredicate(TRUE_DAMAGE.getID()), COLOR_TRUE);
        map.put(new IdPredicate(CRITICAL_DAMAGE.getID()), COLOR_CRIT);
        map.put(new IdPredicate("generic"), COLOR_GENERIC);
        map.put(new IdPredicate("trident"), COLOR_TRIDENT);
        map.put(new IdPredicate("dragon_breath"), COLOR_DRAGON);
        map.put(new IdPredicate("sonic_boom"), COLOR_WARDEN);
        map.put(new IdPredicate("wind_charge"), COLOR_WIND);
        map.put(new IdPredicate("attributeslib:bleeding"), COLOR_BLEED);
        map.put(new TagPredicate(IS_EXPLOSION), COLOR_EXPLOSION);
        map.put(new TagPredicate(IS_COLD), COLOR_FREEZING);
        map.put(new TagPredicate(IS_THORN), COLOR_CACTUS);
        map.put(new TagPredicate(IS_FIRE), COLOR_FIRE);
        map.put(new TagPredicate(IS_WITHER), COLOR_WITHER);
        map.put(new TagPredicate(DamageTypeTags.IS_LIGHTNING), COLOR_LIGHTNING);
        map.put(new TagPredicate(DamageTypeTags.IS_DROWNING), COLOR_WATER);
        map.put(new TagPredicate(DamageTypeTags.WITCH_RESISTANT_TO), COLOR_IND_MAGIC);

        DAMAGE_TO_COLORS = builder.comment("Color to use for each damage type. Keys are damage type ids, or #tags, and are tested in insertion order")
                .defineObject("damage_type_colors", () -> map,
                        Codec.unboundedMap(IdOrTagPredicate.CODEC, ColorUtils.CODEC));

        builder.pop();

        SPEC = builder.build();
    }

    // suboptimal but eh
    public static int getDamageColor(Holder<DamageType> damageTypeId) {
        var values = ClientConfigs.DAMAGE_TO_COLORS.get();

        for (var e : values.entrySet()) {
            if (e.getKey().test(damageTypeId)) {
                return e.getValue();
            }
        }
        return -1;
    }

    public enum SkinType {
        DEFAULT("dummy", "dummy_h"),
        ORIGINAL("dummy_1", "dummy_1"),
        ALTERNATIVE("dummy_3", "dummy_3_h"),
        DUNGEONS("dummy_2", "dummy_2_h");

        private final ResourceLocation texture;
        private final ResourceLocation shearedTexture;

        SkinType(String name, String shearedName) {
            texture = Dummmmmmy.res("textures/entity/" + name + ".png");
            shearedTexture = Dummmmmmy.res("textures/entity/" + shearedName + ".png");
        }

        public ResourceLocation getSkin(Boolean sheared) {
            return sheared ? shearedTexture : texture;
        }
    }

    public interface IdOrTagPredicate extends Predicate<Holder<DamageType>> {
        String toString();

        Codec<IdOrTagPredicate> CODEC = Codec.STRING.comapFlatMap(IdOrTagPredicate::read, IdOrTagPredicate::toString).stable();

        static DataResult<IdOrTagPredicate> read(String location) {
            if (location.startsWith("#")) {
                return ResourceLocation.read(location.substring(1)).map(TagPredicate::new);
            } else {
                return ResourceLocation.read(location).map(IdPredicate::new);
            }
        }
    }

    record IdPredicate(ResourceLocation resourceLocation) implements IdOrTagPredicate {
        public IdPredicate(String name) {
            this(ResourceLocation.tryParse(name));
        }

        @Override
        public String toString() {
            return resourceLocation.toString();
        }

        @Override
        public boolean test(Holder<DamageType> id) {
            return id.unwrapKey().get().location().equals(resourceLocation);
        }
    }

    record TagPredicate(TagKey<DamageType> tag) implements IdOrTagPredicate {

        public TagPredicate(ResourceLocation resourceLocation) {
            this(new TagKey<>(Registries.DAMAGE_TYPE, resourceLocation));
        }

        @Override
        public String toString() {
            return "#" + tag.location();
        }

        @Override
        public boolean test(Holder<DamageType> holder) {
            return holder.is(tag);
        }
    }

}
