package com.hemanda.chatfilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.util.text.ITextComponent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class ChatFilterStorage {
    private static final String DEFAULT_CHANNEL = "__ALL__";
    private static final Gson GSON = new Gson();
    private static final File FILE = new File("config/chatfilter.json");

    private static List<String> filters = new ArrayList<>();
    private static int currentIndex = -1;

    private static final Map<String, List<ITextComponent>> channelMessages = new HashMap<>();

    public static void addToChannel(String prefix, ITextComponent message) {
        if (prefix == null) prefix = DEFAULT_CHANNEL;
        channelMessages.computeIfAbsent(prefix, k -> new ArrayList<>()).add(message);
    }


    public static void addMessage(String prefix, ITextComponent message) {
         if (prefix == null) prefix = DEFAULT_CHANNEL;
        channelMessages.computeIfAbsent(prefix, k -> new ArrayList<>()).add(message);
    }

    // Get messages to display for the current filter
    public static List<ITextComponent> getMessagesForCurrentFilter() {
        String active = getCurrentFilter();
        
        if (active == null) {
            // Show only unfiltered messages
            return channelMessages.getOrDefault(DEFAULT_CHANNEL, Collections.emptyList());
        } else {
            // Show messages for the active filter
            return channelMessages.getOrDefault(active, Collections.emptyList());
        }
    }


    // --- Accessors ---
    public static List<String> getFilters() {
        return filters;
    }

    public static String getCurrentFilter() {
        if (currentIndex == -1 || currentIndex >= filters.size()) {
            return null; // no filter
        }
        return filters.get(currentIndex);
    }

    public static void setCurrentFilter(String filter) {
        currentIndex = filters.indexOf(filter);
    }

    public static void nextFilter() {
        if (filters.isEmpty()) {
            currentIndex = -1;
            return;
        }
        currentIndex = (currentIndex + 1) % (filters.size() + 1);
    }

    public static void previousFilter() {
        if (filters.isEmpty()) {
            currentIndex = -1;
            return;
        }
        currentIndex = (currentIndex - 1 + (filters.size() + 1)) % (filters.size() + 1);
    }

    public static void addFilter(String prefix) {
        if (!filters.contains(prefix)) {
            filters.add(prefix);
            save();
        }
    }

    public static void removeFilter(String prefix) {
        filters.remove(prefix);
        save();
    }

    // --- Persistence ---
    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                Type listType = new TypeToken<List<String>>() {}.getType();
                filters = GSON.fromJson(reader, listType);
                if (filters == null) filters = new ArrayList<>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(filters, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
