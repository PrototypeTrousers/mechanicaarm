package mechanicalarms.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mechanicalarms.MechanicalArms;
import mechanicalarms.client.renderer.shaders.Shader;
import mechanicalarms.client.renderer.shaders.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;

public class InstanceRender {

    public static final Shader base_vao = ShaderManager.loadShader(new ResourceLocation(MechanicalArms.MODID, "shaders/arm_shader")).withUniforms(ShaderManager.LIGHTMAP).withUniforms();

    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);

    static InstanceRender INSTANCE = new InstanceRender();
    InstanceData current;

    static Map<InstanceableModel, InstanceData> modelInstanceData = new Object2ObjectOpenHashMap<>();

    public static void draw() {

        if (modelInstanceData.isEmpty() || MinecraftForgeClient.getRenderPass() != 0) {
            return;
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        base_vao.use();

        int curTexId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW_MATRIX_BUFFER);
        // Get the current projection matrix and store it in the buffer
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);

        int projectionLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "projection");
        int viewLoc = GL20.glGetUniformLocation(base_vao.getShaderId(), "view");

        GL20.glUniformMatrix4(projectionLoc, false, PROJECTION_MATRIX_BUFFER);
        GL20.glUniformMatrix4(viewLoc, false, MODELVIEW_MATRIX_BUFFER);

        for (Map.Entry<InstanceableModel, InstanceData> entry : modelInstanceData.entrySet()) {
            InstanceableModel im = entry.getKey();
            InstanceData instanceData = entry.getValue();
            instanceData.rewindBuffers();


            GL30.glBindVertexArray(im.getVertexArrayBuffer());

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, im.getModelTransformBuffer());
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceData.modelMatrixBuffer, GL15.GL_DYNAMIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, im.getBlockLightBuffer());
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceData.blockLightBuffer, GL15.GL_DYNAMIC_DRAW);

            int t = im.getTexGl();
            if (t == 0) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            } else {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, t);
            }
            GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, im.getVertexCount(), instanceData.getInstanceCount());
            instanceData.rewindBuffers();
            instanceData.resetCount();
        }

        modelInstanceData.clear();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, curTexId);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        base_vao.release();
    }

    public void schedule(InstanceableModel item) {
        current = modelInstanceData.computeIfAbsent(item, v -> new InstanceData());
        current.increaseInstanceCount();
    }

    public void bufferData(int glBufferID, float[] dataToBuffer) {

    }

    public void bufferModelMatrixData(float[] dataToBuffer) {
        current.resizeModelMatrix();
        current.modelMatrixBuffer.put(dataToBuffer, 0, 16);
    }

    public void bufferLight(byte s, byte b) {
        current.resizeLightBuffer();
        current.blockLightBuffer.put(new byte[]{s, b}, 0,  2);
    }


    public class InstanceData {

        FloatBuffer modelMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
        ByteBuffer blockLightBuffer = GLAllocation.createDirectByteBuffer(2);

        private int instanceCount;


        public void resizeModelMatrix() {
            if (modelMatrixBuffer.remaining() < 16) {
                FloatBuffer newBuffer = GLAllocation.createDirectFloatBuffer(modelMatrixBuffer.capacity() * 2);
                int currentPos = modelMatrixBuffer.position();
                modelMatrixBuffer.rewind();
                newBuffer.put(modelMatrixBuffer);
                newBuffer.position(currentPos);
                modelMatrixBuffer = newBuffer;
            }
        }

        public void resizeLightBuffer() {
            if (blockLightBuffer.remaining() < 2) {
                ByteBuffer newBuffer = GLAllocation.createDirectByteBuffer(blockLightBuffer.capacity() * 2);
                int currentPos = blockLightBuffer.position();
                blockLightBuffer.rewind();
                newBuffer.put(blockLightBuffer);
                newBuffer.position(currentPos);
                blockLightBuffer = newBuffer;
            }
        }

        public void rewindBuffers() {
            modelMatrixBuffer.rewind();
            blockLightBuffer.rewind();
        }

        public void increaseInstanceCount() {
            this.instanceCount++;
        }

        public int getInstanceCount() {
            return instanceCount;
        }

        public void resetCount() {
            this.instanceCount = 0;
        }
    }
}
