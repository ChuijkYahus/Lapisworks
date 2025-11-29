package com.luxof.lapisworks.items;

import at.petrak.hexcasting.common.lib.HexSounds;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_1;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_2;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_3;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_4;
import static com.luxof.lapisworks.LapisworksIDs.GOT_ALL_DIARIES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.luxof.lapisworks.init.Mutables.Mutables;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WizardDiaries extends Item {
    public WizardDiaries(Settings settings) { super(settings); }

    /** returns <code>null</code> if the advancement doesn't exist. */
    @Nullable
    private Boolean advancementDone(
        ServerPlayerEntity player,
        Identifier advId
    ) {
        Advancement adv = player.getServer().getAdvancementLoader().get(advId);
        if (adv == null) {
            LOGGER.warn(advId.toString() + " isn't an advancement that exists right now!");
            return null;
        }
        return player.getAdvancementTracker().getProgress(adv).isDone();
    }

    private TypedActionResult<ItemStack> finishUse(
        ServerPlayerEntity suser,
        ItemStack handStack
    ) {
        Criteria.CONSUME_ITEM.trigger(suser, handStack);
        suser.getStatHandler().increaseStat(suser, Stats.USED.getOrCreateStat(this), 1);
        handStack.decrement(1);

        return TypedActionResult.success(handStack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(HexSounds.READ_LORE_FRAGMENT, 1.0F, 1.0F);
        ItemStack handStack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity)) {
            handStack.decrement(1);
            return TypedActionResult.success(handStack);
        }
        ServerPlayerEntity suser = (ServerPlayerEntity)user;
        ServerAdvancementLoader advLoader = suser.getServer().getAdvancementLoader();
        
        if (!advancementDone(suser, id("got_lapis"))) {
            suser.sendMessage(GOT_ALL_DIARIES);

            return TypedActionResult.success(handStack);
        }

        List<Identifier> shuffled = new ArrayList<Identifier>(Mutables.wizardDiariesGainableAdvancements);
        Advancement chosenAdvancement = null;
        Collections.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); i++) {
            Identifier advId = shuffled.get(i);

            chosenAdvancement = advLoader.get(advId);

            Boolean advDone = advancementDone(suser, advId);
            if (advDone == null) continue;
            else if (!advDone) break;
        }

        if (chosenAdvancement == null) {
            suser.sendMessage(GOT_ALL_DIARIES, true);
            suser.addExperience(100);
        } else {
            suser.getAdvancementTracker().grantCriterion(chosenAdvancement, "grant");
        }

        return finishUse(suser, handStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        tooltip.add(DIARIES_TOOLTIP_1.copy().formatted(Formatting.DARK_PURPLE));
        tooltip.add(DIARIES_TOOLTIP_2.copy().formatted(Formatting.DARK_PURPLE));
        tooltip.add(DIARIES_TOOLTIP_3.copy().formatted(Formatting.DARK_PURPLE));
        tooltip.add(DIARIES_TOOLTIP_4.copy().formatted(Formatting.DARK_PURPLE));
    }
}
