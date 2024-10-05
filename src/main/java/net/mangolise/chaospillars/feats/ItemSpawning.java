package net.mangolise.chaospillars.feats;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;
import java.util.Random;

public class ItemSpawning {
    public static void setupItemSpawning(Player player) {
        Random random = new Random();
        List<Material> allMaterials = List.copyOf(Material.values());
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            player.getInventory().addItemStack(ItemStack.of(allMaterials.get(random.nextInt(0, allMaterials.size()))));

            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                return TaskSchedule.stop();
            } else {
                return TaskSchedule.seconds(random.nextInt(4, 5));
            }

        }, TaskSchedule.immediate());
    }
}
