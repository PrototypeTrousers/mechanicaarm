package mechanicalarms.client.renderer.util;

import mechanicalarms.client.renderer.InstanceableModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemStackRenderToVAO implements InstanceableModel {

    private int texGL;
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
        IBakedModel mm = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        IBakedModel model = mm.getOverrides().handleItemState(mm, stack, null, null);
        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GROUND, false);
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

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glViewport(0, 0, 1, 1);

            // Allocate buffer for feedback data

            FloatBuffer feedbackBuffer = GLAllocation.createDirectFloatBuffer(875);
            do {
                feedbackBuffer = GLAllocation.createDirectFloatBuffer(feedbackBuffer.capacity() * 2);
                GL11.glFeedbackBuffer(GL11.GL_3D_COLOR_TEXTURE, feedbackBuffer);
                GL11.glRenderMode(GL11.GL_FEEDBACK);

                // Retrieve feedback data
                if (model.isBuiltInRenderer()) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableRescaleNormal();
                    stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
                } else {
                    Minecraft.getMinecraft().getRenderItem().renderModel(model, stack);
                }

                // Return to normal rendering mode
            } while (GL11.glRenderMode(GL11.GL_RENDER) <= 0);
            //save the current bound texture for later.
            //maybe mixin to GlStateManager bindTexture

            texGL = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();

            boolean end = false;
            float[] posv = new float[9];
            while (!end) {
                float cur = feedbackBuffer.get();
                if ((int) cur == GL11.GL_POLYGON_TOKEN) {
                    feedbackBuffer.get();
                    // 3 floats for pos, 3 floats for normal and 2 floats for texture UV, per vertex. always.
                    if (pos.remaining() < 33) {
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
                if (feedbackBuffer.position() == feedbackBuffer.limit()) {
                    end = true;
                }
            }
            GL11.glEnable(GL11.GL_CULL_FACE);

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
                12,
                0);
        GL20.glEnableVertexAttribArray(0);

        texBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tex, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, true, 8, 0);
        GL20.glEnableVertexAttribArray(1);

        normalBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, norm, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, true, 12, 0);
        GL20.glEnableVertexAttribArray(2);

        lightBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lightBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 2, GL15.GL_STATIC_DRAW);

        //Light
        GL20.glVertexAttribPointer(3, 2, GL11.GL_UNSIGNED_BYTE, false, 2, 0);
        GL20.glEnableVertexAttribArray(3);
        GL33.glVertexAttribDivisor(3, 1);

        modelTransform = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, modelTransform);

        for (int k = 0; k < 4; k++) {
            GL20.glVertexAttribPointer(4 + k, 4, GL11.GL_FLOAT, false, 64, k * 16);
            GL20.glEnableVertexAttribArray(4 + k);
            GL33.glVertexAttribDivisor(4 + k, 1);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        this.vertexCount = v;
    }

    @Override
    public int getTexGl() {
        return texGL;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStackRenderToVAO that = (ItemStackRenderToVAO) o;
        return ItemStack.areItemsEqual(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getItem(), stack.getMetadata());
    }
}
