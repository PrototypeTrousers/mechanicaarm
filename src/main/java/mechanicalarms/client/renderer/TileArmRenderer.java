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
        float[] firstArm = animatable.getAnimationRotation()[0];
        float[] secondArm = animatable.getAnimationRotation()[1];
        float[] Hand = animatable.getAnimationRotation()[1];


        IBone b2 = getGeoModelProvider().getBone("arm2");
        secondArm[0] = secondArm[0] + partialTicks * (animatable.getRotation(1)[0] - secondArm[0]);
        b2.setRotationX(secondArm[0]);

        IBone b = getGeoModelProvider().getBone("arm1");
        firstArm[0] = firstArm[0] + partialTicks * (animatable.getRotation(0)[0] - firstArm[0]);
        b.setRotationX(firstArm[0]);
        firstArm[1] = firstArm[1] + partialTicks * (animatable.getRotation(0)[1] - firstArm[1]);
        b.setRotationY(firstArm[1]);

        IBone b3 = getGeoModelProvider().getBone("hand");
        Hand[2] = Hand[0] + partialTicks * (animatable.getRotation(1)[0] - Hand[0]);
        b3.setRotationX(Hand[0]);
        super.renderEarly(animatable, partialTicks, red, green, blue, alpha);
    }
}
