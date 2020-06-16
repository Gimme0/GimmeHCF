package me.gimme.gimmehcf.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Strings;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class ActionBarSendingUtil {

    /**
     * Sends an action bar message to the player.
     *
     * @param player the player to send the message to
     * @param message the message to send
     * @return if the message was sent successfully
     */
    public static boolean sendActionBar(Player player, String message) {
        if (Strings.isNullOrEmpty(message)) return true;

        PacketContainer chat = new PacketContainer(PacketType.Play.Server.CHAT);
        chat.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
        chat.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, chat);
            return true;
        } catch (InvocationTargetException e) {
            return false;
        }
    }

}
