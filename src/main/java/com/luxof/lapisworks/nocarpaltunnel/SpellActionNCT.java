package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.List;

import net.minecraft.nbt.NbtCompound;

public class SpellActionNCT extends NCTBase implements SpellAction {

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        throw new IllegalStateException("call executeWithUserdata instead.");
    }

    public Result executeWithUserdata(HexIotaStack stack, CastingEnvironment ctx, NbtCompound userData) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, stack.stack, ctx, userData);
    }

    public interface RenderedSpellNCT extends RenderedSpell {

        default void cast(CastingEnvironment ctx) {
            throw new IllegalStateException("call cast(env, image) instead.");
        }

        default CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }

    }


    @Override
    public Result execute(List<? extends Iota> stack, CastingEnvironment ctx) {
        this.world = ctx.getWorld();
        this.vault = ((GetVAULT)ctx).grabVAULT();
        return execute(new HexIotaStack(stack, getArgc(), ctx), ctx);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> arg0, CastingEnvironment arg1, NbtCompound arg2) {
        this.world = arg1.getWorld();
        this.vault = ((GetVAULT)arg1).grabVAULT();
        return executeWithUserdata(new HexIotaStack(arg0, getArgc(), arg1), arg1, arg2);
    }



    @Override
    public boolean awardsCastingStat(CastingEnvironment arg0) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, arg0);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment arg0) {
        return SpellAction.DefaultImpls.hasCastingSound(this, arg0);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }

    // reflection jumpscare
    @Override
    public int getArgc() {
        try {
            return this.getClass().getField("argc").getInt(this);
        } catch (NoSuchFieldException e) {
            LOGGER.error("you must have an argc field in the first place.", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("your argc field must be accessible.", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("your argc field must be an int.", e);
        }
        return 0;
    }
}
