package com.luxof.lapisworks.init;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.last;
import static com.luxof.lapisworks.Lapisworks.log;
import static com.luxof.lapisworks.Lapisworks.pair;
import static com.luxof.lapisworks.Lapisworks.pop;
import static com.luxof.lapisworks.Lapisworks.primitive;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import org.jetbrains.annotations.Nullable;

public class LapisConfig {
    public static File configFile = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("lapisworks.json")
        .toFile();
    @Nullable private static LapisConfig currentConfig = null;

    public static LapisConfig getCurrentConfig() {
        if (currentConfig == null) setCurrentConfig(new LapisConfig());
        return currentConfig;
    }
    public static void renewCurrentConfig() {
        setCurrentConfig(new LapisConfig());
    }
    protected static void renewCurrentConfigAndYell() {
        setCurrentConfig(new LapisConfig(true));
    }
    protected static void setCurrentConfig(LapisConfig newConfig) {
        currentConfig = newConfig;
    }


    private JsonObject obj;
    protected LapisConfig(JsonObject obj) {
        this.obj = obj;
    }
    private static final String OneTime_R = "onetime_ritual";
    private static final String MultiUse_R = "multiuse_ritual";
    private static final String PlayerAmbitMult = "player_ambit_multiplier";
    private static final String TuneableAmbitMult = "tuneable_amethyst_ambit_multiplier";
    private static final String PoweredTrailLength = "trail_of_powered_chalk_length";
    private static final String Grand_R = "grand_ritual";
    private static final String DoAnimation = "do_animation";
    private static final String CostMultiplier = "cost_multiplier";
    private static final String Chariot = "hierophantics_interop";
    private static final String MaxFusedAmalgams = "max_fused_amalgamations";
    // oh no
    private static final String MaxSiARange = "max_simple_amalgam_range";
    private static final String MaxCARange = "max_complex_amalgam_range";
    private static final String SiAErrMult = "simple_amalgam_err_multiplier";
    private static final String CAErrMult = "complex_amalgam_err_multiplier";
    private static final String MaxErr = "max_err";
    private static final String Spells = "spells";
    private static final String AllowReclaimAmethyst = "allow_reclaim_amethyst_but_imbue_lapis_takes_items_instead_of_raw_media";
    private static final String OverenchantLimit = "overenchant_limit_in_imbue_amel";
    private static final String defaultConfig = """
    {
      "onetime_ritual": {
        "tuneable_amethyst_ambit_multiplier": 1.0,
        "player_ambit_multiplier": 0.5,
        "trail_of_powered_chalk_length": 1
      },

      "multiuse_ritual": {
        "tuneable_amethyst_ambit_multiplier": 1.0,
        "trail_of_powered_chalk_length": 5
      },

      "grand_ritual": {
        "do_animation": true,
        "cost_multiplier": 0.5
      },

      "hierophantics_interop": {
        "max_fused_amalgamations": 3,
        "max_simple_amalgam_range": 48.0,
        "max_complex_amalgam_range": 96.0,
        "simple_amalgam_err_multiplier": 0.125,
        "complex_amalgam_err_multiplier": 0.25,
        "max_err": 32.0
      },

      "spells": {
        "allow_reclaim_amethyst_but_imbue_lapis_takes_items_instead_of_raw_media": true
      },

      "overenchant_limit_in_imbue_amel": {
        "comment": "0 equals 32-bit integer limit. behaviour_when_not_present rules: only functions are log, sqrt, max, and min. 0 as a result doesn't equal the 32-bit integer limit. Operators are +-*/^. x = default maximum for the enchantment (and NO OTHER VARIABLE IS ALLOWED). No implicit shit. Results will be rounded and clamped to 0 if below 0.",
        "behaviour_when_not_present": "3*x",
        "minecraft:channeling": 1,
        "minecraft:mending": 1,
        "minecraft:infinity": 1,
        "minecraft:binding_curse": 1,
        "minecraft:vanishing_curse": 1,
        "minecraft:flame": 1,
        "minecraft:multishot": 1,
        "minecraft:piercing": 10,
        "minecraft:silk_touch": 1
      }
    }
    """;
    private static final JsonObject defaultConfigObject = JsonParser.parseString(defaultConfig)
        .getAsJsonObject();

    /** returns if it was valid. */
    private boolean defaultIfInvalid(
        JsonObject obj,
        String key,
        JsonPrimitive fallBackTo
    ) {
        if (fallBackTo.isBoolean()) {
            try {
                obj.get(key).getAsBoolean();
            } catch (Exception e) {
                obj.add(key, fallBackTo);
                return false;
            }
        } else if (fallBackTo.isString()) {
            try {
                obj.get(key).getAsString();
                if (
                    key.startsWith("comment") &&
                    !obj.get(key).getAsString().equals(fallBackTo.getAsString())
                ) {
                    obj.add(key, fallBackTo);
                    return false;
                }
            } catch (Exception e) {
                obj.add(key, fallBackTo);
                return false;
            }
        } else if (fallBackTo.getAsNumber() instanceof Double) {
            try {
                obj.get(key).getAsDouble();
            } catch (Exception e) {
                obj.add(key, fallBackTo);
                return false;
            }
        } else {
            try {
                obj.get(key).getAsInt();
            } catch (Exception e) {
                obj.add(key, fallBackTo);
                return false;
            }
        }
        return true;
    }
    /** returns if it was valid. */
    @SuppressWarnings("unchecked")
    private boolean defaultIfInvalid(
        JsonObject superObj,
        String key,
        Pair<String, JsonPrimitive>... keyAndDefaultPairs
    ) {
        boolean fileIsPerfect = true;
        try {
            JsonObject thisObj = superObj.getAsJsonObject(key);

            for (var pair : keyAndDefaultPairs) {
                fileIsPerfect = fileIsPerfect &&
                    defaultIfInvalid(thisObj, pair.getLeft(), pair.getRight());
            }
        } catch (ClassCastException | NullPointerException e) {
            JsonObject thisObj = new JsonObject();
            
            for (var pair : keyAndDefaultPairs) {
                thisObj.add(pair.getLeft(), pair.getRight());
            }

            superObj.add(key, thisObj);
            fileIsPerfect = false;
        }
        return fileIsPerfect;
    }

    protected LapisConfig() {
        this(false);
    }
    @SuppressWarnings("unchecked")
    // no dumbass it's january
    protected LapisConfig(boolean canIYell) {
        try {
            if (!configFile.exists()) {
                Files.writeString(configFile.toPath(), defaultConfig, StandardOpenOption.CREATE);
            }
            this.obj = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        } catch (Exception e1) {
            if (canIYell) {
                err("Apparently, the Lapisworks config file is such horseshit it doesn't parse as valid JSON.");
                err("Trying to fix that right now...");
            }
            this.obj = defaultConfigObject;

            try {
                Files.writeString(configFile.toPath(), defaultConfig, StandardOpenOption.CREATE);
            } catch (IOException e2) {
                if (!canIYell) return;
                err("Yeah no, I can't fix your Lapisworks config file.");
                err("I've defaulted your config options in-game, though.");
                err("Your first error:");
                e1.printStackTrace();
                err("And your second error:");
                e2.printStackTrace();
                err("Toodles!");
            }

            return;
        }

        boolean fileIsPerfect = true;

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            OneTime_R,
            pair(PlayerAmbitMult, primitive(0.5)),
            pair(TuneableAmbitMult, primitive(1.0)),
            pair(PoweredTrailLength, primitive(1))
        );

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            MultiUse_R,
            pair(TuneableAmbitMult, primitive(1.0)),
            pair(PoweredTrailLength, primitive(5))
        );

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            Grand_R,
            pair(DoAnimation, primitive(true)),
            pair(CostMultiplier, primitive(0.5))
        );

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            Chariot,
            pair(MaxFusedAmalgams, primitive(1)),
            pair(MaxSiARange, primitive(48.0)),
            pair(MaxCARange, primitive(96.0)),
            pair(SiAErrMult, primitive(0.125)),
            pair(CAErrMult, primitive(0.25)),
            pair(MaxErr, primitive(32.0))
        );

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            Spells,
            pair(AllowReclaimAmethyst, primitive(true))
        );

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            OverenchantLimit,
            pair("comment", primitive("0 equals 32-bit integer limit. behaviour_when_not_present rules: only functions are log, sqrt, max, and min. 0 as a result doesn't equal the 32-bit integer limit. Operators are +-*/^. x = default maximum for the enchantment (and NO OTHER VARIABLE IS ALLOWED). No implicit shit. Results will be rounded and clamped to 0 if below 0.")),
            pair("behaviour_when_not_present", primitive("3*x")),
            pair("minecraft:channeling", primitive(1)),
            pair("minecraft:mending", primitive(1)),
            pair("minecraft:infinity", primitive(1)),
            pair("minecraft:binding_curse", primitive(1)),
            pair("minecraft:vanishing_curse", primitive(1)),
            pair("minecraft:flame", primitive(1)),
            pair("minecraft:multishot", primitive(1)),
            pair("minecraft:piercing", primitive(10)),
            pair("minecraft:silk_touch", primitive(1))
        );

        if (!fileIsPerfect) {
            try {
                Files.writeString(
                    configFile.toPath(),
                    new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(obj),
                    StandardOpenOption.CREATE
                );
            } catch (IOException e) {
                err("Tried to correct bad Lapisworks config file, failed!");
                e.printStackTrace();
            }
        }
    }


    public static final record OneTimeRitualSettings(
        double tuneable_amethyst_ambit_multiplier,
        double player_ambit_multiplier,
        int powered_trail_length
    ) {}
    public OneTimeRitualSettings getOneTimeRitualSettings() {
        JsonObject settings = obj.getAsJsonObject(OneTime_R);

        return new OneTimeRitualSettings(
            settings.get(TuneableAmbitMult).getAsDouble(),
            settings.get(PlayerAmbitMult).getAsDouble(),
            settings.get(PoweredTrailLength).getAsInt()
        );
    }


    public static final record MultiUseRitualSettings(
        double tuneable_amethyst_ambit_multiplier,
        int powered_trail_length
    ) {}
    public MultiUseRitualSettings getMultiUseRitualSettings() {
        JsonObject settings = obj.getAsJsonObject(MultiUse_R);

        return new MultiUseRitualSettings(
            settings.get(TuneableAmbitMult).getAsDouble(),
            settings.get(PoweredTrailLength).getAsInt()
        );
    }


    public static final record GrandRitualSettings(
        boolean do_animation,
        double cost_multiplier
    ) {}
    public GrandRitualSettings getGrandRitualSettings() {
        JsonObject settings = obj.getAsJsonObject(Grand_R);

        return new GrandRitualSettings(
            settings.get(DoAnimation).getAsBoolean(),
            settings.get(CostMultiplier).getAsDouble()
        );
    }


    public static final record ChariotSettings(
        int max_fused_amalgamations,
        double max_simple_amalgam_range,
        double max_complex_amalgam_range,
        double simple_amalgam_err_multiplier,
        double complex_amalgam_err_multiplier,
        double max_err
    ) {}
    public ChariotSettings getChariotSettings() {
        JsonObject settings = obj.getAsJsonObject(Chariot);

        return new ChariotSettings(
            settings.get(MaxFusedAmalgams).getAsInt(),
            settings.get(MaxSiARange).getAsDouble(),
            settings.get(MaxCARange).getAsDouble(),
            settings.get(SiAErrMult).getAsDouble(),
            settings.get(CAErrMult).getAsDouble(),
            settings.get(MaxErr).getAsDouble()
        );
    }


    public static final record SpellSettings(
        boolean allow_reclaim_amethyst
    ) {}
    public SpellSettings getSpellSettings() {
        JsonObject settings = obj.getAsJsonObject(Spells);

        return new SpellSettings(
            settings.get(AllowReclaimAmethyst).getAsBoolean()
        );
    }


    public int getOverenchantLimitFor(Enchantment enchantment) {
        return getOverenchantLimitFor(Registries.ENCHANTMENT.getId(enchantment).toString());
    }
    public int getOverenchantLimitFor(String enchantmentId) {
        JsonObject limits = obj.getAsJsonObject(OverenchantLimit);

        return limits.has(enchantmentId)
            ? limits.get(enchantmentId).getAsInt() == 0
                ? Integer.MAX_VALUE // you can't even get it this high...
                : limits.get(enchantmentId).getAsInt()
            : parseMath(
                limits.get("behaviour_when_not_present").getAsString(),
                Registries.ENCHANTMENT.get(new Identifier(enchantmentId)).getMaxLevel()
            );
    }

    // :)
    // ...PREPROCESSOR ON MY EXTENSION--
    private static final String EQUATION_REGEX = r"((?<!\d)-)?\d+(\.\d+)?|[+\-*\/^(),x]|(min|max)(?=\(.+,.+\))|(log|sqrt)(?=\(.+\))";
    private static final String NUMBER_REGEX = r"-?\d+(\.\d+)?";
    private static final String OPERATOR_REGEX = r"[+\-*\/^]";
    private static final String FUNCTION_REGEX = r"min|max|log|sqrt";
    private HashMap<String, List<String>> mathEquationCache = new HashMap<>();
    private Map<String, Integer> precedence = Map.of(
        "max", 5,
        "min", 5,
        "log", 5,
        "sqrt", 5,
        "^", 4,
        "*", 3,
        "/", 3,
        "+", 2,
        "-", 2
    );
    private Map<String, Integer> associative = Map.of(
        "^", 1,
        "*", 0,
        "/", 0,
        "+", 2,
        "-", 2
    );
    private int errMath(String raw, int x, String reason) {
        err("Lapisworks: \"%s\" is not a valid equation: %s", raw, reason);
        err("Reverting to 3x. (%d)", 3*x);
        return 3*x;
    }
    private int parseMath(String raw, int x) {
        if (mathEquationCache.containsKey(raw))
            return doMath(mathEquationCache.get(raw));

        String noSpaces = raw.replaceAll(" ", "");
        if (!noSpaces.matches(EQUATION_REGEX))
            return errMath(raw, x, "unknown symbols, unknown functions, or fucked usage of functions.");

        List<String> math = new ArrayList<>();
        Matcher matcher = Pattern.compile(EQUATION_REGEX).matcher(noSpaces);
        for (int i = 0; i < matcher.groupCount(); i++) {
            math.add(matcher.group(i));
        }

        List<String> rpn = new ArrayList<>();
        List<String> opStack = new ArrayList<>();
        for (String token : math) {

            if (token.matches(NUMBER_REGEX))
                rpn.add(token);

            else if (token.equals("x"))
                rpn.add(String.valueOf(x));

            else if (token.matches(OPERATOR_REGEX)) {
                String topOp = opStack.size() > 0 ? pop(opStack) : null;
                while (
                    topOp != null && !topOp.equals("(") &&
                    //topOp.matches("[+\\\\-*\\\\/^]") &&
                    (
                        precedence.get(topOp) > precedence.get(token) ||
                        precedence.get(topOp) == precedence.get(token) && associative.get(token) == 0
                    )
                ) {
                    rpn.add(pop(opStack));
                    topOp = opStack.size() > 0 ? last(opStack) : null;
                }
                opStack.add(token);
            }

            else if (token.equals(",")) {
                String topOp = opStack.size() > 0 ? last(opStack) : null;
                while (topOp != null && !topOp.equals("(")) {
                    rpn.add(pop(opStack));
                    topOp = opStack.size() > 0 ? last(opStack) : null;
                }
            }

            else if (token.equals("("))
                opStack.add(token);

            else if (token.equals(")")) {

                if (opStack.size() == 0)
                    return errMath(raw, x, "mismatched parenthesis.");

                String topOp = last(opStack);
                while (!topOp.equals("(")) {
                    rpn.add(topOp);
                    if (opStack.size() == 0)
                        return errMath(raw, x, "mismatched parenthesis.");
                    topOp = pop(opStack);
                }
                if (opStack.size() == 0)
                    continue;
                topOp = last(opStack);

                if (topOp.matches(FUNCTION_REGEX))
                    rpn.add(pop(opStack));
            }

            else
                // function
                opStack.add(token);
        }

        for (String operator : opStack) {
            if (operator.equals("("))
                errMath(raw, x, "mismatched parenthesis.");
            rpn.add(operator);
        }

        mathEquationCache.put(raw, rpn);
        return doMath(rpn);
    }

    private int doMath(List<String> rpn) {
        log("doing math! %s", rpn.toString());
        List<Double> nums = new ArrayList<>();
        for (String token : rpn) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                nums.add(Double.parseDouble(token));
                continue;
            }

            switch (token) {
                case "+" -> nums.add(pop(nums) + pop(nums));
                case "-" -> nums.add(pop(nums) - pop(nums));
                case "*" -> nums.add(pop(nums) * pop(nums));
                case "/" -> nums.add(pop(nums) / pop(nums));
                case "^" -> nums.add(Math.pow(pop(nums), pop(nums)));
                case "log" -> nums.add(Math.log(pop(nums)));
                case "sqrt" -> nums.add(Math.sqrt(pop(nums)));
                case "max" -> nums.add(Math.max(pop(nums), pop(nums)));
                case "min" -> nums.add(Math.min(pop(nums), pop(nums)));
            }
        }

        return (int)Math.round(nums.get(0));
    }
}
