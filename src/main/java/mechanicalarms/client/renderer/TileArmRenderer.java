package mechanicalarms.client.renderer;

import mechanicalarms.client.model.ArmModel;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

@SideOnly(Side.CLIENT)
public class TileArmRenderer extends GeoBlockRenderer<TileArmBasic> {
    public TileArmRenderer() {
        super(new ArmModel());
    }

    @Override
    public void renderEarly(TileArmBasic animatable, float partialTicks, float red, float green, float blue, float alpha) {
        float[] baseRotation = animatable.getAnimationRotation()[0];
        float[] firstXRRotation = animatable.getAnimationRotation()[1];
        float[] secondXRRotation = animatable.getAnimationRotation()[1];


        IBone b2 = getGeoModelProvider().getBone("arm2");
        firstXRRotation[0] = firstXRRotation[0] + partialTicks * (animatable.getRotation(1)[0] - firstXRRotation[0]);
        b2.setRotationX(firstXRRotation[0]);

        IBone b = getGeoModelProvider().getBone("arm1");
        baseRotation[0] = baseRotation[0] + partialTicks * (animatable.getRotation(0)[0] - baseRotation[0]);
        b.setRotationX(baseRotation[0]);
        baseRotation[1] = baseRotation[1] + partialTicks * (animatable.getRotation(0)[1] - baseRotation[1]);
        b.setRotationY(baseRotation[1]);

        IBone b3 = getGeoModelProvider().getBone("hand");
        secondXRRotation[2] = secondXRRotation[0] + partialTicks * (animatable.getRotation(1)[0] - secondXRRotation[0]);
        b3.setRotationX(secondXRRotation[0]);
        super.renderEarly(animatable, partialTicks, red, green, blue, alpha);
    }
}
