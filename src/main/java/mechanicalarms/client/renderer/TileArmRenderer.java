package mechanicalarms.client.renderer;

import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TileArmRenderer extends FastTESR<TileArmBasic> {

    public TileArmRenderer() {
        super();
    }

    private Matrix4f tempModelMatrix = new Matrix4f();


    /**
     * A field reference to the rawIntBuffer of the BufferBuilder class. Need reflection since the field is private.
     */
    private static final Field bufferBuilder_rawIntBuffer = ReflectionHelper.findField(BufferBuilder.class, "rawIntBuffer", "field_178999_b");

    /**
     * A vertex definition for a simple 2-dimensional quad defined in counter-clockwise order with the top-left origin.
     */
    private static final Vector4f[] simpleQuad = new Vector4f[]{new Vector4f(1, 1, 0, 0), new Vector4f(1, 0, 0, 0), new Vector4f(0, 0, 0, 0), new Vector4f(0, 1, 0, 0)};

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
        float[] firstXRRotation = tileArmBasic.getRotation(0);

        BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState blockState = tileArmBasic.getWorld().getBlockState(tileArmBasic.getPos());
        List<BakedQuad> quadList = blockRendererDispatcher.getModelForState(blockState).getQuads(blockState, null, 0);

        Matrix4f transformMatrix = new Matrix4f();
        transformMatrix.setIdentity();
        //cage
        renderQuads(quadList.subList(0, 72),
                new Vector3f((float) x, (float) y, (float) z),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));
        //base

        //move to the correct position
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(new Vector3f((float) (-x / 16), (float) (y / 16F), (float) (z / 16)));
        transformMatrix.mul(this.tempModelMatrix);
        //move to pivot
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(new Vector3f(.5F, (float) (20 / 16), (.5F)));
        transformMatrix.mul(this.tempModelMatrix);

        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.rotY(baseRotation[1]);
        transformMatrix.mul(this.tempModelMatrix);

        //this.tempModelMatrix.setIdentity();
        //this.tempModelMatrix.rotX(baseRotation[0]);
        //transformMatrix.mul(this.tempModelMatrix);

        //move back from pivoting
        this.tempModelMatrix.setIdentity();
        this.tempModelMatrix.setTranslation(new Vector3f(-.5F, -20 / 16F, -.5F));
        transformMatrix.mul(this.tempModelMatrix);

        //Matrix4f.translate(new Vector3f(-.5F, -20 / 16F, -.5F), matrixCopy, transformMatrix);

        renderQuads(quadList.subList(72, 102),
                new Vector3f((float) (x), (float) (y), (float) (z)),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));
        //firstArm
/*

        //transformMatrix.rotate((float) (Math.PI / 2), new Vector3f(1, 0, 0));
        //transformMatrix.translate(new Vector3f(-.5F, -.5F, -.5F));


        renderQuads(quadList.subList(102, 156),
                new Vector3f((float) (x), (float) (y), (float) (z)),
                new VertexBufferConsumer(buffer),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));
        //hand

        transformMatrix.rotate(hand[0], new Vector3f(1, 0, 0));
        renderQuads(quadList.subList(156, 192),
                new Vector3f((float) (x + 0.5F), (float) (y + 0.5F), (float) (z + 0.5F)),
                new VertexBufferConsumer(buffer),
                buffer,
                transformMatrix,
                240,
                color(0xFF, 0xFF, 0xFF));*/
    }

    /**
     * Renders a collection of BakedQuads into the BufferBuilder given. This method allows you to render any model in game in the FastTESR, be it a block model or an item model.
     * Alternatively a custom list of quads may be constructed at runtime to render things like text.
     * Drawbacks: doesn't transform normals as they are not guaranteed to be present in the buffer. Not relevant for a FastTESR but may cause issues with Optifine's shaders.
     *
     * @param quads      the iterable of BakedQuads. This may be any iterable object.
     * @param baseOffset the base position offset for the rendering. This position will not be transformed by the model matrix.
     * @param buffer     the buffer to upload vertices to.
     * @param transform  the model matrix that is used to transform quad vertices.
     * @param brightness the brightness of the model. The packed lightmap coordinate system is pretty complex and a lot of parameters are not necessary here so only the dominant one is implemented.
     * @param color      the color of the quad. This is a color multiplier in the ARGB format.
     */
    public void renderQuads(Iterable<BakedQuad> quads, Vector3f baseOffset, BufferBuilder buffer, Matrix4f transform, float brightness, int color) {
        // Get the raw int buffer of the buffer builder object.
        IntBuffer intBuf = getIntBuffer(buffer);

        // Uploading the brightness to the buffer.

        // Iterate the iterable
        int i = 0;
        for (BakedQuad quad : quads) {
            if (i > 71) break;
            // Push the quad to the consumer so it can be uploaded onto the buffer.
            buffer.addVertexData(quad.getVertexData());

            // After the quad has been uploaded the buffer contains enough info to apply the model matrix transformation.
            // Getting the vertex size for the given format.
            int vertexSize = buffer.getVertexFormat().getIntegerSize();

            // Getting the offset for the current quad.
            int quadOffset = (buffer.getVertexCount() - 4) * vertexSize;

            // Each quad is made out of 4 vertices, so looping 4 times.
            for (int k = 0; k < 4; ++k) {
                // Getting the offset for the current vertex.
                int vertexIndex = quadOffset + k * vertexSize;

                // Grabbing the position vector from the buffer.
                float vertX = Float.intBitsToFloat(intBuf.get(vertexIndex));
                float vertY = Float.intBitsToFloat(intBuf.get(vertexIndex + 1));
                float vertZ = Float.intBitsToFloat(intBuf.get(vertexIndex + 2));
                Tuple4f vert = new Vector4f(vertX, vertY, vertZ, 1);

                // Transforming it by the model matrix.
                transform.transform(vert);

                // Uploading the difference back to the buffer. Have to use the helper function since the provided putX methods upload the data for a quad, not a vertex and this data is vertex-dependent.
                putPositionForVertex(buffer, intBuf, vertexIndex, new Vector3f(vert.x - vertX, vert.y - vertY, vert.z - vertZ));
            }

            // Uploading the origin position to the buffer. This is an addition operation.
            buffer.putPosition(baseOffset.x, baseOffset.y, baseOffset.z);

            // Constructing the most basic packed lightmap data with a mask of 0x00FF0000.
            int bVal = ((byte) (brightness * 255));

            buffer.putBrightness4(64, 64, 64, 64);

            // Uploading the color multiplier to the buffer
            buffer.putColor4(color);
            i++;
        }
    }


    /**
     * A helper method that grabs all BakedQuads of a given model of a given IBlockState and joins them onto a single iterable.
     *
     * @param state the block state object to get the quads from.
     * @return the iterable of BakedQuads.
     */
    protected static Iterable<BakedQuad> iterateQuadsOfBlock(IBlockState state) {
        return Arrays.stream(EnumFacing.values()).map(q -> Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state).getQuads(state, q, 0L)).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    /**
     * A setter for the vertex-based positions for a given BufferBuilder object.
     *
     * @param buffer the buffer to set the positions in.
     * @param intBuf the raw int buffer.
     * @param offset the offset for the int buffer, in ints.
     * @param pos    the position to add to the buffer.
     */
    protected static void putPositionForVertex(BufferBuilder buffer, IntBuffer intBuf, int offset, Vector3f pos) {
        // Getting the old position data in the buffer currently.
        float ox = Float.intBitsToFloat(intBuf.get(offset));
        float oy = Float.intBitsToFloat(intBuf.get(offset + 1));
        float oz = Float.intBitsToFloat(intBuf.get(offset + 2));

        // Converting the new data to ints.
        int x = Float.floatToIntBits(pos.x + ox);
        int y = Float.floatToIntBits(pos.y + oy);
        int z = Float.floatToIntBits(pos.z + oz);

        // Putting the data into the buffer
        intBuf.put(offset, x);
        intBuf.put(offset + 1, y);
        intBuf.put(offset + 2, z);
    }

    /**
     * A getter for the rawIntBuffer field value of the BufferBuilder.
     *
     * @param buffer the buffer builder to get the buffer from
     * @return the rawIntbuffer component
     */
    protected static IntBuffer getIntBuffer(BufferBuilder buffer) {
        try {
            return (IntBuffer) bufferBuilder_rawIntBuffer.get(buffer);
        } catch (IllegalAccessException e) {
            // Some other mod messed up and reset the access flag of the field.
            FMLCommonHandler.instance().raiseException(e, "An impossible error has occurred!", true);
        }

        return null;
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
