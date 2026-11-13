package com.example.verity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.loading.FMLPaths;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/** Minimal OpenRouter chat client. The API key is read from the config folder, never from code. */
public final class OpenRouterClient {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String MODEL = "openrouter/auto";
    private static final String ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";

    private OpenRouterClient() {}

    /** Reads config/verity-openrouter.txt in the game folder; the first non-empty line is the key. */
    public static Optional<String> readApiKey() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("verity-openrouter.txt");
        try {
            if (!Files.exists(path)) {
                return Optional.empty();
            }
            String key = Files.readString(path, StandardCharsets.UTF_8).trim();
            return key.isEmpty() ? Optional.empty() : Optional.of(key);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** One-shot prompt, kept for the right-click greeting. */
    public static CompletableFuture<String> ask(String apiKey, String prompt) {
        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        messages.add(message);
        return askMessages(apiKey, messages, 80);
    }

    /** Sends a full message list (system/user/assistant turns) and returns the reply text. */
    public static CompletableFuture<String> askMessages(String apiKey, JsonArray messages, int maxTokens) {
        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        body.addProperty("max_tokens", maxTokens);
        body.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        return "(OpenRouter error " + response.statusCode() + ")";
                    }
                    JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                    return root.getAsJsonArray("choices").get(0).getAsJsonObject()
                            .getAsJsonObject("message").get("content").getAsString().trim();
                })
                .exceptionally(e -> "(request failed)");
    }
}
