package com.hemanda.chatfilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
// import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ChatFilterUtils {

    /**
     * Refreshes the in-game chat to show only messages for the current active filter.
     */
    public static String getPrefix(String message) {
        List<String> filters = ChatFilterStorage.getFilters();
        if (message == null || message.isEmpty()) return null;
        for (String filter : filters) {
            if (message.toLowerCase().startsWith(filter.toLowerCase())) {
                return filter;
            }
        }
        return null; // no prefix matched
    }
    public static void refreshChat() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;


        // Get chat messages for current filter
        List<ITextComponent> messages = ChatFilterStorage.getMessagesForCurrentFilter();


        // Access the chat GUI directly
        NewChatGui chatGui = mc.gui.getChat();
        chatGui.clearMessages(false);

        for (ITextComponent msg : messages) {
            chatGui.addMessage(msg); // preserves formatting
        }

    }
}
