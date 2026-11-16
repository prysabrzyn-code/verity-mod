package com.example.verity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** White dog. 64 bones tame it; after 10 in-game days it turns into a Chery Beast. */
public class ChreyEntity extends TamableAnimal {

    public static final int BONES_TO_TAME = 64;
    public static final long TEN_DAYS_TICKS = 24000L * 10L;

    private int boneCount = 0;
    private long tamedAt = -1L;

    public ChreyEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isTame() && stack.is(Items.BONE)) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.boneCount++;
                if (this.boneCount >= BONES_TO_TAME) {
                    this.tame(player);
                    this.tamedAt = this.level().getGameTime();
                    this.setHealth(this.getMaxHealth());
                    this.level().broadcastEntityEvent(this, (byte) 7); // hearts
                    player.sendSystemMessage(Component.literal("<Chery> حالا دوستت هستم!"));
                } else {
                    player.sendSystemMessage(Component.literal(
                            "<Chery> " + this.boneCount + "/" + BONES_TO_TAME + " استخوان"));
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.isTame() && this.tamedAt >= 0
                && this.level().getGameTime() - this.tamedAt >= TEN_DAYS_TICKS) {
            transform();
        }
    }

    private void transform() {
        ChreyBeastEntity beast = VerityMod.CHREY_BEAST.get().create(this.level());
        if (beast != null) {
            beast.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            if (this.getOwner() instanceof ServerPlayer owner) {
                owner.sendSystemMessage(Component.literal("<Chery> دارم تبدیل به حیوان میشم..."));
            }
            this.level().addFreshEntity(beast);
        }
        this.discard();
    }

    public int getBoneCount() {
        return this.boneCount;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BoneCount", this.boneCount);
        tag.putLong("TamedAt", this.tamedAt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.boneCount = tag.getInt("BoneCount");
        this.tamedAt = tag.contains("TamedAt") ? tag.getLong("TamedAt") : -1L;
    }
}
