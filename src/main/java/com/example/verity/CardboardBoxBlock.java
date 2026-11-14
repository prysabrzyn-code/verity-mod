package com.example.verity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CardboardBoxBlock extends Block {

    public CardboardBoxBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /** Rounded hitbox matching the stepped-sphere model. */
    private static final VoxelShape SHAPE = buildShape();

    private static VoxelShape buildShape() {
        VoxelShape shape = Shapes.empty();
        for (int y = 0; y < 16; y++) {
            double dy = (y + 0.5D) - 8.0D;
            int r = Math.max(1, (int) Math.round(Math.sqrt(Math.max(0.0D, 64.0D - dy * dy))));
            shape = Shapes.or(shape, Block.box(8 - r, y, 8 - r, 8 + r, y + 1, 8 + r));
        }
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.removeBlock(pos, false);
            VerityEntity verity = VerityMod.VERITY.get().create(level);
            if (verity != null) {
                verity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), 0.0F);
                level.addFreshEntity(verity);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
