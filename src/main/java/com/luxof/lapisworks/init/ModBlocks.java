package com.luxof.lapisworks.init;

import com.google.common.collect.ImmutableSet;

//import com.luxof.lapisworks.blocks.ChalkBlock;
import com.luxof.lapisworks.blocks.ConjuredColorable;
import com.luxof.lapisworks.blocks.EnchBrewer;
import com.luxof.lapisworks.blocks.JumpSlate;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.ReboundSlate;
import com.luxof.lapisworks.blocks.SimpleImpetus;
import com.luxof.lapisworks.blocks.LiveJukebox;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.blocks.stuff.AbstractBrewerEntity;
import com.luxof.lapisworks.blocks.entities.EnchBrewerEntity;
import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

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
    //public static ChalkBlock CHALK_BLOCK = new ChalkBlock();
    public static BlockEntityType<MindEntity> MIND_ENTITY_TYPE = new BlockEntityType<MindEntity>(
        MindEntity::new,
        ImmutableSet.of(MIND_BLOCK),
        null
    );
    public static BlockEntityType<LiveJukeboxEntity> LIVE_JUKEBOX_ENTITY_TYPE = new BlockEntityType<LiveJukeboxEntity>(
        LiveJukeboxEntity::new,
        ImmutableSet.of(LIVE_JUKEBOX_BLOCK),
        null
    );
    public static BlockEntityType<SimpleImpetusEntity> SIMPLE_IMPETUS_ENTITY_TYPE = new BlockEntityType<SimpleImpetusEntity>(
        SimpleImpetusEntity::new,
        ImmutableSet.of(SIMPLE_IMPETUS),
        null
    );
    public static BlockEntityType<AbstractBrewerEntity> ENCH_BREWER_ENTITY_TYPE = new BlockEntityType<AbstractBrewerEntity>(
        EnchBrewerEntity::new,
        ImmutableSet.of(ENCH_BREWER),
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
        //pickACropTop("chalk", CHALK_BLOCK);
        dontForgetStockings("mind_entity_type", MIND_ENTITY_TYPE);
        dontForgetStockings("live_jukebox_entity_type", LIVE_JUKEBOX_ENTITY_TYPE);
        dontForgetStockings("amel_constructs/simple_impetus", SIMPLE_IMPETUS_ENTITY_TYPE);
        dontForgetStockings("amel_constructs/enchbrewer", ENCH_BREWER_ENTITY_TYPE);
    }

    public static void pickACropTop(String name, Block block) {
        Registry.register(Registries.BLOCK, id(name), block);
    }

    public static <T extends BlockEntityType<?>> void dontForgetStockings(String name, T blockEntityType) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id(name), blockEntityType);
    }
}
