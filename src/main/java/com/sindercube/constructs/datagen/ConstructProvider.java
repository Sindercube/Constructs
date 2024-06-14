package com.sindercube.constructs.datagen;

import com.sindercube.constructs.main.Blockish;
import com.sindercube.constructs.main.ConstructData;
import com.sindercube.constructs.main.StringPattern;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ConstructProvider implements DataProvider {

	@Override
	public String getName() {
		return "Constructs";
	}


	protected final FabricDataOutput dataOutput;
	protected final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

	public ConstructProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		this.dataOutput = dataOutput;
		this.registryLookup = registryLookup;
	}


	public abstract void generateConstructs(RegistryWrapper.WrapperLookup registryLookup, ConstructsBuilder builder);


	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return registryLookup.thenCompose(lookup -> this.run(writer, lookup));
	}

	public CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup lookup) {
		var builder = new ConstructsBuilder();
		generateConstructs(lookup, builder);
		var constructs = builder.constructData;

		List<CompletableFuture<?>> result = new ArrayList<>();
		constructs.forEach((path, data) -> {
			var id = dataOutput
					.getResolver(DataOutput.OutputType.DATA_PACK, "constructs")
					.resolveJson(Identifier.of(dataOutput.getModId(), path));
			result.add(DataProvider.writeToPath(writer, data.toJson(), id));
		});

		return CompletableFuture.allOf(result.toArray(CompletableFuture[]::new));
	}


	public static class ConstructsBuilder {

		protected final HashMap<String, ConstructData> constructData;

		private ConstructsBuilder() {
			this.constructData = new HashMap<>();
		}

		public ConstructsBuilder add(String path, ConstructData data) {
			constructData.put(path, data);
			return this;
		}

		public ConstructsBuilder add(
				String path,
				StringPattern pattern,
				Map<Character, Blockish> keys,
				Vec3i offset,
				EntityType<?> entity
		) {
			var data = ConstructData.create(pattern, keys, offset, entity);
			add(path, data);
			return this;
		}

		public ConstructsBuilder add(
				String path,
				StringPattern pattern,
				Map<Character, Blockish> keys,
				EntityType<?> entity
		) {
			var data = ConstructData.create(pattern, keys, Vec3i.ZERO, entity);
			add(path, data);
			return this;
		}

		public ConstructsBuilder add(
				String path,
				BlockPattern pattern,
				Vec3i offset,
				EntityType<?> entity
		) {
			var data = new ConstructData(pattern, offset, entity);
			add(path, data);
			return this;
		}

		public ConstructsBuilder add(
				String path,
				BlockPattern pattern,
				EntityType<?> entity
		) {
			var data = new ConstructData(pattern, Vec3i.ZERO, entity);
			add(path, data);
			return this;
		}

	}

}
