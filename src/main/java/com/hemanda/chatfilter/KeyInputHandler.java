package com.hemanda.chatfilter;

import net.minecraft.client.Minecraft;
// import net.minecraft.client.gui.NewChatGui;
// import net.minecraft.client.settings.KeyBinding;
// import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
// import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = "chatfilter", value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Open GUI
        if (ChatFilter.openFilterGuiKey.consumeClick()) {
            mc.setScreen(new FilterGuiScreen());
        }

        // Cycle next filter
        if (ChatFilter.nextFilterKey.consumeClick()) {
            ChatFilterStorage.nextFilter();
            System.out.println("Switched to next filter: " + ChatFilterStorage.getCurrentFilter());

            // Refresh chat to show only messages of the active filter
            ChatFilterUtils.refreshChat();
        }

        // Cycle previous filter
        if (ChatFilter.prevFilterKey.consumeClick()) {
            ChatFilterStorage.previousFilter();
            System.out.println("Switched to previous filter: " + ChatFilterStorage.getCurrentFilter());

            // Refresh chat to show only messages of the active filter
            ChatFilterUtils.refreshChat();
        }
    }
}
