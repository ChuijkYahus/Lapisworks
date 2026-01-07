package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.List;

public abstract class ConstMediaActionNCT implements ConstMediaAction, NCTHelpers {
    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        throw new IllegalStateException("call executeWithOpCount instead.");
    }

    public CostMediaActionResult executeWithOpCount(HexIotaStack stack, CastingEnvironment ctx) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, stack.stack, ctx);
    }

    protected <AnyIota extends Iota> List<AnyIota> asActionResult(AnyIota iota) {
        return List.of(iota);
    }


    @Override
    public List<Iota> execute(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return execute(new HexIotaStack(arg0, getArgc(), arg1), arg1).stream().map(it -> (Iota)it).toList();
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return executeWithOpCount(new HexIotaStack(arg0, getArgc(), arg1), arg1);
    }



    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }

    // boo!
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

    // boo! (part 2: electric boogaloo)
    @Override
    public long getMediaCost() {
        try {
            return this.getClass().getField("mediaCost").getLong(this);
        } catch (NoSuchFieldException e) {
            LOGGER.error("you must have a mediaCost field in the first place.", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("your mediaCost field must be accessible.", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("your mediaCost field must be a long.", e);
        }
        return 0;
    }
}
