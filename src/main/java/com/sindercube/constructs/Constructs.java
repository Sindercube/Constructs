package com.sindercube.constructs;

import com.sindercube.constructs.main.ConstructLoader;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constructs implements ModInitializer {

	public static final String MOD_ID = "constructs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier of(String name) {
		return new Identifier(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Constructs Loaded!");
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ConstructLoader.INSTANCE);
	}

}