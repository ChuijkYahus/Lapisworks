package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.circles.CircleExecutionState;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.chalk.RitualCastEnv;
import com.luxof.lapisworks.chalk.RitualExecutionState;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mishaps.MishapOutsideOfRitual;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.mojang.datafixers.util.Either;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.LINKABLE_MEDIA_BLOCK;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

// VSCode has heinous Kotlin support for Minecraft modding,
// and I REFUSE to use IntelliJ.
// So I made this just to stop it from being mad at me EVERYWHERE.
public class MishapThrowerJava {
    public static <ANY extends Object> ANY throwIfEmpty(Optional<ANY> opt, Mishap mishap) {
        if (opt.isEmpty()) throw mishap;
        return opt.get();
    }
    public static <ANY extends Object> ANY throwIfNull(@Nullable ANY nullable, Mishap mishap) {
        if (nullable == null) throw mishap;
        return nullable;
    }

    public static void assertAmelAmount(CastingEnvironment ctx, int cost) {
        int present = ((GetVAULT)ctx).grabVAULT().fetch(
            Mutables::isAmel,
            Flags.PRESET_Stacks_InvItem_UpToHotbar
        );
        if (present < cost)
            throw new MishapNotEnoughItems(AMEL, present, cost);
    }
    public static LinkableMediaBlock assertLinkableThere(BlockPos pos, CastingEnvironment ctx) {
        BlockEntity bE = ctx.getWorld().getBlockEntity(pos);

        if (!(bE instanceof LinkableMediaBlock))
            throw new MishapBadBlock(pos, LINKABLE_MEDIA_BLOCK);

        return (LinkableMediaBlock)bE;
    }
    public static void assertIsLinked(
        LinkableMediaBlock linkable,
        BlockPos pos
    ) {
        if (!linkable.isLinkedTo(pos))
            throw new MishapBadBlock(
                linkable.getThisPos(),
                Text.translatable(
                    "mishaps.lapisworks.descs.linked_linkable",
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
                )
            );
    }
    public static void assertIsntLinked(
        LinkableMediaBlock linkable,
        BlockPos pos
    ) {
        if (linkable.isLinkedTo(pos))
            throw new MishapBadBlock(
                linkable.getThisPos(),
                Text.translatable(
                    "mishaps.lapisworks.descs.unlinked_linkable",
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
                )
            );
    }
    public static void assertNotTooManyLinks(
        LinkableMediaBlock linkable1,
        LinkableMediaBlock linkable2,
        BlockPos pos1,
        BlockPos pos2
    ) {
        if (linkable1.getNumberOfLinks() >= linkable1.getMaxNumberOfLinks())
            throw new MishapBadBlock(
                pos1,
                Text.translatable("mishaps.lapisworks.descs.nottoomanylinks_linkable")
            );
        else if (linkable2.getNumberOfLinks() >= linkable2.getMaxNumberOfLinks())
            throw new MishapBadBlock(
                pos2,
                Text.translatable("mishaps.lapisworks.descs.nottoomanylinks_linkable")
            );
    }
    public static Either<BlockPos, Entity> getBlockPosOrEntity(
        List<? extends Iota> args,
        int idx,
        int argc
    ) {
        if (idx > args.size()) throw new MishapNotEnoughArgs(idx, args.size());
        else if (args.size() < argc) throw new MishapNotEnoughArgs(argc, args.size());


        Iota iota = args.get(idx);

        if (iota instanceof Vec3Iota vecIota) return Either.left(BlockPos.ofFloored(vecIota.getVec3()));

        else if (iota instanceof EntityIota entIota) return Either.right(entIota.getEntity());

        else
            throw new MishapInvalidIota(
                iota,
                idx,
                Text.translatable("mishaps.lapisworks.descs.entityorblockposiota")
            );
    }
    public static <T extends Object> T assertIsThisBlock(
        CastingEnvironment ctx,
        BlockPos pos,
        Class<T> thisBlock,
        Text blockName
    ) {
        BlockEntity bE = ctx.getWorld().getBlockEntity(pos);
        if (bE != null && thisBlock.isInstance(bE)) return thisBlock.cast(bE);
        throw new MishapBadBlock(pos, blockName);
    }
    public static CircleExecutionState assertInSpellCircle(CastingEnvironment ctx) {
        if (ctx instanceof CircleCastEnv circleCtx) return circleCtx.circleState();
        throw new MishapNoSpellCircle();
    }
    public static RitualExecutionState assertInRitual(CastingEnvironment ctx) {
        if (ctx instanceof RitualCastEnv ritualCtx) return ritualCtx.ritual();
        throw new MishapOutsideOfRitual(false);
    }
    public static OneTimeRitualExecutionState assertInOneTimeRitual(CastingEnvironment ctx) {
        if (
            ctx instanceof RitualCastEnv ritualCtx &&
            ritualCtx.ritual() instanceof OneTimeRitualExecutionState ritual
        )
            return ritual;
        else
            throw new MishapOutsideOfRitual(true);
    }
}
