package com.example.verity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = VerityMod.MODID)
public class ChreyEvents {

    /** When you talk in chat near your own tamed Chery, it answers through OpenRouter. */
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        List<ChreyEntity> dogs = player.serverLevel().getEntitiesOfClass(
                ChreyEntity.class, player.getBoundingBox().inflate(16.0D),
                dog -> dog.isTame() && dog.isOwnedBy(player));
        if (dogs.isEmpty()) {
            return;
        }
        Optional<String> key = OpenRouterClient.readApiKey();
        if (key.isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    "<Chery> کلید OpenRouter پیدا نشد. داخل config/verity-openrouter.txt بگذارش"));
            return;
        }
        MinecraftServer server = player.getServer();
        String prompt = "You are Chery, a loyal white dog in Minecraft and the best friend of the player. "
                + "Reply to the player in Persian, in one short cheerful sentence. The player says: "
                + event.getMessage();
        OpenRouterClient.ask(key.get(), prompt)
                .thenAccept(reply -> server.execute(
                        () -> player.sendSystemMessage(Component.literal("<Chery> " + reply))));
    }
}
