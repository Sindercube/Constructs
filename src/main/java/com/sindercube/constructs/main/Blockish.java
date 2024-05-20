package com.sindercube.constructs.main;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.insomniacs.nucleus.api.utils.CodecUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Predicate;

public interface Blockish {

	Codec<Blockish> CODEC = CodecUtils.merge(BlockEntry.CODEC, TagEntry.CODEC, StateEntry.CODEC);

	Predicate<BlockState> match();

	record TagEntry(TagKey<Block> tag) implements Blockish {
		static final Codec<TagEntry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag").forGetter(TagEntry::tag)
		).apply(instance, TagEntry::new));

		@Override
		public Predicate<BlockState> match() {
			return state -> state.isIn(tag);
		}
	}

	record BlockEntry(Block block) implements Blockish {
		static final Codec<BlockEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Registries.BLOCK.getCodec().fieldOf("block").forGetter(BlockEntry::block)
		).apply(instance, BlockEntry::new));

		@Override
		public Predicate<BlockState> match() {
			return state -> state.isOf(block);
		}
	}

	record StateEntry(BlockState state) implements Blockish {
		static final Codec<StateEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				BlockState.CODEC.fieldOf("state").forGetter(StateEntry::state)
		).apply(instance, StateEntry::new));

		@Override
		public Predicate<BlockState> match() {
			return pState -> pState.equals(state);
		}
	}

}
