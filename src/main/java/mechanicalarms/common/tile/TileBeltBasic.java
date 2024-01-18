package mechanicalarms.common.tile;

import mechanicalarms.common.logic.behavior.Action;
import mechanicalarms.common.logic.behavior.ActionResult;
import mechanicalarms.common.logic.behavior.InteractionType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;


public class TileBeltBasic extends TileEntity {

    AxisAlignedBB renderBB;

    protected ItemStackHandler itemHandler = new ItemStackHandler(1);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", itemHandler.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        itemHandler.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
    }

    public ItemStack getItemStack() {
        return itemHandler.getStackInSlot(0);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (renderBB == null) {
            renderBB = super.getRenderBoundingBox().expand(4, 4, 4);
        }
        return renderBB;
    }
}
