package net.mangolise.chaospillars;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.mangolise.chaospillars.feats.ItemSpawning;
import net.mangolise.combat.CombatConfig;
import net.mangolise.combat.MangoCombat;
import net.mangolise.combat.events.PlayerKilledEvent;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.features.*;
import net.mangolise.gamesdk.util.ChatUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.*;

public class ChaosPillars extends BaseGame<ChaosPillars.Config> {
    public Instance instance;

    final Set<Player> remainingPlayers = new HashSet<>(config().players);

    public ChaosPillars(Config config) {
        super(config);
    }

    @Override
    public void setup() {
        super.setup();

        instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.enableAutoChunkLoad(true);
        instance.setChunkSupplier(LightingChunk::new);

        worldAndPlayerSpawn();
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            MangoCombat.enableGlobal(CombatConfig.create().withFakeDeath(true).withVoidDeath(true));
        });
    }

    private void worldAndPlayerSpawn() {
        Random random = new Random();

        int radius = random.nextInt(7, 12);
        int y = 69;
        double funny = Math.PI;

        for (Player player : remainingPlayers) {
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

        MinecraftServer.getGlobalEventHandler().addListener(PlayerKilledEvent.class, e -> {
            Player victim = e.victim();
            Player killer = e.killer();

            remainingPlayers.remove(victim);

            if (killer != null) {
                killer.sendMessage(Component.text("KILL! ").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN)
                        .append(ChatUtil.getDisplayName(victim).decoration(TextDecoration.BOLD, false)));
                killer.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1f, 1f));

                victim.sendMessage(Component.text("DEATH! ").decorate(TextDecoration.BOLD).color(NamedTextColor.RED)
                        .append(ChatUtil.getDisplayName(killer).decoration(TextDecoration.BOLD, false)));
            } else {
                victim.sendMessage(Component.text("DEATH! ").decorate(TextDecoration.BOLD).color(NamedTextColor.RED)
                        .append(ChatUtil.getDisplayName(victim).decoration(TextDecoration.BOLD, false)));
            }

            victim.setGameMode(GameMode.SPECTATOR);
            victim.setInvisible(true);
            victim.teleport(new Pos(0.5, 70, 0.5));
            victim.playSound(Sound.sound(SoundEvent.BLOCK_ANVIL_LAND, Sound.Source.PLAYER, 1f, 0.5f));

            victim.showTitle(Title.title(Component.text("You Died!").color(NamedTextColor.RED),
                    Component.text(""), Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(700), Duration.ofMillis(550))));

            if (remainingPlayers.size() == 1) {
                Player winner = remainingPlayers.stream().findFirst().get();

                winner.showTitle(Title.title(Component.text("You WIN!").color(NamedTextColor.GREEN),
                        Component.text(""), Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1000), Duration.ofMillis(600))));
            }
        });
    }

    @Override
    public List<Feature<?>> features() {
        return List.of(
                new AdminCommandsFeature(),
                new ItemDropFeature(),
                new ItemPickupFeature(),
                new EnderChestFeature(),
                new LiquidFeature(),
                new NoCollisionFeature()
        );
    }

    public record Config(Set<Player> players) { }
}
