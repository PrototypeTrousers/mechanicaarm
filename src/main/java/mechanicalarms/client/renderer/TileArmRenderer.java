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
		b.setRotationX( animatable.getRotation( 0 )[0]);

		IBone b2 = getGeoModelProvider().getBone( "bone2" );
		b2.setRotationX( animatable.getRotation( 1 )[0]);

		super.renderEarly( animatable, ticks, red, green, blue, partialTicks );
	}
}
