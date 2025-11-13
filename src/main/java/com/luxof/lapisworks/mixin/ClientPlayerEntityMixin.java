package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.BlockDowser;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements BlockDowser {
    public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    private Map<Block, Pair<BlockPos, Double>> map = new HashMap<>();
    private Map<Block, Pair<BlockPos, Double>> mapHOT = new HashMap<>();
    protected void Dowser() {
        LOGGER.info("The thread has started!");
        while (true) {
            if (MinecraftClient.getInstance().isPaused()) continue;
            mapHOT.forEach((block, any) -> {
                map.put(block, any);
                mapHOT.put(block, null);
            });
            Vec3d eyePos = this.getEyePos();
            Vec3d size = new Vec3d(100, 100, 100);
            Box box = new Box(eyePos.subtract(size), eyePos.add(size));

            for (double x = box.minX; x < box.maxX; x++) {
                for (double y = box.minY; y < box.maxY; y++) {
                    for (double z = box.minZ; z < box.maxZ; z++) {
                        BlockPos pos = BlockPos.ofFloored(x, y, z);
                        double dist = pos.getSquaredDistance(eyePos);
                        BlockState state = this.getWorld().getBlockState(pos);
                        Block block = state.getBlock();

                        if (!mapHOT.containsKey(block)) continue;
                        Pair<BlockPos, Double> contending = mapHOT.get(block);
                        if (contending != null && contending.getSecond() < dist) continue;

                        mapHOT.put(block, new Pair<BlockPos, Double>(pos, dist));
                    }
                }
            }
            try {
                Thread.sleep(timeBetweenDowsesMillis);
            } catch (InterruptedException e) {
                LOGGER.error("Why the fuck was the dowser's sleeping interrupted?");
                break;
            }
        }
    }

    private long timeBetweenDowsesMillis = 1000;

    private Thread dowser = null;
    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (dowser == null || !dowser.isAlive()) {
            dowser = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Dowser();
                    } finally {
                        LOGGER.error("THE THREAD DIED. WHY DID IT DIE???");
                    }
                }
            });
            dowser.start();
        }
    }

    @Override
    public boolean addTarget(Block block) {
        if (map.containsKey(block)) return false;
        map.put(block, null);
        mapHOT.put(block, null);
        return true;
    }
    @Override
    public boolean removeTarget(Block block) {
        if (!map.containsKey(block)) return false;
        map.remove(block);
        mapHOT.remove(block);
        return true;
    }
    @Override
    public List<Block> getTargets() {
        return map.keySet().stream().toList();
    }
    @Override @Nullable
    public Pair<BlockPos, Double> dowse(Block block) { return map.get(block); }
}
