package com.luxof.lapisworks.blocks.bigchalk;

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.fabric.cc.CCStaffcastImage;
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;

import com.luxof.lapisworks.blocks.stuff.StampableBE;
import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.Markable;

import static com.luxof.lapisworks.Lapisworks.getPigmentFromDye;
import static com.luxof.lapisworks.Lapisworks.makeParticlesInSpiralGoUp;
import static com.luxof.lapisworks.Lapisworks.sameAxis;

import java.util.List;
import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

import org.joml.Quaternionf;

public class BigChalkCenterEntity extends BlockEntity implements StampableBE {
    private static boolean skipAnimation() {
        return !LapisConfig.getCurrentConfig().getGrandRitualSettings().do_animation();
    }

    public final Direction facing;
    public final Direction attachedTo;
    public BigChalkCenterEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BIG_CHALK_CENTER_ENTITY_TYPE, pos, state);
        this.attachedTo = state.get(BigChalkCenter.ATTACHED);
        this.facing = state.get(BigChalkCenter.FACING);

        /*long seed = pos.getX() * 73428767L
              ^ pos.getY() * 912931L
              ^ pos.getZ() * 42317861L;

        seed = (seed ^ (seed >>> 13)) * 1274126177L;
        seed ^= (seed >>> 16);

        textVariant = (int)Math.round((seed & 0xFFFFFFFFL) / (double) 0x100000000L);*/
        textVariant = Math.min((int)Math.floor(3 * Math.random()), 2);
        // how doesn't this err on the server?
        renderData = new RenderData(attachedTo);
    }

    /** decides what text is displayed on the chalk. */
    public int textVariant;
    public boolean altTexture = false;

    @Nullable protected UUID playerWhoTouchedMe = null;
    protected Hand handThatTouchedMe = Hand.MAIN_HAND;
    protected boolean disappearAfterCasting = false;

    @Nullable private HexPattern pattern = null;
    @Nullable public HexPattern getPattern() { return pattern; }
    @Override
    public void stamp(HexPattern pattern, Direction horizontalPlayerFacing) {
        this.pattern = pattern;
        save();
    }

    private boolean powered = false;
    public boolean isPowered() { return powered; }
    public void power(boolean on) { power(on, true); }
    public void power(boolean on, boolean shouldSave) {
        if (skipAnimation() && !this.powered && on) {
            if (world.isClient)
                castPatternClient();
            else
                castPatternServer();
            return;
        }

        if (!this.powered && on) ticksElapsed = 0;
        this.powered = on;
        if (shouldSave) save();
    }

    //@Environment(EnvType.SERVER) // ????? literally fucking crashed on the server thread btw
    private void castPatternServer() {
        ServerPlayerEntity player = (ServerPlayerEntity)world.getPlayerByUuid(
            playerWhoTouchedMe
        );
        if (player == null) return;

        CCStaffcastImage ccStaffcastImg = HexCardinalComponents.STAFFCAST_IMAGE.get(player);
        CastingVM vm = ccStaffcastImg.getVM(handThatTouchedMe);

        vm.queueExecuteAndWrapIota(
            // muahahahahaha
            ((Markable)new PatternIota(pattern)).mark(),
            (ServerWorld)world
        );

        ccStaffcastImg.setImage(vm.getImage());
        HexCardinalComponents.STAFFCAST_IMAGE.sync(player);
    }
    public void serverTick(BlockState state) {
        if (!powered) {
            ticksElapsed = 0;
            return;
        } else if (ticksElapsed > animationLength) {
            if (disappearAfterCasting)
                world.breakBlock(pos, false);
            powered = false;
            return;
        }

        if (ticksElapsed == 100 && pattern != null)
            castPatternServer();

        ticksElapsed++;
    }
    @Environment(EnvType.CLIENT)
    private void castPatternClient() {
        spewParticles(true, DyeColor.MAGENTA);
        makeDaSound(HexSounds.CAST_HERMES, 3f, 0.7f);
    }
    public void clientTick(BlockState state) {
        if (!powered) {
            ticksElapsed = 0;
            renderData.rotation = 0f;
            renderData.rotationNext = 0f;
            renderData.translation = 0f;
            renderData.translationNext = 0f;
            return;
        } else if (ticksElapsed > animationLength) {
            powered = false;
            return;
        }

        renderData.rotateForMe(ticksElapsed);
        renderData.translateForMe(ticksElapsed);

        if (particleTicks.contains(ticksElapsed)) {
            spewParticles(false, ticksElapsed >= 100 ? DyeColor.MAGENTA : DyeColor.PINK);
            makeDaSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, 1f, 0.8f);
        }
        if (ticksElapsed == 100)
            castPatternClient();

        ticksElapsed++;
    }
    public void tick(BlockState state) {
        if (!world.isClient)
            serverTick(state);
        else
            clientTick(state);
    }

    public void save() {
        markDirty();
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, 0);
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        altTexture = nbt.getBoolean("altTexture");
        textVariant = nbt.getInt("textVariant");
        if (nbt.contains("pattern")) pattern = HexPattern.fromNBT(nbt.getCompound("pattern"));

        // i tried really hard to sync this animation
        // good enough!
        ticksElapsed = nbt.getInt("ticksElapsed");

        power(nbt.getBoolean("powered"), false);
        if (nbt.contains("playerWhoTouchedMe"))
            playerWhoTouchedMe = nbt.getUuid("playerWhoTouchedMe");
        handThatTouchedMe = Hand.valueOf(nbt.getString("handThatTouchedMe"));
        disappearAfterCasting = nbt.getBoolean("disappearAfterCasting");
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("altTexture", altTexture);
        nbt.putInt("textVariant", textVariant);
        if (pattern != null) nbt.put("pattern", pattern.serializeToNBT());
        nbt.putBoolean("powered", powered);
        nbt.putInt("ticksElapsed", ticksElapsed);
        if (playerWhoTouchedMe != null)
            nbt.putUuid("playerWhoTouchedMe", playerWhoTouchedMe);
        nbt.putString("handThatTouchedMe", handThatTouchedMe.toString());
        nbt.putBoolean("disappearAfterCasting", disappearAfterCasting);
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    private final int animationLength = 140;
    private int ticksElapsed = animationLength;

    @Environment(EnvType.CLIENT)
    private final List<Integer> particleTicks = List.of(
        30,
        42,
        51,
        59,
        66,
        72,
        78,
        84,
        89,
        94,
        98,
        103,
        108,
        114,
        124,
        140
    );
    @Environment(EnvType.CLIENT)
    private void spewParticles(boolean special, DyeColor urplePinkMagentaWhatever) {
        Vec3d up = renderData.translateDir.toVec3d();

        Vec3d centerBottom = pos.toCenterPos().subtract(up.multiply(0.5));
        // hexagon is at the tippy top.
        Vec3d basePos = centerBottom.add(up.multiply(renderData.translation).multiply(2));

        // yo bro why you urple
        int color = getPigmentFromDye(urplePinkMagentaWhatever)
            .getColorProvider()
            .getColor(world.getTime(), pos.toCenterPos());

        if (special)
            makeParticlesInSpiralGoUp(
                world,
                centerBottom,
                up,
                // sqrt of 2 matches up with the chalk outer edge line
                1.41421356,
                1.41421356,
                color,
                // this makes sure the last particle's speed is as close to 0 as possible
                // 0 vel as it can be
                i -> 0.217 - (double)i*0.0006,
                false
            );

        for (int i = 0; i < (special ? 50 : 20); i++) {
            spewParticle(
                up,
                basePos,
                color,
                special
            );
        }
    }
    @Environment(EnvType.CLIENT)
    private void spewParticle(
        Vec3d up,
        Vec3d basePos,
        int color,
        boolean special
    ) {
        double deviationNum = 0.1;
        Vec3d deviation = new Vec3d(
            deviationNum - deviationNum*2*Math.random(),
            deviationNum - deviationNum*2*Math.random(),
            deviationNum - deviationNum*2*Math.random()
        );
        Vec3d pos = new Vec3d(
            basePos.x + deviation.x*up.x,
            basePos.y + deviation.y*up.y,
            basePos.z + deviation.z*up.z
        );

        double theta = 2*Math.PI*Math.random();
        double radius = (special ? 0.75 : 0.5)*Math.random();
        double x = Math.cos(theta)*radius;
        double y = Math.sin(theta)*radius;

        Vec3d vel = switch (
            Direction.getFacing(
                Math.abs(up.x),
                Math.abs(up.y),
                Math.abs(up.z)
            )
        ) {
            case UP -> new Vec3d(x, up.y, y).normalize();
            case EAST -> new Vec3d(up.x, y, x).normalize();
            case SOUTH -> new Vec3d(x, y, up.z).normalize();
            default -> new Vec3d(0.0, 0.0, 0.0);
        };
        vel = vel.multiply(0.1);

        world.addParticle(
            new ConjureParticleOptions(color),
            pos.x, pos.y, pos.z,
            vel.x, vel.y, vel.z
        );
    }
    @Environment(EnvType.CLIENT)
    private void makeDaSound(SoundEvent sound, float volume, float pitch) {
        Vec3d pos = this.pos.toCenterPos();
        world.playSound(
            pos.x,
            pos.y,
            pos.z,
            sound,
            SoundCategory.BLOCKS,
            volume,
            pitch,
            false
        );
    }
    @Environment(EnvType.CLIENT)
    public RenderData renderData;
    // power-on is a 7-second animation
    // it charges up for 5 seconds, casts, then cools down for 2
    // therefore, whatever target
    @Environment(EnvType.CLIENT)
    protected class RenderData {
        private RenderData(Direction whereverDownGoes) {
            // argument name shorten
            Direction axis = whereverDownGoes;

            translateDir = new ANormalFuckingVector3f(
                axis.getOpposite()
            );

            if (sameAxis(axis, Direction.WEST)) {
                rotationAxis = RotationAxis.POSITIVE_X;
                inverseRotationAxis = RotationAxis.NEGATIVE_X;

            } else if (sameAxis(axis, Direction.DOWN)) {
                rotationAxis = RotationAxis.POSITIVE_Y;
                inverseRotationAxis = RotationAxis.NEGATIVE_Y;

            } else {
                rotationAxis = RotationAxis.POSITIVE_Z;
                inverseRotationAxis = RotationAxis.NEGATIVE_Z;
            }

        }

        public Quaternionf rotate(float tickDelta) {
            return rotationAxis.rotationDegrees(
                MathHelper.lerp(tickDelta, rotation, rotationNext)
            );
        }
        public Quaternionf rotateInverse(float tickDelta) {
            return inverseRotationAxis.rotationDegrees(
                MathHelper.lerp(tickDelta, rotation, rotationNext)
            );
        }
        public ANormalFuckingVector3f translate(float tickDelta) {
            return translateDir.mul(MathHelper.lerp(tickDelta, translation, translationNext));
        }


        public final RotationAxis rotationAxis;
        public final RotationAxis inverseRotationAxis;

        private float rotateEquation(int x) {
            // math sure is neat
            // thanks robbie (and nathansnail)
            return x <= 100 ?
                18f/175f*x*x :
                -9f*x*x/35f + 72f*x - 3600f;
        }
        private void rotateForMe(int ticksElapsed) {
            rotation = rotateEquation(ticksElapsed);
            rotationNext = rotateEquation(ticksElapsed + 1);
        }
        public float rotation = 0f;
        public float rotationNext = 0f;


        private float translateLerp(float x) {
            return x <= 100 ?
                MathHelper.lerp(x / peakTranslationTime, 0f, peakTranslation) :
                MathHelper.lerp((x - 100f) / translationDropOffTime, peakTranslation, 0f);
        }
        private void translateForMe(int ticksElapsed) {
            translation = translateLerp(ticksElapsed);
            translationNext = translateLerp(ticksElapsed + 1);
        }
        public final ANormalFuckingVector3f translateDir;
        private final float peakTranslation = 0.7f;
        private final float peakTranslationTime = 100f; // 5s
        private final float translationDropOffTime = 40f; // 2s
        public float translation = 0f;
        public float translationNext = 0f;
    }
}
