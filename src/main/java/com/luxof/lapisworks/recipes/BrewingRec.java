package com.luxof.lapisworks.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import com.luxof.lapisworks.inv.BrewerInv;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.potionEquals;
import static net.minecraft.item.ItemStack.EMPTY;

import com.mojang.datafixers.util.Either;

import java.util.ArrayList;
import java.util.List;

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

    /** provides a separate result for each potion it's brewing into.
     * does not mutate what's coming in.
     * idx=3 to idx=5 tell you what must be ejected.
     * idx=6 stack tells you how much of the input you've got left. */
    public List<ItemStack> craft(BrewerInv inv) {

        List<ItemStack> brewing = new ArrayList<>(inv.brewingInto);
        List<ItemStack> brewed = new ArrayList<>();
        List<ItemStack> eject = new ArrayList<>();

        for (ItemStack brew : brewing) {
            if (brew.isEmpty()) {
                brewed.add(brew);
                eject.add(EMPTY.copy());
                continue;
            }
            if (isItemBrew) {
                Ingredient ingredient = itemFrom.ingredient;
                int requiredForOneBatch = itemFrom.amount;
                if (ingredient.test(brew) && brew.getCount() >= requiredForOneBatch) {
                    brewed.add(this.itemOutput.copy());
                    eject.add(brew.copyWithCount(brew.getCount() - requiredForOneBatch));
                }

            } else {
                if (potionEquals(potionFrom, brew)) {
                    ItemStack potionOut = brew.copy();
                    PotionUtil.setPotion(potionOut, Potion.byId(potionOutput));
                    brewed.add(potionOut);
                    eject.add(brew.copyWithCount(brew.getCount() - 1));

                } else {
                    brewed.add(brew);
                    eject.add(EMPTY.copy());
                }
            }
        }
        LOGGER.info("brewing len: " + brewing.size());
        LOGGER.info("eject len: " + eject.size());

        ItemStack inputStack = inv.input;

        for (BrewerIngredientWithCount ing : this.input) {
            if (!(ing.ingredient.test(inputStack) && ing.amount >= inputStack.getCount())) continue;
            eject.add(inputStack.copyWithCount(inputStack.getCount() - ing.amount));
            break;
        }
        LOGGER.info("brewing len after ing: " + brewing.size());
        LOGGER.info("eject len after ing: " + eject.size());

        brewed.addAll(eject);
        return brewed;
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
                itemFrom.ingredient.test(stack) && stack.getCount() >= itemFrom.amount
                : potionEquals(stack, potionFrom)
            )
            .count() > 0;

        return validInp && validBrewingInto;
    }

    @Override public boolean fits(int width, int height) { return true; }
    @Override public RecipeType<?> getType() { return Type.INSTANCE; }
}
