package com.sindercube.constructs.mixin;

import com.sindercube.constructs.main.ConstructData;
import com.sindercube.constructs.main.ConstructLoader;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

	@Shadow private static void spawnEntity(World world, BlockPattern.Result patternResult, Entity entity, BlockPos pos) {}

	/**
	 * @author Sindercube
	 * @reason Data driven constructs
	 */
	@Overwrite
	private void trySpawnEntity(World world, BlockPos pos) {
		ConstructLoader.INSTANCE.getConstructs().forEach(construct -> trySpawnConstruct(world, pos, construct));
	}

	@Unique
	public void trySpawnConstruct(World world, BlockPos pos, ConstructData construct) {
		BlockPattern.Result result = construct.getPattern().searchAround(world, pos);
		if (result == null) return;

		Entity entity = construct.getEntity().create(world);
		if (entity == null) return;

		spawnEntity(world, result, entity, construct.offsetPosition(result));
		if (entity instanceof IronGolemEntity ironGolem) ironGolem.setPlayerCreated(true);
	}

	/**
	 * @author Sindercube
	 * @reason Data driven constructs
	 */
	@Overwrite
	public boolean canDispense(WorldView world, BlockPos pos) {
		for (ConstructData construct : ConstructLoader.INSTANCE.getConstructs()) {
			if (construct.getPattern().searchAround(world, pos) != null) return true;
		}
		return false;
	}

}