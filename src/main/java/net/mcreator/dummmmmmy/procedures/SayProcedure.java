package net.mcreator.dummmmmmy.procedures;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.minecraft.world.IWorld;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;

import net.mcreator.dummmmmmy.DummmmmmyModElements;

import java.util.Map;

@DummmmmmyModElements.ModElement.Tag
public class SayProcedure extends DummmmmmyModElements.ModElement {
	public SayProcedure(DummmmmmyModElements instance) {
		super(instance, 6);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure Say!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure Say!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure Say!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure Say!");
			return;
		}
		double x = dependencies.get("x") instanceof Integer ? (int) dependencies.get("x") : (double) dependencies.get("x");
		double y = dependencies.get("y") instanceof Integer ? (int) dependencies.get("y") : (double) dependencies.get("y");
		double z = dependencies.get("z") instanceof Integer ? (int) dependencies.get("z") : (double) dependencies.get("z");
		IWorld world = (IWorld) dependencies.get("world");
		if ((world.isAirBlock(new BlockPos((int) x, (int) y, (int) z)))) {
			{
				MinecraftServer mcserv = ServerLifecycleHooks.getCurrentServer();
				if (mcserv != null)
					mcserv.getPlayerList().sendMessage(new StringTextComponent("Message"));
			}
		}
	}
}
