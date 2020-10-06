package net.mcreator.dummmmmmy.procedures;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.server.MinecraftServer;

import net.mcreator.dummmmmmy.DummmmmmyModElements;

import java.util.Map;

@DummmmmmyModElements.ModElement.Tag
public class SaywProcedure extends DummmmmmyModElements.ModElement {
	public SaywProcedure(DummmmmmyModElements instance) {
		super(instance, 10);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		{
			MinecraftServer mcserv = ServerLifecycleHooks.getCurrentServer();
			if (mcserv != null)
				mcserv.getPlayerList().sendMessage(new StringTextComponent("Message"));
		}
	}
}
