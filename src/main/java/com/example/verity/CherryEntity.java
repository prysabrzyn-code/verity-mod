package com.example.verity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

/**
 * Cherry the dog. Friendly and tameable: bones and Cherry Treats tame her,
 * a Cherry Collar makes her sit (Shift = sleep), a Cherry Treat makes her stand.
 * Follows and guards her owner.
 */
public class CherryEntity extends PathfinderMob {

    public static final int STAND = 0;
    public static final int SIT = 1;
    public static final int SLEEP = 2;

    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(CherryEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(CherryEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public CherryEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, STAND);
        this.entityData.define(OWNER, Optional.empty());
    }

    public int getState() {
        return this.entityData.get(STATE);
    }

    public void setState(int state) {
        this.entityData.set(STATE, state);
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER).orElse(null);
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(OWNER, Optional.ofNullable(uuid));
    }

    public boolean isTame() {
        return getOwnerUUID() != null;
    }

    public boolean isOwner(Player player) {
        UUID owner = getOwnerUUID();
        return owner != null && owner.equals(player.getUUID());
    }

    public Player getOwnerPlayer() {
        UUID owner = getOwnerUUID();
        return owner == null ? null : this.level().getPlayerByUUID(owner);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CherryState", getState());
        UUID owner = getOwnerUUID();
        if (owner != null) {
            tag.putUUID("CherryOwner", owner);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setState(tag.getInt("CherryState"));
        if (tag.hasUUID("CherryOwner")) {
            setOwnerUUID(tag.getUUID("CherryOwner"));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 3.0D, 12.0D));
        this.goalSelector.addGoal(4, new GuardOwnerGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        boolean bone = stack.is(Items.BONE);
        boolean treat = stack.is(VerityMod.CHERRY_TREAT.get());
        boolean collar = stack.is(VerityMod.CHERRY_COLLAR.get());
        if (!bone && !treat && !collar) {
            return super.mobInteract(player, hand);
        }
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (bone) {
            if (isTame() && !isOwner(player)) {
                return InteractionResult.PASS;
            }
            if (!isTame()) {
                tame(player);
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (player instanceof ServerPlayer serverPlayer
                    && CherryStory.stage(serverPlayer) == CherryStory.STAGE_FEED) {
                CherryStory.countBone(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }

        if (treat) {
            if (!isTame()) {
                tame(player);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            if (isOwner(player)) {
                setState(STAND);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // collar
        if (isOwner(player)) {
            setState(player.isShiftKeyDown() ? SLEEP : SIT);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void tame(Player player) {
        setOwnerUUID(player.getUUID());
        this.setHealth(this.getMaxHealth());
        player.sendSystemMessage(Component.literal("[Cherry] Ok, alan to sahebe manam!"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 10 == 0) {
            updateNameTag();
        }
    }

    /** Shows a small health bar above her head. */
    private void updateNameTag() {
        float max = Math.max(1.0F, this.getMaxHealth());
        int current = Math.max(0, Math.round(this.getHealth()));
        int filled = Math.round((current / max) * 10.0F);
        Component name = Component.literal("Cherry ")
                .append(Component.literal("|".repeat(filled)).withStyle(ChatFormatting.RED))
                .append(Component.literal("|".repeat(10 - filled)).withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(" " + current + "/" + Math.round(max)));
        this.setCustomName(name);
        this.setCustomNameVisible(true);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Being attacked wakes her up.
        if (getState() != STAND) {
            setState(STAND);
        }
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return VerityMod.CHERRY_BARK.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return VerityMod.CHERRY_BARK.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return VerityMod.CHERRY_BARK.get();
    }

    /** Sits or sleeps while her state is not STAND. Other movement goals are blocked. */
    static class SitGoal extends Goal {
        private final CherryEntity mob;

        SitGoal(CherryEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return mob.getState() != STAND;
        }

        @Override
        public boolean canContinueToUse() {
            return mob.getState() != STAND;
        }

        @Override
        public void start() {
            this.mob.getNavigation().stop();
        }
    }

    /** Walks toward her owner when they are farther than stopDistance. */
    static class FollowOwnerGoal extends Goal {
        private final CherryEntity mob;
        private final double speed;
        private final double stopDistance;
        private final double searchRange;
        private Player owner;

        FollowOwnerGoal(CherryEntity mob, double speed, double stopDistance, double searchRange) {
            this.mob = mob;
            this.speed = speed;
            this.stopDistance = stopDistance;
            this.searchRange = searchRange;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (mob.getState() != STAND || mob.getTarget() != null) {
                return false;
            }
            this.owner = mob.getOwnerPlayer();
            return this.owner != null
                    && mob.distanceToSqr(owner) > stopDistance * stopDistance
                    && mob.distanceToSqr(owner) < searchRange * searchRange;
        }

        @Override
        public boolean canContinueToUse() {
            return this.owner != null
                    && this.owner.isAlive()
                    && mob.getState() == STAND
                    && mob.distanceToSqr(owner) > stopDistance * stopDistance;
        }

        @Override
        public void tick() {
            this.mob.getLookControl().setLookAt(this.owner, 10.0F, this.mob.getMaxHeadXRot());
            this.mob.getNavigation().moveTo(this.owner, this.speed);
        }

        @Override
        public void stop() {
            this.owner = null;
            this.mob.getNavigation().stop();
        }
    }

    /** Attacks whatever attacked her owner (or what the owner attacked). */
    static class GuardOwnerGoal extends Goal {
        private final CherryEntity mob;
        private LivingEntity threat;

        GuardOwnerGoal(CherryEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            Player owner = mob.getOwnerPlayer();
            if (owner == null) {
                return false;
            }
            LivingEntity candidate = owner.getLastHurtByMob();
            if (candidate == null || owner.getLastHurtByMobTimestamp() < owner.tickCount - 100) {
                candidate = owner.getLastHurtMob();
            }
            if (candidate == null || candidate == mob || candidate == owner || !candidate.isAlive()) {
                return false;
            }
            this.threat = candidate;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return threat != null && threat.isAlive() && mob.distanceToSqr(threat) < 400.0D;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.threat);
        }

        @Override
        public void stop() {
            this.threat = null;
            this.mob.setTarget(null);
        }
    }
}
