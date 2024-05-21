package com.sindercube.constructs.mixin;

import com.sindercube.constructs.main.ConstructData;
import com.sindercube.constructs.main.ConstructLoader;
import net.minecraft.block.Block;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

	@Unique
	private static final TagKey<Block> CONSTRUCT_BLOCK = TagKey.of(
			RegistryKeys.BLOCK,
			new Identifier("constructs", "construct_blocks")
	);

	@Shadow public abstract Block getBlock();


	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("RETURN"))
	private void injected(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (!cir.getReturnValue().isAccepted()) return;
		if (!this.getBlock().getDefaultState().isIn(CONSTRUCT_BLOCK)) return;

		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		ConstructLoader.INSTANCE.getConstructs().forEach(construct -> trySpawnConstruct(world, pos, construct));
	}

	@Unique
	public void trySpawnConstruct(World world, BlockPos pos, ConstructData construct) {
		BlockPattern.Result result = construct.getPattern().searchAround(world, pos);
		if (result == null) return;

		Entity entity = construct.getEntity().create(world);
		if (entity == null) return;

		CarvedPumpkinBlock.spawnEntity(world, result, entity, construct.offsetPosition(result));
		entity.onConstructed(result, world, pos);
	}

}
