package com.luxof.lapisworks.init;

import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.items.AmelRing;
import com.luxof.lapisworks.items.AmelStaff;
import com.luxof.lapisworks.items.CastingRing;
import com.luxof.lapisworks.items.DiamondSword;
import com.luxof.lapisworks.items.GoldSword;
import com.luxof.lapisworks.items.IronSword;
import com.luxof.lapisworks.items.PartiallyAmelStaff;
import com.luxof.lapisworks.items.WizardDiaries;
import com.luxof.lapisworks.items.shit.AmelSword;
import com.luxof.lapisworks.items.shit.FullyAmelInterface;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ModItems {
    public static FabricItemSettings fullStack = new FabricItemSettings().maxCount(64);
    public static FabricItemSettings unstackable = new FabricItemSettings().maxCount(1);
    public static FabricItemSettings partamel = new FabricItemSettings().maxCount(1).maxDamage(100);
    
    public static final Item AMEL_ITEM = new Item(fullStack);
    public static final Item AMEL2_ITEM = new Item(fullStack);
    public static final Item AMEL3_ITEM = new Item(fullStack);
    public static final Item AMEL4_ITEM = new Item(fullStack);
    public static final FullyAmelInterface AMEL_STAFF = new AmelStaff(unstackable);
    public static final PartiallyAmelStaff PARTAMEL_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_ACACIA_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_BAMBOO_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_BIRCH_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_CHERRY_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_CRIMSON_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_DARK_OAK_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_EDIFIED_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_JUNGLE_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_MANGROVE_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_MINDSPLICE_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_OAK_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_SPRUCE_STAFF = new PartiallyAmelStaff(partamel);
    public static final PartiallyAmelStaff PARTAMEL_WARPED_STAFF = new PartiallyAmelStaff(partamel);
    public static final FullyAmelInterface AMEL_RING = new AmelRing(unstackable);
    public static final FullyAmelInterface AMEL_RING2 = new AmelRing(unstackable) {
        @Override
        public int whichOneAmI() { return 1; }
    };
    public static final CastingRing CASTING_RING = new CastingRing(unstackable);
    public static final AmelSword DIAMOND_SWORD = new DiamondSword();
    public static final AmelSword IRON_SWORD = new IronSword();
    public static final AmelSword GOLD_SWORD = new GoldSword();
    public static final Item WIZARD_DIARIES = new WizardDiaries(unstackable);
    public static final Item MIND = new BlockItem(ModBlocks.MIND_BLOCK, fullStack);

    private static final List<String> itemNames = List.of(
        "amel",
        "amel2",
        "amel3",
        "amel4",
        "staves/amel_staff",
        "staves/incomplete/generic",
        "staves/incomplete/acacia",
        "staves/incomplete/bamboo",
        "staves/incomplete/birch",
        "staves/incomplete/cherry",
        "staves/incomplete/crimson",
        "staves/incomplete/dark_oak",
        "staves/incomplete/edified",
        "staves/incomplete/jungle",
        "staves/incomplete/mangrove",
        "staves/incomplete/mindsplice",
        "staves/incomplete/oak",
        "staves/incomplete/spruce",
        "staves/incomplete/warped",
        "staves/amel_ring",
        "staves/amel_ring2",
        "staves/ring",
        "amel_constructs/diamond_sword",
        "amel_constructs/iron_sword",
        "amel_constructs/gold_sword",
        "wizard_diaries",
        "mind"
    );
    private static final List<Item> items = List.of(
        AMEL_ITEM,
        AMEL2_ITEM,
        AMEL3_ITEM,
        AMEL4_ITEM,
        (Item)AMEL_STAFF,
        PARTAMEL_STAFF,
        PARTAMEL_ACACIA_STAFF,
        PARTAMEL_BAMBOO_STAFF,
        PARTAMEL_BIRCH_STAFF,
        PARTAMEL_CHERRY_STAFF,
        PARTAMEL_CRIMSON_STAFF,
        PARTAMEL_DARK_OAK_STAFF,
        PARTAMEL_EDIFIED_STAFF,
        PARTAMEL_JUNGLE_STAFF,
        PARTAMEL_MANGROVE_STAFF,
        PARTAMEL_MINDSPLICE_STAFF,
        PARTAMEL_OAK_STAFF,
        PARTAMEL_SPRUCE_STAFF,
        PARTAMEL_WARPED_STAFF,
        (Item)AMEL_RING,
        (Item)AMEL_RING2,
        CASTING_RING,
        DIAMOND_SWORD,
        IRON_SWORD,
        GOLD_SWORD,
        WIZARD_DIARIES,
        MIND
    );

    // i'm mainly using the item tag for checking if anything is Amel, but this is still required for
    // the Mold Amel spell
    public static List<Item> AMEL_MODELS = new ArrayList<Item>(
        List.of(AMEL_ITEM, AMEL2_ITEM, AMEL3_ITEM, AMEL4_ITEM)
    );
    public static List<ItemStaff> HEX_STAVES = new ArrayList<ItemStaff>(List.of(
        HexItems.STAFF_ACACIA,
        HexItems.STAFF_BAMBOO,
        HexItems.STAFF_BIRCH,
        HexItems.STAFF_CHERRY,
        HexItems.STAFF_CRIMSON,
        HexItems.STAFF_DARK_OAK,
        HexItems.STAFF_EDIFIED,
        HexItems.STAFF_JUNGLE,
        HexItems.STAFF_MANGROVE,
        HexItems.STAFF_MINDSPLICE,
        HexItems.STAFF_OAK,
        HexItems.STAFF_SPRUCE,
        HexItems.STAFF_WARPED
    ));
    public static List<PartiallyAmelStaff> PARTAMEL_STAVES = new ArrayList<PartiallyAmelStaff>(List.of(
        PARTAMEL_ACACIA_STAFF,
        PARTAMEL_BAMBOO_STAFF,
        PARTAMEL_BIRCH_STAFF,
        PARTAMEL_CHERRY_STAFF,
        PARTAMEL_CRIMSON_STAFF,
        PARTAMEL_DARK_OAK_STAFF,
        PARTAMEL_EDIFIED_STAFF,
        PARTAMEL_JUNGLE_STAFF,
        PARTAMEL_MANGROVE_STAFF,
        PARTAMEL_MINDSPLICE_STAFF,
        PARTAMEL_OAK_STAFF,
        PARTAMEL_SPRUCE_STAFF,
        PARTAMEL_WARPED_STAFF
    ));

    public static final ItemGroup LapisMagicShitGroup = FabricItemGroup.builder()
        .icon(() -> new ItemStack(AMEL_ITEM))
        .displayName(Text.translatable("itemgroup.lapisworks.lapismagicshitgroup"))
        .entries((context, entries) -> {
            items.forEach(
                (Item item) -> {
                    entries.add(item);
                }
            );
        })
        .build();

    public static void init_shit() {
        Registry.register(
            Registries.ITEM_GROUP,
            id("lapismagicshitgroup"),
            LapisMagicShitGroup
        );
        for (int i = 0; i < items.size(); i++) {
            register(itemNames.get(i), items.get(i));
        }
    }

    public static void register(String name, Item item) {
        Registry.register(Registries.ITEM, id(name), item);
    }
}
