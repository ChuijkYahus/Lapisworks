package com.luxof.lapisworks;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.primitive;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import net.fabricmc.loader.api.FabricLoader;

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
                LOGGER.error("Apparently, the Lapisworks config file is such horseshit it doesn't parse.");
                LOGGER.error("Trying to fix that right now...");
            }
            this.obj = defaultConfigObject;

            try {
                Files.writeString(configFile.toPath(), defaultConfig, StandardOpenOption.CREATE);
            } catch (IOException e2) {
                if (!canIYell) return;
                LOGGER.error("Yeah no, I can't fix your Lapisworks config file.");
                LOGGER.error("I've defaulted your config options in-game, though.");
                LOGGER.error("Your first error:");
                e1.printStackTrace();
                LOGGER.error("And your second error:");
                e2.printStackTrace();
                LOGGER.error("Toodles!");
            }

            return;
        }

        boolean fileIsPerfect = true;

        var defaultPlayerAmbitMult = primitive(0.5);
        var defaultTuneableAmbitMult = primitive(1.0);
        var defaultPoweredTrailLength = primitive(1);
        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            OneTime_R,
            new Pair<>(PlayerAmbitMult, defaultPlayerAmbitMult),
            new Pair<>(TuneableAmbitMult, defaultTuneableAmbitMult),
            new Pair<>(PoweredTrailLength, defaultPoweredTrailLength)
        );

        defaultPoweredTrailLength = primitive(5);

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            MultiUse_R,
            new Pair<>(TuneableAmbitMult, defaultTuneableAmbitMult),
            new Pair<>(PoweredTrailLength, defaultPoweredTrailLength)
        );

        var defaultDoAnimation = primitive(true);
        var defaultCostMultiplier = primitive(0.5);

        fileIsPerfect = fileIsPerfect && defaultIfInvalid(
            obj,
            Grand_R,
            new Pair<>(DoAnimation, defaultDoAnimation),
            new Pair<>(CostMultiplier, defaultCostMultiplier)
        );

        if (!fileIsPerfect) {
            try {
                Files.writeString(
                    configFile.toPath(),
                    new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .fromJson(defaultPlayerAmbitMult, TypeToken.get(String.class)),
                    StandardOpenOption.CREATE
                );
            } catch (IOException e) {
                LOGGER.error("Tried to correct bad Lapisworks config file, failed!");
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
}
