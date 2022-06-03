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
		IBone b3 = getGeoModelProvider().getBone( "secondX" );
		b3.setRotationX( animatable.getRotation( 2 )[0]);

		IBone b2 = getGeoModelProvider().getBone( "firstX" );
		b2.setRotationX( animatable.getRotation( 1 )[0]);

		IBone b = getGeoModelProvider().getBone( "baseXYZ" );
		b.setRotationX( animatable.getRotation( 0 )[0]);
		b.setRotationY( animatable.getRotation( 0 )[1]);


		super.renderEarly( animatable, ticks, red, green, blue, partialTicks );
	}
}
