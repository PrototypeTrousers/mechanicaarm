package mechanicalarms.common.logic.movement;

import mechanicalarms.common.logic.behavior.ActionResult;
import mechanicalarms.common.logic.behavior.InteractionType;
import mechanicalarms.common.tile.TileArmBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
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

    public ActionResult move(Vec3d armPoint, Vec3d target, EnumFacing facing) {
        Vec3d combinedVec = target.subtract(armPoint);
        float pitch = (float) Math.atan2(combinedVec.y, Math.sqrt(combinedVec.x * combinedVec.x + combinedVec.z * combinedVec.z));
        float yaw = (float) Math.atan2(-combinedVec.z, combinedVec.x);

        float dist = (float) combinedVec.length();

        float extraPitchArc = (float) Math.acos(dist / armSize / 2);
        if (Double.isNaN(extraPitchArc)) {
            extraPitchArc = 0;
        }
        float armArcTarget = (float) (Math.asin(dist / armSize / 2) * 2 - Math.PI);
        if (Double.isNaN(armArcTarget)) {
            armArcTarget = 0;
        }

        pitch = pitch + extraPitchArc;

        boolean distReached = false;

        animationRotation[1][0] = rotation[1][0];
        rotation[1][0] = (rotateToReach(rotation[1][0], 0.1f, armArcTarget));
        if (rotation[1][0] == armArcTarget) {
            if (extraPitchArc != 0) {
                distReached = true;
            } else {
                distReached = dist <= 2 * armSize + 0.5;
            }
        }

        animationRotation[0][0] = rotation[0][0];
        rotation[0][0] = rotateX(rotation[0][0], 0.1f, pitch);
        boolean pitchReached = rotation[0][0] == pitch;

        animationRotation[0][1] = rotation[0][1];
        rotation[0][1] = rotateX(rotation[0][1], 0.1f, yaw);
        boolean yawReached = (rotation[0][1] == yaw || Math.abs(yaw) + Math.abs(rotation[0][1]) == Math.abs(2 * (float) Math.PI));

        animationRotation[2][0] = rotation[2][0];
        animationRotation[2][1] = rotation[2][1];
        rotation[2][0] = (float) (-Math.PI / 2 - rotation[1][0] - rotation[0][0]);

        if (yawReached && pitchReached && distReached) {
            if (facing.getOpposite() == EnumFacing.WEST) {
                rotation[2][0] = (float) (rotation[2][0] + Math.PI / 2);
                rotation[2][1] = (float) -(rotation[0][1] + Math.PI);
            } else if (facing.getOpposite() == EnumFacing.EAST) {
                rotation[2][0] = (float) (rotation[2][0] + Math.PI / 2);
                rotation[2][1] = (float) -(rotation[0][1]);
            } else if (facing.getOpposite() == EnumFacing.NORTH) {
                rotation[2][0] = (float) (rotation[2][0] + Math.PI / 2);
                rotation[2][1] = (float) -(rotation[0][1] - Math.PI / 2);
            } else if (facing.getOpposite() == EnumFacing.SOUTH) {
                rotation[2][0] = (float) (rotation[2][0] + Math.PI / 2);
                rotation[2][1] = (float) -(rotation[0][1] + Math.PI / 2);
            } else if (facing.getOpposite() == EnumFacing.UP) {
                rotation[2][0] = (float) (rotation[2][0] + Math.PI);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONTINUE;
    }

    public float[] getRotation(int idx) {
        return rotation[idx];
    }

    float rotateX(float currentRotation, float angularSpeed, float targetRotation) {
        currentRotation = currentRotation % (2 * (float) Math.PI);

        float shortestAngle = (float) (((targetRotation - currentRotation) + 3 * Math.PI) % (2 * Math.PI) - Math.PI);

        if (shortestAngle > 0.1F) {
            float result = currentRotation + angularSpeed;
            return result % (2 * (float) Math.PI);
        } else if (shortestAngle < -0.1F) {
            float result = currentRotation - angularSpeed;
            return result % (2 * (float) Math.PI);
        } else {
            return (currentRotation + shortestAngle) % (2 * (float) Math.PI);
        }
    }

    float rotateToReach(float currentRotation, float angularSpeed, float targetedRotation) {
        float diff = targetedRotation - currentRotation;
        if (diff <= -0.1) {
            return currentRotation - angularSpeed;
        } else if (diff >= 0.1) {
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

        for (int i = 0; i < rotation.length; i++) {
            System.arraycopy(rotation[i], 0, animationRotation[i], 0, animationRotation.length);
        }
    }
}
