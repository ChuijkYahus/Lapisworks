package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.util.Pair;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LapisworksTest {
    public static Logger LOGGER = LoggerFactory.getLogger("lapisworks");

    @Test
    void testPatternmatching() {
        HexPattern altiora = HexPattern.fromAngles("qawawqaaewdq", HexDir.WEST);
        HexPattern realAltiora = HexPattern.fromAngles("eawqwawawqaq", HexDir.NORTH_WEST);
        Assertions.assertTrue(matchShape(altiora, realAltiora));
    }

    public static String printPairList(List<Pair<HexCoord, HexDir>> pairs) {
        String buf = "[";
        for (Pair<HexCoord, HexDir> pair : pairs) {
            buf += "Pair<" + pair.getLeft().toString() + "," + pair.getRight().toString() + ">";
        }
        buf += "]";
        return buf;
    }

    public static boolean matchShape(HexPattern pat1, HexPattern p2) {
		return equalsButUnordered(setTopLeftOrigin(pat1.positions()), setTopLeftOrigin(p2.positions()));
	}
	public static List<HexCoord> setTopLeftOrigin(List<HexCoord> pat) {
		HexCoord runningTopLeft = new HexCoord(0, 0);
		for (HexCoord coord : pat) {
			if (coord.getQ() < runningTopLeft.getQ() && coord.getR() <= runningTopLeft.getR()) {
				runningTopLeft = new HexCoord(coord.getQ(), coord.getR());
			} else if (coord.getR() < runningTopLeft.getR()) {
				runningTopLeft = new HexCoord(coord.getQ(), coord.getR());
			}
		}
        LOGGER.info("top left!: " + runningTopLeft.toString());
		// "must be final" my ass
		HexCoord topLeft = new HexCoord(runningTopLeft.getQ(), runningTopLeft.getR());
        return pat.stream().map((coord) -> {
            return new HexCoord(coord.getQ() - topLeft.getQ(), coord.getR() - topLeft.getR());
        }).collect(Collectors.toList());
	}
    /** Checks if two lists are equal, but does not check if their elements are ordered the same way. */
    public static <T extends Object> boolean equalsButUnordered(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) { return false; }
        else if (list1.size() == 0) { return true; }
        List<T> l2 = new ArrayList<T>(list2);
        for (T thing : list1) {
            int idx = l2.indexOf(thing);
            if (idx == -1) { return false; }
            l2.remove(idx);
        }
        return true;
    }
}
