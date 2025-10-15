package com.luxof.lapisworks.VAULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** syntax sugar */
public class Flags {
    public static final int EQ_TRINKETS = 0;
    public static final int HANDS = 1;
    public static final int HOTBAR = 2;
    public static final int INVENTORY = 3;
    private static final List<Integer> BASE = List.of(EQ_TRINKETS, HANDS, HOTBAR, INVENTORY);
    public static final int SEARCH_EQ_TRINKETS = 4;
    public static final int INVITEM_WORK_IN_EQ_TRINKETS = 5;
    //public static final int STACKS_WORK_IN_EQ_TRINKETS = 6; // <-- how would i even impl this anyway lmao
    public static final int SEARCH_HANDS = 7;
    public static final int INVITEM_WORK_IN_HANDS = 8;
    public static final int STACKS_WORK_IN_HANDS = 9;
    public static final int SEARCH_HOTBAR = 10;
    public static final int INVITEM_WORK_IN_HOTBAR = 11;
    public static final int STACKS_WORK_IN_HOTBAR = 12;
    public static final int SEARCH_INVENTORY = 13;
    public static final int INVITEM_WORK_IN_INVENTORY = 14;
    public static final int STACKS_WORK_IN_INVENTORY = 15;
    public static final int INVITEM = 1;
    public static final int STACKS = 2;
    private static final List<Integer> CAN_SEARCH_FOR = List.of(INVITEM, STACKS);
    private List<Integer> flags;
    // used for maffs
    private static final int stepsBetween = SEARCH_HANDS - SEARCH_EQ_TRINKETS;


    public static final Flags PRESET_StacksUptoHotbar_InvItemUptoHands = Flags.build(
        Flags.SEARCH_EQ_TRINKETS,  Flags.INVITEM_WORK_IN_EQ_TRINKETS,  //Flags.STACKS_WORK_IN_EQ_TRINKETS,
        Flags.SEARCH_HANDS,        Flags.INVITEM_WORK_IN_HANDS,        Flags.STACKS_WORK_IN_HANDS,
        Flags.SEARCH_HOTBAR,       Flags.STACKS_WORK_IN_HOTBAR
    );
    /** use case: finding Amel */
    public static final Flags PRESET_Stacks_InvItem_UpToHotbar = Flags.build(
        Flags.SEARCH_EQ_TRINKETS,  Flags.INVITEM_WORK_IN_EQ_TRINKETS,  //Flags.STACKS_WORK_IN_EQ_TRINKETS,
        Flags.SEARCH_HANDS,        Flags.INVITEM_WORK_IN_HANDS,        Flags.STACKS_WORK_IN_HANDS,
        Flags.SEARCH_HOTBAR,       Flags.INVITEM_WORK_IN_HOTBAR,       Flags.STACKS_WORK_IN_HOTBAR
    );


    private Flags(List<Integer> flags) { this.flags = flags; };
    /** Build <code>Flags</code> for use.
     * <p>The only flags that should concern you building this are <code>SEARCH_*</code> and <code>*_WORK_IN_*</code> flags.
     * This is because those flags are the ones that will be looked at. None others.
     * <p>See the PRESET_* constants here for examples. */
    public static Flags build(int... flags) { return new Flags(Arrays.stream(flags).boxed().toList()); }

    // as long as i follow the organization exhibited above, these formulae will work
    /** use <code>EQ_TRINKTS</code>, <code>HANDS</code> or <code>HOTBAR</code> etc. */
    public boolean canSearch(int flag) {
        return hasFlag(SEARCH_EQ_TRINKETS + flag * stepsBetween) && BASE.contains(flag);
    }
    /** <code>whatCan</code> can be <code>INVITEM</code> or <code>STACKS</code>.
     * <p><code>inWhat</code> can be <code>EQ_TRINKTS</code> or <code>HANDS</code> etc. */
    public boolean canWorkIn(int whatCan, int inWhat) {
        if (!(BASE.contains(inWhat) && CAN_SEARCH_FOR.contains(whatCan))) return false;
        return flags.contains(SEARCH_EQ_TRINKETS + stepsBetween * inWhat + whatCan);
    }

    public boolean hasFlag(int flag) { return flags.contains(flag); }
    /** example: pass in <code>HOTBAR</code>, this will return all present flags about it,
     * like <code>SEARCH_HOTBAR</code>. You don't use this unless you're making a VAULT type. */
    public Flags getKindsOfFlag(int flag) {
        List<Integer> newFlags = new ArrayList<>();
        for (
            int i = SEARCH_EQ_TRINKETS + flag * stepsBetween;
            i < SEARCH_EQ_TRINKETS + stepsBetween + flag * (stepsBetween);
            i++
        ) { if (hasFlag(i)) { newFlags.add(i); } }
        return new Flags(newFlags);
    }
}
