package arathain.miku_machines.logic.worldshell;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Map;

/**
 * Abstract extension of {@link Worldshell} that contains an axis it faces and rotates around. All current implementations of {@link Worldshell} extend this class.
 * @see ConstantAxisLimitedWorldshell
 * @see SplineFollowingAxisLimitedWorldshell
 **/
public abstract class AxisLimitedWorldshell extends Worldshell {
	protected Vector3d axis;
	public AxisLimitedWorldshell(Map<BlockPos, BlockState> contained, Vec3d initialPos, BlockPos pivot, Vector3f initialAxis) {
		super(contained, initialPos, pivot);
		this.axis = initialAxis.get(new Vector3d());
	}

	public AxisLimitedWorldshell putAxis(Vector3d axis) {
		this.axis = axis;
		return this;
	}

	public Vector3d getAxis() {
		return axis;
	}


}
