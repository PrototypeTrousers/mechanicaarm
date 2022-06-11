package mechanicalarms.common.tile;

import mechanicalarms.common.logic.behavior.Action;
import mechanicalarms.common.logic.behavior.ActionTypes;
import mechanicalarms.common.logic.behavior.Targeting;
import mechanicalarms.common.logic.behavior.WorkStatus;
import mechanicalarms.common.logic.movement.MotorCortex;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TileArmBasic extends TileEntity implements ITickable, IAnimatable {

    private final Targeting targeting = new Targeting();
    AnimationFactory animationFactory = new AnimationFactory(this);
    float[][] animationRotation = new float[3][3];
    float armSize = 2;
    private final MotorCortex motorCortex = new MotorCortex(this, armSize);
    private Vec3d currentTarget;
    private Vec3d armPoint;
    private final WorkStatus workStatus = new WorkStatus();

    public TileArmBasic() {
        super();
    }

    public float[][] getAnimationRotation() {
        return animationRotation;
    }

    public float[] getRotation(int idx) {
        return motorCortex.getRotation(idx);
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
    public void onLoad() {
        super.onLoad();
        armPoint = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList rotation = compound.getTagList("rotation", 5);
        int axis = 0;
        int coords = 0;
        for (int i = 0; i < rotation.tagCount(); i++) {
            if (coords > 2) {
                axis += 1;
                coords = 0;
            }
            getRotation(axis)[coords] = rotation.getFloatAt(i);
            coords++;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList rotation = new NBTTagList();
        for (int i = 0; i < 3; i++) {
            for (float r : motorCortex.getRotation(i)) {
                rotation.appendTag(new NBTTagFloat(r));
            }
        }
        compound.setTag("rotation", rotation);
        return compound;
    }

    @Override
    public void update() {
        if (workStatus.getAction() == Action.IDLING) {
            if (hasInput() && hasOutput()) {
                currentTarget = targeting.getSourceVec();
                if (motorCortex.move(armPoint, currentTarget)) {
                    workStatus.setAction(Action.RETRIEVE);
                    workStatus.setType(ActionTypes.INTERACTION);
                }
            }
        } else if (workStatus.getType() == ActionTypes.MOVEMENT) {
            if (workStatus.getAction() == Action.RETRIEVE) {

            }

        } else if (workStatus.getType() == ActionTypes.INTERACTION) {

        }
    }

    public void setSource(BlockPos sourcePos) {
        targeting.setSource(sourcePos);
    }

    public void setTarget(BlockPos targetPos) {
        targeting.setTarget(targetPos);
    }

    public boolean hasInput() {
        return targeting.hasInput();
    }

    public boolean hasOutput() {
        return targeting.hasOutput();
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }
}
