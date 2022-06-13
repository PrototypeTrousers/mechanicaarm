package mechanicalarms.common.logic.movement;

import mechanicalarms.common.logic.behavior.ActionResult;
import mechanicalarms.common.logic.behavior.InteractionType;
import mechanicalarms.common.tile.TileArmBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;

public class MotorCortex implements INBTSerializable<NBTTagList> {

    private final float armSize;
    private final InteractionType interactionType;
    float[][] rotation = new float[3][3];
    float[][] animationRotation = new float[3][3];
    TileArmBase te;

    public MotorCortex(TileArmBase tileArmBase, float armSize, InteractionType interactionType) {
        te = tileArmBase;
        this.armSize = armSize;
        this.interactionType = interactionType;
    }

    public ActionResult move(Vec3d armPoint, Vec3d target) {
        Vec3d combinedVec = target.subtract(armPoint);
        double pitch = Math.atan2(combinedVec.y, Math.sqrt(combinedVec.x * combinedVec.x + combinedVec.z * combinedVec.z));
        double yaw = Math.atan2(-combinedVec.z, combinedVec.x);
/*
        if (yaw <= -Math.PI / 2) {
            yaw = yaw + Math.PI / 2;
        } else if (yaw > Math.PI / 2) {
            yaw = yaw - Math.PI / 2;
        }
*/
        float dist = (float) combinedVec.length();

        double extraPitchArc = Math.acos(dist / armSize / 2);
        if (Double.isNaN(extraPitchArc)) {
            extraPitchArc = 0;
        }
        double armArcTarget = Math.asin(dist / armSize / 2) * 2 - Math.PI;
        if (Double.isNaN(armArcTarget)) {
            armArcTarget = 0;
        }

        if (dist > 2 * armSize + 0.5) {
            return ActionResult.FAILURE;
        }

        pitch = pitch + extraPitchArc;

        boolean distReached = false;

        rotation[1][0] = (rotateToReach(rotation[1][0], 0.1f, (float) armArcTarget));
        if (rotation[1][0] >= (armArcTarget - 0.01f) && rotation[1][0] <= (armArcTarget + 0.01f)) {
            if (extraPitchArc != 0) {
                distReached = true;
            } else {
                distReached = dist <= 2 * armSize + 0.5;
            }
        }

        float rotPitch = rotateX(rotation[0][0], 0.1f, (float) pitch);
        boolean pitchReached = false;
        rotation[0][0] = rotPitch;

        if (rotPitch >= 0 && pitch >= 0) {
            if (rotPitch <= (pitch + 0.1f) && rotPitch >= (pitch - 0.1f)) {
                pitchReached = true;
            }
        } else if (rotPitch < 0 && pitch < 0) {
            if (rotPitch <= (pitch + 0.1f) && rotPitch >= (pitch - 0.1f)) {
                pitchReached = true;
            }
        }

        float rotYaw = rotateX(rotation[0][1], 0.1f, (float) yaw);
        boolean yawReached = false;
        rotation[0][1] = rotYaw;

        if (rotYaw >= 0 && yaw >= 0) {
            if (rotYaw <= (yaw + 0.1f) && rotYaw >= (yaw - 0.1f)) {
                yawReached = true;
            }
        } else if (rotYaw < 0 && yaw < 0) {
            if (rotYaw <= (yaw + 0.1f) && rotYaw >= (yaw - 0.1f)) {
                yawReached = true;
            }
        }

        if (yawReached && pitchReached && distReached) {
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONTINUE;
    }

    public float[] getRotation(int idx) {
        return rotation[idx];
    }

    float rotateX(float currentRotation, float angularSpeed, float targetRotation) {
        float diff = targetRotation - currentRotation;
        if (diff < -0.1) {
            float result = currentRotation - angularSpeed;
            return Math.max(result, targetRotation);

        } else if (diff > 0.1) {
            float result = currentRotation + angularSpeed;
            return Math.min(result, targetRotation);
        } else if (diff > -0.1 && diff < 0.1) {
            return targetRotation;
        }
        return targetRotation;
    }

    float rotateToReach(float currentRotation, float angularSpeed, float targetedRotation) {
        float diff = targetedRotation - currentRotation;
        if (diff < -0.1) {
            return currentRotation - angularSpeed;
        } else if (diff > 0.1) {
            return currentRotation + angularSpeed;
        } else if (diff > -0.1 && diff < 0.1) {
            return targetedRotation;
        }
        return currentRotation;
    }

    public void rest() {
        if (rotation[0][1] >= 0.099F) {
            rotation[0][1] = rotation[0][1] - 0.1F;
        } else if (rotation[0][1] <= -0.099F) {
            rotation[0][1] = rotation[0][1] + 0.1F;
        } else {
            rotation[0][1] = 0;
        }

        if (rotation[0][0] >= 0.099F) {
            rotation[0][0] = rotation[0][0] - 0.1F;
        } else if (rotation[0][0] <= -0.099F) {
            rotation[0][0] = rotation[0][0] + 0.1F;
        } else {
            rotation[0][0] = 0;
        }

        if (rotation[1][0] >= 0.099F) {
            rotation[1][0] = rotation[1][0] - 0.1F;
        } else if (rotation[0][0] <= -0.099F) {
            rotation[1][0] = rotation[1][0] + 0.1F;
        } else {
            rotation[1][0] = 0;
        }

        if (rotation[2][0] >= 0.099F) {
            rotation[2][0] = rotation[2][0] - 0.1F;
        } else if (rotation[0][0] <= -0.099F) {
            rotation[2][0] = rotation[2][0] + 0.1F;
        } else {
            rotation[2][0] = 0;
        }
    }

    public float[] getAnimationRotation(int idx) {
        return animationRotation[idx];
    }

    @Override
    public NBTTagList serializeNBT() {
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < 3; i++) {
            for (float r : getRotation(i)) {
                tagList.appendTag(new NBTTagFloat(r));
            }
        }
        return tagList;
    }

    @Override
    public void deserializeNBT(NBTTagList tagList) {
        int axis = 0;
        int coords = 0;
        for (int i = 0; i < tagList.tagCount(); i++) {
            if (coords > 2) {
                axis += 1;
                coords = 0;
            }
            getRotation(axis)[coords] = tagList.getFloatAt(i);
            coords++;
        }
    }
}
