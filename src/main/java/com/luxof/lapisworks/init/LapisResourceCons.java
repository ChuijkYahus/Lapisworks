package com.luxof.lapisworks.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.Lapisworks.isModLoaded;
import static com.luxof.lapisworks.mixin.plugins.ModSpecificMCP.verDifference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

public class LapisResourceCons {
    private static String getVersion(Matcher match) {
        try { return match.group(2); }
        catch (IllegalStateException e) {
            throw new JsonParseException(
                "This constraint type requires a version that wasn't provided: " + match.group(1)
            );
        }
    }

    public static void doBondagePlay() {
        ResourceConditions.register(
            id("all_mods_loaded_with_specific_versions"),
            json -> {
                for (JsonElement ele : json.get("values").getAsJsonArray()) {
                    if (!(ele instanceof JsonPrimitive primitive) || !primitive.isString())
                        throw new JsonParseException("Invalid mod@version pair: " + ele.toString());

                    String[] modAndVersion = ele.getAsString().split("@");
                    if (modAndVersion.length != 2)
                        throw new JsonParseException("Invalid mod@version pair: " + ele.getAsString());

                    String modId = modAndVersion[0];
                    String versionConstraint = modAndVersion[1];

                    Pattern constraintPattern = Pattern.compile("(<=|>=|!=|[<>=*])(.*)");
                    String constraintType;
                    Matcher matcher = constraintPattern.matcher(versionConstraint);
                    try {
                        constraintType = matcher.group(1);
                    } catch (IllegalStateException e) {
                        throw new JsonParseException("Invalid version constraint: " + versionConstraint);
                    }

                    if (!switch (constraintType) {
                        case "<=" -> verDifference(modId, getVersion(matcher)) <= 0;
                        case ">=" -> verDifference(modId, getVersion(matcher)) >= 0;
                        case "<" -> verDifference(modId, getVersion(matcher)) < 0;
                        case ">" -> verDifference(modId, getVersion(matcher)) > 0;
                        case "!=" -> verDifference(modId, getVersion(matcher)) != 0;
                        case "=" -> verDifference(modId, getVersion(matcher)) == 0;
                        case "*" -> isModLoaded(modId);
                        default -> false;
                    })
                        return false;
                }
                return true;
            }
        );
    }
}
