Ah hell naw chat you're tweaking I can't do this fucking marathon
Luxof didn't even DREAM of this shit in his prime (1.1.0-1.2.0)
1.5.1
- Updated Cubic Exaltation and Spherical Exaltation's out-of-date names
- Updated Cubic Exaltation's arguments to be `[pattern], vec, vec, bool` instead of `[pattern], vec, num, bool`
- Fixed bug where Cubic Exaltation wouldn't clear the arguments after use
- Added the Simple Mind Container (and its scrying lens info overlay)
- Added Thought Sieving
- Added Mind Liquefaction
- Added Cognition Purification
1.5.1.5
- fixed bug where Simple Mind Container wouldn't gain Mind (tied to bug below, actually)
- fixed bug where just by existing for long enough you could nullify villager consumption cd
- fixed bug where Mind Liquefaction wouldn't take anything but a full container
1.5.2
- Patchouli and hexdoc interop for per world shape patterns
- Refactored like 40% of the code to be less painful to fw lmao
- fixed a capitalization mistake and "Format Error:" in some places of the book
- fixed no translation for the individual variants of Summon Enchanted Sentinel
- fixed weird ass bug with Enchant X body part patterns where they'd try to take negative Amel
  (i dunno if this one was purely in the devving or not)
- fixed enchanted attrs not carrying over across logins
- Added what I forgor to the sword descs
- made Thought Sieve consume 50% of the mind, and not just 25%
- Added Live Jukebox and it's companion pattern "Teach Song"
- Added Imbue Mind with recipes:
    - Amethyst Block -> Budding Amethyst
    - Jukebox -> Live Jukebox
1.5.3
- Fixed enchantments being able to take too much Amel and making negative nums
- Fixed Imbue Amel taking too much media
- Fixed Imbue Amel not properly doing it's fucking thing of repairing shit
- Fixed a potential crash in Imbue Amel
- Fixed LivingEntity mixin crashing because i can't mixin to constructors for shit (just removed the inject)
- Made the Patchouli book read better in some places
- Renamed the old Imbue Mind (the one that recharges stuff) to Mind Liquefaction
- Mainhand-reading patterns have been generalized to any hand
- Also generalized many patterns to take any hand
- generalization has allowed most patterns to work on casting circles too
- Mainhand mishap has been generalized to any hand as well
- Not Enough Items In Offhand mishap has been generalized as well
- Added Equivalent Block D.
- Added Equal Block Dist.
- Added Hastenature
- Imbue Amel can now lowkey make enchanted books more powerful
1.5.4
- Renamed the "Wrong Item In a Hand" mishap to "Wrong Item In Hand" in the book
- Fixed Imbue Amel using the Incorrect Item mishap for the off-hand where it needs Amel and the
  Wrong Item In Hand mishap for the main-hand where it needs an imbueable item
- Made Simple Mind Container's filling have more frames
  (now a whopping 15 instead of 4, that's almost 4²! /sarcasm)
- Gave Enchanted Book enhancement with Imbue Amel an actual page in the book
- Made Enchanted Book enhancement with Imbue Amel take 20 * previous level Amel
- Added the Jump Slate
  (WHY WAS THAT SO HARD TO MIXIN)
- Added variants of the Jump Slate
  gave em support with Mold Amel too
- Turned the ancient wizard fully gender neutral this time (headcanon the gender yourself)
1.5.4.5
Whoopsies
- "pages.lapisworks.imbuement_artmind.reflection2" lmao deleted that
- Fixed Jump Slate stuff appearing before enlightenment
  made it appear after enlightenment and got_lapis in a scuffed ahh way that is hopefully never seen
1.5.5
- Fixed up the ingame book a little (stopped implying GSent was visible, etc.)
  Also fixed the bug where it wouldn't load on multiplayer!
- Gave the Warped Infused Staff an actual translation key (bruh how did i forger that)
- Fixed possible bug with Hastenature (wtf)
- Fixed bug with PWShape interop for Patchouli
- Fixed bug where Jump Slate would always jump forwards no matter what
- Gave the web book a custom icon
- Gave Jump Slate a friend: Rebound Slate
- Hextended Gear's staves have Partially Amel-infused variations of them now
  my hands bleed
  31 staves
  - extended wood staves
  - mossy staves
  - prismarine staves
  - obsidian staves
  - purpur staves
  - extended fanciful staves
- fully amel wands have 28% hex grid boost
- partially amel wands have 40% hex grid boost
- Buffed partially amel staves' durability from 100 to 200
- Debuffed fully amel staves' hex grid boost from 25% to 20%
- Debuffed partially amel staves' hex grid boost from 33% to 30%
- Amel staff and incomplete amel staves are now held like the vanilla hexcasting staves
- Changed every hex grid space modifying item to multiply by base, not by total.
- Also added the block counterpart to the Drawing Orb, the Amel-tuned Drawing Orb

**NOTE FOR MIGRATORS:**
per-world shape patterns have once again changed, this will be the last time. i think.
1.5.5.5
- Fixed staffcasting not working if you don't have hextended (WTF????)
- Also fixed some staves not opening their spellcasting gui
1.1.7: "haha i added some funny swords"
1.5.6's honest reaction to that information:
- Imbue Amel now costs 2xAmel *in dust*, not *in shards*
- Many patterns that previously took Amel are more convenient now.
- Simple Mind Containers now look good in the offhand too
- Refactored, like, another 40% of the codebase
- Fixed up the book a little
- Fixed a few errors in the book
- Fixed Amel Swords not working for a bit
- Fixed Amel Wand being able to be made with 10 Amel for a bit and shit like that (wtf?)
- Fixed Enchant Skin
- Fixed Imbue Amel and mishap bugs
- Fixed Lapisworks crashing with hexxy4's Hex Casting build
- Fixed Mold Amel saying it needs Amel and not a moldable substance when it doesn't find a moldable substance
- Fixed partamel variants of the Obsidian wand/staff not existing
- Fixed the Incomplete Staff of Amethyst Lazuli never being able to graduate to a complete staff
- Lapisworks can be datapacked for shit now (will add a wiki for how right after this update)
- Added Reclaim Amethyst
- Added the Amel Jar to store 4 stacks of Amel
  - It also renders on you when you equip it in the belt slot
  - Works from your hotbar too
- Added the Enchantment Energy Container to store 16 stacks of Amel
  - Can't be equipped but works from your hotbar like the Amel Jar
- Added interop with Hexical
  - Added the Copper Rod
  - Added the Amel-Copper Item Cradle
  - Added the Handed Prison for v2.0.0

**NOTE FOR MIGRATORS:**
Super sorry, but this is the LAST!! time per-world shape patterns change!
1.5.6.5
- Fixed the Copper Rod and Amel-Copper Item Cradle and the Handed Prison not dropping their items
- Fixed those items also not being mineable
1.5.6.6
- Fixed up the book a little (online and patchouli)
- Fixed Dark Primarine Staves having no Amel Imbuement recipe
- Fixed crash lmao
1.5.6.7
- Technically Amel Imbuement is datapack-friendlier now but untrusted (unfinished i think)
- Fixed BeegInfusions not fucking working a lot of the time
- Fixed no Amel Imbuement recipe for Casting Rings
1.5.6.8
- Read 1.5.6.7's changelog. Yeah.
- Fixed partially amel stuff's durability not changing.
1.5.6.9
- Fixed requiring Hexical or it'll break the book :sob:
1.5.7
- Amel Imbuement is datapack-friendlier now
- You can datapack Simple Mind Infusion recipes now
- Fixed up the book a lil
- Added interop with Hexal
  - Enchanted Slipways
    Simple Mind Infusion recipe (that costs Amel as well)
    They produce twice as many wandering wisps per second but they can't be turned into portals with Oneironaut
- Added the Geode Dowser
  - Imbue 5 Amel into a compass
  - Consumes 1 amethyst dust per use
- Added the Enchanted Brewing Stand
  - Imbue 10 Amel into a Brewing Stand
  - 2x blaze usage for 2x speed
  - Takes 2 amethyst dust per brewing session/step in brewing process
- Added the Thinking Brewing Stand
  - Infuse a Simple Mind into a Brewing Stand
  - Can remember up to 5 potion recipes
  - Each write is permanent, stops brewing anything but remembered potions when at the limit
  - When a potion from memory is selected, takes items automatically and always takes 2 brewing
    sessions worth of time (so if there's 7 in-between potions it only takes 2 steps worth of time)
- Added the Enchanted Scroll
  - It's literally a Hex Casting IDE, I don't know what more to tell you
1.5.8
- Hierophantics interop
  - Max experience fishermen villagers can be flayed into you
    costs 64 amel and 10 charged amethyst
    they only have the on_my_reference_found trigger, triggers when your reference is found in a stack of an
    offender within "range"
      starts with a "guess" vector pointing from you to the enemy
    has a "vigilance" attribute which can range from 0-3
      0: no notification
      1: chat notification
      2: chat + audio notification
      3: chat + on-screen + audio notification
    they also have a "range" attribute (0-256)
      the higher, the more inaccurate the guess (err_margin=range/4)
      e.g. range=64 means guess can be 16 blocks from the offender
      or range=256 means guess can be 64 blocks from the offender
      however, if the offender is in your ambit the guess is always precise
  - Less than max experience fishermen can also be flayed into you
    costs 48 amel and 10 charged amethyst
    they are almost equivalent to the other mind
    err=range/8 by default, err=range/32 when offender is within ambit
    range is only 0-96
    starts casting with an entity reference to the offender on the stack
    has a 1 in err chance of not detecting the offender
- EMI interop
  - You can now see Imbue Amel, Mold Amel and Simple Mind Infusion recipes in EMI
  - You can also see BeegInfusion recipes in EMI
1.6.0
You can have four arms now (procrastination slain)
- Your third and fourth arms can auto-cast 20x a second
  Both must be devoted to auto-cast, but one can hold something (e.g. Focus, Amel-tuned Orb, etc.)
- Your third and fourth arms can hold items
- You may swap arms 1-2 with 3-4, or use them with mouse3-4 (yes middle click and mouse4, rebindable)
