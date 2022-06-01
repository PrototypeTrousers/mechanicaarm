package mechanicalarms.client.renderer;

import mechanicalarms.client.model.ArmModel;
import mechanicalarms.common.tile.TileArmBasic;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class TileArmRenderer extends GeoBlockRenderer<TileArmBasic>
{
	public TileArmRenderer()
	{
		super( new ArmModel() );
	}

	@Override
	public void renderEarly( TileArmBasic animatable, float ticks, float red, float green, float blue, float partialTicks )
	{
		IBone b = getGeoModelProvider().getBone( "bone" );
		float cx = animatable.getRotation( 0 )[0];
		b.setRotationX( cx + 0.1F );
		animatable.getRotation( 0 )[0] = b.getRotationX();

		IBone b2 = getGeoModelProvider().getBone( "bone2" );
		float cx2 = animatable.getRotation( 1 )[0];
		b2.setRotationX( cx2 + 0.1F );
		animatable.getRotation( 1 )[0] = b2.getRotationX();

		super.renderEarly( animatable, ticks, red, green, blue, partialTicks );
	}
}
