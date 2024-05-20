package com.sindercube.constructs.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.sindercube.constructs.Constructs;
import net.insomniacs.nucleus.api.dataLoader.SimpleFileLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConstructLoader implements SimpleFileLoader {

	public static final ConstructLoader INSTANCE = new ConstructLoader();

	private final List<ConstructData> constructs;

	private ConstructLoader() {
		this.constructs = new ArrayList<>();
	}

	public List<ConstructData> getConstructs() {
		return constructs;
	}

	@Override
	public Identifier getFabricId() {
		return Constructs.of("constructs");
	}

	@Override
	public void init(DataFileLoader loader, ResourceManager manager) {
		loader.JSON.findGlob("constructs/*.json", this::readConstructFile);
	}

	public void readConstructFile(Identifier id, JsonElement data) {
		JsonObject object = data.getAsJsonObject();
		ConstructData construct = ConstructData.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow();
		this.constructs.add(construct);
	}

}
