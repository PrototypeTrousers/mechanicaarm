package mechanicalarms.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Vao2 {


    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);

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
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glViewport(0,0,1,1);

        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);

        // Get the current projection matrix and store it in the buffer
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();


        Matrix4f mv = (Matrix4f) new Matrix4f().load(MODELVIEW_MATRIX_BUFFER);
        Matrix4f p = (Matrix4f) new Matrix4f().load(PROJECTION_MATRIX_BUFFER);
        mv.invert();
        p.invert();
        mv.transpose();
        p.transpose();

        Matrix4f.mul(mv, p, p);

        GlStateManager.translate(0, 0, 0);

        // Allocate buffer for feedback data
        FloatBuffer feedbackBuffer = GLAllocation.createDirectFloatBuffer(3000);

        // Retrieve feedback data
        GL11.glFeedbackBuffer(GL11.GL_3D_COLOR_TEXTURE, feedbackBuffer);
        GL11.glRenderMode(GL11.GL_FEEDBACK);

        // Render your Minecraft model here
        ItemStack chestStack = new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:chest")));
        Minecraft.getMinecraft().getRenderItem().renderItem(chestStack, ItemCameraTransforms.TransformType.NONE);

        // Return to normal rendering mode
        GL11.glRenderMode(GL11.GL_RENDER);
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();


        int vao2 = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao2);


        ByteBuffer pos = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        ByteBuffer norm = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        ByteBuffer tex = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);

        float[] f = new float[3000];
        feedbackBuffer.get(f);

        feedbackBuffer.rewind();

        boolean end = false;
        int v =0;
        while (!end) {
            float cur = feedbackBuffer.get();
            if ((int) cur == GL11.GL_POLYGON_TOKEN) {
                boolean triangle = feedbackBuffer.get() == 3;
                for (int j = 0; j < 3; j++) {
                    v++;
                    float x = feedbackBuffer.get();
                    float y = feedbackBuffer.get();
                    float z = feedbackBuffer.get();

                    pos.putFloat(x); //x
                    pos.putFloat(y); //y
                    pos.putFloat(z); //z

                    Vector4f vPre = new Vector4f(x, y, z, 1);
                    Vector4f vPos = Matrix4f.transform(p, vPre, new Vector4f());
                    feedbackBuffer.get();//r
                    feedbackBuffer.get();//g
                    feedbackBuffer.get();//b
                    feedbackBuffer.get();//a
                    norm.putFloat(1);
                    norm.putFloat(1);
                    norm.putFloat(1);
                    tex.putFloat(feedbackBuffer.get()); // u
                    tex.putFloat(feedbackBuffer.get()); // v
                    feedbackBuffer.get(); // unused
                    feedbackBuffer.get(); // unused
                }
            } else {
                end = true;
            }
        }

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

    public int getVertexCount() {
        return vertexCount;
    }

}
