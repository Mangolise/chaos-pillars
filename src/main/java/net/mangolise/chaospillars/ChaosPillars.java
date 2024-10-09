package net.mangolise.chaospillars;

import net.mangolise.chaospillars.feats.PlayerDeathFeature;
import net.mangolise.chaospillars.feats.SpawnEggFeature;
import net.mangolise.combat.CombatConfig;
import net.mangolise.combat.MangoCombat;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.features.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;

import java.util.*;

public class ChaosPillars extends BaseGame<ChaosPillars.Config> {
    public Instance instance;

    private final Set<Player> remainingPlayers = new HashSet<>(config().players);

    public ChaosPillars(Config config) {
        super(config);
    }

    @Override
    public void setup() {
        super.setup();

        instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.enableAutoChunkLoad(true);
        instance.setChunkSupplier(LightingChunk::new);

        PlayerSpawnHelper.spawnPlayers(instance, remainingPlayers);
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            MangoCombat.enableGlobal(CombatConfig.create().withFakeDeath(true).withVoidDeath(true));
        });
    }

    public Set<Player> getRemainingPlayers() {
        return remainingPlayers;
    }

    @Override
    public List<Feature<?>> features() {
        return List.of(
                new AdminCommandsFeature(),
                new ItemDropFeature(),
                new ItemPickupFeature(),
                new EnderChestFeature(),
                new LiquidFeature(),
                new NoCollisionFeature(),
                new PlayerDeathFeature(),
                new SpawnEggFeature()
        );
    }

    public record Config(Set<Player> players) { }
}
