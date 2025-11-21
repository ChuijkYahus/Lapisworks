package com.luxof.lapisworks.blocks;

import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexBlocks;

import com.luxof.lapisworks.blocks.entities.MediaCondenserEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class MediaCondenser extends BlockWithEntity {
    public static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(1, 0, 0, 15, 14, 16),
        Block.createCuboidShape(0, 0, 1, 16, 14, 15),
        Block.createCuboidShape(6, 14, 6, 10, 16, 10)
    );

    public MediaCondenser() {
        super(Settings.copy(HexBlocks.SLATE_BLOCK));
    }
    
    public static final IntProperty FILLED = IntProperty.of("filled", 0, 14);

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new MediaCondenserEntity(arg0, arg1);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState pState, BlockView pLevel, BlockPos pPos, ShapeContext pContext) {
        return SHAPE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (type == ModBlocks.MEDIA_CONDENSER_ENTITY_TYPE) {
            return (a, b, c, bE) -> ((MediaCondenserEntity)bE).tick(a, b, c);
        } else return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    // fffffFFFUCK your explosion drop chances!
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        MediaCondenserEntity condenser = (MediaCondenserEntity)world.getBlockEntity(pos);
        ItemStack stack = new ItemStack(ModItems.MEDIA_CONDENSER);
        NBTHelper.putLong(stack, "media", condenser.media);
        NBTHelper.putLong(stack, "max", condenser.mediaCap);

        ItemScatterer.spawn(
            world,
            pos,
            DefaultedList.copyOf(ItemStack.EMPTY.copy(), stack)
        );
    }

    @Override
    public void onPlaced(
        World world,
        BlockPos pos,
        BlockState state,
        @Nullable LivingEntity placer,
        ItemStack itemStack
    ) {
        MediaCondenserEntity condenser = (MediaCondenserEntity)world.getBlockEntity(pos);
        condenser.media = NBTHelper.getLong(itemStack, "media", 0L);
        condenser.mediaCap = NBTHelper.getLong(itemStack, "max", 640000L);
        condenser.tick(world, pos, state);
    }
}
