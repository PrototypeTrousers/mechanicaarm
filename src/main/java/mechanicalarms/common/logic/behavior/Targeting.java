package mechanicalarms.common.logic.behavior;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;

public class Targeting implements INBTSerializable<NBTTagCompound> {

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
    
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("sourcePos", NBTUtil.createPosTag(sourcePos));
        compound.setTag("targetPos", NBTUtil.createPosTag(targetPos));
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        setSource(NBTUtil.getPosFromTag(compound.getCompoundTag("sourcePos")));
        setTarget(NBTUtil.getPosFromTag(compound.getCompoundTag("targetPos")));
    }
}
