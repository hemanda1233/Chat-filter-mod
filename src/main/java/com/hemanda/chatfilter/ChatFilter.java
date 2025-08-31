package com.hemanda.chatfilter;

import net.minecraft.block.Block;
// import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
// import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
// import java.util.stream.Collectors;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import net.minecraft.util.text.ITextComponent;
// import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("chatfilter")
public class ChatFilter
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static KeyBinding nextFilterKey;
    public static KeyBinding prevFilterKey;
    public static KeyBinding openFilterGuiKey;

    public ChatFilter() {
        // Temporary test filters


        // Optional: set the first filter active
        ChatFilterStorage.nextFilter(); // activates the first filter

        
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        ChatFilterStorage.load();
    }

    

    

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("ChatFilter setup complete");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        openFilterGuiKey = new KeyBinding("key.chatfilter.open_gui", GLFW.GLFW_KEY_BACKSLASH, "key.categories.chatfilter");
        nextFilterKey = new KeyBinding("key.chatfilter.next", GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.chatfilter");
        prevFilterKey = new KeyBinding("key.chatfilter.prev", GLFW.GLFW_KEY_LEFT_BRACKET, "key.categories.chatfilter");
        ClientRegistry.registerKeyBinding(openFilterGuiKey);
        ClientRegistry.registerKeyBinding(nextFilterKey);
        ClientRegistry.registerKeyBinding(prevFilterKey);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        LOGGER.debug("enqueueIMC called");
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.debug("processIMC called");
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        LOGGER.info("Server stopping - saving chat filters");
        // Save filters when game closes
        ChatFilterStorage.save();
    }
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("ChatFilter mod detected server starting!");
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        ITextComponent messageComponent = event.getMessage();
        String rawMessage = TextFormatting.stripFormatting(messageComponent.getString()).trim();

        String matchedPrefix = null;
        for (String prefix : ChatFilterStorage.getFilters()) {
            if (rawMessage.toLowerCase().startsWith(prefix.toLowerCase())) {
                matchedPrefix = prefix;
                break;
            }
        }

        // Save all messages: either in matched prefix or default channel
        ChatFilterStorage.addToChannel(matchedPrefix, messageComponent);

        // Cancel messages that don’t match the active filter
        String activeFilter = ChatFilterStorage.getCurrentFilter();
        if (activeFilter != null && (matchedPrefix == null || !matchedPrefix.equalsIgnoreCase(activeFilter))) {
            event.setCanceled(true);
        }
    }
        
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        ITextComponent messageComponent = event.getMessage();

        String rawMessage = TextFormatting.stripFormatting(event.getMessage().getString()).trim();
        String prefix = ChatFilterUtils.getPrefix(rawMessage);

        if (prefix != null) {
            ChatFilterStorage.addToChannel(prefix, messageComponent);
        }

        String activeFilter = ChatFilterStorage.getCurrentFilter();
        
        if (activeFilter != null) {
            if (prefix == null || !prefix.equalsIgnoreCase(activeFilter)) {
                event.setCanceled(true);
            }
        }
    }                    





    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
