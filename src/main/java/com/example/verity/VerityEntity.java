package com.example.verity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import java.util.Optional;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class VerityEntity extends PathfinderMob {

    public VerityEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FollowPlayerGoal(this, 1.0D, 3.0D, 16.0D));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    /** Right-click Verity to hear a short line from an OpenRouter model. */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!(this.level() instanceof ServerLevel serverLevel) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        Optional<String> key = OpenRouterClient.readApiKey();
        if (key.isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    "<Verity> No OpenRouter key found. Put it in config/verity-openrouter.txt"));
            return InteractionResult.SUCCESS;
        }
        MinecraftServer server = serverLevel.getServer();
        OpenRouterClient.ask(key.get(),
                        "You are Verity, a friendly yellow smiley ball creature in Minecraft. "
                                + "Reply to a player in one short, cheerful sentence in Persian.")
                .thenAccept(reply -> server.execute(
                        () -> player.sendSystemMessage(Component.literal("<Verity> " + reply))));
        return InteractionResult.SUCCESS;
    }

    /** Walks toward the nearest player when they are farther than stopDistance. */
    static class FollowPlayerGoal extends Goal {
        private final VerityEntity mob;
        private final double speed;
        private final double stopDistance;
        private final double searchRange;
        private Player target;

        FollowPlayerGoal(VerityEntity mob, double speed, double stopDistance, double searchRange) {
            this.mob = mob;
            this.speed = speed;
            this.stopDistance = stopDistance;
            this.searchRange = searchRange;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.target = this.mob.level().getNearestPlayer(this.mob, this.searchRange);
            return this.target != null
                    && this.mob.distanceToSqr(this.target) > this.stopDistance * this.stopDistance;
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && this.mob.distanceToSqr(this.target) > this.stopDistance * this.stopDistance;
        }

        @Override
        public void tick() {
            this.mob.getLookControl().setLookAt(this.target, 10.0F, this.mob.getMaxHeadXRot());
            this.mob.getNavigation().moveTo(this.target, this.speed);
        }

        @Override
        public void stop() {
            this.target = null;
            this.mob.getNavigation().stop();
        }
    }
}
