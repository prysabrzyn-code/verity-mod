package com.example.verity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VerityMod.MODID)
public class ServerEvents {

    private static final String GIVEN_KEY = "verity_box_given";

    /** Typing "ویریتی ..." near a Verity sends the message to the chat model. */
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        VerityChat.onChat(event);
    }

    /** The first time a player joins, a cardboard box appears two blocks in front of them. */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.getPersistentData().getBoolean(GIVEN_KEY)) {
            return;
        }
        Level level = player.level();
        BlockPos target = player.blockPosition().relative(player.getDirection(), 2);
        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, target);
        level.setBlock(ground, VerityMod.CARDBOARD_BOX.get().defaultBlockState(), 3);
        player.getPersistentData().putBoolean(GIVEN_KEY, true);
    }
}
