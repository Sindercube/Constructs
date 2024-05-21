package com.sindercube.constructs.main;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;

public class ConstructData {

	private static Character fromString(String string) {
		return string.charAt(0);
	}

	private static final Codec<Character> CHAR_CODEC = Codec.STRING.xmap(ConstructData::fromString, Object::toString);
	private static final Codec<Map<Character, Blockish>> KEY_CODEC = Codec.unboundedMap(CHAR_CODEC, Blockish.CODEC);

	public static final Codec<ConstructData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			StringPattern.CODEC.fieldOf("pattern").forGetter(null),
			KEY_CODEC.fieldOf("key").forGetter(null),
			Vec3i.CODEC.fieldOf("offset").forGetter(null),
			Registries.ENTITY_TYPE.getCodec().fieldOf("entity").forGetter(null)
	).apply(instance, ConstructData::new));

	private final BlockPattern pattern;
	private final Vec3i offset;
	private final EntityType<?> entity;

	public ConstructData (
			StringPattern pattern,
			Map<Character, Blockish> keys,
			Vec3i offset,
			EntityType<?> entity
	) {
		keys = modifyKeys(keys);
		this.pattern = pattern.generateBlockPattern(keys);
		this.offset = offset;
		this.entity = entity;
	}

	public Map<Character, Blockish> modifyKeys(Map<Character, Blockish> keys) {
		var copy = new HashMap<>(keys);
		copy.put(' ', new Blockish.BlockEntry(Blocks.AIR));
		return ImmutableMap.copyOf(copy);
	}

	public BlockPattern getPattern() {
		return pattern;
	}

	public EntityType<?> getEntity() {
		return entity;
	}

	public BlockPos offsetPosition(BlockPattern.Result result) {
		return result.translate(offset.getX(), offset.getY(), offset.getZ()).getBlockPos();
	}

}
