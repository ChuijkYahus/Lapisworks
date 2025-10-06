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
1.5.5.5:
- Fixed staffcasting not working if you don't have hextended (WTF????)
- Also fixed some staves not opening their spellcasting gui
1.1.7: "haha i added some funny swords"
1.5.6:
- Added Enchanted Slipways as interop with Hexal
  They produce twice as many wisps per second but they can't be turned into portals with Oneironaut
- Added the Specific Pedestal as interop with Hexical
1.6.0
You can have four arms now (procrastination slain)
