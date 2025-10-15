package com.luxof.lapisworks.interop.hexical.blocks;

import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.interop.hexical.Lapixical;

import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Cradle extends BlockWithEntity {
    public static final VoxelShape SHAPE = Block.createCuboidShape(
        6, 0, 0,
        10, 16, 10
    );

    public Cradle() { super(Settings.copy(Blocks.LIGHTNING_ROD)); }
 
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {}

    @Override
    public VoxelShape getOutlineShape(BlockState bs, BlockView bv, BlockPos bp, ShapeContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        CradleEntity bE = (CradleEntity)world.getBlockEntity(pos);
        ItemStack prevStack = bE.getStack(0);
        bE.setStack(0, player.getStackInHand(hand));
        player.setStackInHand(hand, prevStack);
        NBTHelper.putBoolean(bE.getStack(0), IS_IN_CRADLE, true);
        NBTHelper.remove(prevStack, IS_IN_CRADLE);
        bE.updateItemEntity();
        bE.markDirty();
        if (!world.isClient) {
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState bs) {
        return new CradleEntity(pos, bs);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        // checkType() makes me required to do an "unsafe cast" for whatever reason
        if (type == Lapixical.CRADLE_ENTITY_TYPE) return CradleEntity::tick;
        if (type == ModBlocks.MIND_ENTITY_TYPE) { return MindEntity::tick; }
        else { return null; }
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        CradleEntity bE = (CradleEntity)world.getBlockEntity(pos);
        if (bE.heldEntity != null) {
            bE.heldEntity.discard();
            bE.heldEntity = null;
        }
        if (!bE.getStack(0).isEmpty()) {
            world.spawnEntity(
                new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), bE.getStack(0))
            );
            NBTHelper.remove(bE.getStack(0), IS_IN_CRADLE);
        }
        bE.markDirty();
    }
}
