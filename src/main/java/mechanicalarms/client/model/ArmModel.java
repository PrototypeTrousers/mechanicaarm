package mechanicalarms.client.model;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

public class ArmModel extends AnimatedGeoModel<TileArmBasic>
{
	public ArmModel()
	{
	}

	@Override
	public ResourceLocation getModelLocation( TileArmBasic object )
	{
		return new ResourceLocation( MechanicalArms.MODID, "geo/arm.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation( TileArmBasic object )
	{
		return new ResourceLocation(MechanicalArms.MODID, "textures/block/arm.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation( TileArmBasic animatable )
	{
		return new ResourceLocation( MechanicalArms.MODID, "animations/nothing.json");
	}
}
