package mechanicalarms.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Vao2 {

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
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glLoadIdentity();
        GlStateManager.translate(0, 0, -5);

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
        GL11.glPopMatrix();

        int vao2 = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao2);


        ByteBuffer pos = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        ByteBuffer norm = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        ByteBuffer tex = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);


        boolean end = false;
        int i = 0;
        int v =0;
        while (!end) {
            float cur = feedbackBuffer.get(i);
            if ((int) cur == GL11.GL_POLYGON_TOKEN) {
                for (int j = 0; j < 4; j++) {
                    v++;
                    boolean triangle = feedbackBuffer.get() == 3;
                    pos.putFloat(feedbackBuffer.get()); //x
                    pos.putFloat(feedbackBuffer.get()); //y
                    pos.putFloat(feedbackBuffer.get()); //z
                    feedbackBuffer.get();//r
                    feedbackBuffer.get();//g
                    feedbackBuffer.get();//b
                    feedbackBuffer.get();//a
                    tex.putFloat(feedbackBuffer.get()); // u
                    tex.putFloat(feedbackBuffer.get()); // v
                    tex.putFloat(feedbackBuffer.get()); // unused
                    tex.putFloat(feedbackBuffer.get()); // unused
                }
                i += 35;
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

        return new Vao2(vao2, GL11.GL_QUADS, v, false);

    }
}
