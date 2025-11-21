package com.luxof.lapisworks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.AbstractReadStateHolder;
import vazkii.patchouli.client.book.BookEntry;

// VAZKII WHY
// WHY IS THE ADVANCEMENT STRING STORED AS A STRING IN PAGES
// BUT AS AN IDENTIFIER IN ENTRIES????????????????
// PLEASE DUDE JUST FUCKING IMPLEMENT LOGICAL OPERATORS FOR YOUR ADVANCEMENTS
// IT'S NOT EVEN THAT HARD
@Mixin(value = BookEntry.class, remap = false)
public abstract class BookEntryMixin2 extends AbstractReadStateHolder implements Comparable<BookEntry> {
    @Shadow private boolean locked;
    private String logicalAdvs;

    @Inject(
        method = "updateLockStatus()V",
        at = @At(
            value = "INVOKE", // not sure what to use here so
            target = "vazkii/patchouli/client/base/ClientAdvancements.hasDone(Ljava/lang/String;)Z",
            shift = At.Shift.BY,
            by = 3 // 3 opcodes after that
        )
    )
    public void updateLockStatus() {
        if (logicalAdvs != null) this.locked = !ClientAdvancements.hasDone(logicalAdvs);
    }
}
