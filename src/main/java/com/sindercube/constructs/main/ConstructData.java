package com.sindercube.constructs.main;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.Map;

public class ConstructData {

	private final BlockPattern pattern;
	private final Vec3i offset;
	private final EntityType<?> entity;

	public BlockPattern getPattern() {
		return pattern;
	}

	public EntityType<?> getEntity() {
		return entity;
	}

	public BlockPos offsetPosition(BlockPattern.Result result) {
		return result.translate(offset.getX(), offset.getY(), offset.getZ()).getBlockPos();
	}

	public ConstructData (
			List<String> pattern,
			Map<String, Blockish> keys,
			Vec3i offset,
			EntityType<?> entity
	) {
		this.pattern = fromData(pattern, keys);
		this.offset = offset;
		this.entity = entity;
	}

	public static BlockPattern fromData(List<String> pattern, Map<String, Blockish> keys) {
		var builder = BlockPatternBuilder.start();
		builder = builder.aisle(pattern.toArray(String[]::new));
		for (String key : keys.keySet()) {
			var blockish = keys.get(key);
			builder.where(key.charAt(0), CachedBlockPosition.matchesBlockState(blockish.match()));
		}
		// TODO maybe one day make it ignore blocks around it
//		builder.where(' ', p -> true);
		builder.where(' ', CachedBlockPosition.matchesBlockState(AbstractBlock.AbstractBlockState::isAir));
		return builder.build();
	}

	private static final Codec<Map<String, Blockish>> KEY_CODEC = Codec.unboundedMap(Codec.STRING, Blockish.CODEC);

	public static final Codec<ConstructData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("pattern").forGetter(null),
			KEY_CODEC.fieldOf("key").forGetter(null),
			Vec3i.CODEC.fieldOf("offset").forGetter(null),
			Registries.ENTITY_TYPE.getCodec().fieldOf("entity").forGetter(null)
	).apply(instance, ConstructData::new));

}
