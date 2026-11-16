package com.example.verity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.Level;

import java.util.EnumSet;

/** The red-eyed Cherry. Hostile only after she has killed Tom and started hunting. */
public class CherryMonsterEntity extends PathfinderMob {

    private static final EntityDataAccessor<Boolean> HUNTING =
            SynchedEntityData.defineId(CherryMonsterEntity.class, EntityDataSerializers.BOOLEAN);

    public CherryMonsterEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HUNTING, false);
    }

    public boolean isHunting() {
        return this.entityData.get(HUNTING);
    }

    public void setHunting(boolean hunting) {
        this.entityData.set(HUNTING, hunting);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("CherryHunting", isHunting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHunting(tag.getBoolean("CherryHunting"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HuntGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 10 == 0) {
            Component name = Component.literal("Cherry")
                    .withStyle(isHunting() ? ChatFormatting.DARK_RED : ChatFormatting.RED);
            this.setCustomName(name);
            this.setCustomNameVisible(true);
        }
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

    /** Once hunting is on, targets the nearest player within 48 blocks. */
    static class HuntGoal extends Goal {
        private final CherryMonsterEntity mob;
        private Player target;

        HuntGoal(CherryMonsterEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!mob.isHunting()) {
                return false;
            }
            this.target = mob.level().getNearestPlayer(mob, 48.0D);
            return this.target != null && this.target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return mob.isHunting() && this.target != null && this.target.isAlive();
        }

        @Override
        public void start() {
            this.mob.setTarget(this.target);
        }

        @Override
        public void stop() {
            this.target = null;
        }
    }
}
