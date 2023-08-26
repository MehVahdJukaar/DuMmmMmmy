package net.mehvahdjukaar.dummmmmmy.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.api.client.util.ColorUtil;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClientConfigs {

    public static void init() {
    }

    public static final ConfigSpec SPEC;


    public static final Supplier<Double> ANIMATION_INTENSITY;
    public static final Supplier<Boolean> SHOW_HEARTHS;
    public static final Supplier<SkinType> SKIN;
    public static final Supplier<Boolean> DAMAGE_NUMBERS;
    public static final Supplier<Boolean> LIT_UP_PARTICLES;

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
    private static final int COLOR_BLEED = 0x810A0A;
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
        LIT_UP_PARTICLES = builder.comment("Display particles fullbright")
                .define("full_bright_damage_numbers", true);

        SKIN = builder.comment("Skin used by the dummy").define("texture", SkinType.DEFAULT);


        Map<IdOrTagPredicate, Integer> map = new HashMap<>();
        map.put(new IdPredicate(Dummmmmmy.res("true")), COLOR_TRUE);
        map.put(new IdPredicate(Dummmmmmy.res("crit")), COLOR_CRIT);
        map.put(new IdPredicate("generic"), COLOR_GENERIC);
        map.put(new IdPredicate("trident"), COLOR_TRIDENT);
        map.put(new IdPredicate("dragon_breath"), COLOR_DRAGON);
        map.put(new IdPredicate("sonic_boom"), COLOR_WARDEN);
        map.put(new IdPredicate("attributeslib:bleeding"), COLOR_BLEED);
        map.put(new TagPredicate(Dummmmmmy.res("is_explosion")), COLOR_EXPLOSION);
        map.put(new TagPredicate(Dummmmmmy.res("is_cold")), COLOR_FREEZING);
        map.put(new TagPredicate(Dummmmmmy.res("is_thorn")), COLOR_CACTUS);
        map.put(new TagPredicate(Dummmmmmy.res("is_fire")), COLOR_FIRE);
        map.put(new TagPredicate(Dummmmmmy.res("is_wither")), COLOR_WITHER);
        map.put(new TagPredicate(DamageTypeTags.IS_LIGHTNING), COLOR_LIGHTNING);
        map.put(new TagPredicate(DamageTypeTags.IS_DROWNING), COLOR_WATER);
        map.put(new TagPredicate(DamageTypeTags.WITCH_RESISTANT_TO), COLOR_IND_MAGIC);

        DAMAGE_TO_COLORS = builder.comment("Add here custom colors (in hex format) to associate with your damage types. This is a map from damage source ID to a color where you can add new entries for each")
                .defineObject("damage_number_colors", () -> map,
                        Codec.unboundedMap(IdOrTagPredicate.CODEC, ColorUtil.CODEC));


        builder.pop();

        SPEC = builder.buildAndRegister();
    }

    // suboptimal but eh
    public static int getDamageColor(ResourceLocation damageTypeId) {
        var values = ClientConfigs.DAMAGE_TO_COLORS.get();
        var opt = Utils.hackyGetRegistry(Registries.DAMAGE_TYPE)
                .getHolder(ResourceKey.create(Registries.DAMAGE_TYPE, damageTypeId));
        if (opt.isEmpty()) {
            Dummmmmmy.LOGGER.error("Received invalid damage type: " + damageTypeId);
        } else {
            var holder = opt.get();
            for (var e : values.entrySet()) {
                if (e.getKey().test(holder)) {
                    return e.getValue();
                }
            }
        }
        return -1;
    }

    public enum SkinType {
        DEFAULT("dummy", "dummy_h"),
        ORIGINAL("dummy_1", "dummy_1"),
        DUNGEONS("dummy_3", "dummy_3_h"),
        ALTERNATIVE("dummy_2", "dummy_2_h");

        private final ResourceLocation texture;
        private final ResourceLocation shearedTexture;

        SkinType(String name, String shearedName) {
            texture = new ResourceLocation(Dummmmmmy.MOD_ID + ":textures/entity/" + name + ".png");
            shearedTexture = new ResourceLocation(Dummmmmmy.MOD_ID + ":textures/entity/" + shearedName + ".png");
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
            this(new ResourceLocation(name));
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
