package com.example.verity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chat with a nearby Verity by typing. Start a message with "ویریتی " (or "verity ")
 * and Verity answers in Persian. Each player keeps a short conversation history.
 */
public final class VerityChat {

    private static final String PREFIX_FA = "ویریتی ";
    private static final String PREFIX_EN = "verity ";
    private static final double RANGE = 16.0D;
    private static final int MAX_HISTORY = 10;
    private static final int MAX_TOKENS = 200;
    private static final String SYSTEM_PROMPT =
            "You are Verity, a friendly yellow smiley ball creature in Minecraft. "
                    + "Always reply in Persian (Farsi), in one or two short, cheerful sentences.";

    private static final Map<UUID, Deque<JsonObject>> HISTORY = new ConcurrentHashMap<>();

    private VerityChat() {}

    public static void onChat(ServerChatEvent event) {
        String raw = event.getMessage().trim();
        String text;
        if (raw.regionMatches(true, 0, PREFIX_EN, 0, PREFIX_EN.length())) {
            text = raw.substring(PREFIX_EN.length()).trim();
        } else if (raw.startsWith(PREFIX_FA)) {
            text = raw.substring(PREFIX_FA.length()).trim();
        } else {
            return;
        }

        ServerPlayer player = event.getPlayer();
        List<?> nearby = player.serverLevel().getEntitiesOfClass(
                VerityEntity.class, player.getBoundingBox().inflate(RANGE));
        if (nearby.isEmpty()) {
            return; // no Verity close by: let the message go through as normal chat
        }
        event.setCanceled(true);

        if (text.isEmpty()) {
            return;
        }

        Optional<String> key = OpenRouterClient.readApiKey();
        if (key.isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    "<Verity> کلید OpenRouter پیدا نشد. آن را در config/verity-openrouter.txt بگذارید."));
            return;
        }

        Deque<JsonObject> history = HISTORY.computeIfAbsent(player.getUUID(), id -> new ArrayDeque<>());
        JsonArray messages = new JsonArray();
        messages.add(turn("system", SYSTEM_PROMPT));
        for (JsonObject past : history) {
            messages.add(past);
        }
        messages.add(turn("user", text));

        MinecraftServer server = player.getServer();
        OpenRouterClient.askMessages(key.get(), messages, MAX_TOKENS)
                .thenAccept(reply -> server.execute(() -> {
                    remember(player.getUUID(), turn("user", text));
                    remember(player.getUUID(), turn("assistant", reply));
                    player.sendSystemMessage(Component.literal("<Verity> " + reply));
                }));
    }

    private static JsonObject turn(String role, String content) {
        JsonObject o = new JsonObject();
        o.addProperty("role", role);
        o.addProperty("content", content);
        return o;
    }

    private static void remember(UUID player, JsonObject message) {
        Deque<JsonObject> history = HISTORY.computeIfAbsent(player, id -> new ArrayDeque<>());
        history.addLast(message);
        while (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
    }
}
