package mechanicalarms.common.logic.movement;

import mechanicalarms.common.logic.behavior.ActionResult;
import mechanicalarms.common.logic.behavior.InteractionType;
import mechanicalarms.common.tile.TileArmBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;

import static net.minecraft.util.EnumFacing.*;

public class MotorCortex implements INBTSerializable<NBTTagList> {

    private final float armSize;
    private final InteractionType interactionType;
    float[][] rotation = new float[3][3];
    float[][] animationRotation = new float[3][3];
    TileArmBase te;
    private final static float PI = (float) Math.PI;

    public MotorCortex(TileArmBase tileArmBase, float armSize, InteractionType interactionType) {
        te = tileArmBase;
        this.armSize = armSize;
        this.interactionType = interactionType;
    }

    public ActionResult move(Vec3d armPoint, Vec3d target, EnumFacing facing) {
        Vec3d combinedVec = target.subtract(armPoint);
        float pitch = (float) Math.atan2(combinedVec.y, Math.sqrt(combinedVec.x * combinedVec.x + combinedVec.z * combinedVec.z));
        float yaw = (float) (Math.atan2(-combinedVec.z, combinedVec.x) - Math.PI /2);

        float dist = (float) combinedVec.length();

        float extraPitchArc = (float) Math.acos(dist / armSize / 2);
        if (Double.isNaN(extraPitchArc)) {
            extraPitchArc = 0;
        }

        float armArcTarget = (float) (Math.asin(dist / armSize / 2) * 2 - PI);
        if (Double.isNaN(armArcTarget)) {
            armArcTarget = 0;
        }

        pitch = pitch + extraPitchArc;

        boolean distReached = false;

        animationRotation[1][0] = rotation[1][0];
        rotation[1][0] = (rotateShortest(rotation[1][0], 0.1f, armArcTarget));
        if (rotation[1][0] == armArcTarget) {
            if (extraPitchArc != 0) {
                distReached = true;
            } else {
                distReached = dist <= 2 * armSize + 0.5;
            }
        }

        animationRotation[0][0] = rotation[0][0];
        rotation[0][0] = rotateShortest(rotation[0][0], 0.1f, pitch);
        boolean pitchReached = rotation[0][0] == pitch;

        animationRotation[0][1] = rotation[0][1];
        rotation[0][1] = rotateShortest(rotation[0][1], 0.1f, yaw);
        float yawDiff = (Math.abs(rotation[0][1]) - Math.abs(animationRotation[0][1]));
        boolean yawReached = (yawDiff < 0.01 && yawDiff > -0.01);

        animationRotation[2][0] = rotation[2][0];
        animationRotation[2][1] = rotation[2][1];

        if (yawReached && pitchReached && distReached) {

            float targetHandYaw = (PI - rotation[0][1]) % PI;
            float targetHandPitch = -(rotation[1][0] + rotation[0][0]) % PI;
            float targetHandRoll = 0;


            if (facing == UP) {
                targetHandYaw = 0;
                targetHandPitch = targetHandPitch -PI /2;
               
            } else if (facing == DOWN) {
                targetHandYaw = 0;
                targetHandPitch = targetHandPitch + PI/2;
                
            } else if (armPoint.x > target.x && armPoint.z < target.z) {
                //arm is NE from target
                if (facing == SOUTH) {
                    targetHandYaw = targetHandYaw + PI;
                } else if (facing == NORTH) {
                    targetHandPitch = targetHandPitch + PI;
                    targetHandYaw = -targetHandYaw + PI ;
                } else if (facing == EAST) {
                    targetHandPitch = targetHandPitch - PI;
                    targetHandYaw = -targetHandYaw - PI/2;
                } else {
                    targetHandYaw =  targetHandYaw +PI/2;
                }
            } else if (armPoint.x < target.x && armPoint.z < target.z) {
                //arm is NW from target
                if (facing == SOUTH) {
                    targetHandYaw = targetHandYaw -2 * PI;
                } else if (facing == NORTH) {
                    targetHandPitch = targetHandPitch + PI;
                    targetHandYaw = -targetHandYaw ;
                } else if (facing == WEST) {
                    targetHandPitch = targetHandPitch - PI;
                    targetHandYaw = -targetHandYaw -PI/2;
                }else if (facing == EAST) {
                    targetHandPitch = targetHandPitch - PI;
                    targetHandYaw = -targetHandYaw + PI/2;
                }
            } else if (armPoint.x < target.x && armPoint.z > target.z) {
                //arm is SW from target
                if (facing == NORTH) {
                    targetHandYaw = targetHandYaw - PI ;
                } else if (facing == SOUTH) {
                    targetHandPitch = targetHandPitch - PI;
                    targetHandYaw = -targetHandYaw + PI;
                } else if (facing == EAST) {
                    targetHandPitch = targetHandPitch - PI;
                    targetHandYaw = -targetHandYaw + PI /2;
                } else {
                    targetHandPitch =targetHandPitch + PI;
                    targetHandYaw = -targetHandYaw - PI/2;
                }
            } else if (armPoint.x > target.x && armPoint.z > target.z) {
                //arm is SE from target
                if (facing == NORTH) {
                    targetHandYaw = targetHandYaw - 2* PI ;
                } else if (facing == EAST) {
                    targetHandPitch = targetHandPitch - PI;
                    targetHandYaw = -targetHandYaw - PI/2;
                } else if (facing == SOUTH) {
                    targetHandYaw = -targetHandYaw + 2* PI;
                    targetHandPitch = targetHandPitch - PI;
                } else {
                    targetHandYaw = targetHandYaw + PI /2 ;
                }
            } else if (armPoint.x == target.x) {
                if (facing == NORTH ) {
                    targetHandPitch = targetHandPitch -PI;
                    targetHandYaw = 0;
                }
                if (facing == SOUTH) {
                    targetHandYaw = 0;
                }

                if (armPoint.z > target.z) {
                    if (facing == WEST) {
                        targetHandYaw = targetHandYaw - PI / 2;
                        targetHandPitch = targetHandPitch - PI / 2;
                    } else if (facing == NORTH) {
                        targetHandYaw = 0;
                    } else if (facing == EAST) {
                        targetHandYaw = targetHandYaw + PI / 2;
                        targetHandPitch = targetHandPitch + PI / 2;
                    }
                } else {
                    if (facing == WEST) {
                        targetHandYaw = targetHandYaw + PI /2;
                        targetHandPitch = targetHandPitch + PI / 2;
                    targetHandRoll = targetHandRoll - PI;
                    } else if (facing == EAST) {
                        targetHandYaw = targetHandYaw - PI / 2;
                        targetHandPitch = targetHandPitch - PI / 2;
                    } else if (facing == SOUTH) {
                        targetHandYaw = 0;
                        targetHandPitch = targetHandPitch - PI;
                       
                    }
                }
            } else if (armPoint.z == target.z) {
                if (armPoint.x > target.x) {
                    //looks ok
                    if (facing == SOUTH) {
                        targetHandYaw = targetHandYaw - PI;
                         targetHandRoll = targetHandRoll - PI /2;
                        targetHandPitch = targetHandPitch - PI / 2;
                    } else if (facing == NORTH) {
                        targetHandRoll = targetHandRoll + PI /2;
                        targetHandPitch = targetHandPitch + PI / 2;
                    } else if (facing == EAST) {
                        targetHandYaw = targetHandYaw - PI / 2;
                    } else if (facing == WEST) {
                        targetHandYaw = targetHandYaw - PI / 2;
                        targetHandPitch = targetHandPitch - PI;
                        
                    }
                } else {
                    if (facing == NORTH) {
                        targetHandYaw = targetHandYaw - PI;
                        targetHandRoll = targetHandRoll - PI /2;
                        targetHandPitch = targetHandPitch - PI / 2;
                    } else if (facing == SOUTH) {
                        targetHandRoll = targetHandRoll + PI /2;
                        targetHandPitch = targetHandPitch + PI / 2;
                    } else if (facing == WEST) {
                        targetHandYaw = targetHandYaw - PI / 2;
                    } else {
                        targetHandYaw = targetHandYaw - PI / 2;
                        targetHandPitch = targetHandPitch - PI;
                        
                    }
                }
            }

            animationRotation[2][1] = rotation[2][1];
            rotation[2][1] = rotateShortest(rotation[2][1], 0.10f, targetHandYaw);
            float yawDiffHand = (Math.abs(rotation[2][1]) - Math.abs(animationRotation[2][1]));
            boolean yawHandReached = (yawDiffHand < 0.01 && yawDiffHand > -0.01);

            animationRotation[2][0] = rotation[2][0];
            rotation[2][0] = rotateShortest(rotation[2][0], 0.10f, targetHandPitch);
            float pitchDiffHand = (Math.abs(rotation[2][0]) - Math.abs(animationRotation[2][0]));
            boolean pitchHandReached = (pitchDiffHand < 0.01 && pitchDiffHand > -0.01);


            animationRotation[2][2] = rotation[2][2];
            rotation[2][2] = rotateShortest(rotation[2][2], 0.10f, targetHandRoll);
            float rollDiffHand = (Math.abs(rotation[2][2]) - Math.abs(animationRotation[2][2]));
            boolean rollHandReached = (rollDiffHand < 0.01 && rollDiffHand > -0.01);

            if (pitchHandReached && yawHandReached && rollHandReached) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONTINUE;
        } else {
            float targetHandYaw = 0;
            float targetHandPitch = -PI / 2 - rotation[1][0] - rotation[0][0];
            float targetHandRoll = 0;

            animationRotation[2][2] = rotation[2][2];
            rotation[2][2] = rotateShortest(rotation[2][2], 0.10f, targetHandRoll);
            animationRotation[2][1] = rotation[2][1];
            rotation[2][1] = rotateShortest(rotation[2][1], 0.10F, targetHandYaw);
            animationRotation[2][0] = rotation[2][0];
            rotation[2][0] = rotateToReach(rotation[2][0], 0.10F, targetHandPitch);
        }


        return ActionResult.CONTINUE;
    }

    public float[] getRotation(int idx) {
        return rotation[idx];
    }

    float rotateShortest(float currentRotation, float angularSpeed, float targetRotation) {
        currentRotation = currentRotation % (2 * PI);

        float shortestAngle = (float) (((targetRotation - currentRotation) + 3 * Math.PI) % (2 * Math.PI) - Math.PI);

        if (shortestAngle >= 0.1F) {
            float result = currentRotation + Math.min(angularSpeed, shortestAngle);
            return result % (2 * PI);
        } else if (shortestAngle <= -0.1F) {
            float result = currentRotation + Math.max(-angularSpeed, shortestAngle);
            return result % (2 * PI);
        } else {
            return (currentRotation + shortestAngle) % (2 * PI);
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
