package com.luxof.lapisworks.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import com.luxof.lapisworks.inv.BrewerInv;

import com.mojang.datafixers.util.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class BrewingRec implements Recipe<BrewerInv> {
    private final Identifier id;
    private final boolean isItemBrew;

    private final BrewerIngredientWithCount itemFrom;
    private final ItemStack itemOutput;
    private final String potionFrom;
    private final String potionOutput;

    private final List<BrewerIngredientWithCount> input;

    public static class Type implements RecipeType<BrewingRec> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }

    public static class BrewerIngredientWithCount {
        public Ingredient ingredient;
        public int amount;
        protected BrewerIngredientWithCount(Ingredient ing, int am) {
            this.ingredient = ing;
            this.amount = am;
        }
        protected static BrewerIngredientWithCount fromJson(JsonObject json) {
            return new BrewerIngredientWithCount(
                Ingredient.fromJson(json),
                JsonHelper.getInt(json, "count")
            );
        }

        protected static BrewerIngredientWithCount fromJson(JsonElement json) {
            if (!json.isJsonObject()) throw new JsonSyntaxException("Invalid field in (probably the input field of) the recipe: " + BrewingRecSerializer.currentId.toString());
            return fromJson(json.getAsJsonObject());
        }

        protected void write(PacketByteBuf buf) {
            ingredient.write(buf);
            buf.writeInt(amount);
        }

        protected static void write(PacketByteBuf buf, BrewerIngredientWithCount ing) {
            ing.ingredient.write(buf);
            buf.writeInt(ing.amount);
        }

        protected static BrewerIngredientWithCount read(PacketByteBuf buf) {
            return new BrewerIngredientWithCount(
                Ingredient.fromPacket(buf),
                buf.readInt()
            );
        }
    }

    public BrewingRec(
        Identifier id,
        BrewerIngredientWithCount from,
        List<BrewerIngredientWithCount> input,
        ItemStack to
    ) {
        this.id = id;
        this.input = input;
        this.isItemBrew = true;

        this.itemFrom = from;
        this.itemOutput = to;

        this.potionFrom = null;
        this.potionOutput = null;
    }
    public BrewingRec(
        Identifier id,
        Identifier from,
        List<BrewerIngredientWithCount> input,
        Identifier to
    ) {
        this.id = id;
        this.input = input;
        this.isItemBrew = false;

        this.itemFrom = null;
        this.itemOutput = null;

        this.potionFrom = from.toString();
        this.potionOutput = to.toString();
    }

    @Override
    public Identifier getId() { return this.id; }
    /** if yields <code>true</code>, any <code>Either</code>-returning methods you find will only have
     * their left sides present. opposite if not. */
    public boolean isItemBrew() { return this.isItemBrew; }
    public List<BrewerIngredientWithCount> getPossibleInputs() { return this.input; }

    public Either<BrewerIngredientWithCount, String> getFrom() {
        return this.isItemBrew ? Either.left(this.itemFrom) : Either.right(this.potionFrom);
    }
    public Either<ItemStack, String> getOutput() {
        return this.isItemBrew ? Either.left(this.itemOutput) : Either.right(this.potionOutput);
    }


    // hmm..
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) { return ItemStack.EMPTY.copy(); }

    @Nullable
    private ItemStack getFirstValidBrewingInto(List<ItemStack> stacks) {
        try {
            return stacks.stream().filter(itemFrom.ingredient::test).findFirst().get();
        } catch (NoSuchElementException e) { return null; }
    }

    /** provides a separate result for each potion it's brewing into.
     * does not mutate what's coming in, though it has many checks (doesn't always craft).
     * returns 4th, 5th and 6th stacks which tell you what must be ejected.
     * the 7th stack tells you how much of the input you've got left.
     * the 7th element may not exist if this method is invoked without `recipe.matches(s,w) == true`. */
    public List<ItemStack> craft(BrewerInv inv) {
        ItemStack inputStack = inv.input;

        List<ItemStack> brewing = new ArrayList<>(inv.brewingInto);
        List<ItemStack> eject = new ArrayList<>();

        for (ItemStack brew : brewing) {
            if (brew.isEmpty()) continue;
            if (isItemBrew) {
                Ingredient needFrom = itemFrom.ingredient;
                int requiredForOneBatch = itemFrom.amount;
                if (needFrom.test(brew) && brew.getCount() >= requiredForOneBatch) {
                    eject.add(brew.copyWithCount(brew.getCount() - requiredForOneBatch));
                    brew = this.itemOutput.copy();
                }
            } else {
                if (Potion.byId(potionFrom) == PotionUtil.getPotion(brew)) {
                    eject.add(brew.copyWithCount(brew.getCount() - 1));
                    PotionUtil.setPotion(brew, Potion.byId(potionOutput));
                }
            }
        }
        
        for (BrewerIngredientWithCount ing : this.input) {
            if (!ing.ingredient.test(inputStack) && ing.amount >= inputStack.getCount()) continue;
            eject.add(inputStack.copyWithCount(inputStack.getCount() - ing.amount));
            break;
        }

        brewing.addAll(eject);
        return brewing;
    }
    
    /** don't use this. */
    @Override
    public ItemStack craft(BrewerInv inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() { return BrewingRecSerializer.INSTANCE; }

    @Override
    public boolean matches(BrewerInv inventory, World world) {
        ItemStack inputStack = inventory.input;

        boolean validInp = this.input.stream()
            .filter(ing -> ing.ingredient.test(inputStack) && inputStack.getCount() >= ing.amount)
            .count() > 0;

        boolean validBrewingInto = inventory.brewingInto.stream()
            .filter(stack -> isItemBrew ?
                itemFrom.ingredient.test(stack) && stack.getCount() >= itemFrom.amount :
                PotionUtil.getPotion(stack) == Potion.byId(potionFrom))
            .count() > 0;

        return validInp && validBrewingInto;
    }

    @Override public boolean fits(int width, int height) { return true; }
    @Override public RecipeType<?> getType() { return Type.INSTANCE; }
}
