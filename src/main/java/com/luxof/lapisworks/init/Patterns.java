package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.common.lib.hex.HexActions;

import static at.petrak.hexcasting.api.misc.MediaConstants.CRYSTAL_UNIT;

import static com.luxof.lapisworks.init.ThemConfigFlags.registerPWShapePattern;

import java.util.List;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.actions.CheckAttr;
import com.luxof.lapisworks.actions.ImbueLap;
import com.luxof.lapisworks.actions.MindLiquefaction;
import com.luxof.lapisworks.actions.SwapAmel;
import com.luxof.lapisworks.actions.TeachSong;
import com.luxof.lapisworks.actions.MoarAttr;
import com.luxof.lapisworks.actions.great.BanishMySent;
import com.luxof.lapisworks.actions.great.BanishOtherSent;
import com.luxof.lapisworks.actions.great.CreateEnchSent;
import com.luxof.lapisworks.actions.great.GenericEnchant;
import com.luxof.lapisworks.actions.great.Hastenature;
import com.luxof.lapisworks.actions.misc.ConjureColor;
import com.luxof.lapisworks.actions.misc.CubeExalt;
import com.luxof.lapisworks.actions.misc.EmptyPrfn;
import com.luxof.lapisworks.actions.misc.EqualBlock;
import com.luxof.lapisworks.actions.misc.EquivBlock;
import com.luxof.lapisworks.actions.misc.ReadFromHand;
import com.luxof.lapisworks.actions.misc.ReadableInHand;
import com.luxof.lapisworks.actions.misc.SphereDst;
import com.luxof.lapisworks.actions.misc.VisibleDstl;
import com.luxof.lapisworks.actions.misc.WritableInHand;
import com.luxof.lapisworks.actions.misc.WriteToHand;
import com.luxof.lapisworks.actions.CheckEnchant;
import com.luxof.lapisworks.actions.CognitionPrfn;
import com.luxof.lapisworks.actions.FlayArtMind;
import com.luxof.lapisworks.actions.HexResearchYoink;
import com.luxof.lapisworks.actions.ImbueAmel;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Patterns {
    public static void init() {
        MoarAttr MoarHealthAction = new MoarAttr(
            EntityAttributes.GENERIC_MAX_HEALTH,
            2.0,
            0.0,
            1.0,
            2,
            false
        );
        MoarAttr MoarAttackAction = new MoarAttr(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            4.0, // the barbarian and the monk fistfighting a demon because magic is for nerds
            0.0,
            1.0,
            5,
            false
        );
        // i'd add armor but narratively it makes no sense, you can already enchant your skin
        // so what else is there to enchant to make yourself stronger?
        // man i wish i could, i had such a cool ass fucking pattern too
        // north east wwwaqeeeqawww
        // yeah just gonna use CheckAttr instead
        MoarAttr MoarSpeedAction = new MoarAttr(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            3.0,
            0.0,
            10.0,
            5,
            false
        );
        MoarAttr GibDexterityAction = new MoarAttr(
            EntityAttributes.GENERIC_ATTACK_SPEED,
            1.0,
            4.0,
            1.0,
            16,
            true
        );
        register("imbue_lap", "qadwawdaqqeae", HexDir.NORTH_EAST, new ImbueLap());
        register("swap_amel", "wqwawwqwaqeq", HexDir.EAST, new SwapAmel());
        
        register("moar_health", "wqadaqwwawwwqwwawdwawwqwwwwa", HexDir.NORTH_EAST, MoarHealthAction);
        register("moar_attack", "qaqwweaeaqwww", HexDir.EAST, MoarAttackAction);
        register("moar_speed", "ddqwaqeqa", HexDir.WEST, MoarSpeedAction);
        register("gib_dexterity", "aeaqqdeeeqewdwqwdwe", HexDir.WEST, GibDexterityAction);
        register("check_attr", "wwwaqeeqawww", HexDir.NORTH_EAST, new CheckAttr());

        GenericEnchant fireyFists       = new GenericEnchant(1, 64, CRYSTAL_UNIT * 10, "lapisenchantments.lapisworks.fireyfists");
        GenericEnchant lightningBending = new GenericEnchant(3, 64, CRYSTAL_UNIT * 20, "lapisenchantments.lapisworks.lightningbending");
        GenericEnchant fallDmgRes       = new GenericEnchant(2, 32, CRYSTAL_UNIT * 5, "lapisenchantments.lapisworks.falldmgres");
        GenericEnchant longBreath       = new GenericEnchant(2, 10, CRYSTAL_UNIT, "lapisenchantments.lapisworks.longbreath");
        GenericEnchant fireResist       = new GenericEnchant(1, 64, CRYSTAL_UNIT * 10, "lapisenchantments.lapisworks.fireresist");
        register("fireyfists", "wwewdawdewqewedadad", HexDir.EAST, fireyFists);
        register("lightningbending", "wewdawdewqewdqqeedqe", HexDir.EAST, lightningBending);
        register("falldmgres", "qqwwqqqadwewdeq", HexDir.SOUTH_WEST, fallDmgRes);
        register("longbreath", "wewdwewewewewdwew", HexDir.SOUTH_EAST, longBreath);
        register("fireresist", "wwqwqwadwawdawqwaeqqaqqe", HexDir.EAST, fireResist);
        register("checkenchant", "aqawwqqwqqw", HexDir.SOUTH_EAST, new CheckEnchant());

        register("imbue_amel", "wqwwawwqwwaqwewaawewa", HexDir.NORTH_EAST, new ImbueAmel());
        register("conjure_color", "qqaa", HexDir.NORTH_EAST, new ConjureColor());
        register("spherical_dstl", "wqwqwqwqwqwaeaqaaeaqaa", HexDir.NORTH_WEST, new SphereDst());
        register("cubic_exalt", "wqwawqwqqwqwq", HexDir.NORTH_WEST, new CubeExalt());
        register("visible_dstl", "edeewadwewdwe", HexDir.SOUTH_EAST, new VisibleDstl());
        register("empty_prfn", "qqqqqaw", HexDir.NORTH_WEST, new EmptyPrfn());
        register("read_spechand", "aqqqqa", HexDir.EAST, new ReadFromHand());
        register("readable_spechand", "qqqqadww", HexDir.NORTH_WEST, new ReadableInHand());
        register("write_spechand", "deeeed", HexDir.EAST, new WriteToHand());
        register("writable_spechand", "eeeedaww", HexDir.SOUTH_WEST, new WritableInHand());
        register("writable_offhand", "eeedqww", HexDir.SOUTH_WEST, new WritableInHand());
        register("equiv_block", "qqqqqeqeeeee", HexDir.NORTH_WEST, new EquivBlock());
        register("equal_block", "qwawqwadadwewdwe", HexDir.NORTH_WEST, new EqualBlock());

        register("thought_sieve", "qadaadadqaqdadqaq", HexDir.WEST, new HexResearchYoink());
        register("absorb_mind", "aawqqwqqqaede", HexDir.WEST, new MindLiquefaction());
        register("check_mind", "aawqqwqqq", HexDir.WEST, new CognitionPrfn());

        register("teach_song", "aawwawqwwdd", HexDir.WEST, new TeachSong());

        // hol up, let him cook
        // i said LET HIM COOK
        // LET. HIM. COOK :fire:
        SpellAction createEnchSent = new CreateEnchSent();
        register("create_enchsent0", "aqaeawdwwwdwqwdwwwdweqqaqwedeewqded", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent1", "aqaeawdwwwdwqwdwwwdwewweaqa", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent2", "wdwewdwwwdwwwdwqwdwwwdw", HexDir.NORTH_EAST, createEnchSent);
        register("create_enchsent3", "aqaeawdwwwdwqwdwwwdweqaawddeweaqa", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent4", "wdwwwdwqwdwwwdweqaawdde", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent5", "wdwwwdwqwdwwwdwweeeee", HexDir.NORTH_WEST, createEnchSent);
        registerPWShapePattern(
            "lapisworks:create_enchsent",
            List.of(
                "aqaeawdwwwdwqwdwwwdweqqaqwedeewqded",
                "aqaeawdwwwdwqwdwwwdwewweaqa",
                "wdwewdwwwdwwwdwqwdwwwdw",
                "aqaeawdwwwdwqwdwwwdweqaawddeweaqa",
                "wdwwwdwqwdwwwdweqaawdde",
                "wdwwwdwqwdwwwdwweeeee"
            )
        );
        register("banish_my_enchsent", "wdwewdwdwqwawwwawewawwwaw", HexDir.NORTH_EAST, new BanishMySent());
        register("banish_other_enchsent", "eeeeedwqwawwwawewawwwaw", HexDir.NORTH_EAST, new BanishOtherSent());
        
        register("flay_artmind0", "ewewedwqwqqwqwqaeqe", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind1", "ewewedwqwaqaedqdeaqdewewe", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind2", "ewewedwqwqqwqwqaeqeqaqeqeqa", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind3", "ewewedwqwaqaeweeeweaqdedaeade", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind4", "ewewedwqwaqeqwqadqwqwqdaqeqwqwq", HexDir.SOUTH_EAST, new FlayArtMind());
        registerPWShapePattern(
            "lapisworks:flay_artmind",
            List.of(
                "ewewedwqwqqwqwqaeqe",
                "ewewedwqwaqaedqdeaqdewewe",
                "ewewedwqwqqwqwqaeqeqaqeqeqa",
                "ewewedwqwaqaeweeeweaqdedaeade",
                "ewewedwqwaqeqwqadqwqwqdaqeqwqwq"
            )
        );

        register("hastenature0", "awawwwdwdww", HexDir.NORTH_EAST, new Hastenature());
        register("hastenature1", "qwdedwqqwdedweawawwwdwdww", HexDir.WEST, new Hastenature());
        register("hastenature2", "wawqwaweawawwwdwdww", HexDir.WEST, new Hastenature());
        register("hastenature3", "awwdedwwawwdedweawawwwdwdww", HexDir.NORTH_WEST, new Hastenature());
        register("hastenature4", "aaqawawweddedwdww", HexDir.NORTH_WEST, new Hastenature());
        register("hastenature5", "aeaeaeaeaeadawawwwdwdww", HexDir.NORTH_WEST, new Hastenature());
        registerPWShapePattern(
            "lapisworks:hastenature",
            List.of(
                "awawwwdwdww",
                "qwdedwqqwdedweawawwwdwdww",
                "wawqwaweawawwwdwdww",
                "awwdedwwawwdedweawawwwdwdww",
                "aaqawawweddedwdww",
                "aeaeaeaeaeadawawwwdwdww"
            )
        );
    }

    private static void register(
        String name,
        String signature,
        HexDir startDir,
        Action action
    ) {
        Registry.register(HexActions.REGISTRY, new Identifier(Lapisworks.MOD_ID, name), new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action));
    }
}
