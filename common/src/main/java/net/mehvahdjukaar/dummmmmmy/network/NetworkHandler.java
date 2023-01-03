
package net.mehvahdjukaar.dummmmmmy.network;

import net.mehvahdjukaar.dummmmmmy.Dummmmmmy;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;

public class NetworkHandler {

    public static final ChannelHandler CHANNEL = ChannelHandler.createChannel(Dummmmmmy.res("network"));

    public static void registerMessages() {

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT, ClientBoundDamageNumberMessage.class,
                ClientBoundDamageNumberMessage::new);

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT, ClientBoundUpdateAnimationMessage.class,
                ClientBoundUpdateAnimationMessage::new);

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT, ClientBoundSyncEquipMessage.class,
                ClientBoundSyncEquipMessage::new);

    }


}
