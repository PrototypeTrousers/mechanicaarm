package mechanicalarms.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

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

        // Render your Minecraft model here
        //ItemStack stack = new ItemStack(Blocks.PISTON);
        ItemStack stack = new ItemStack(Items.STICK);
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);

        ByteBuffer pos = GLAllocation.createDirectByteBuffer(2400000 );
        ByteBuffer norm = GLAllocation.createDirectByteBuffer(2400000 );
        ByteBuffer tex = GLAllocation.createDirectByteBuffer(2400000 );

        int v =0;

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


            // Return to normal rendering mode
            GL11.glRenderMode(GL11.GL_RENDER);
            GL11.glMatrixMode(GL11.GL_PROJECTION);

            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();

            boolean end = false;
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

                        feedbackBuffer.get();//r
                        feedbackBuffer.get();//g
                        feedbackBuffer.get();//b
                        feedbackBuffer.get();//a
                        tex.putFloat(feedbackBuffer.get()); // u
                        tex.putFloat(feedbackBuffer.get()); // v
                        feedbackBuffer.get(); // unused
                        feedbackBuffer.get(); // unused
                    }
                } else {
                    end = true;
                }
            }
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        else {
            List<BakedQuad> loq = model.getQuads(null, null, 0);
            for (BakedQuad bq : loq) {
                int[] quadData = bq.getVertexData();
                Vec3i vec3i = bq.getFace().getDirectionVec();
                for (int k = 0; k < 3; ++k) {
                    v++;
                    // Getting the offset for the current vertex.
                    int vertexIndex = k * 7;
                    pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex]));
                    pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 1]));
                    pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 2]));

                    tex.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 4])); //texture
                    tex.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 5])); //texture

                    int packedNormal = quadData[vertexIndex + 6];
                    norm.putFloat( ((packedNormal) & 255) / 127.0F);
                    norm.putFloat( ((packedNormal >> 8) & 255) / 127.0F);
                    norm.putFloat( ((packedNormal >> 16) & 255) / 127.0F);

                }
                for (int k = 2; k < 4; ++k) {
                    v++;
                    // Getting the offset for the current vertex.
                    int vertexIndex = k * 7;
                    pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex]));
                    pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 1]));
                    pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 2]));

                    tex.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 4])); //texture
                    tex.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 5])); //texture

                    int packedNormal = quadData[vertexIndex + 6];
                    norm.putFloat( ((packedNormal) & 255) / 127.0F);
                    norm.putFloat( ((packedNormal >> 8) & 255) / 127.0F);
                    norm.putFloat( ((packedNormal >> 16) & 255) / 127.0F);
                }
                v++;
                // Getting the offset for the current vertex.
                int vertexIndex = 0;
                pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex]));
                pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 1]));
                pos.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 2]));

                tex.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 4])); //texture
                tex.putFloat(Float.intBitsToFloat(quadData[vertexIndex + 5])); //texture

                int packedNormal = quadData[vertexIndex + 6];
                norm.putFloat( ((packedNormal) & 255) / 127.0F);
                norm.putFloat( ((packedNormal >> 8) & 255) / 127.0F);
                norm.putFloat( ((packedNormal >> 16) & 255) / 127.0F);
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

    public int getVertexCount() {
        return vertexCount;
    }

}
