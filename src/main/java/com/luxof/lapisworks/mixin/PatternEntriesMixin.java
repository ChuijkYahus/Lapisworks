package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexDir;

import static com.luxof.lapisworks.Lapisworks.log;
import static com.luxof.lapisworks.init.Mutables.Mutables.wizardDiariesGainableAdvancements;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.isPWShapePattern;
import static com.luxof.lapisworks.init.ThemConfigFlags.specificToGenericId;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import com.llamalad7.mixinextras.sugar.Local;

import com.luxof.lapisworks.mixinsupport.AccessPWBookEntries;
import com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport;

import dev.tizu.hexcessible.entries.BookEntries;
import dev.tizu.hexcessible.entries.PatternEntries;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.BookRegistry;

@Mixin(value = PatternEntries.class, remap = false)
public class PatternEntriesMixin implements HexcessiblePWShapeSupport {

    @Shadow private List<PatternEntries.Entry> entries;
    @Shadow private List<String> perWorld;
    @Unique public HashMap<String, PatternEntries.Entry> pwShapePatterns = new HashMap<>();

    @Inject(
        method = "reindex",
        at = @At("HEAD")
    )
    public void lapisworks$clearThosePWShapePatternEntries(CallbackInfo ci) {
        pwShapePatterns.clear();
    }

    @Inject(
        method = "lambda$reindex$1",
        at = @At(
            value = "INVOKE",
            target = "Ldev/tizu/hexcessible/entries/BookEntries;get(Lnet/minecraft/util/Identifier;)Ljava/util/List;",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void lapisworks$registerPWShapePatternsDifferently(
        RegistryKey<ActionRegistryEntry> key,
        CallbackInfo ci,
        @Local Identifier id,
        @Local String name,
        @Local Supplier<Boolean> checkLock,
        @Local HexDir dir,
        @Local List<List<HexAngle>> sig
    ) {
        String specificId = id.toString();
        String genericId = specificToGenericId.get(specificId);

        if (isPWShapePattern(specificId)) {
            List<BookEntries.Entry> bookEntries = getEntriesForPWShapePattern(genericId);
            log("Yo. %s %b", specificId, shouldLockPWShapePattern(genericId, bookEntries));
            if (shouldLockPWShapePattern(genericId, bookEntries)) {
                pwShapePatterns.put(
                    specificId,
                    new PatternEntries.Entry(specificId, name, checkLock, dir, sig, bookEntries, 0)
                );
                ci.cancel();
            }

            // if it's not something that's locked by an advancement hide it if not chosen
            // useful for Robbie's Exaltation
            int chosen = chosenFlags.get(genericId);
            int specificIdSuffix = Integer.parseInt(specificId.substring(genericId.length()));
            log("uh oh %b", chosen == specificIdSuffix);

            if (chosen != specificIdSuffix) {
                ci.cancel();
            }
        }
    }

    @Unique
    private static Identifier bookId = new Identifier("hexcasting", "thehexbook");
    @Unique
    private static boolean shouldLockPWShapePattern(
        String genericId,
        List<BookEntries.Entry> bookEntries
    ) {
        BookContents hexBook = BookRegistry.INSTANCE.books.get(bookId).getContents();

        for (BookEntries.Entry bookEntry : bookEntries) {
            for (BookPage page : hexBook.entries.get(bookEntry.entryid()).getPages()) {

                // please don't do anything funky... thanks.
                String pageType = JsonHelper.getString(page.sourceObject, "type");
                if (!pageType.equals("hexcasting:lapisworks/pwshape")) continue;

                return true;

            }
        }
        return false;
    }

    @Unique
    private static List<BookEntries.Entry> getEntriesForPWShapePattern(String genericId) {
        return ((AccessPWBookEntries)BookEntries.INSTANCE).getEntriesOfPWShapePattern(genericId);
    }

    @Override
    public void unlockPWShapeByAdvancement(Identifier advancementId) {
        String genericId = wizardDiariesGainableAdvancements.get(advancementId);
        if (genericId == null) return;
        int chosen = chosenFlags.get(genericId);

        PatternEntries.Entry entry = pwShapePatterns.get(genericId + String.valueOf(chosen));
        entries.add(entry);
    }

    @Override
    public void calibratePWShapeUnlocks() {
        for (Identifier advancement : wizardDiariesGainableAdvancements.keySet()) {
            if (ClientAdvancements.hasDone(advancement.toString()))
                unlockPWShapeByAdvancement(advancement);
        }
    }
}
