package net.mehvahdjukaar.dummmmmmy.configs;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;
import net.minecraft.world.BossEvent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommonConfigs {


    public static void init() {
    }

    public static final ModConfigHolder SPEC;

    public static final Supplier<Boolean> SCARECROW;
    public static final Supplier<List<String>> WHITELIST;
    public static final Supplier<List<String>> BLACKLIST;
    public static final Supplier<Integer> RADIUS;
    public static final Supplier<Boolean> DAMAGE_EQUIPMENT;
    public static final Supplier<Boolean> DECOY;
    public static final Supplier<BossEvent.BossBarColor> BOSS_HEALTH_COLOR;
    public static final Supplier<DpsMode> DYNAMIC_DPS;
    public static final Supplier<Mode> DAMAGE_NUMBERS_MODE;
    public static final Supplier<Mode> HEALING_NUMBERS_MODE;
    public static final Supplier<Double> DROP_XP;
    public static final Supplier<Integer> BOSS_HEALTH;

    public enum Mode {
        ALL_ENTITIES, ALL_PLAYERS, LOCAL_PLAYER, NONE;
    }

    static {
        ConfigBuilder builder = ConfigBuilder.create(Dummmmmmy.res("common"), ConfigType.COMMON);


        builder.icon("minecraft:carved_pumpkin").push("scarecrow");
        SCARECROW = builder.comment("Equip a dummy with a pumpkin to make it act as a scarecrow, keeping animals away and stopping mobs from spawning around it")
                .mainFeature();
        WHITELIST = builder.comment("All animal entities will be scared. add here additional ones that are not included")
                .define("mobs_whitelist", Collections.singletonList(""));
        BLACKLIST = builder.comment("Animal entities that will not be scared")
                .define("mobs_blacklist", Collections.singletonList(""));
        RADIUS = builder.worldReload().comment("Scaring radius")
                .defineSlider("scare_radius", 12, 0, 100);
        builder.pop();

        builder.icon("target_dummy").push("general");
        //TODO: move to client...
        DYNAMIC_DPS = builder.comment("Does dps message update dynamically or will it only appear after each parse?")
                .define("DPS_mode", DpsMode.DYNAMIC);

        DAMAGE_EQUIPMENT = builder.comment("Enable this to prevent your equipment from getting damaged when attacking the dummy")
                .define("disable_equipment_damage", true);

        DROP_XP = builder.comment("Makes dummy drop xp when hit. Training yay! Depends on damage done")
                .define("xp_per_damage_on_hit", 0d, 0d, 100d);

        DECOY = builder.icon("minecraft:player_head").worldReload()
                .comment("Makes monsters target a dummy that is wearing a player head")
                .feature("dummy_decoy", false);
        builder.pop();

        builder.icon("minecraft:white_banner").push("boss_mode");
        BOSS_HEALTH = builder.gameRestart().comment("How much health the dummy should have when in boss mode (when given a banner)")
                .define("health", 200, 1, 1000000);
        BOSS_HEALTH_COLOR = builder.comment("Health bar color when in boss mode")
                .define("health_bar_color", BossEvent.BossBarColor.YELLOW);
        builder.pop();

        builder.icon("minecraft:zombie_head").push("mobs_damage_numbers");
        DAMAGE_NUMBERS_MODE = builder.comment("Show damage taken by regular mobs too, not just by dummies")
                .define("damage_mode", Mode.NONE);
        HEALING_NUMBERS_MODE = builder.comment("Show healing received by regular mobs")
                .define("healing_mode", Mode.NONE);
        builder.pop();

        SPEC = builder.build();
        SPEC.forceLoad();
    }

    public enum DpsMode {
        DYNAMIC,
        STATIC,
        OFF
    }
}
