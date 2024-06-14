package com.sindercube.constructs.main;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record ConstructData (
		BlockPattern pattern,
		Vec3i offset,
		EntityType<?> entity
) {

	private static final Codec<Map<Character, Blockish>> KEY_CODEC = Codec.unboundedMap(ConstructUtil.CHAR_CODEC, Blockish.CODEC);

	public static final Codec<ConstructData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			StringPattern.CODEC.fieldOf("pattern").forGetter(null),
			KEY_CODEC.fieldOf("key").forGetter(null),
			Vec3i.CODEC.fieldOf("offset").forGetter(null),
			Registries.ENTITY_TYPE.getCodec().fieldOf("entity").forGetter(null)
	).apply(instance, ConstructData::create));

	public JsonObject toJson() {
		return CODEC.encode(this, JsonOps.INSTANCE, null).getOrThrow().getAsJsonObject();
	}

	public static ConstructData create (
			StringPattern rawPattern,
			Map<Character, Blockish> keys,
			Vec3i offset,
			EntityType<?> entity
	) {
		keys = modifyKeys(keys);
		var pattern = rawPattern.generateBlockPattern(keys);
		return new ConstructData(pattern, offset, entity);
	}


	public static Map<Character, Blockish> modifyKeys(Map<Character, Blockish> keys) {
		var copy = new HashMap<>(keys);
		copy.put(' ', new Blockish.BlockEntry(Blocks.AIR));
		return ImmutableMap.copyOf(copy);
	}

	public BlockPos offsetPosition(BlockPattern.Result result) {
		return result.translate(offset.getX(), offset.getY(), offset.getZ()).getBlockPos();
	}

	public boolean canConstruct(WorldView world, BlockPos pos) {
		return pattern.searchAround(world, pos) != null;
	}


	@Nullable
	public BlockPattern.Result matchPattern(WorldView world, BlockPos pos) {
		return pattern.searchAround(world, pos);
	}

	@Nullable
	public Entity createEntity(World world) {
		return entity.create(world);
	}

}
