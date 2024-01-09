package mechanicalarms.client.renderer.util;

import mechanicalarms.client.renderer.InstanceableModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ItemStackRenderToVAO implements InstanceableModel {

    private final IntBuffer texGL = GLAllocation.createDirectByteBuffer(16).asIntBuffer();
    private int posBuffer;
    private int texBuffer;
    private int normalBuffer;
    private int lightBuffer;
    private int modelTransform;
    private int vertexCount;

    private int vertexArrayBuffer;

    private ItemStack stack;

    public ItemStackRenderToVAO(ItemStack stack) {
        this.stack = stack.copy();
        this.setupVAO(this.stack);
    }

    public void setupVAO(ItemStack stack) {
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);

        FloatBuffer pos = GLAllocation.createDirectFloatBuffer(3000);
        FloatBuffer norm = GLAllocation.createDirectFloatBuffer(3000);
        FloatBuffer tex = GLAllocation.createDirectFloatBuffer(2000);

        int v = 0;

        List<BakedQuad> loq = new ArrayList<>(model.getQuads(null, null, 0));
        for (EnumFacing e : EnumFacing.VALUES) {
            loq.addAll(model.getQuads(null, e, 0));
        }

        //if an item model has no quads, attempt to capture its rendering
        //a missing item model has quads.

        if (loq.isEmpty()) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glViewport(0, 0, 1, 1);

            // Allocate buffer for feedback data

            boolean overflowed = false;
            int bufferMultiplier = 1;
            FloatBuffer feedbackBuffer = GLAllocation.createDirectFloatBuffer(3000 * bufferMultiplier);

            while (!overflowed) {

                // Retrieve feedback data
                GL11.glFeedbackBuffer(GL11.GL_3D_COLOR_TEXTURE, feedbackBuffer);
                GL11.glRenderMode(GL11.GL_FEEDBACK);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
                overflowed = GL11.glGetError() == 0;
                if (overflowed) {
                    bufferMultiplier++;
                    feedbackBuffer = GLAllocation.createDirectFloatBuffer(3000 * bufferMultiplier);
                }
            }

            //save the current bound texture for later.
            //maybe mixin to GlStateManager bindTexture

            GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, texGL);

            // Return to normal rendering mode
            GL11.glRenderMode(GL11.GL_RENDER);
            GL11.glMatrixMode(GL11.GL_PROJECTION);

            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();

            boolean end = false;
            float[] posv = new float[9];
            while (!end) {
                float cur = feedbackBuffer.get();
                if ((int) cur == GL11.GL_POLYGON_TOKEN) {
                    // 3 floats for pos, 3 floats for normal and 2 floats for texture UV, per vertex. always.
                    if (pos.remaining() == 0) {
                        pos = GLAllocation.createDirectFloatBuffer(pos.capacity() * 2).put(pos);
                        norm = GLAllocation.createDirectFloatBuffer(norm.capacity() * 2).put(norm);
                        tex = GLAllocation.createDirectFloatBuffer(tex.capacity() * 2).put(tex);
                    }
                    for (int j = 0; j < 3; j++) {
                        v++;
                        float x = feedbackBuffer.get();
                        float y = feedbackBuffer.get();
                        float z = feedbackBuffer.get();

                        pos.put(x); //x
                        pos.put(y); //y
                        pos.put(z); //z

                        feedbackBuffer.get();//r
                        feedbackBuffer.get();//g
                        feedbackBuffer.get();//b
                        feedbackBuffer.get();//a
                        tex.put(feedbackBuffer.get()); // u
                        tex.put(feedbackBuffer.get()); // v
                        feedbackBuffer.get(); // unused
                        feedbackBuffer.get(); // unused
                    }

                    //face normal:
                    pos.position(pos.position() - 9);
                    pos.get(posv);
                    Vector3f v0 = new Vector3f(posv[0], posv[1], posv[2]);
                    Vector3f v1 = new Vector3f(posv[3], posv[4], posv[5]);
                    Vector3f v2 = new Vector3f(posv[6], posv[7], posv[8]);

                    Vector3f edge1 = Vector3f.sub(v2, v1, new Vector3f());
                    Vector3f edge2 = Vector3f.sub(v0, v1, new Vector3f());

                    Vector3f crsProd = Vector3f.cross(edge1, edge2, new Vector3f()); // Cross product between edge1 and edge2

                    Vector3f normal = (Vector3f) crsProd.normalise(); // Normalization of the vector
                    for (int i = 0; i < 3; i++) {
                        norm.put(normal.x);
                        norm.put(normal.y);
                        norm.put(normal.z);
                    }
                } else {
                    end = true;
                }
            }
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        else {
            for (BakedQuad bq : loq) {
                int[] quadData = bq.getVertexData();
                for (int k = 0; k < 3; ++k) {
                    v++;
                    // Getting the offset for the current vertex.
                    int vertexIndex = k * 7;
                    pos.put(Float.intBitsToFloat(quadData[vertexIndex]));
                    pos.put(Float.intBitsToFloat(quadData[vertexIndex + 1]));
                    pos.put(Float.intBitsToFloat(quadData[vertexIndex + 2]));

                    tex.put(Float.intBitsToFloat(quadData[vertexIndex + 4])); //texture
                    tex.put(Float.intBitsToFloat(quadData[vertexIndex + 5])); //texture

                    int packedNormal = quadData[vertexIndex + 6];
                    norm.put(((packedNormal) & 255) / 127.0F);
                    norm.put(((packedNormal >> 8) & 255) / 127.0F);
                    norm.put(((packedNormal >> 16) & 255) / 127.0F);

                }
                for (int k = 2; k < 4; ++k) {
                    v++;
                    // Getting the offset for the current vertex.
                    int vertexIndex = k * 7;
                    pos.put(Float.intBitsToFloat(quadData[vertexIndex]));
                    pos.put(Float.intBitsToFloat(quadData[vertexIndex + 1]));
                    pos.put(Float.intBitsToFloat(quadData[vertexIndex + 2]));

                    tex.put(Float.intBitsToFloat(quadData[vertexIndex + 4])); //texture
                    tex.put(Float.intBitsToFloat(quadData[vertexIndex + 5])); //texture

                    int packedNormal = quadData[vertexIndex + 6];
                    norm.put(((packedNormal) & 255) / 127.0F);
                    norm.put(((packedNormal >> 8) & 255) / 127.0F);
                    norm.put(((packedNormal >> 16) & 255) / 127.0F);
                }
                v++;
                // Getting the offset for the current vertex.
                int vertexIndex = 0;
                pos.put(Float.intBitsToFloat(quadData[vertexIndex]));
                pos.put(Float.intBitsToFloat(quadData[vertexIndex + 1]));
                pos.put(Float.intBitsToFloat(quadData[vertexIndex + 2]));

                tex.put(Float.intBitsToFloat(quadData[vertexIndex + 4])); //texture
                tex.put(Float.intBitsToFloat(quadData[vertexIndex + 5])); //texture

                int packedNormal = quadData[vertexIndex + 6];
                norm.put(((packedNormal) & 255) / 127.0F);
                norm.put(((packedNormal >> 8) & 255) / 127.0F);
                norm.put(((packedNormal >> 16) & 255) / 127.0F);
            }
        }

        vertexArrayBuffer = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayBuffer);

        pos.rewind();
        norm.rewind();
        tex.rewind();

        posBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, pos, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0,
                3,
                GL11.GL_FLOAT,
                false,
                0,
                0);
        GL20.glEnableVertexAttribArray(0);

        texBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tex, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, true, 0, 0);
        GL20.glEnableVertexAttribArray(1);


        normalBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, norm, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, true, 0, 0);
        GL20.glEnableVertexAttribArray(2);

        lightBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lightBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 2, GL15.GL_DYNAMIC_DRAW);

        //Light
        GL20.glVertexAttribPointer(3, 2, GL11.GL_UNSIGNED_BYTE, false, 0, 0);
        GL20.glEnableVertexAttribArray(3);
        GL33.glVertexAttribDivisor(3, 1);

        modelTransform = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, modelTransform);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 64, GL15.GL_DYNAMIC_DRAW);

        for (int k = 0; k < 4; k++) {
            GL20.glVertexAttribPointer(4 + k, 4, GL11.GL_FLOAT, false, 0, k * 16);
            GL20.glEnableVertexAttribArray(4 + k);
            GL33.glVertexAttribDivisor(4 + k, 1);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        this.vertexCount = v;
    }

    @Override
    public int getTexGl() {
        texGL.rewind();
        return texGL.get(0);
    }

    @Override
    public int getBlockLightBuffer() {
        return lightBuffer;
    }

    @Override
    public int getVertexArrayBuffer() {
        return vertexArrayBuffer;
    }

    @Override
    public int getModelTransformBuffer() {
        return modelTransform;
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    public ItemStack getStack() {
        return stack;
    }
}
