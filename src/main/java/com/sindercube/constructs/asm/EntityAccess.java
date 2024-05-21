package com.sindercube.constructs.asm;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface EntityAccess {

	default void onConstructed(BlockPattern.Result result, World world, BlockPos headPos) {}

}
