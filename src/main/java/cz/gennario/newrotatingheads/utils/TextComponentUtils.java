package cz.gennario.newrotatingheads.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextComponentUtils {

    public static TextComponent create(String message) {
        TextComponent textComponent = null;
        if(Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) >= 16) {
            textComponent = new TextComponent(new ComponentBuilder().appendLegacy(message).create());
        }else {
            textComponent = new TextComponent(message);
        }
        return textComponent;
    }

    /**
     * It sets the hover text of a TextComponent
     *
     * @param textComponent The TextComponent you want to set the hover text for.
     * @param hover The text to be displayed when the player hovers over the text.
     */
    public void setHover(TextComponent textComponent, String hover) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)));
    }

    /**
     * It sets the click event of a text component to a new click event with the specified action type and value
     *
     * @param textComponent The TextComponent you want to set the click event for.
     * @param actionType The type of action to perform when the player clicks the text.
     * @param value The value of the click event.
     */
    public void setClick(TextComponent textComponent, ClickEvent.Action actionType, String value) {
        textComponent.setClickEvent(new ClickEvent(actionType, value));
    }

    public static void send(Player p, TextComponent... components) {
        p.spigot().sendMessage(components);
    }

    public static void broadcast(TextComponent... components) {
        Bukkit.spigot().broadcast(components);
    }

}
