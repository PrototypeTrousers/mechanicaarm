package mechanicalarms.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Vao2 {
    protected static final IntBuffer texGL = GLAllocation.createDirectIntBuffer(16);
    public static int lightBuffer;
    public static int vboInstance;

    public static int posBuffer;
    public static int normalBuffer;
    public static int texBuffer;
    public int vaoId;
    public int drawMode;
    public int vertexCount;
    public boolean useElements;

    public Vao2(int vao, int mode, int length, boolean b) {
        this.vaoId = vao;
        this.drawMode = mode;
        this.vertexCount = length;
        this.useElements = b;
    }

    public static Vao2 setupVAO() {

        // Render your Minecraft model here
        //ItemStack stack = new ItemStack(Blocks.PISTON);
        ItemStack stack = new ItemStack(Items.STICK);

        NBTTagCompound data = new NBTTagCompound();
        data.setString("id", "minecraft:enchanted_book");
        data.setByte("Count", (byte) 1);
        //data.setShort("Damage", (short) 324);
        //ItemStack stack = new ItemStack(Blocks.CHEST);

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);

        FloatBuffer pos = GLAllocation.createDirectFloatBuffer(3000);
        FloatBuffer norm = GLAllocation.createDirectFloatBuffer(3000);
        FloatBuffer tex = GLAllocation.createDirectFloatBuffer(3000);

        int v = 0;

        if (model.isBuiltInRenderer()) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glViewport(0, 0, 1, 1);

            // Allocate buffer for feedback data


            FloatBuffer feedbackBuffer = GLAllocation.createDirectFloatBuffer(3000);

            // Retrieve feedback data
            GL11.glFeedbackBuffer(GL11.GL_3D_COLOR_TEXTURE, feedbackBuffer);
            GL11.glRenderMode(GL11.GL_FEEDBACK);


            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);

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
                    boolean triangle = feedbackBuffer.get() == 3;
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
        } else {
            List<BakedQuad> loq = new ArrayList<>(model.getQuads(null, null, 0));
            for (EnumFacing e : EnumFacing.VALUES) {
                loq.addAll(model.getQuads(null, e, 0));
            }
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


        int vao2 = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao2);

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
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 2, GL15.GL_DYNAMIC_DRAW);

        //Light
        GL20.glVertexAttribPointer(3, 2, GL11.GL_UNSIGNED_BYTE, false, 2, 0);
        GL20.glEnableVertexAttribArray(3);
        GL33.glVertexAttribDivisor(3, 1);

        vboInstance = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboInstance);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 64, GL15.GL_DYNAMIC_DRAW);

        for (int k = 0; k < 4; k++) {
            GL20.glVertexAttribPointer(4 + k, 4, GL11.GL_FLOAT, false, 16, k * 16);
            GL20.glEnableVertexAttribArray(4 + k);
            GL33.glVertexAttribDivisor(4 + k, 1);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        return new Vao2(vao2, GL11.GL_TRIANGLES, v, false);

    }

    public static int getTexGl() {
        texGL.rewind();
        return texGL.get(0);
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
