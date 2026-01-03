package com.luxof.lapisworks.init;

import at.petrak.hexcasting.common.lib.HexBlocks;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.blocks.*;
import com.luxof.lapisworks.blocks.entities.*;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ModBlocks {
    public static ConjuredColorable CONJURED_COLORABLE = new ConjuredColorable();
    public static Mind MIND_BLOCK = new Mind();
    public static LiveJukebox LIVE_JUKEBOX_BLOCK = new LiveJukebox();
    public static JumpSlate JUMP_SLATE_AM1 = new JumpSlate();
    public static JumpSlate JUMP_SLATE_AM2 = new JumpSlate();
    public static JumpSlate JUMP_SLATE_AMETH = new JumpSlate();
    public static JumpSlate JUMP_SLATE_LAPIS = new JumpSlate();
    public static ReboundSlate REBOUND_SLATE_1 = new ReboundSlate();
    public static ReboundSlate REBOUND_SLATE_2 = new ReboundSlate();
    public static SimpleImpetus SIMPLE_IMPETUS = new SimpleImpetus();
    public static EnchBrewer ENCH_BREWER = new EnchBrewer();
    public static MediaCondenser MEDIA_CONDENSER = new MediaCondenser();
    public static Chalk CHALK = new Chalk();
    public static ChalkWithPattern CHALK_WITH_PATTERN = new ChalkWithPattern();
    // GET THE UPDATE OUT
    public static Block UNCRAFTED_CONDENSER = new Block(Settings.copy(HexBlocks.SLATE_BLOCK)) {
        public static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1, 0, 0, 15, 14, 16),
            Block.createCuboidShape(0, 0, 1, 16, 14, 15),
            Block.createCuboidShape(6, 14, 6, 10, 16, 10)
        );
        @Override
        public BlockRenderType getRenderType(BlockState state) {
            return BlockRenderType.MODEL;
        }
        @Override
        public VoxelShape getOutlineShape(BlockState pState, BlockView pLevel, BlockPos pPos, ShapeContext pContext) {
            return SHAPE;
        }
    };
    //public static ChalkBlock CHALK_BLOCK = new ChalkBlock();
    public static BlockEntityType<MindEntity> MIND_ENTITY_TYPE = new BlockEntityType<>(
        MindEntity::new,
        ImmutableSet.of(MIND_BLOCK),
        null
    );
    public static BlockEntityType<LiveJukeboxEntity> LIVE_JUKEBOX_ENTITY_TYPE = new BlockEntityType<>(
        LiveJukeboxEntity::new,
        ImmutableSet.of(LIVE_JUKEBOX_BLOCK),
        null
    );
    public static BlockEntityType<SimpleImpetusEntity> SIMPLE_IMPETUS_ENTITY_TYPE = new BlockEntityType<>(
        SimpleImpetusEntity::new,
        ImmutableSet.of(SIMPLE_IMPETUS),
        null
    );
    public static BlockEntityType<EnchBrewerEntity> ENCH_BREWER_ENTITY_TYPE = new BlockEntityType<>(
        EnchBrewerEntity::new,
        ImmutableSet.of(ENCH_BREWER),
        null
    );
    public static BlockEntityType<MediaCondenserEntity> MEDIA_CONDENSER_ENTITY_TYPE = new BlockEntityType<>(
        MediaCondenserEntity::new,
        ImmutableSet.of(MEDIA_CONDENSER),
        null
    );
    public static BlockEntityType<ChalkEntity> CHALK_ENTITY_TYPE = new BlockEntityType<>(
        ChalkEntity::new,
        ImmutableSet.of(CHALK),
        null
    );
    public static BlockEntityType<ChalkWithPatternEntity> CHALK_WITH_PATTERN_ENTITY_TYPE = new BlockEntityType<>(
        ChalkWithPatternEntity::new,
        ImmutableSet.of(CHALK_WITH_PATTERN),
        null
    );

    public static void wearASkirt() {
        pickACropTop("conjureable", CONJURED_COLORABLE);
        pickACropTop("mind", MIND_BLOCK);
        pickACropTop("amel_constructs/live_jukebox", LIVE_JUKEBOX_BLOCK);
        pickACropTop("amel_constructs/jumpslate/am1", JUMP_SLATE_AM1);
        pickACropTop("amel_constructs/jumpslate/am2", JUMP_SLATE_AM2);
        pickACropTop("amel_constructs/jumpslate/ameth", JUMP_SLATE_AMETH);
        pickACropTop("amel_constructs/jumpslate/lapis", JUMP_SLATE_LAPIS);
        pickACropTop("amel_constructs/jumpslate/rebound_1", REBOUND_SLATE_1);
        pickACropTop("amel_constructs/jumpslate/rebound_2", REBOUND_SLATE_2);
        pickACropTop("amel_constructs/simple_impetus", SIMPLE_IMPETUS);
        pickACropTop("amel_constructs/enchbrewer", ENCH_BREWER);
        pickACropTop("media_condenser_unit", MEDIA_CONDENSER);
        pickACropTop("uncrafted_condenser", UNCRAFTED_CONDENSER);
        pickACropTop("chalk", CHALK);
        pickACropTop("chalk_with_pattern", CHALK_WITH_PATTERN);
        dontForgetStockings("mind_entity_type", MIND_ENTITY_TYPE);
        dontForgetStockings("live_jukebox_entity_type", LIVE_JUKEBOX_ENTITY_TYPE);
        dontForgetStockings("amel_constructs/simple_impetus", SIMPLE_IMPETUS_ENTITY_TYPE);
        dontForgetStockings("amel_constructs/enchbrewer", ENCH_BREWER_ENTITY_TYPE);
        dontForgetStockings("media_condenser_unit", MEDIA_CONDENSER_ENTITY_TYPE);
        dontForgetStockings("chalk", CHALK_ENTITY_TYPE);
        dontForgetStockings("chalk_with_pattern", CHALK_WITH_PATTERN_ENTITY_TYPE);
    }

    public static void pickACropTop(String name, Block block) {
        Registry.register(Registries.BLOCK, id(name), block);
    }

    public static <T extends BlockEntityType<?>> void dontForgetStockings(String name, T blockEntityType) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id(name), blockEntityType);
    }
}
