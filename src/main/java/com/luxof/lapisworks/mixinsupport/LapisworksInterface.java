package com.luxof.lapisworks.mixinsupport;

import java.util.List;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;

public interface LapisworksInterface {
    public class AllEnchantments {
        public static final int fireyFists = 0;
        public static final int lightningBending = 1;
        public static final int fallDmgRes = 2;
        public static final int longBreath = 3;
        public static final int fireResist = 4;
    }

    double getAmountOfAttrJuicedUpByAmel(EntityAttribute attribute);
    void setAmountOfAttrJuicedUpByAmel(EntityAttribute attribute, double value);
    void setAllJuicedUpAttrsToZero();
    
    AttributeContainer getLapisworksAttributes();
    void setLapisworksAttributes(AttributeContainer attributes);

	/** expands list up to index if the index is out of bounds, filling the empty spots with 0. */
    int getEnchant(int whatEnchant);
	/** expands list blah blah like getEnchant(). */
    void setEnchantmentLevel(int whatEnchant, int level);
    /** expands list blah blah like getEnchant(). */
    void incrementEnchant(int whatEnchant);
    /** expands list blah blah like getEnchant(). */
    void incrementEnchant(int whatEnchant, int amount);
    /** expands list blah blah like getEnchant(). */
    void decrementEnchant(int whatEnchant);
    /** expands list blah blah like getEnchant(). */
    void decrementEnchant(int whatEnchant, int amount);
	/** resulting list is not mutable. */
    List<Integer> getEnchantments();
	/** this is for nbt stuff. */
    void setEnchantments(int[] levels);
    void setAllEnchantsToZero();
}
