package arathain.miku_machines.mixin;

import arathain.miku_machines.logic.ryanhcode.WorldshellCollisionPass;
import arathain.miku_machines.logic.worldshell.WorldshellWrapper;
import arathain.miku_machines.logic.worldshell.WorldshellWrapperHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin implements WorldshellWrapperHolder {
	@Unique
	private final WorldshellWrapper shell = new WorldshellWrapper();

	@Override
	public WorldshellWrapper getWorldshell() {
		return shell;
	}

	@Shadow
	public abstract World getWorld();

	/**
	 * Universal worldshell collision hook.
	 * */
	@ModifyArg(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"), index = 0)
	private Vec3d connate$collideWorldshells(Vec3d movement) {
		Vec3d result = WorldshellCollisionPass.collideWithWorldshells(this.getWorld(), shell, (Entity) (Object) this, movement);
		return result;
	}


}
