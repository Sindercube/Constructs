package com.sindercube.constructs.main;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.insomniacs.nucleus.api.utils.CodecUtils;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.Vec3i;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface StringPattern {

//	Codec<StringPattern> CODEC = CodecUtils.merge(Pattern1D.CODEC, Pattern2D.CODEC, Pattern3D.CODEC);
	Codec<StringPattern> CODEC = CodecUtils.merge(Pattern1D.CODEC, Pattern2D.CODEC);

	BlockPattern generateBlockPattern(Map<Character, Blockish> keys);

	default BlockPattern generateSimple(Map<Character, Blockish> keys, String... pattern) {
		var builder = BlockPatternBuilder.start();

		builder = builder.aisle(pattern);
		for (Character key : keys.keySet()) {
			var blockish = keys.get(key);
			builder.where(key, CachedBlockPosition.matchesBlockState(blockish.match()));
		}

		return builder.build();
	}

	record Pattern1D (String pattern) implements StringPattern {

		public static final Codec<Pattern1D> CODEC = Codec.STRING
				.xmap(Pattern1D::new, Pattern1D::pattern);

		@Override
		public BlockPattern generateBlockPattern(Map<Character, Blockish> keys) {
			return generateSimple(keys, pattern);
		}

	}

	record Pattern2D (String[] pattern) implements StringPattern {

		public static final Codec<Pattern2D> CODEC = Codec.STRING.listOf()
				.xmap(Pattern2D::toArray, Pattern2D::toList)
				.xmap(Pattern2D::new, Pattern2D::pattern);

		@Override
		public BlockPattern generateBlockPattern(Map<Character, Blockish> keys) {
			return generateSimple(keys, pattern);
		}

		static String[] toArray(List<String> list) {
			return list.toArray(String[]::new);
		}

		static List<String> toList(String[] array) {
			return Arrays.stream(array).toList();
		}

	}

	record Pattern3D (
			String[][] pattern
	) implements StringPattern {

		public static final Codec<Pattern3D> CODEC = Codec.STRING.listOf().listOf()
				.xmap(Pattern3D::toArray, Pattern3D::toList)
				.xmap(Pattern3D::new, Pattern3D::pattern);

		@Override
		@SuppressWarnings("unchecked")
		public BlockPattern generateBlockPattern(Map<Character, Blockish> keys) {
//			validate(keys);
			var size = getSize();
			Predicate<CachedBlockPosition>[][][] predicates = (Predicate<CachedBlockPosition>[][][]) Array.newInstance(
					Predicate.class,
					new int[]{size.getX(), size.getY(), size.getZ()}
			);

			for(int i = 0; i < size.getX(); ++i) {
				for(int j = 0; j < size.getY(); ++j) {
					for(int k = 0; k < size.getZ(); ++k) {
						char key = ' ';
						try {
							key = pattern[i][j].charAt(k);
						} catch (StringIndexOutOfBoundsException ignored) {}
						var blockish = keys.get(key);
						var predicate = CachedBlockPosition.matchesBlockState(blockish.match());
						predicates[i][j][k] = predicate;
					}
				}
			}

			return new BlockPattern(predicates);
		}

		private Vec3i getSize() {
			int width = 0;
			int height = 0;
			int length = 0;
			for (String[] xA : pattern) {
				int x = xA.length;
				if (x > width) width = x;
				for (String yA : xA) {
					int y = yA.length();
					if (y > height) height = y;
					for (char z : yA.toCharArray()) {
						if (z > length) length = z;
					}
				}
			}
			return new Vec3i(width, height, length);
		}

		private static final Joiner JOINER = Joiner.on(",");
		private void validate(Map<Character, Blockish> keys) {
			List<Character> list = Lists.newArrayList();

			keys.forEach((key, value) -> {
				if (value == null) list.add(key);
			});

			if (!list.isEmpty()) {
				throw new IllegalStateException("Predicates for character(s) " + JOINER.join(list) + " are missing");
			}
		}

		static String[][] toArray(List<List<String>> list) {
			return list.stream().map(Pattern2D::toArray).toArray(String[][]::new);
		}

		static List<List<String>> toList(String[][] array) {
			return Arrays.stream(array).map(Pattern2D::toList).toList();
		}

	}

}
