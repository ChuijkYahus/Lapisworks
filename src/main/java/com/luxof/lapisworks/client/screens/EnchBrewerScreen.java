package com.luxof.lapisworks.client.screens;

import com.luxof.lapisworks.recipes.BrewingRec;

import static com.luxof.lapisworks.Lapisworks.id;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnchBrewerScreen extends HandledScreen<EnchBrewerScreenHandler> {
    private static final Identifier TEXTURE = id("textures/gui/brewer/enchanted.png");

    public EnchBrewerScreen(EnchBrewerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private void drawIsBrewing(DrawContext context, int x, int y) {
        int isBrewingIconX = x + 102;
        int isBrewingIconY = y + 30;

        int yesBrewingIconX = 176;
        int yesBrewingIconY = 10;
        context.drawTexture(
            TEXTURE,
            isBrewingIconX,
            isBrewingIconY,
            yesBrewingIconX,
            yesBrewingIconY,
            18,
            18
        );
    }

    private void drawFuelRod(DrawContext ctx, int x, int y) {
        int fuel = this.handler.getFuel();
        int rodHeight = 10;
        int filledHeight = fuel; // fuel is 0-10 anyway

        int rodX = x + 79;
        int rodY = y + 34 + (rodHeight - filledHeight);
        int rodU = 176;
        int rodV = rodHeight - filledHeight;

        ctx.drawTexture(
            TEXTURE,
            rodX,
            rodY,
            rodU,
            rodV,
            3,
            filledHeight
        );
    }

    private void drawBrewTimeRod(DrawContext ctx, int x, int y) {
        int brewTime = this.handler.getBrewTime();
        if (brewTime <= 0) return; // i don't wanna know what happens here when brewTime is -1
        int rodHeight = 10;
        int maxBrewTime = 200;
        int filledHeight = brewTime * (rodHeight / maxBrewTime);

        int rodX = x + 92;
        int rodY = y + 34 + (rodHeight - filledHeight);
        int rodU = 179;
        int rodV = rodHeight - filledHeight;

        ctx.drawTexture(
            TEXTURE,
            rodX,
            rodY,
            rodU,
            rodV,
            3,
            filledHeight
        );
    }

    private void drawBrewingRods(DrawContext ctx, int x, int y) {
        List<BrewingRec> recipes = handler.getRecipes();
        List<ItemStack> brewingInto = handler.inventory.brewingInto;
        drawBrewingRod(ctx, x + 73, y + 57, 5, 3, brewingInto.get(0), recipes);
        drawBrewingRod(ctx, x + 86, y + 62, 3, 1, brewingInto.get(1), recipes);
        drawBrewingRod(ctx, x + 96, y + 57, 5, 3, brewingInto.get(2), recipes);
    }

    private void drawBrewingRod(
        DrawContext ctx,
        int rodX,
        int rodY,
        int width,
        int height,
        ItemStack stack,
        List<BrewingRec> recipes
    ) {
        for (BrewingRec recipe : recipes) {
            if (recipe.isItemBrew()) {
                if (!recipe.getFrom().left().get().ingredient.test(stack)) continue;
                drawOnBrewingRod(
                    ctx,
                    rodX,
                    rodY,
                    width,
                    height,
                    PotionUtil.getColor(recipe.getOutput().left().get())
                );
                break;
            } else {
                Potion fromPotion = Potion.byId(recipe.getFrom().right().get());
                if (PotionUtil.getPotion(stack) != fromPotion) continue;
                drawOnBrewingRod(
                    ctx,
                    rodX,
                    rodY,
                    width,
                    height,
                    PotionUtil.getColor(Potion.byId(recipe.getOutput().right().get()))
                );
                break;
            }
        }
    }

    private int darken(int color) {
        int R = color & 255; // get lower 8 bits from the 24
        int G = color & (255 * 256); // get mid 8 bits
        int B = color & (255 * 256 * 256); // get upper 8 bits
        return (
            Math.max(R - 68, 0) + // R - 0x44
            Math.max(G - 68 * 256, 0) +
            Math.max(B - 68 * 256 * 256, 0)
        );
    }

    private void drawOnBrewingRod(
        DrawContext ctx,
        int rodX,
        int rodY,
        int width,
        int height,
        int color // hex code
    ) {
        int border = darken(color);
        if (width > height) {
            ctx.drawHorizontalLine(rodX, rodX+width, rodY, border);
            ctx.drawHorizontalLine(rodX, rodX+width, rodY+height-1, border);
            for (int y = rodY; y < rodY+height-1; y++) {
                ctx.drawVerticalLine(rodX, rodX+width, y, color);
            }
        } else {
            ctx.drawVerticalLine(rodX, rodY, rodY+height, border);
            ctx.drawVerticalLine(rodX+width-1, rodY, rodY+height, border);
            for (int x = rodX; x < rodX+width-1; x++) {
                ctx.drawVerticalLine(x, rodY, rodY+height, color);
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (this.handler.getIsBrewing()) {
            drawIsBrewing(context, x, y);
            drawBrewingRods(context, x, y);
        };
        drawFuelRod(context, x, y);
        drawBrewTimeRod(context, x, y);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
