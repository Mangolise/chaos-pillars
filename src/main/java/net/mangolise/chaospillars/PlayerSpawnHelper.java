package net.mangolise.chaospillars;

import net.mangolise.chaospillars.feats.ItemSpawning;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Random;
import java.util.Set;

public class PlayerSpawnHelper {
    public static void spawnPlayers(Instance instance, Set<Player> players) {
        Random random = new Random();

        int radius = random.nextInt(7, 12);
        int y = 69;
        double funny = Math.PI;

        for (Player player : players) {
            Vec spawnCircle = new Vec(radius, y, 0).rotateAroundY(funny).add(0.5, 0, 0.5);

            ItemSpawning.setupItemSpawning(player);

            for (int i = 0; i < 10; i++) {
                spawnCircle = spawnCircle.withY(y);
                instance.setBlock(spawnCircle, Block.STONE);
                y--;
            }

            funny += Math.PI;
            y = 69;

            Pos playerSpawn = new Pos(new BlockVec(spawnCircle.x(), 70, spawnCircle.z())).add(0.5, 0, 0.5);

            player.setInstance(instance, playerSpawn.withDirection(playerSpawn.asVec().neg().withY(0).normalize()));
            player.setRespawnPoint(new Pos(0.5, 70, 0.5));
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
}
