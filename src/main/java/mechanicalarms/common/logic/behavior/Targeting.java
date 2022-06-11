package mechanicalarms.common.logic.behavior;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Targeting {

    private BlockPos sourcePos;
    private Vec3d sourceVec;
    private BlockPos targetPos;
    private Vec3d targetVec;
    public Targeting() {

    }

    public Vec3d getSourceVec() {
        return sourceVec;
    }

    public Vec3d getTargetVec() {
        return targetVec;
    }

    public void setSource(BlockPos sourcePos) {
        this.sourcePos = sourcePos;
        this.sourceVec = new Vec3d(sourcePos.getX(), sourcePos.getY(), sourcePos.getZ());
    }

    public void setTarget(BlockPos targetPos) {
        this.targetPos = targetPos;
        this.targetVec = new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ());
    }

    public boolean hasInput() {
        return sourcePos != null;
    }

    public boolean hasOutput() {
        return targetPos != null;
    }
}
