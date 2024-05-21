package com.sindercube.constructs.mixin;

import com.sindercube.constructs.asm.EntityAccess;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("unused")
public class EntityFixMixins {

	@Mixin(IronGolemEntity.class)
	public abstract static class IronGolemMixin implements EntityAccess {

		@Shadow public abstract void setPlayerCreated(boolean playerCreated);

		@Override
		public void onConstructed(BlockPattern.Result result, World world, BlockPos headPos) {
			setPlayerCreated(true);
		}

	}


	@Mixin(WitherEntity.class)
	public abstract static class WitherEntityMixin extends Entity {

		public WitherEntityMixin(EntityType<?> type, World world) {
			super(type, world);
		}

		@Shadow
		public abstract void onSummoned();

		@Override
		public void onConstructed(BlockPattern.Result result, World world, BlockPos headPos) {
			BlockPos blockPos = result.translate(1, 2, 0).getBlockPos();
			this.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.55, (double)blockPos.getZ() + 0.5, result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
			this.setBodyYaw(result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F);
			this.onSummoned();
			var box = this.getBoundingBox().expand(50.0);
			world.getNonSpectatingEntities(ServerPlayerEntity.class, box)
					.forEach(player -> Criteria.SUMMONED_ENTITY.trigger(player, this));
		}

	}


}
