package mechanicalarms.client.model;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileArmBase;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ArmModel extends AnimatedGeoModel<TileArmBase>
{
	public ArmModel()
	{
	}

	@Override
	public ResourceLocation getModelLocation( TileArmBase object )
	{
		return new ResourceLocation( MechanicalArms.MODID, "geo/arm.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation( TileArmBase object )
	{
		return new ResourceLocation(GeckoLib.ModID, "textures/block/arm.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation( TileArmBase animatable )
	{
		return new ResourceLocation( GeckoLib.ModID, "animations/jackinthebox.animation.json");
	}
}
