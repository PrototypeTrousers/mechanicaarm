package mechanicalarms.client.renderer;

import mechanicalarms.client.mixin.interfaces.IBufferBuilderMixin;
import mechanicalarms.common.proxy.ClientProxy;
import mechanicalarms.common.tile.TileArmBasic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.FastTESR;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Quaternion;

import javax.vecmath.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.renderer.OpenGlHelper.*;
import static org.lwjgl.opengl.GL11.*;

public class TileArmRenderer extends TileEntitySpecialRenderer<TileArmBasic> {

    private static final Vector3f V3F_ZERO = new Vector3f();
    private static int[][][] vertexArray = null;
    private final Matrix4f tempModelMatrix = new Matrix4f();
    private final Tuple4f vertexTransformingVec = new Vector4f();
    private final Vector3f V3F_POS = new Vector3f();
    private final Vector3f PIVOT_1 = new Vector3f(.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_1 = new Vector3f(-.5F, -(1 + 7 / 16F), -.5F);
    private final Vector3f PIVOT_2 = new Vector3f(0.5F, 1 + 7 / 16F, .5F);
    private final Vector3f ANTI_PIVOT_2 = new Vector3f(-0.5F, -(1 + 7 / 16F), -.5F);

    private final Vector3f PIVOT_3 = new Vector3f(0.5F, 1 + 7 / 16F, 0);
    private final Vector3f ANTI_PIVOT_3 = new Vector3f(-0.5F, -(1 + 7 / 16F), 0);
    private int[] vertexDataArray;
    private int[] vertexItemDataArray;
    private int quadCount = 0;

    Vertex bottomLeft =  new Vertex(-0.5F, -0.5F, 0F, 0F, 0F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
    Vertex bottomRight = new Vertex(0.5F, -0.5F, 0F, 1F, 0F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
    Vertex topLeft =     new Vertex(-0.5F, 0.5F, 0F, 0F, 1F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
    Vertex topRight =    new Vertex(0.5F, 0.5F, 0F, 1F, 1F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
    Vertex[] vertices = new Vertex[]{bottomLeft, bottomRight, topRight, topLeft};
    FixedFunctionVbo vbo;

    public TileArmRenderer() {
        super();
        Vertex bottomLeft =  new Vertex(-0.5F, -0.5F, 0F, 0F, 0F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
        Vertex bottomRight = new Vertex(0.5F, -0.5F, 0F, 1F, 0F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
        Vertex topLeft =     new Vertex(-0.5F, 0.5F, 0F, 0F, 1F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
        Vertex topRight =    new Vertex(0.5F, 0.5F, 0F, 1F, 1F, 0F, 0F, 1F, 1F, 1F, 1F, 1F);
        Vertex[] vertices = new Vertex[]{bottomLeft, bottomRight, topRight, topLeft};

        vbo = FixedFunctionVbo.setupVbo(vertices);
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

        return -16777216 | blue << 16 | green << 8 | red;

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

    }

    @Override
    public void render(TileArmBasic te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 4, z + 0.5);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(""));
        vbo.draw();

        GL11.glPopMatrix();
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

    public void renderQuads(IBufferBuilderMixin buffer, int[][] quadDataList, Vector3f baseOffset, Matrix4f transformMatrix, int brightness, int color) {
        for (int i = 0; i < quadDataList.length; i++) {
            int[] quadData = quadDataList[i];
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
                int x = Float.floatToRawIntBits((float) (vertexTransformingVec.x + baseOffset.x + buffer.getOffsetX()));
                int y = Float.floatToRawIntBits((float) (vertexTransformingVec.y + baseOffset.y + buffer.getOffsetY()));
                int z = Float.floatToRawIntBits((float) (vertexTransformingVec.z + baseOffset.z + buffer.getOffsetZ()));

                int destIndex = quadCount * 28 + vertexIndex;
                // vertex position data
                vertexDataArray[destIndex] = x;
                vertexDataArray[destIndex + 1] = y;
                vertexDataArray[destIndex + 2] = z;

                vertexDataArray[destIndex + 3] = color;

                vertexDataArray[destIndex + 4] = quadData[vertexIndex + 4]; //texture
                vertexDataArray[destIndex + 5] = quadData[vertexIndex + 5];

                // vertex brightness
                vertexDataArray[destIndex + 6] = brightness;
            }
            quadCount++;
        }
    }

    public void renderItemQuads(IBufferBuilderMixin buffer, int[][] quadDataList, Vector3f baseOffset, Matrix4f transformMatrix, int brightness, int color) {
        for (int i = 0; i < quadDataList.length; i++) {
            int[] quadData = quadDataList[i];
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
                int x = Float.floatToRawIntBits((float) (vertexTransformingVec.x + baseOffset.x + buffer.getOffsetX()));
                int y = Float.floatToRawIntBits((float) (vertexTransformingVec.y + baseOffset.y + buffer.getOffsetY()));
                int z = Float.floatToRawIntBits((float) (vertexTransformingVec.z + baseOffset.z + buffer.getOffsetZ()));

                int destIndex = i * 28 + vertexIndex;
                // vertex position data
                vertexItemDataArray[destIndex] = x;
                vertexItemDataArray[destIndex + 1] = y;
                vertexItemDataArray[destIndex + 2] = z;

                vertexItemDataArray[destIndex + 3] = color;

                vertexItemDataArray[destIndex + 4] = quadData[vertexIndex + 4]; //texture
                vertexItemDataArray[destIndex + 5] = quadData[vertexIndex + 5];

                // vertex brightness
                vertexItemDataArray[destIndex + 6] = brightness;
            }
        }
    }

    private float lerp(float previous, float current, float partialTick) {
        return (previous * (1.0F - partialTick)) + (current * partialTick);
    }


}