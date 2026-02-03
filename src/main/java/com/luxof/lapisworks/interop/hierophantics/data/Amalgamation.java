package com.luxof.lapisworks.interop.hierophantics.data;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Amalgamation {
    public int notifLevel;
    public double range;
    public boolean forNoobs;
    public Amalgamation(int notifLevel, double range, boolean forNoobs) {
        this.notifLevel = notifLevel;
        this.range = range;
        this.forNoobs = forNoobs;
    }
    public Amalgamation() {
        this(0, 0.0, false);
    }
    public Amalgamation(NbtCompound nbt) {
        this(
            nbt.getInt("notifLevel"),
            nbt.getDouble("range"),
            nbt.getBoolean("forNoobs")
        );
    }

    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("notifLevel", notifLevel);
        nbt.putDouble("range", range);
        nbt.putBoolean("forNoobs", forNoobs);
        return nbt;
    }

    public double getErr() {
        return Math.min(range / (forNoobs ? 8.0 : 4.0), 32.0);
    }
    public double getMaxRange() {
        return forNoobs ? 64.0 : 256.0;
    }
    
    public boolean equals(Object other) {
        return other != null
            && other instanceof Amalgamation otherAmalgamation
            && notifLevel == otherAmalgamation.notifLevel
            && range == otherAmalgamation.range
            && forNoobs == otherAmalgamation.forNoobs;
    }

    public static class AmalgamationIota extends Iota {
        public static IotaType<AmalgamationIota> TYPE = new IotaType<>() {

            @Override @Nullable
            public AmalgamationIota deserialize(NbtElement nbt, ServerWorld world) {
                return AmalgamationIota.deserialize((NbtCompound)nbt);
            }

            @Override
            public Text display(NbtElement nbt) {
                return AmalgamationIota.display(new Amalgamation((NbtCompound)nbt));
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

        public static Text display(Amalgamation amalgamation) {
            return Text.translatable(
                "render.lapisworks.iota_descs.amalgamation.general",
                amalgamation.forNoobs
                    ? Text.translatable(
                        "render.lapisworks.iota_descs.amalgamation.lesser"
                    ).setStyle(Style.EMPTY.withColor(0xFF00FF))
                    : Text.translatable(
                        "render.lapisworks.iota_descs.amalgamation.greater"
                    ).setStyle(Style.EMPTY.withColor(0x440088)),
                amalgamation.range
            ).setStyle(Style.EMPTY.withColor(0x7000FF));
        }
        public static AmalgamationIota deserialize(NbtCompound nbt) {
            return new AmalgamationIota(new Amalgamation(nbt));
        }
    }
}
