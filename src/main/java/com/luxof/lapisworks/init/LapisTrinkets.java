package com.luxof.lapisworks.init;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import dev.emi.trinkets.api.TrinketsApi;

public class LapisTrinkets {
    public static void startFeelingCute() {
        TrinketsApi.registerTrinket(COLLAR, COLLAR);
    }
}
