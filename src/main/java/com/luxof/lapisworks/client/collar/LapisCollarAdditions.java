package com.luxof.lapisworks.client.collar;

import com.luxof.lapisworks.client.collar.additions.*;

import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ModItems.COLLAR;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LapisCollarAdditions {
    private static HashMap<Identifier, LapisCollarAddition> additions = new HashMap<>();

    public static void register(
        Identifier id,
        @NotNull LapisCollarAddition addition
    ) {
        if (!additions.containsKey(id))
            additions.put(id, addition);
        else
            err("Lapis Collar Addition already registered: %s", id.toString());
    }

    @Nullable
    public static LapisCollarAddition get(Identifier id) {
        if (!additions.containsKey(id))
            err("Lapis Collar Addition not registered: %s", id.toString());
        return additions.get(id);
    }

    public static boolean exists(Identifier id) {
        return additions.containsKey(id);
    }

    public static Map<Identifier, LapisCollarAddition> getAll() {
        return additions;
    }

    public static void renderAll(
        ItemStack stack,
        @Nullable LivingEntity entity,
        ModelTransformationMode mode,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay
    ) {
        for (Identifier id : COLLAR.getAdditions(stack)) {
            LapisCollarAddition addition = get(id);
            if (addition != null)
                addition.render(stack, id, entity, mode, matrices, vertexConsumers, light, overlay);
        }
    }

    public static void meowForMe() {
        // shhh my urge...
        // i will add a bell with full physics and ringing NEXT UPDATE!
        // i need to get this one out quickly.
        register(DyeCollarAddition.ID, new DyeCollarAddition());
        register(BellCollarAddition.ID, new BellCollarAddition());
        register(FocusCollarAddition.ID, new FocusCollarAddition());
    }
}
