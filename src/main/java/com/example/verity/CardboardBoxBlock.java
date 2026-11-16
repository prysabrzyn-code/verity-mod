package com.example.verity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

/** Cardboard box: right-click to open it. The creature inside pops out with its Persian word above it. */
public class CardboardBoxBlock extends Block {

    private final Supplier<? extends EntityType<? extends Mob>> creature;
    private final String word;

    public CardboardBoxBlock(BlockBehaviour.Properties properties,
                             Supplier<? extends EntityType<? extends Mob>> creature,
                             String word) {
        super(properties);
        this.creature = creature;
        this.word = word;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.removeBlock(pos, false);
            Mob mob = this.creature.get().create(level);
            if (mob != null) {
                mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), 0.0F);
                mob.setCustomName(Component.literal(this.word));
                mob.setCustomNameVisible(true);
                mob.setPersistenceRequired();
                level.addFreshEntity(mob);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
