package com.luxof.lapisworks.actions.great;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class CreateEnchSent implements SpellAction {
    public int getArgc() {
        return 2;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        if (!ctx.isEnlightened()) {
            MishapThrowerJava.throwMishap(new MishapUnenlightened());
        }
        Optional<LivingEntity> casterOp = Optional.of(ctx.getCastingEntity());
        if (casterOp.isEmpty()) {
            MishapThrowerJava.throwMishap(new MishapBadCaster());
        } else if (!(casterOp.get() instanceof PlayerEntity)) {
            MishapThrowerJava.throwMishap(new MishapBadCaster());
        }
        PlayerEntity caster = (PlayerEntity)casterOp.get();

        Vec3d pos = OperatorUtils.getVec3(args, 0, getArgc());
        if (caster.getPos().distanceTo(pos) > 32.0) {
            // you will NOT fuck with this to do better sent walk!
            MishapThrowerJava.throwMishap(new MishapBadLocation(pos, "too_far"));
        }
        double ambit = OperatorUtils.getDoubleBetween(args, 1, 1.0, 64.0, getArgc());


        return new SpellAction.Result(
            new Spell(caster, pos, ambit),
            MediaConstants.DUST_UNIT * 5,
            List.of(ParticleSpray.burst(caster.getPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final PlayerEntity caster;
        public final Vec3d pos;
        public final double ambit;

        public Spell(PlayerEntity caster, Vec3d pos, double ambit) {
            this.caster = caster;
            this.pos = pos;
            this.ambit = ambit;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ((EnchSentInterface)this.caster).setEnchantedSentinel(this.pos, this.ambit);
            // can you tell i have no clue what the fuck i'm doing?
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(false);
            buf.writeVector3f(this.pos.toVector3f());
            buf.writeDouble(this.ambit);
            // "this code runs on the server, so the caster must be a ServerPlayerEntity"
            //   -my dumbass praying this code doesn't explode in my face
            ServerPlayNetworking.send((ServerPlayerEntity)this.caster, SEND_SENT, buf);
		}

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public boolean awardsCastingStat(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> args, CastingEnvironment env, NbtCompound userData) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
