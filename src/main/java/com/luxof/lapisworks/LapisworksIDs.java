package com.luxof.lapisworks;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LapisworksIDs {
    /** shorthand */
    private static Text t(String str) { return Text.translatable(str); }

    // packets
    public static final Identifier OPEN_CASTING_GRID = id("open_casting_grid");
    public static final Identifier SEND_SENT = id("send_sent");
    public static final Identifier SEND_PWSHAPE_PATS = id("send_pwshape_pats");
    public static final Identifier DOWSE_TS = id("dowse_ts");
    public static final Identifier DOWSE_RESULT = id("dowse_result");

    // mpp
    public static final Identifier BLOCKING_MPP = id("blocking");
    public static final Identifier AMEL_ORB_IS_FILLED = id("amel_orb_is_filled");

    // items and shit
    public static final Identifier LAPIS_MAGIC_SHIT_GROUP = id("lapismagicshitgroup");
    public static final Identifier AMEL_TAG = id("amel");

    // advancements
    public static final Identifier ENCHSENT_ADVANCEMENT = id("rediscover_enchsent");
    public static final Identifier FLAY_ARTMIND_ADVANCEMENT = id("rediscover_flay_artmind");
    public static final Identifier HASTENATURE_ADVANCEMENT = id("rediscover_hastenature");
    public static final Identifier GOT_LAPIS = id("got_lapis");
    public static final Identifier CLUEDIN_ADVANCEMENT = id("cluedin");

    // beeginfusions
    public static final Identifier ENHANCE_ENCHANTED_BOOK = id("enhance_enchanted_book");
    public static final Identifier MAKE_GENERIC_PARTAMEL = id("make_generic_partamel");
    // simple mind infusions
    public static final Identifier SIMPLE_MIND_INTO_AMETHYST = id("make_budding_am");
    public static final Identifier JUKEBOX_INTO_LIVE_JUKEBOX = id("make_live_jukbx");
    public static final Identifier EMPTY_IMP_INTO_SIMP = id("make_simp");
    public static final Identifier UNFLAY_FLAYED_VILLAGER = id("unflay_flayed_villager");

    // V.A.U.L.T.
    public static final Identifier PLAYER_VAULT = id("player_vault");
    public static final Identifier CASTENV_VAULT = id("castenv_vault");

    // tooltips
    public static final Text DIARIES_TOOLTIP_1 = t("tooltips.lapisworks.wizard_diaries.1");
    public static final Text DIARIES_TOOLTIP_2 = t("tooltips.lapisworks.wizard_diaries.2");
    public static final Text DIARIES_TOOLTIP_3 = t("tooltips.lapisworks.wizard_diaries.3");
    public static final Text DIARIES_TOOLTIP_4 = t("tooltips.lapisworks.wizard_diaries.4");

    public static final Text IMBUEABLE = t("mishaps.lapisworks.descs.imbueable");
    public static final Text INFUSEABLE_WITH_SMIND = t("mishaps.lapisworks.descs.smind_infuseable");
    public static final Text READABLE = t("mishaps.lapisworks.descs.readable");
    public static final Text WRITEABLE = t("mishaps.lapisworks.descs.writeable");
    public static final Text NON_IOTAHOLDER = t("mishaps.lapisworks.descs.noniotaholder");
    public static final Text READONLY_HOLDER = t("mishaps.lapisworks.descs.readonly");
    public static final Text SCROLL = t("mishaps.lapisworks.descs.scrolls");
    public static final Text PAT_SCROLL = t("mishaps.lapisworks.descs.scroll_with_pat");
    public static final Text GREAT_SCROLL = t("mishaps.lapisworks.descs.scroll_with_gs");
    public static final Text FULL_SIMPLE_MIND = t("mishaps.lapisworks.bad_block.full_mind");
    public static final Text MAINHAND = t("hands.lapisworks.main");
    public static final Text OFFHAND = t("hands.lapisworks.off");
    public static final Text GENERIC_BADITEM = t("mishaps.lapisworks.some_hand.bad_item.generic");
    public static final Text MIND_BLOCK = t("block.lapisworks.mind");
    public static final Text LIVE_JUKEBOX_BLOCK = t("block.lapisworks.amel_constructs.live_jukebox");
    public static final Text SIMP_IMP_BLOCK = t("block.lapisworks.amel_constructs.simple_impetus");
    public static final Text NOTELIST = t("mishaps.lapisworks.invalid_iota.need_notelist.intlist");
    public static final Text NOTELIST_MOFO = t("mishaps.lapisworks.invalid_iota.need_notelist.intlistmotherfucker");
    public static final Text NOTELIST_OUTOFRANGE = t("mishaps.lapisworks.invalid_iota.need_notelist.outofrange");
    public static final Text SCRYING_MIND_START = t("render.lapisworks.scryinglens.mind.start");
    public static final Text SCRYING_MIND_END = t("render.lapisworks.scryinglens.mind.end");
    public static final Text LAPISMAGICSHITGROUPTEXT = t("itemgroup.lapisworks.lapismagicshitgroup");
    public static final Text GOT_ALL_DIARIES = t("notif.lapisworks.wizard_diaries.all_gotten");
    public static final Text ENCHBOOK_WITH_ONE_ENCH = t("mishaps.lapisworks.descs.oneench_enchbook");
    public static final Text ENCHBOOK_WITH_NOTONE_ENCH = t("mishaps.lapisworks.descs.moreench_enchbook");
    public static final Text AMEL = t("hexcasting.mishap.bad_item.amel");
    public static final Text DOWSER_COULDNT_FIND = t("notif.lapisworks.dowser.couldnt_find");

    // mishaps
    public static final String NOT_ENOUGH = "mishaps.lapisworks.not_enough_items";
    public static final String ALREADY_ENCHANTED = "mishaps.lapisworks.already_enchanted";
    public static final String SPECHAND_NOITEM = "mishaps.lapisworks.some_hand.no_item";
    public static final String SPECHAND_BADITEM = "mishaps.lapisworks.some_hand.no_item";
    public static final String NOT_EQUIPPED = "mishaps.lapisworks.equipped.not";
    public static final String WRONG_EQUIPPED = "mishaps.lapisworks.equipped.bad";

    public static final String INFUSED_AMEL = "lapisworks:infused_amel";
    public static final String DOWSER_NOT_ENOUGH_MEDIA = "notif.lapisworks.dowser.not_enough_media";
    public static final String GEODE_DOWSER_REQUEST = id("request_from_geode_dowser_item").toString(); // doesn't work otherwise

    // lapixtended
    public static final Identifier MAKE_PARTAMEL_WAND = id("make_partamel_wand");
    // lexical
    public static final String RH_HOLDER = id("righthanded_holder").toString();
    public static final String HEXICAL_IMPETUS_HAND = "impetus_hand";
    public static final String IS_IN_CRADLE = "lapisworks:is_in_cradle";
    public static final Text HOLDER_MAINHAND = t("render.lapisworks.scryinglens.holder.mainhand");
    public static final Text HOLDER_OFFHAND = t("render.lapisworks.scryinglens.holder.offhand");
    // lapisal
    public static final Identifier OPEN_DIMENSIONAL_RIFT = id("open_dimensional_rift");
    public static final Identifier TURN_MIND_TO_WISP = id("turn_mind_to_wisp");
}
