package net.mangolise.chaospillars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.mangolise.gamesdk.util.ChatUtil;
import net.minestom.server.entity.Player;

public class KillDeathMessage {
    public static void playerMessage(Player killer, Player victim, String text, NamedTextColor color) {
        killer.sendMessage(Component.text(text).decorate(TextDecoration.BOLD).color(color)
                .append(ChatUtil.getDisplayName(victim).decoration(TextDecoration.BOLD, false)));
    }
}
