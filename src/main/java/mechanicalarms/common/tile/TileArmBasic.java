package mechanicalarms.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TileArmBasic extends TileEntity implements IAnimatable, ITickable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private final AnimationBuilder builder = new AnimationBuilder().addAnimation("nothing", true);
    private AnimationController<TileArmBasic> animationController;
    private final boolean extend = true;

    static float HARD_LIMIT = (float) (3 * Math.PI / 4);
    static float MINUS_HARD_LIMIT = (float) (-Math.PI / 4);
    private boolean hasInput;
    private BlockPos sourcePos;
    private boolean hasOutput;
    private BlockPos targetPos;
    boolean isOnInput;
    private boolean carrying = false;
    private boolean isOnOnput;


    public TileArmBasic() {
        super();
    }

    float[][] rotation = new float[3][3];

    private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.transitionLengthTicks = 0;
        controller.setAnimation(builder);
        controller.markNeedsReload();

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        animationController = new AnimationController<>(this, "controller", 0, this::predicate);
        data.addAnimationController(animationController);
    }

    public float[] getRotation(int idx) {
        return rotation[idx];
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        // getUpdateTag() is called whenever the chunkdata is sent to the
        // client. In contrast getUpdatePacket() is called when the tile entity
        // itself wants to sync to the client. In many cases you want to send
        // over the same information in getUpdateTag() as in getUpdatePacket().
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        // Prepare a packet for syncing our TE to the client. Since we only have to sync the stack
        // and that's all we have we just write our entire NBT here. If you have a complex
        // tile entity that doesn't need to have all information on the client you can write
        // a more optimal NBT here.
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        // Here we get the packet from the server and read it into our client side tile entity
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        return compound;
    }

    @Override
    public void update() {
        BlockPos inPos = pos.offset(EnumFacing.SOUTH, 1);
        TileEntity teIn = this.world.getTileEntity(inPos);
        if (teIn != null) {
            IItemHandler handler = teIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler != null) {
                this.hasInput = true;
                this.sourcePos = inPos;
            } else {
                this.hasInput = false;
            }
        } else {
            this.hasInput = false;
        }

        BlockPos outPos = pos.offset(EnumFacing.NORTH, 1);
        outPos = outPos.offset(EnumFacing.UP, 2);
        TileEntity teOut = this.world.getTileEntity(outPos);
        if (teOut != null) {
            IItemHandler handler = teOut.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler != null) {
                this.hasOutput = true;
                this.targetPos = outPos;
            } else {
                this.hasOutput = false;
            }
        } else {
            this.hasOutput = false;
        }

        if (hasInput && hasOutput && !carrying) {
            Vec3d vec1 = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vec3d vec2 = new Vec3d(sourcePos.getX() + 0.5, sourcePos.getY() + 0.5, sourcePos.getZ() + 0.5);
            Vec3d combinedVec = vec2.subtract(vec1);

            if (sucess(combinedVec)) {
                this.isOnInput = true;
                this.carrying = true;
            }
        } else if (hasOutput && carrying) {
            Vec3d vec1 = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vec3d vec2 = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 1.5, targetPos.getZ() + 0.5);
            Vec3d combinedVec = vec2.subtract(vec1);
            if (sucess(combinedVec)) {
                this.isOnOnput = true;
                this.carrying = false;
            }
        } else {
            this.isOnInput = false;
            walkToIdlePosition();
        }
    }

    private boolean sucess(Vec3d combinedVec) {
        Vec3d vPitch = new Vec3d(combinedVec.x, 0, combinedVec.z);
        double projectionPitch = combinedVec.normalize().dotProduct(vPitch.normalize());
        double pitch = Math.acos(projectionPitch);
        if (combinedVec.y < 0) {
            pitch *= -1;
        }

        Vec3d vYaw = new Vec3d(combinedVec.x, 0, 0);
        double projectionYaw = combinedVec.dotProduct(vYaw);
        double yaw = Math.acos(projectionYaw);

        if (combinedVec.x == 0 && combinedVec.z > 0) {
            yaw -=  Math.PI;
        }
        else if (combinedVec.x < 0 ) {
            if (combinedVec.z == 0) {
                yaw += Math.PI;
            } else if (combinedVec.z < 0) {
                yaw += 3 * Math.PI / 4;
            } else if (combinedVec.z > 0) {
                yaw -= 3 * Math.PI / 4;
            }
        }
        else if (combinedVec.x > 0 ) {
            if (combinedVec.z > 0) {
                yaw -= Math.PI / 4;
            } else if (combinedVec.z < 0) {
                yaw += Math.PI / 4;
            }
        }

        float rotPitch = rotateX(rotation[0][0], 0.1f, (float) pitch);
        boolean pitchReached = false;
        if (rotPitch != 0) {
            rotation[0][0] = rotPitch;
        } else {
            pitchReached = true;
        }

        float rotYaw = rotateX(rotation[0][1], 0.1f, (float) yaw);
        boolean yawReached = false;
        if (rotYaw != 0) {
            rotation[0][1] = rotYaw;
        } else {
            yawReached = true;
        }

        float dist = (float) combinedVec.length();
        boolean distReached = false;
        double distWalked = distX(rotation[1][0], 0.1f, dist);
        if (distWalked != rotation[1][0]) {
            rotation[1][0] = (float) distWalked;
        } else {
            distReached = true;
        }

        if (yawReached && pitchReached && distReached) {
            this.isOnInput = true;
            this.carrying = true;
            return true;
        }
        return false;
    }

    float rotateX(float source, float walkSpeed, float target) {
        float diff = target - source;
        if (diff < -0.1D) {
            return source - walkSpeed;
        } else if (diff > 0.1D) {
            return source + walkSpeed;
        }
        return 0;
    }

    float distX(float source, float walkSpeed, float target) {
        float currentArmLenght = 0.875f + 0.125f * source;
        if (Math.abs(currentArmLenght - target) <= 0.1F) {
            return source;
        }
        if (source + walkSpeed >= Math.PI / 2) {
            return source;
        }
        float diff = target - currentArmLenght;
        if (diff < -0) {
            return source - walkSpeed;
        } else {
            return source + walkSpeed;
        }
    }

    void walkToIdlePosition() {
        if (rotation[0][1] > 0.1F) {
            rotation[0][1] = rotation[0][1] - 0.1F;
        } else if (rotation[0][1] < -0.1F) {
            rotation[0][1] = rotation[0][1] + 0.1F;
        }

        if (rotation[0][0] >= 0.1F) {
            rotation[0][0] = rotation[0][0] - 0.1F;
        } else if (rotation[0][0] <= -0.1F) {
            rotation[0][0] = rotation[0][0] + 0.1F;
        }

        if (rotation[1][0] >= 0.1F) {
            rotation[1][0] = rotation[1][0] - 0.1F;
        } else if (rotation[0][0] <= -0.1F) {
            rotation[1][0] = rotation[1][0] + 0.1F;
        }
    }

	/*
	 public void update()
	 {
		 float currentXd = rotation[2][0];
		 float currentXm = rotation[1][0];
		 float currentXn = rotation[0][0];
		 float futureXd = currentXd + 0.314f;
		 if( extend )
		 {
			 if( futureXd >= HARD_LIMIT )
			 {
				 rotation[2][0] = HARD_LIMIT;
				 float futureXm = currentXm + 0.314f;
				 if( futureXm >= HARD_LIMIT )
				 {
					 rotation[1][0] = HARD_LIMIT;
					 float futureXn = currentXn + 0.314f;
					 if( futureXn >= HARD_LIMIT )
					 {
						 rotation[0][0] = HARD_LIMIT;
						 extend = false;
					 }
					 else
					 {
						 rotation[0][0] = futureXn;
					 }
				 }
				 else
				 {
					 rotation[1][0] = futureXm;
				 }
			 }
			 else
			 {
				 rotation[2][0] = futureXd;
			 }
		 }
		 else
		 {
			 futureXd = currentXd - 0.314f;
			 if( futureXd <= MINUS_HARD_LIMIT )
			 {
				 rotation[2][0] = MINUS_HARD_LIMIT;
				 float futureXm = currentXm - 0.314f;
				 if( futureXm <= MINUS_HARD_LIMIT )
				 {
					 rotation[1][0] = MINUS_HARD_LIMIT;
					 float futureXn = currentXn - 0.314f;
					 if( futureXn <= MINUS_HARD_LIMIT )
					 {
						 rotation[0][0] = MINUS_HARD_LIMIT;
						 extend = true;
					 }
					 else
					 {
						 rotation[0][0] = futureXn;
					 }
				 }
				 else
				 {
					 rotation[1][0] = futureXm;
				 }
			 }
			 else
			 {
				 rotation[2][0] = futureXd;
			 }
		 }
	 }
	 */

}
