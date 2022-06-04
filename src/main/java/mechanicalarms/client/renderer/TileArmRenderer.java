package mechanicalarms.client.renderer;

import mechanicalarms.client.model.ArmModel;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.client.model.animation.FastTESR;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class TileArmRenderer extends GeoBlockRenderer<TileArmBasic>
{
	public TileArmRenderer()
	{
		super( new ArmModel() );
	}

	@Override
	public void renderEarly( TileArmBasic animatable, float partialTicks, float red, float green, float blue, float alpha )
	{
		float[] baseRotation = animatable.getAnimationRotation()[0];
		float[] firstXRRotation = animatable.getAnimationRotation()[1];

		IBone b2 = getGeoModelProvider().getBone( "firstX" );
		firstXRRotation[0] = firstXRRotation[0] + partialTicks * (animatable.getRotation( 1 )[0] - firstXRRotation[0]);
		b2.setRotationX( firstXRRotation[0]);

		IBone b = getGeoModelProvider().getBone( "baseXYZ" );
		baseRotation[0] = baseRotation[0] + partialTicks * (animatable.getRotation( 0 )[0] - baseRotation[0]);
		b.setRotationX( baseRotation[0] );
		baseRotation[1] = baseRotation[1] + partialTicks * (animatable.getRotation( 0 )[1] - baseRotation[1]);
		b.setRotationY( baseRotation[1] );
		AnimationTESR;
				FastTESR;

		super.renderEarly( animatable, partialTicks, red, green, blue, alpha );
	}
}
