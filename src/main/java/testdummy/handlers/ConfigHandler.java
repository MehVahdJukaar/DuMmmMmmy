package testdummy.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import testdummy.TestDummy;

@Config(modid = TestDummy.MODID)
public class ConfigHandler {

    @Config.Comment("Server-Side Options")
    @Config.Name("Server Options")
    public static final ServerConfig server = new ServerConfig();

    public static class ServerConfig {

        @Config.Comment("How many ticks it will take for the dummy to show DPS")
        @Config.Name("Ticks until DPS")
        public int ticksUntilDPS = 50;

        @Config.Comment("Output message (DPS = dmg per second, AVG = average damage per hit, HPS = hits per second, MAX = max dmg of a single hit")
        @Config.Name("Output message")
        public String outputMessage = "DPS";

        @Config.Comment("Send chat msg with all tracked dmg stats")
        @Config.Name("Send full msg in chat")
        public boolean sendMsgInChat = true;

        @Config.Comment("Show hearts instead of damage")
        @Config.Name("Show Hearts")
        public boolean showHearts = false;

        @Config.Comment("Fire Forge LivingHurtEvent and add result to tracked dmg")
        @Config.Name("Use LivingHurtEvent")
        public boolean useLivingHurtEvent = true;

        @Config.Comment("Fire Forge LivingDamageEvent and add result to tracked dmg")
        @Config.Name("Use LivingDamageEvent")
        public boolean useLivingDamageEvent = true;

        @Config.Comment("Apply Armor calculation (applyArmorCalculations) to tracked dmg")
        @Config.Name("Use armor calc")
        public boolean useArmorCalc = true;

        @Config.Comment("Apply Resistance calculation (applyPotionDamageCalculations) to tracked dmg")
        @Config.Name("Use resistance calc")
        public boolean useResistanceCalc = true;

        @Config.Comment("Apply invincibility frame calculation to tracked dmg")
        @Config.Name("Use invincibility frames")
        public boolean useIframes = true;

        @Config.Comment("Add dmg that was done during the hit on the side without an actual extra hit to tracked dmg")
        @Config.Name("Track added dmg no event")
        public boolean useNoEventDmg = true;

        @Config.Comment("Whether the dummy entity is considered \"alive\", meaning it can be targeted by certain monsters.")
        @Config.Name("Dummy is alive")
        public boolean isAlive = false;

    }

    @Mod.EventBusSubscriber(modid = TestDummy.MODID)
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(TestDummy.MODID)) {
                ConfigManager.sync(TestDummy.MODID, Config.Type.INSTANCE);
            }
        }
    }
}