package net.mangolise.chaospillars.feats;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.mangolise.chaospillars.ChaosPillars;
import net.mangolise.chaospillars.KillDeathMessage;
import net.mangolise.combat.events.PlayerKilledEvent;
import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class PlayerDeathFeature implements Game.Feature<ChaosPillars> {
    private ChaosPillars game;

    private void playerKilled(PlayerKilledEvent e) {
        Player victim = e.victim();
        Player killer = e.killer();

        game.getRemainingPlayers().remove(victim);

        if (killer != null) {
            KillDeathMessage.playerMessage(killer, victim, "KILL! ", NamedTextColor.GREEN);
            killer.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 0.8f, 1f));

            KillDeathMessage.playerMessage(victim, killer, "DEATH! ", NamedTextColor.RED);
        } else {
            KillDeathMessage.playerMessage(victim, victim, "DEATH! ", NamedTextColor.RED);
        }

        victim.setGameMode(GameMode.SPECTATOR);
        victim.setInvisible(true);
        victim.teleport(new Pos(0.5, 70, 0.5));
        victim.playSound(Sound.sound(SoundEvent.BLOCK_ANVIL_LAND, Sound.Source.PLAYER, 0.8f, 0.5f));

        victim.showTitle(Title.title(Component.text("You Died!").color(NamedTextColor.RED),
                Component.text(""), Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(700), Duration.ofMillis(550))));

        if (game.getRemainingPlayers().size() == 1) {
            Player winner = game.getRemainingPlayers().stream().findFirst().get();

            winner.showTitle(Title.title(Component.text("You WIN!").color(NamedTextColor.GREEN),
                    Component.text(""), Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1000), Duration.ofMillis(600))));
        }
    }

    @Override
    public void setup(Context<ChaosPillars> context) {
        this.game = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerKilledEvent.class, this::playerKilled);
    }
}
