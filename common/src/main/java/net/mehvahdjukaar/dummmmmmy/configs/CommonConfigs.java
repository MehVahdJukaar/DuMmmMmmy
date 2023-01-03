package net.mehvahdjukaar.dummmmmmy.configs;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommonConfigs {

    public static void init() {
    }

    public static final ConfigSpec SPEC;

    public static final Supplier<List<String>> WHITELIST;
    public static final Supplier<List<String>> BLACKLIST;
    public static final Supplier<Integer> RADIUS;
    public static final Supplier<Boolean> DAMAGE_EQUIPMENT;
    public static final Supplier<DpsMode> DYNAMIC_DPS;
    public static final Supplier<Integer> MAX_COMBAT_INTERVAL;

    static {
        ConfigBuilder builder = ConfigBuilder.create(Dummmmmmy.res("common"), ConfigType.COMMON);


        builder.push("scarecrow").comment("Equip a dummy with a pumpkin to make hit act as a scarecrow");

        WHITELIST = builder.comment("All animal entities will be scared. add here additional ones that are not included")
                .define("mobs_whitelist", Collections.singletonList(""));
        BLACKLIST = builder.comment("Animal entities that will not be scared")
                .define("mobs_blacklist", Collections.singletonList(""));

        RADIUS = builder.comment("Scaring radius").define("scare_radius", 12, 0, 100);

        builder.pop();
        //TODO: move to client...
        DYNAMIC_DPS = builder.comment("Does dps message update dynamically or will it only appear after each parse? ")
                .define("DPS_mode", DpsMode.DYNAMIC);

        DAMAGE_EQUIPMENT = builder.comment("Enable this to prevent your equipment from getting damaged when attacking the dummy")
                .define("disable_equipment_damage", true);

        MAX_COMBAT_INTERVAL = builder.comment("Time in ticks that it takes for a dummy to be considered out of combat after having recieved damage")
                .define("maximum_out_of_combat_interval", 6 * 20, 20, 1000);


        SPEC = builder.buildAndRegister();
    }

    public enum DpsMode {
        DYNAMIC,
        STATIC,
        OFF
    }
}
