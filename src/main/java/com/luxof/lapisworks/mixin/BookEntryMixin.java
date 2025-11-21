package com.luxof.lapisworks.mixin;

import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.book.AbstractReadStateHolder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

// i hate the "remap" option
// can't you just fucking ignore what you can't remap and fail if it's inaccessible later you hucklebuck
// chucklefuck bitchshitting pissdrinking donkeying sack of shit?
// i am very professional
@Mixin(value = BookEntry.class)
public abstract class BookEntryMixin extends AbstractReadStateHolder implements Comparable<BookEntry> {
    // please transfer to the other mixin i'm fucking begging you right now
    // IT TRANSFERS
    private String logicalAdvs;

    @Inject(
        // browsing bytecode :)
        method = "<init>(Lcom/google/gson/JsonObject;Lnet/minecraft/util/Identifier;Lvazkii/patchouli/common/book/Book;Ljava/lang/String;)V",
        at = @At("TAIL")
    )
    private void constructor(JsonObject root, Identifier id, Book book, @Nullable String addedBy, CallbackInfo ci) {
        logicalAdvs = JsonHelper.getString(root, "vazkii_why", null);
    }
}
