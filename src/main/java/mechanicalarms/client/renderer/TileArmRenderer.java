package mechanicalarms.client.renderer;

import mechanicalarms.client.mixin.interfaces.IBufferBuilderMixin;
import mechanicalarms.common.block.BlockArm;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.animation.FastTESR;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.List;

public class TileArmRenderer extends FastTESR<TileArmBasic> {

    private static final Vector3f V3F_ZERO = new Vector3f();
    private static int[][][] vertexArray = null;
    private final Matrix4f tempModelMatrix = new Matrix4f();
    private final Tuple4f vertexTransformingVec = new Vector4f();
    private final Vector3f V3F_POS = new Vector3f();
    private final Vector3f PIVOT_1 = new Vector3f(.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_1 = new Vector3f(-.5F, -(1 + 7 / 16F), -.5F);
    private final Vector3f PIVOT_2 = new Vector3f(0.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_2 = new Vector3f(-0.5F, -(1 + 7 / 16F), -.5F);
    private int[] vertexDataArray;
    private int quadCount = 0;

    public TileArmRenderer() {
        super();
    }

    public static int color(int red, int green, int blue) {

        red = MathHelper.clamp(red, 0x00, 0xFF);
        green = MathHelper.clamp(green, 0x00, 0xFF);
        blue = MathHelper.clamp(blue, 0x00, 0xFF);

        final int alpha = 0xFF;

        // 0x alpha red green blue
        // 0xaarrggbb

        // int colorRGBA = 0;
        // colorRGBA |= red << 16;
        // colorRGBA |= green << 8;
        // colorRGBA |= blue << 0;
        // colorRGBA |= alpha << 24;

        return blue | red << 16 | green << 8 | alpha << 24;

    }

    /**
     * The render method that gets called for your FastTESR implementation. This is where you render things.
     *
     * @param tileArmBasic your TileEntity instance.
     * @param x            the X position of the TE in view space.
     * @param y            the Y position of the TE in view space.
     * @param z            the Z position of the TE in view space.
     * @param partialTicks the amount of partial ticks escaped. Partial ticks happen when there are multiple frames per tick.
     * @param destroyStage the destroy progress of the TE. You may use it to render the "breaking" animation.
     * @param partial      currently seems to be a 1.0 constant.
     * @param buffer       the BufferBuilder containing vertex data for vertices being rendered. It is safe to assume that the format is {@link net.minecraft.client.renderer.vertex.DefaultVertexFormats DefaultVertexFormats}.BLOCK. It is also safe to assume that the GL primitive for drawing is QUADS.
     */
    @Override
    public void renderTileEntityFast(final TileArmBasic tileArmBasic, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float partial, final BufferBuilder buffer) {
        float[] firstArmRotation = tileArmBasic.getRotation(0);
        float[] firstArmAnimationAngle = tileArmBasic.getAnimationRotation(0);

        float[] secondArmRotation = tileArmBasic.getRotation(1);
        float[] secondArmAnimationAngle = tileArmBasic.getAnimationRotation(1);

        float[] handRotation = tileArmBasic.getRotation(2);
        float[] handRotationAnimationAngle = tileArmBasic.getAnimationRotation(2);


        V3F_POS.x = (float) x;
        V3F_POS.y = (float) y;
        V3F_POS.z = (float) z;

        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        if (vertexArray == null) {
            vertexArray = new int[5][][];
            IBlockState blockState;
            for (int i = 1; i < 6; i++) {
                blockState = tileArmBasic.getWorld().getBlockState(tileArmBasic.getPos()).withProperty(BlockArm.ARM_PART_NUMBER, i);
                List<BakedQuad> quads = blockRendererDispatcher.getModelForState(blockState).getQuads(blockState, null, i);
                vertexArray[i - 1] = new int[quads.size()][];
                for (int j = 0; j < quads.size(); j++) {
                    vertexArray[i - 1][j] = quads.get(j).getVertexData();
                }
            }
            int size = 0;
            for (int[][] vertexData : vertexArray) {
                size += vertexData.length;
            }
            this.vertexDataArray = new int[2 * size * 28 + 28];
        }

        int light = tileArmBasic.getWorld().getBlockState(tileArmBasic.getPos()).getPackedLightmapCoords(tileArmBasic.getWorld(), tileArmBasic.getPos());

        Matrix4f transformMatrix = new Matrix4f();
        transformMatrix.setIdentity();
        this.tempModelMatrix.setIdentity();
        //firstArm

        moveToPivot(transformMatrix, PIVOT_1);
        rotateY(transformMatrix, (float) (-Math.PI / 2));
        rotateY(transformMatrix, lerp(firstArmAnimationAngle[1], firstArmRotation[1], partialTicks));
        rotateX(transformMatrix, lerp(firstArmAnimationAngle[0], firstArmRotation[0], partialTicks));
        moveToPivot(transformMatrix, ANTI_PIVOT_1);

        renderQuads(vertexArray[1],
                V3F_POS,
                transformMatrix,
                light,
                color(0xFF, 0xFF, 0xFF));

        //position second arm
        translate(transformMatrix, new Vector3f(0, 0, -(1 + 12 / 16F)));

        moveToPivot(transformMatrix, PIVOT_2);
        rotateX(transformMatrix, lerp(secondArmAnimationAngle[0], secondArmRotation[0], partialTicks));
        moveToPivot(transformMatrix, ANTI_PIVOT_2);

        renderQuads(vertexArray[1],
                V3F_POS,
                transformMatrix,
                light,
                color(0xFF, 0xFF, 0xFF));

        //hand
        translate(transformMatrix, new Vector3f(0, 3 / 16F, -(1 + 13 / 16F)));
        moveToPivot(transformMatrix, PIVOT_2);
        //rotateY(transformMatrix, (float) (Math.PI /4));
        //rotateX(transformMatrix, (float) (-Math.PI / 2));

        //rotateY(transformMatrix, lerp(handRotationAnimationAngle[1], handRotation[1], partialTicks));
        //rotateX(transformMatrix, lerp(handRotationAnimationAngle[0], handRotation[0], partialTicks));
        moveToPivot(transformMatrix, ANTI_PIVOT_2);

        renderQuads(vertexArray[3],
                V3F_POS,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));

        //claw
        translate(transformMatrix, new Vector3f(0, 2 / 16F, -0.5F));

        renderQuads(vertexArray[4],
                V3F_POS,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));

        //render item
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        List<BakedQuad> quads = renderItem.getItemModelMesher().getItemModel(new ItemStack(Items.STONE_SWORD)).getQuads(null, null, 0);
        int[][] item = new int[quads.size()][];
        for (int i = 0, quadsSize = quads.size(); i < quadsSize; i++) {
            item[i] = quads.get(i).getVertexData();
        }

        translate(transformMatrix, new Vector3f(0.25F, 1F, -0.25F));

        renderQuads(item,
                V3F_POS,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));

        ((IBufferBuilderMixin) buffer).putIntBulkData(vertexDataArray);
        quadCount = 0;
    }

    void rotateX(Matrix4f matrix, float angle) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotX(angle);
        matrix.mul(this.tempModelMatrix);
    }

    void rotateY(Matrix4f matrix, float angle) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotY(angle);
        matrix.mul(this.tempModelMatrix);
    }

    void rotateZ(Matrix4f matrix, float angle) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotZ(angle);
        matrix.mul(this.tempModelMatrix);
    }

    void translate(Matrix4f matrix, Vector3f translation) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(translation);
        matrix.mul(this.tempModelMatrix);
    }

    void restoreScale(Matrix4f matrix) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setM00(1F);
        this.tempModelMatrix.setM11(1F);
        this.tempModelMatrix.setM22(1F);
        matrix.mul(this.tempModelMatrix);
    }

    void moveToPivot(Matrix4f matrix, Vector3f pivot) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(pivot);
        matrix.mul(this.tempModelMatrix);
    }

    public void renderQuads(int[][] quadDataList, Vector3f baseOffset, Matrix4f transformMatrix, int brightness, int color) {
        for (int[] quadData : quadDataList) {
            System.arraycopy(quadData, 0, vertexDataArray, quadCount * 28, quadData.length);
            for (int k = 0; k < 4; ++k) {
                // Getting the offset for the current vertex.
                int vertexIndex = k * 7;
                vertexTransformingVec.x = Float.intBitsToFloat(quadData[vertexIndex]);
                vertexTransformingVec.y = Float.intBitsToFloat(quadData[vertexIndex + 1]);
                vertexTransformingVec.z = Float.intBitsToFloat(quadData[vertexIndex + 2]);
                vertexTransformingVec.w = 1;

                // Transforming it by the model matrix.
                transformMatrix.transform(vertexTransformingVec);

                // Converting the new data to ints.
                int x = Float.floatToRawIntBits(vertexTransformingVec.x + baseOffset.x);
                int y = Float.floatToRawIntBits(vertexTransformingVec.y + baseOffset.y);
                int z = Float.floatToRawIntBits(vertexTransformingVec.z + baseOffset.z);

                int destIndex = quadCount * 28 + vertexIndex;
                // vertex position data
                vertexDataArray[destIndex] = x;
                vertexDataArray[destIndex + 1] = y;
                vertexDataArray[destIndex + 2] = z;

                // vertex brightness
                vertexDataArray[destIndex + 6] = brightness;
            }
            quadCount++;
        }
    }

    private float lerp(float previous, float current, float partialTick) {
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }
}
