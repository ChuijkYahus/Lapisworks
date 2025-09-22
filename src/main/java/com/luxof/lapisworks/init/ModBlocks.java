package com.luxof.lapisworks.init;

import com.google.common.collect.ImmutableSet;

import com.luxof.lapisworks.blocks.ConjuredColorable;
import com.luxof.lapisworks.blocks.JumpSlate;
import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.blocks.LiveJukebox;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

// i ran out of new names
public class ModBlocks {
    public static ConjuredColorable CONJURED_COLORABLE = new ConjuredColorable();
    public static Mind MIND_BLOCK = new Mind();
    public static LiveJukebox LIVE_JUKEBOX_BLOCK = new LiveJukebox();
    public static JumpSlate JUMP_SLATE_AM1 = new JumpSlate();
    public static JumpSlate JUMP_SLATE_AM2 = new JumpSlate();
    public static JumpSlate JUMP_SLATE_AMETH = new JumpSlate();
    public static JumpSlate JUMP_SLATE_LAPIS = new JumpSlate();
    // FUCK BlockEntityType.Builder.create bro that shit didn't take ANYTHING i gave it
    public static BlockEntityType<BlockEntity> MIND_ENTITY_TYPE = new BlockEntityType<BlockEntity>(
        MindEntity::new,
        ImmutableSet.of(MIND_BLOCK),
        null
    );
    public static BlockEntityType<BlockEntity> LIVE_JUKEBOX_ENTITY_TYPE = new BlockEntityType<BlockEntity>(
        LiveJukeboxEntity::new,
        ImmutableSet.of(LIVE_JUKEBOX_BLOCK),
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
        dontForgetStockings("mind_entity_type", MIND_ENTITY_TYPE);
        dontForgetStockings("live_jukebox_entity_type", LIVE_JUKEBOX_ENTITY_TYPE);
    }

    public static void pickACropTop(String name, Block block) {
        Registry.register(Registries.BLOCK, id(name), block);
    }

    public static <T extends BlockEntityType<?>> void dontForgetStockings(String name, T blockEntityType) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id(name), blockEntityType);
    }
}
