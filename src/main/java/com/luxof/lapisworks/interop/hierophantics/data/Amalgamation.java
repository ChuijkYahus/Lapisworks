package com.luxof.lapisworks.interop.hierophantics.data;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.LapisConfig.ChariotSettings;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMindEntity;

import static com.luxof.lapisworks.Lapisworks.deserializeBlockPos;
import static com.luxof.lapisworks.Lapisworks.nbtListOf;
import static com.luxof.lapisworks.Lapisworks.serializeBlockPos;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Amalgamation {
    public final BlockPos origin;
    public final boolean forNoobs;
    public int notifLevel;
    public double range;
    public ArrayList<Iota> hex;
    public Amalgamation(
        BlockPos origin,
        boolean forNoobs,
        int notifLevel,
        double range,
        List<Iota> hex
    ) {
        this.origin = origin;
        this.forNoobs = forNoobs;
        this.notifLevel = notifLevel;
        this.range = range;
        this.hex = new ArrayList<>(hex);
    }
    public Amalgamation(NbtCompound nbt, ServerWorld world) {
        this(
            deserializeBlockPos(nbt.getCompound("origin")),
            nbt.getBoolean("forNoobs"),
            nbt.getInt("notifLevel"),
            nbt.getDouble("range"),
            nbt.getList("hex", NbtElement.LIST_TYPE)
                .stream()
                .map(
                    ele -> IotaType.deserialize(
                        (NbtCompound)ele,
                        world
                    )
                )
                .toList()
        );
    }

    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("origin", serializeBlockPos(origin));
        nbt.putBoolean("forNoobs", forNoobs);
        nbt.putInt("notifLevel", notifLevel);
        nbt.putDouble("range", range);
        nbt.put("hex", nbtListOf(hex.stream().map(IotaType::serialize).toList()));
        return nbt;
    }

    public double getErr() {
        ChariotSettings chariotSettings = LapisConfig.getCurrentConfig().getChariotSettings();
        return Math.min(
            range *
            (forNoobs
                ? chariotSettings.simple_amalgam_err_multiplier()
                : chariotSettings.complex_amalgam_err_multiplier()),
            chariotSettings.max_err()
        );
    }
    public double getMaxRange() {
        ChariotSettings chariotSettings = LapisConfig.getCurrentConfig().getChariotSettings();
        return forNoobs
            ? chariotSettings.max_simple_amalgam_range()
            : chariotSettings.max_complex_amalgam_range();
    }
    
    public boolean equals(Object other) {
        return other != null
            && other instanceof Amalgamation otherAmalgamation
            && notifLevel == otherAmalgamation.notifLevel
            && range == otherAmalgamation.range
            && forNoobs == otherAmalgamation.forNoobs;
    }

    public void updateOrigin(ServerWorld world) {
        if (!(world.getBlockEntity(origin) instanceof ChariotMindEntity chariotMind))
            return;

        chariotMind.storedAmalgamationNbt = serialize();
    }

    public static class AmalgamationIota extends Iota {
        public static IotaType<AmalgamationIota> TYPE = new IotaType<>() {

            @Override @Nullable
            public AmalgamationIota deserialize(NbtElement nbt, ServerWorld world) {
                return AmalgamationIota.deserialize((NbtCompound)nbt, world);
            }

            @Override
            public Text display(NbtElement nbt) {
                return AmalgamationIota.display((NbtCompound)nbt);
            }

            @Override
            public int color() {
                return 0x7000ff;
            }
            
        };

        public AmalgamationIota(Amalgamation payload) {
            super(TYPE, payload);
        }

        public Amalgamation getAmalgamation() {
            return (Amalgamation)this.payload;
        }

        @Override
        public boolean isTruthy() {
            return true;
        }

        @Override
        public boolean toleratesOther(Iota that) {
            return typesMatch(this, that)
                && that instanceof AmalgamationIota thatIota
                && getAmalgamation().equals(thatIota.getAmalgamation());
        }

        @Override
        public @NotNull NbtElement serialize() {
            return getAmalgamation().serialize();
        }

        public static Text display(NbtCompound nbt) {
            int notifLevel = nbt.getInt("notifLevel");
            boolean forNoobs = nbt.getBoolean("forNoobs");
            double range = nbt.getDouble("range");
            return Text.translatable(
                "render.lapisworks.iota_descs.amalgamation.general",
                forNoobs
                    ? Text.translatable(
                        "render.lapisworks.iota_descs.amalgamation.lesser"
                    ).setStyle(Style.EMPTY.withColor(0x7029FF))
                    : Text.translatable(
                        "render.lapisworks.iota_descs.amalgamation.greater"
                    ).setStyle(Style.EMPTY.withColor(0x440088)),
                Text.translatable(
                    "render.lapisworks.iota_descs.amalgamation." +
                    String.valueOf(notifLevel)
                ),
                range
            ).setStyle(Style.EMPTY.withColor(0x7000FF));
        }

        @Nullable
        public static AmalgamationIota deserialize(NbtCompound nbt, ServerWorld world) {
            return new AmalgamationIota(new Amalgamation(nbt, world));
        }

        public static boolean originIsValid(NbtCompound nbt, ServerWorld world) {
            return world.getBlockEntity(
                deserializeBlockPos(
                    nbt.getCompound("origin")
                )
            ) instanceof ChariotMindEntity;
        }
    }
}
