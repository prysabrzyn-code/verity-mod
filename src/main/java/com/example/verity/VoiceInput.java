package com.example.verity;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;
import com.google.gson.JsonParser;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

/**
 * Push-to-talk on the V key. Hold V to record, release to send. The speech is turned into text
 * with OpenAI Whisper (Persian) and posted to chat as "ویریتی ...", so VerityChat answers it.
 * The key is read from config/verity-speech.txt.
 */
public final class VoiceInput {

    public static final KeyMapping TALK_KEY = new KeyMapping(
            "key.verity.talk", InputConstants.Type.KEYSYM, InputConstants.KEY_V, "key.categories.verity");

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static final double RANGE = 16.0D;
    private static final float SAMPLE_RATE = 16000f;

    private static volatile boolean recording = false;
    private static TargetDataLine line;
    private static Thread captureThread;
    private static final ByteArrayOutputStream PCM = new ByteArrayOutputStream();

    private VoiceInput() {}

    /** Called every client tick. Starts recording when V goes down and sends when it comes up. */
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        boolean held = TALK_KEY.isDown() && mc.screen == null;
        if (held && !recording) {
            start(mc);
        } else if (!held && recording) {
            stop(mc);
        }
    }

    private static void start(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }
        if (mc.level.getEntitiesOfClass(VerityEntity.class, player.getBoundingBox().inflate(RANGE)).isEmpty()) {
            return; // only talk when Verity is close by
        }
        if (readKey().isEmpty()) {
            say(mc, "<Verity> کلید تبدیل گفتار پیدا نشد. آن را در config/verity-speech.txt بگذارید.");
            return;
        }
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();
        } catch (Exception e) {
            say(mc, "<Verity> میکروفون در دسترس نیست.");
            return;
        }
        PCM.reset();
        recording = true;
        captureThread = new Thread(() -> {
            byte[] chunk = new byte[4096];
            while (recording) {
                int n = line.read(chunk, 0, chunk.length);
                if (n > 0) {
                    synchronized (PCM) {
                        PCM.write(chunk, 0, n);
                    }
                }
            }
        }, "verity-mic");
        captureThread.start();
        say(mc, "<Verity> گوش می‌دهم...");
    }

    private static void stop(Minecraft mc) {
        recording = false;
        try {
            captureThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        line.stop();
        line.close();

        byte[] pcm;
        synchronized (PCM) {
            pcm = PCM.toByteArray();
        }
        if (pcm.length < 3200) { // less than 0.1 s of audio
            return;
        }
        transcribe(mc, wav(pcm), readKey().orElse(""));
    }

    private static void transcribe(Minecraft mc, byte[] wav, String key) {
        String boundary = "verity" + System.nanoTime();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        field(out, boundary, "model", "whisper-1");
        field(out, boundary, "language", "fa");
        String head = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"speech.wav\"\r\n"
                + "Content-Type: audio/wav\r\n\r\n";
        out.writeBytes(head.getBytes(StandardCharsets.UTF_8));
        out.writeBytes(wav);
        out.writeBytes(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofByteArray(out.toByteArray()))
                .build();

        HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> mc.execute(() -> handleResponse(mc, response.statusCode(), response.body())))
                .exceptionally(e -> {
                    mc.execute(() -> say(mc, "<Verity> ارتباط با سرویس تبدیل گفتار برقرار نشد."));
                    return null;
                });
    }

    private static void handleResponse(Minecraft mc, int status, String body) {
        if (status != 200) {
            say(mc, "<Verity> خطای سرویس تبدیل گفتار: " + status);
            return;
        }
        String text = JsonParser.parseString(body).getAsJsonObject().get("text").getAsString().trim();
        if (text.isEmpty()) {
            say(mc, "<Verity> صدایی شنیده نشد.");
            return;
        }
        if (mc.player != null) {
            mc.player.connection.sendChat("ویریتی " + text);
        }
    }

    private static Optional<String> readKey() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("verity-speech.txt");
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

    private static void field(ByteArrayOutputStream out, String boundary, String name, String value) {
        String part = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n";
        out.writeBytes(part.getBytes(StandardCharsets.UTF_8));
    }

    /** Wraps raw 16 kHz mono 16-bit PCM in a WAV header. */
    private static byte[] wav(byte[] pcm) {
        ByteBuffer header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN);
        header.put("RIFF".getBytes(StandardCharsets.US_ASCII)).putInt(36 + pcm.length)
                .put("WAVE".getBytes(StandardCharsets.US_ASCII))
                .put("fmt ".getBytes(StandardCharsets.US_ASCII)).putInt(16)
                .putShort((short) 1).putShort((short) 1)
                .putInt(16000).putInt(32000)
                .putShort((short) 2).putShort((short) 16)
                .put("data".getBytes(StandardCharsets.US_ASCII)).putInt(pcm.length);
        byte[] result = new byte[44 + pcm.length];
        System.arraycopy(header.array(), 0, result, 0, 44);
        System.arraycopy(pcm, 0, result, 44, pcm.length);
        return result;
    }

    private static void say(Minecraft mc, String message) {
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(message), false);
        }
    }
}
