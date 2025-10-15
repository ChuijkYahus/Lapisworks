package com.luxof.lapisworks.mixin;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

import org.jetbrains.annotations.Nullable;

import org.objectweb.asm.tree.ClassNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class FullLapixicalMixinConfigPlugin implements IMixinConfigPlugin {
	@Nullable
	public static Integer verDifference(String modid, String targetVersion) {
		try {
			Version currentVer = FabricLoader.getInstance().getModContainer(modid).get()
				.getMetadata().getVersion();
			Version targetVer = Version.parse(targetVersion);
			return currentVer.compareTo(targetVer);
		} catch (Exception e) {
			return null;
		}
	}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Integer verDiff = verDifference("hexical", "1.5.0");
        return verDiff != null && verDiff > 0;
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
