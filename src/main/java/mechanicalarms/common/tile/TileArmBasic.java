package mechanicalarms.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TileArmBasic extends TileEntity implements IAnimatable, ITickable
{
	private final AnimationFactory factory = new AnimationFactory( this);
	private final AnimationBuilder builder = new AnimationBuilder().addAnimation( "nothing", true );
	private AnimationController<TileArmBasic> animationController;
	private boolean extend = true;


	public TileArmBasic(){
		super();
	}
	float[][] rotation = new float[3][3];

	private <E extends TileEntity & IAnimatable> PlayState predicate( AnimationEvent<E> event )
	{
		AnimationController controller = event.getController();
		controller.transitionLengthTicks = 0;
		controller.setAnimation( builder );
		controller.markNeedsReload();

		return PlayState.CONTINUE;
	}
	@Override
	public void registerControllers( AnimationData data )
	{
		animationController = new AnimationController<>( this, "controller", 0, this::predicate );
		data.addAnimationController( animationController );
	}

	public float[] getRotation( int idx )
	{
		return rotation[idx];
	}

	@Override
	public AnimationFactory getFactory()
	{
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
	public void onDataPacket( NetworkManager net, SPacketUpdateTileEntity packet ) {
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
	public void update()
	{
		float currentXn=rotation[0][0];
		float currentXm=rotation[1][0];
		float currentXd=rotation[2][0];
		float futureX = currentXd + 0.314f;
		if (extend) {
			if (futureX >= Math.PI) {
				futureX = currentXm + 0.314f;
				if (futureX >= Math.PI) {
					futureX = currentXn + 0.314f;
					if (futureX >= Math.PI) {
						rotation[0][0] = (float) Math.PI;
						extend = false;
					}
					else {
						rotation[0][0] = futureX;
					}
				} else {
					rotation[1][0] = futureX;
				}
			} else {
				rotation[2][0] = futureX;
			}
		}
		else {
			futureX = currentXn - 0.314f;
			if (futureX < Math.PI/2) {
				rotation[0][0] = (float) Math.PI/2;
				futureX = currentXm - 0.314f;
				if (futureX < Math.PI/2) {
					rotation[1][0] = (float) Math.PI/2;

					futureX = currentXd - 0.314f;
					if (futureX < Math.PI/2) {
						rotation[2][0] = (float) Math.PI/2;

						extend = true;
					}
					else {
						rotation[2][0] = futureX;
					}
				} else {
					rotation[1][0] = futureX;
				}
			} else {
				rotation[0][0] = futureX;
			}
		}

	}
}
