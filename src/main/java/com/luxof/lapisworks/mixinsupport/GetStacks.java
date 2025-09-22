package com.luxof.lapisworks.mixinsupport;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;

import java.util.List;

public interface GetStacks {
    public List<HeldItemInfo> getHeldStacks();
}
