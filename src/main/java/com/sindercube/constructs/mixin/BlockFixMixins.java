package com.sindercube.constructs.mixin;

import com.sindercube.constructs.main.ConstructUtil;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BlockFixMixins {

	@Mixin(CarvedPumpkinBlock.class)
	public abstract static class CarvedPumpkinBlockMixin {

		@Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
		private void cancelOnBlockAdded(CallbackInfo ci) {
			ci.cancel();
		}

		@Inject(method = "canDispense", at = @At("RETURN"), cancellable = true)
		private void modifyCanDispense(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
			cir.setReturnValue(ConstructUtil.canDispense(world, pos));
		}

	}

	@Mixin(WitherSkullBlock.class)
	public static class WitherSkullBlockMixin {

		@Inject(method = "onPlaced*", at = @At("HEAD"), cancellable = true)
		private void cancelOnBlockAdded(CallbackInfo ci) {
			ci.cancel();
		}

		@Inject(method = "canDispense", at = @At("RETURN"), cancellable = true)
		private static void modifyCanDispense(World world, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
			cir.setReturnValue(ConstructUtil.canDispense(world, pos));
		}

	}

}
