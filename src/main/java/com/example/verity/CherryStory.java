package com.example.verity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * The Cherry story, saved per player (in the player's persistent data):
 * 1. Join: quest to collect 64 bones.
 * 2. Quest done: Cherry appears behind the player. Feed her 16 bones to befriend her.
 * 3. Two in-game days later she turns into the monster.
 * 4. A wolf named "Tom" within 16 blocks of the monster gets killed.
 * 5. Two days later the monster hunts the player. Killing the player ends the story.
 * Chat: "!reset" restarts the story, "!skip" moves it one day forward (for testing).
 */
@Mod.EventBusSubscriber(modid = VerityMod.MODID)
public final class CherryStory {

    public static final int STAGE_QUEST = 0;
    public static final int STAGE_FEED = 1;
    public static final int STAGE_FRIEND = 2;
    public static final int STAGE_MONSTER = 3;
    public static final int STAGE_RAGE = 4;
    public static final int STAGE_HUNT = 5;
    public static final int STAGE_END = 6;

    /** Test build: quick story (1 bone, no Tom, no waiting). Set to false for the full story. */
    private static final boolean TEST_MODE = true;

    private static final int BONES_FOR_QUEST = TEST_MODE ? 1 : 64;
    private static final int BONES_TO_BEFRIEND = TEST_MODE ? 1 : 16;
    private static final long DAY_TICKS = 24000L;
    private static final double DAYS_TO_MONSTER = TEST_MODE ? 0.0D : 2.0D;
    private static final double DAYS_TO_HUNT = TEST_MODE ? 0.0D : 2.0D;
    private static final double SEARCH_RANGE = 128.0D;

    private static final String KEY_STAGE = "cherry_stage";
    private static final String KEY_START = "cherry_stage_start";
    private static final String KEY_FED = "cherry_bones_fed";

    private CherryStory() {
    }

    public static int stage(ServerPlayer player) {
        return player.getPersistentData().getInt(KEY_STAGE);
    }

    private static void setStage(ServerPlayer player, int stage) {
        CompoundTag data = player.getPersistentData();
        data.putInt(KEY_STAGE, stage);
        data.putLong(KEY_START, player.level().getGameTime());
    }

    private static double daysSinceStageStart(ServerPlayer player) {
        long elapsed = player.level().getGameTime() - player.getPersistentData().getLong(KEY_START);
        return elapsed / (double) DAY_TICKS;
    }

    private static void say(ServerPlayer player, String text) {
        player.sendSystemMessage(Component.literal("[Cherry Story] " + text));
    }

    /** Called by CherryEntity every time the player feeds her a bone during the feeding stage. */
    public static void countBone(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        int fed = data.getInt(KEY_FED) + 1;
        data.putInt(KEY_FED, fed);
        if (fed >= BONES_TO_BEFRIEND) {
            setStage(player, STAGE_FRIEND);
            say(player, "Cherry alan doostet e! Chand rooz dige ye ettefaghi barash mioftad...");
        } else {
            say(player, "Cherry ostokhan khord. (" + fed + "/" + BONES_TO_BEFRIEND + ")");
        }
    }

    private static int countBones(ServerPlayer player) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.BONE)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (stage(player) == STAGE_QUEST) {
            player.sendSystemMessage(Component.literal(
                    "[Mamooriat] " + BONES_FOR_QUEST + " ta ostokhan jam kon."));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player) || player.tickCount % 20 != 0) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        switch (stage(player)) {
            case STAGE_QUEST -> {
                if (countBones(player) >= BONES_FOR_QUEST) {
                    completeQuest(player, level);
                }
            }
            case STAGE_FRIEND -> {
                if (daysSinceStageStart(player) >= DAYS_TO_MONSTER) {
                    turnIntoMonster(player, level);
                }
            }
            case STAGE_MONSTER -> checkTom(player, level);
            case STAGE_RAGE -> {
                if (daysSinceStageStart(player) >= DAYS_TO_HUNT) {
                    startHunt(player, level);
                }
            }
            default -> {
            }
        }
    }

    private static void completeQuest(ServerPlayer player, ServerLevel level) {
        Vec3 look = player.getLookAngle();
        CherryEntity cherry = VerityMod.CHERRY.get().create(level);
        if (cherry == null) {
            return;
        }
        cherry.moveTo(player.getX() - look.x * 3.0D, player.getY(), player.getZ() - look.z * 3.0D,
                player.getYRot(), 0.0F);
        level.addFreshEntity(cherry);
        setStage(player, STAGE_FEED);
        say(player, "Mamooriat tamoom shod! Cherry posht-e sarat zaher shod. "
                + "Ba raste-kelik ostokhan bedeh ta doostet beshe (0/" + BONES_TO_BEFRIEND + ").");
    }

    private static void turnIntoMonster(ServerPlayer player, ServerLevel level) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYRot();

        List<CherryEntity> dogs = level.getEntitiesOfClass(CherryEntity.class,
                player.getBoundingBox().inflate(SEARCH_RANGE));
        if (!dogs.isEmpty()) {
            CherryEntity first = dogs.get(0);
            x = first.getX();
            y = first.getY();
            z = first.getZ();
            yaw = first.getYRot();
        }
        for (CherryEntity dog : dogs) {
            dog.discard();
        }

        CherryMonsterEntity monster = VerityMod.CHERRY_MONSTER.get().create(level);
        if (monster == null) {
            return;
        }
        monster.moveTo(x, y, z, yaw, 0.0F);
        level.addFreshEntity(monster);
        setStage(player, STAGE_MONSTER);
        say(player, "Cherry dare avaz mishe... cheshmash ghermez shod. Havasset bashe!");
    }

    private static void checkTom(ServerPlayer player, ServerLevel level) {
        if (TEST_MODE) {
            setStage(player, STAGE_RAGE);
            say(player, "(Test) Tom skipped.");
            return;
        }
        for (CherryMonsterEntity monster : level.getEntitiesOfClass(CherryMonsterEntity.class,
                player.getBoundingBox().inflate(SEARCH_RANGE))) {
            for (Wolf wolf : level.getEntitiesOfClass(Wolf.class, monster.getBoundingBox().inflate(16.0D))) {
                Component name = wolf.getCustomName();
                if (name != null && name.getString().trim().equals("Tom")) {
                    wolf.kill();
                    setStage(player, STAGE_RAGE);
                    say(player, "Cherry asabani shod va Tom ro kosht!");
                    return;
                }
            }
        }
    }

    private static void startHunt(ServerPlayer player, ServerLevel level) {
        for (CherryMonsterEntity monster : level.getEntitiesOfClass(CherryMonsterEntity.class,
                player.getBoundingBox().inflate(SEARCH_RANGE))) {
            monster.setHunting(true);
        }
        setStage(player, STAGE_HUNT);
        say(player, "Cherry dombale toe...");
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof CherryMonsterEntity)) {
            return;
        }
        if (stage(player) != STAGE_HUNT) {
            return;
        }
        setStage(player, STAGE_END);
        say(player, "Cherry toro kosht. Dastan tamoom shod. (Baraye shoroo-e dobare dar chat benevis: !reset)");
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String text = event.getRawText().trim().toLowerCase(Locale.ROOT);

        if (text.equals("!reset")) {
            event.setCanceled(true);
            reset(player);
            return;
        }
        if (text.equals("!skip")) {
            event.setCanceled(true);
            CompoundTag data = player.getPersistentData();
            data.putLong(KEY_START, data.getLong(KEY_START) - DAY_TICKS);
            say(player, "(Test) Yek rooz jolo raftim.");
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        CherryEntity cherry = level.getEntitiesOfClass(CherryEntity.class,
                        player.getBoundingBox().inflate(12.0D)).stream()
                .min(Comparator.comparingDouble(c -> c.distanceToSqr(player)))
                .orElse(null);
        if (cherry != null) {
            answerChat(cherry, player, text);
        }
    }

    private static void answerChat(CherryEntity cherry, ServerPlayer player, String text) {
        String line;
        if (containsAny(text, "\u0628\u0634\u06cc\u0646", "sit")) {
            cherry.setState(CherryEntity.SIT);
            line = pick("Bashe, neshastam.", "Neshastam! *neshast*");
        } else if (containsAny(text, "\u0628\u062e\u0648\u0627\u0628", "sleep")) {
            cherry.setState(CherryEntity.SLEEP);
            line = pick("Bashe, ye chorti mizanam... zzz", "Khabam miad... zzz");
        } else if (containsAny(text, "\u0628\u0644\u0646\u062f \u0634\u0648", "\u0628\u06cc\u0627", "stand", "come")) {
            cherry.setState(CherryEntity.STAND);
            line = pick("Oomadam!", "Bashe, bolandam!");
        } else if (containsAny(text, "\u0633\u0644\u0627\u0645", "hi", "hello", "hey", "\u0647\u06cc")) {
            line = pick("Hav! Salam!", "Arf! Salam, rafigh!");
        } else if (containsAny(text, "\u062e\u0648\u0628\u06cc", "\u0686\u0637\u0648\u0631\u06cc", "how are you")) {
            line = pick("Man khoobam, dommam takon mikhore!", "Aali! Bazi konim?");
        } else if (containsAny(text, "\u062f\u0648\u0633\u062a\u062a \u062f\u0627\u0631\u0645", "love you", "love")) {
            line = "Arf! Man ham dooset daram!";
        } else {
            line = pick("Arf!", "Haa?", "*dommesh ro takon mide*", "Gof gof!");
        }
        player.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("<Cherry> " + line), false);
    }

    private static boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private static String pick(String... options) {
        return options[(int) (Math.random() * options.length)];
    }

    private static void reset(ServerPlayer player) {
        if (player.level() instanceof ServerLevel level) {
            for (CherryEntity cherry : level.getEntitiesOfClass(CherryEntity.class,
                    player.getBoundingBox().inflate(SEARCH_RANGE))) {
                cherry.discard();
            }
            for (CherryMonsterEntity monster : level.getEntitiesOfClass(CherryMonsterEntity.class,
                    player.getBoundingBox().inflate(SEARCH_RANGE))) {
                monster.discard();
            }
        }
        player.getPersistentData().putInt(KEY_FED, 0);
        setStage(player, STAGE_QUEST);
        say(player, "Dastan reset shod.");
    }
}
