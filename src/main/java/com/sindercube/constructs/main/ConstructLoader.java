package com.sindercube.constructs.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.sindercube.constructs.Constructs;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConstructLoader implements SimpleSynchronousResourceReloadListener {

	@Override
	public Identifier getFabricId() {
		return Constructs.of("constructs");
	}

	public static final ConstructLoader INSTANCE = new ConstructLoader();

	private static final ResourceFinder FINDER = new ResourceFinder("constructs", ".json");

	private final List<ConstructData> constructs;

	private ConstructLoader() {
		this.constructs = new ArrayList<>();
	}


	@Override
	public void reload(ResourceManager manager) {
		FINDER.findResources(manager).forEach((id, resource) -> {
			try {
				JsonObject object = JsonParser.parseReader(resource.getReader()).getAsJsonObject();
				ConstructData construct = ConstructData.CODEC.parse(JsonOps.INSTANCE, object).getOrThrow();
				constructs.add(construct);
			} catch (Exception exception) {
				Constructs.LOGGER.warn("Unable to load construct '{}' in {} in data pack: '{}'", id, "fonts.json", resource.getPackId(), exception);
			}
		});
	}


	public static List<ConstructData> getConstructs() {
		return INSTANCE.constructs;
	}

}
