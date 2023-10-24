package arathain.connatepassage.logic.worldshell;

import arathain.connatepassage.ConnatePassage;
import arathain.connatepassage.logic.spline.CatmullRomCurveSpline;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

/**
 * An implementation of a {@link Worldshell} that moves the worldshell along a {@link CatmullRomCurveSpline}.
 **/
public class SplineFollowingAxisLimitedWorldshell extends AxisLimitedWorldshell implements ScrollableWorldshell {
	private float speed = 1f;
	private boolean loop = false;
	private CatmullRomCurveSpline spline;
	public SplineFollowingAxisLimitedWorldshell(Map<BlockPos, BlockState> contained, Vec3d initialPos, BlockPos pivot, Vector3f initialAxis) {
		super(contained, initialPos, pivot, initialAxis);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		spline.writeNbt(nbt);
	}

	@Override
	public void readUpdateNbt(NbtCompound nbt) {
		if(this.spline != null)
			this.spline.prevPos = nbt.getFloat("pos");
		this.speed = nbt.getFloat("speed");
		this.shutdownTickCountdown = nbt.getInt("sCd");
		this.invertedMotion = nbt.getBoolean("invM");
	}
	@Override
	public NbtCompound writeUpdateNbt(NbtCompound nbt) {
		nbt.putFloat("speed", speed);
		nbt.putFloat("pos", this.spline.pos);
		nbt.putInt("sCd", shutdownTickCountdown);
		nbt.putBoolean("invM", invertedMotion);
		return nbt;
	}
	@Override
	public Identifier getId() {
		return new Identifier(ConnatePassage.MODID, "spline_following");
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.spline = CatmullRomCurveSpline.readNbt(nbt);
		this.loop = spline.lastPointsMatch();
	}

	public void setSpeed(float spd) {
		speed = MathHelper.clamp(spd, -2, 2);
	}

	public float getSpeed() {
		return speed * (this.invertedMotion ? -1 : 1);
	}

	/**
	 * Creates a new spline for the worldshell to follow.
	 **/
	public SplineFollowingAxisLimitedWorldshell constructSpline(Vec3d... points) {
		this.spline = new CatmullRomCurveSpline(points);
		this.loop = spline.lastPointsMatch();
		if(!loop) {
			this.spline = CatmullRomCurveSpline.fromRaw(points);
		}
		return this;
	}

	public List<Vec3d> getPoints(float tickDelta, int amount, float distance) {
		return spline.getPointsAroundPos(tickDelta, amount, distance, loop);
	}

	@Override
	public void tick() {
		super.tick();

		if(this.shutdownTickCountdown > 0 || this.shutdownTickCountdown == -666) {
			if(this.shutdownTickCountdown != -666)
				this.shutdownTickCountdown--;
			if (loop) {
				this.spline.moveLoop(getSpeed());
			} else {
				this.spline.moveClamped(getSpeed());
			}

		}
		this.prevPos = this.pos;
		this.pos = this.spline.getPos(1);
		Vec3d prod = this.spline.getVelocity(1).normalize();
		checkRotation();
		this.axis = prod.toVector3f();
		this.prevRotation = rotation;
		this.rotation = new Quaternionf().rotateTo(new Vector3f(0, 0, -1), axis);
	}
}
