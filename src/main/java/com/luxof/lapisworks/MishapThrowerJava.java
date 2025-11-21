package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.MishapThrower;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.LINKABLE_MEDIA_BLOCK;

import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

// VSCode has heinous Kotlin support for Minecraft modding,
// and I REFUSE to use IntelliJ.
// So I made this just to stop it from being mad at me EVERYWHERE.
public class MishapThrowerJava {
    public static void throwMishap(Mishap mishap) {
        MishapThrower.throwMishap(mishap);
    }

    public static <T extends Object> T throwIfEmpty(Optional<T> opt, Mishap mishap) {
        if (opt.isEmpty()) { MishapThrower.throwMishap(mishap); }
        return opt.get();
    }

    public static void assertInRange(CastingEnvironment ctx, Entity e) {
        try { ctx.assertEntityInRange(e); }
        catch (Mishap m) { throwMishap(m); }
    }
    public static void assertInRange(CastingEnvironment ctx, BlockPos pos) {
        try { ctx.assertPosInRange(pos); }
        catch (Mishap m) { throwMishap(m); }
    }
    public static void assertInRange(CastingEnvironment ctx, Vec3d pos) {
        try { ctx.assertVecInRange(pos); }
        catch (Mishap m) { throwMishap(m); }
    }
    public static void assertInRangeForEditing(CastingEnvironment ctx, BlockPos pos) {
        try { ctx.assertPosInRangeForEditing(pos); }
        catch (Mishap m) { throwMishap(m); }
    }
    public static void assertAmelAmount(CastingEnvironment ctx, int cost) {
        int present = ((GetVAULT)ctx).grabVAULT().fetch(Mutables::isAmel, Flags.PRESET_Stacks_InvItem_UpToHotbar);
        if (present < cost) {
            throwMishap(new MishapNotEnoughItems(AMEL, present, cost));
        }
    }
    public static LinkableMediaBlock assertLinkableThere(BlockPos pos, CastingEnvironment ctx) {
        BlockEntity bE = ctx.getWorld().getBlockEntity(pos);

        if (!(bE instanceof LinkableMediaBlock))
            throwMishap(new MishapBadBlock(pos, LINKABLE_MEDIA_BLOCK));

        return (LinkableMediaBlock)bE;
    }
    public static void assertIsLinked(
        LinkableMediaBlock linkable,
        BlockPos pos
    ) {
        if (!linkable.isLinkedTo(pos))
            throwMishap(
                new MishapBadBlock(
                    linkable.getThisPos(),
                    Text.translatable(
                        "mishaps.lapisworks.descs.linked_linkable",
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                    )
                )
            );
    }
    public static void assertIsntLinked(
        LinkableMediaBlock linkable,
        BlockPos pos
    ) {
        if (linkable.isLinkedTo(pos))
            throwMishap(
                new MishapBadBlock(
                    linkable.getThisPos(),
                    Text.translatable(
                        "mishaps.lapisworks.descs.unlinked_linkable",
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                    )
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
            throwMishap(
                new MishapBadBlock(
                    pos1,
                    Text.translatable("mishaps.lapisworks.descs.nottoomanylinks_linkable")
                )
            );
        else if (linkable2.getNumberOfLinks() >= linkable2.getMaxNumberOfLinks())
            throwMishap(
                new MishapBadBlock(
                    pos2,
                    Text.translatable("mishaps.lapisworks.descs.nottoomanylinks_linkable")
                )
            );
    }
}
