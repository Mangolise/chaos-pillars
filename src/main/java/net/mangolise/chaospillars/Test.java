package net.mangolise.chaospillars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.limbo.Limbo;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.permission.Permission;

// This is a dev server, not used in production
public class Test {
    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setUuidProvider((connection, username) -> GameSdkUtils.createFakeUUID(username));

        // Simulate a player without a rank (gray name)
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, e ->
                e.getPlayer().setDisplayName(Component.text(e.getPlayer().getUsername()).color(NamedTextColor.GRAY)));

        Limbo.waitForPlayers(2).thenAccept((players) -> {
            ChaosPillars.Config config = new ChaosPillars.Config(players);
            ChaosPillars game = new ChaosPillars(config);
            game.setup();
        });

        if (GameSdkUtils.useBungeeCord()) {
            BungeeCordProxy.enable();
        }

        // give every permission to every player
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, e ->
                e.getPlayer().addPermission(new Permission("*")));

        server.start("0.0.0.0", GameSdkUtils.getConfiguredPort());
    }
}
