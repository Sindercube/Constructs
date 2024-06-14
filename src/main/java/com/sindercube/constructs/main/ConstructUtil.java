package com.sindercube.constructs.main;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ConstructUtil {

	public static final TagKey<Block> CONSTRUCT_BLOCK = TagKey.of(
			RegistryKeys.BLOCK,
			Identifier.of("constructs", "construct_blocks")
	);

	public static final Codec<Character> CHAR_CODEC = Codec.STRING.xmap(ConstructUtil::fromString, Object::toString);


	public static boolean isConstructBlock(BlockState state) {
		return state.isIn(CONSTRUCT_BLOCK);
	}

	public static boolean isConstructBlock(Block block) {
		return isConstructBlock(block.getDefaultState());
	}

	public static void trySpawnConstructs(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (isConstructBlock(block)) ConstructLoader.getConstructs()
				.forEach(construct -> trySpawnConstruct(world, pos, construct));
	}

	public static void trySpawnConstruct(World world, BlockPos pos, ConstructData construct) {
		BlockPattern.Result result = construct.matchPattern(world, pos);
		if (result == null) return;

		Entity entity = construct.createEntity(world);
		if (entity == null) return;

		CarvedPumpkinBlock.spawnEntity(world, result, entity, construct.offsetPosition(result));
		entity.onConstructed(result, world, pos);
	}

	public static boolean canDispense(WorldView world, BlockPos pos) {
		return ConstructLoader.getConstructs().stream()
				.map(construct -> construct.canConstruct(world, pos))
				.toList().contains(true);
	}


	private static Character fromString(String string) {
		return string.charAt(0);
	}

}
