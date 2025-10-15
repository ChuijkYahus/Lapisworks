Because I had a sick idea to expand Hex Casting and it really stuck and it was actually fun to do.  
Only Fabric 1.20.1 bcause I believe in Fabric supremacy RAHHHH (I can't be bothered to learn Architectury)
I MIGHT make an Architectury addon sometime in the future (it's 3/8/2025 as of writing this) 'cause the Hex Casting Forge addons scene is literally a desert and I pity Mekanism fans. (also other Forge mod fans too I guess but Mekanism is a cool mod, it's a shame it's only on Forge.)  

Would I recommend this addon as a template for Hex Casting addons? No.  
Would I recommend it for *reading* to make Hex Casting addons? Sure, whatever.  

### FOR CODE READERS:  
- "What's the VAULT?"  
    `Very Ass and Unnecessary but not use-Less Terminal (for items)`.  
    I use it to fetch, drain and give items. You don't need to use it at all.  
    You can look at `VAULT.java`, but you shouldn't look at it's implementations like `PlayerVAULT.java` or `CastEnvVAULT.java` unless you're trying to implement it for whatever cursed ass reason.  
    Prime example of usage in BeegInfusions.
- "What's `Flags.java`?"  
    Part of the VAULT. It's used to filter where to search for items. Just trust the formulae lmao.  
    Prime example of usage in BeegInfusions.
- "What's the `InventoryItem.java` interface?"  
    *Also* part of the VAULT. Implement it and the VAULT will be able to interact with items in your InventoryItem's inventory.
- "What's a BeegInfusion"?  
    NBT-dependent or otherwise dynamic Amel imbuement of items.  
    Prime example in `EnhanceEnchantedBook.java`.
