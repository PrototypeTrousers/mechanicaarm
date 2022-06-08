package mechanicalarms.client.renderer;

import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.animation.FastTESR;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class TileArmRenderer extends FastTESR<TileArmBasic> {

    public TileArmRenderer() {
        super();
    }

    private final Matrix4f tempModelMatrix = new Matrix4f();
    private static final Vector3f V3F_ZERO = new Vector3f();
    private static final Vector3f PIVOT_1 = new Vector3f(8 / 16F, (float) (18 / 16), (8 / 16F));
    private static final Vector3f ANTI_PIVOT_1 = new Vector3f(-8 / 16F, (float) (-18 / 16), (-8 / 16F));

    private static final Vector3f PIVOT_2 = new Vector3f(8 / 16F, (float) (18 / 16), (-6 / 16F));
    private static final Vector3f ANTI_PIVOT_2 = new Vector3f(-8 / 16F, (float) (-18 / 16), (6 / 16F));
    private static BakedQuad[] quads = null;

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
        float[] baseRotation = tileArmBasic.getRotation(0);
        float[] firstXRRotation = tileArmBasic.getRotation(1);

        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState blockState = tileArmBasic.getWorld().getBlockState(tileArmBasic.getPos());
        if (quads == null) {
            quads = blockRendererDispatcher.getModelForState(blockState).getQuads(blockState, null, 0).toArray(new BakedQuad[0]);
        }

        Matrix4f transformMatrix = new Matrix4f();
        transformMatrix.setIdentity();
        //cage
        renderQuads(quads, 0, 72,
                new Vector3f((float) x, (float) y, (float) z),
                buffer,
                transformMatrix,
                255,
                color(0xFF, 0xFF, 0xFF));
        //base

        //move to the correct position
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(new Vector3f(0, 2 / 16F, 0));
        transformMatrix.mul(this.tempModelMatrix);
        //move to pivot
        moveToPivot(transformMatrix, PIVOT_1);

        //rotate
        rotateX(transformMatrix, baseRotation[0]);
        rotateY(transformMatrix, baseRotation[1]);

        //scale back
        restoreScale(transformMatrix);

        //move back from pivoting
        moveToPivot(transformMatrix, ANTI_PIVOT_1);

        renderQuads(quads, 72, 102,
                new Vector3f((float) (x), (float) (y), (float) (z)),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));
        //firstArm

        moveToPivot(transformMatrix, PIVOT_2);
        //rotate
        rotateX(transformMatrix, firstXRRotation[0]);
        //scale back
        restoreScale(transformMatrix);
        //move back from pivoting
        moveToPivot(transformMatrix, ANTI_PIVOT_2);

        renderQuads(quads, 102, 156,
                new Vector3f((float) (x), (float) (y), (float) (z)),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));

        renderQuads(quads, 156, 192,
                new Vector3f((float) (x), (float) (y), (float) (z)),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));

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

    void restoreScale(Matrix4f matrix) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setM00(1.125F);
        this.tempModelMatrix.setM11(1.125F);
        this.tempModelMatrix.setM22(1.125F);
        matrix.mul(this.tempModelMatrix);
    }

    void moveToPivot(Matrix4f matrix, Vector3f pivot) {
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(pivot);
        matrix.mul(this.tempModelMatrix);
    }

    /**
     * Renders a collection of BakedQuads into the BufferBuilder given. This method allows you to render any model in game in the FastTESR, be it a block model or an item model.
     * Alternatively a custom list of quads may be constructed at runtime to render things like text.
     * Drawbacks: doesn't transform normals as they are not guaranteed to be present in the buffer. Not relevant for a FastTESR but may cause issues with Optifine's shaders.
     *
     * @param quads           the iterable of BakedQuads. This may be any iterable object.
     * @param baseOffset      the base position offset for the rendering. This position will not be transformed by the model matrix.
     * @param buffer          the buffer to upload vertices to.
     * @param transformMatrix the model matrix that is used to transform quad vertices.
     * @param brightness      the brightness of the model. The packed lightmap coordinate system is pretty complex and a lot of parameters are not necessary here so only the dominant one is implemented.
     * @param color           the color of the quad. This is a color multiplier in the ARGB format.
     */
    public void renderQuads(BakedQuad[] quads, int rangeStart, int rangeEnd, Vector3f baseOffset, BufferBuilder buffer, Matrix4f transformMatrix, float brightness, int color) {
        // Uploading the brightness to the buffer.
        Tuple4f vert = new Vector4f();
        // Iterate the iterable
        int[] transformedVertexData = new int[28];
        for (; rangeStart < rangeEnd; rangeStart++) {
            BakedQuad currentQuad = quads[rangeStart];
            int[] quadData = currentQuad.getVertexData();
            System.arraycopy(quadData, 0, transformedVertexData, 0, quadData.length);

            for (int k = 0; k < 4; ++k) {
                // Getting the offset for the current vertex.
                int vertexIndex = k * 7;

                // Grabbing the position vector from the buffer.
                float vertX = Float.intBitsToFloat(quadData[vertexIndex]);
                float vertY = Float.intBitsToFloat(quadData[vertexIndex + 1]);
                float vertZ = Float.intBitsToFloat(quadData[vertexIndex + 2]);
                vert.x = vertX;
                vert.y = vertY;
                vert.z = vertZ;
                vert.w = 1;

                // Transforming it by the model matrix.
                transformMatrix.transform(vert);

                // Converting the new data to ints.
                int x = Float.floatToIntBits(vert.x);
                int y = Float.floatToIntBits(vert.y);
                int z = Float.floatToIntBits(vert.z);

                // Putting the data into the buffer
                transformedVertexData[vertexIndex] = x;
                transformedVertexData[vertexIndex + 1] = y;
                transformedVertexData[vertexIndex + 2] = z;
            }
            buffer.addVertexData(transformedVertexData);

            // Uploading the origin position to the buffer. This is an addition operation.
            buffer.putPosition(baseOffset.x, baseOffset.y, baseOffset.z);

            // Constructing the most basic packed lightmap data with a mask of 0x00FF0000.
            int bVal = ((byte) (brightness * 255)) << 16;

            buffer.putBrightness4(192, 192, 192, 192);

            // Uploading the color multiplier to the buffer
            buffer.putColor4(color);
        }
    }

    /**
     * Maps a value from one range to another range. Taken from https://stackoverflow.com/a/5732117
     *
     * @param input       the input
     * @param inputStart  the start of the input's range
     * @param inputEnd    the end of the input's range
     * @param outputStart the start of the output's range
     * @param outputEnd   the end of the output's range
     * @return the newly mapped value
     */
    public static double map(final double input, final double inputStart, final double inputEnd, final double outputStart, final double outputEnd) {
        final double input_range = inputEnd - inputStart;
        final double output_range = outputEnd - outputStart;

        return (((input - inputStart) * output_range) / input_range) + outputStart;
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
}
