package mechanicalarms.client.renderer;

import mechanicalarms.client.model.ArmModel;
import mechanicalarms.common.tile.TileArmBase;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class TileArmRenderer extends GeoBlockRenderer<TileArmBase>
{
	public TileArmRenderer()
	{
		super( new ArmModel() );
	}
}
