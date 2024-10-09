package net.mangolise.chaospillars.feats;

import net.mangolise.chaospillars.ChaosPillars;
import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.item.Material;

public class SpawnEggFeature implements Game.Feature<ChaosPillars> {
    private ChaosPillars game;

    private void spawnEntity(PlayerUseItemOnBlockEvent e) {
        Material material = e.getItemStack().material();
        if (!material.namespace().value().endsWith("_spawn_egg")) {
            return;
        }

        String mob = material.namespace().value().replace("_spawn_egg", "");
        EntityType type = EntityType.fromNamespaceId(mob);

        Entity entity = new Entity(type);
        entity.setInstance(game.instance, e.getPlayer().getPosition());
    }

    @Override
    public void setup(Context<ChaosPillars> context) {
        this.game = context.game();
        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemOnBlockEvent.class, this::spawnEntity);
    }
}
