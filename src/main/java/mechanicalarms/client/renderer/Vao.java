package mechanicalarms.client.renderer;

import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import mechanicalarms.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Vao {

    public static int indirectBuffer;
    public static int boneBuffer;
    public static int lightBuffer;
    public static int vboInstance;

    public static int posBuffer;
    public static int normalBuffer;
    public static int texBuffer;
    public int vaoId;
    public int drawMode;
    public int vertexCount;
    public boolean useElements;

    public Vao(int vao, int mode, int length, boolean b) {
        this.vaoId = vao;
        this.drawMode = mode;
        this.vertexCount = length;
        this.useElements = b;
    }

    public void draw() {
        GL30.glBindVertexArray(vaoId);
        if (useElements) {
            //Unsigned int because usually elements are specified as unsigned integer values
            GL11.glDrawElements(drawMode, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        } else {
            GL11.glDrawArrays(drawMode, 0, vertexCount);
        }
        GL30.glBindVertexArray(0);
    }

    public static Vao setupVAO() {

        GltfModelReader reader = new GltfModelReader();
        GltfModel model;
        try {
            model = reader.readWithoutReferences(new BufferedInputStream(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(Tags.MODID, "models/block/arm.gltf")).getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DefaultGltfModel dm = (DefaultGltfModel) model;
        NodeModel nodeModel = dm.getSceneModel(0).getNodeModels().get(0);

        for (NodeModel child : nodeModel.getChildren()) {
            for (NodeModel child2 : child.getChildren()) {
                for (MeshModel meshModel : child2.getMeshModels()) {
                    for (MeshPrimitiveModel meshPrimitiveModel : meshModel.getMeshPrimitiveModels()) {
                        Map<String, AccessorModel> attributes = meshPrimitiveModel.getAttributes();
                        AccessorModel position = attributes.get("POSITION");
                        posBuffer = GL15.glGenBuffers();
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posBuffer);
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, position.getBufferViewModel().getBufferViewData(), GL15.GL_STATIC_DRAW);

                        texBuffer = GL15.glGenBuffers();
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBuffer);

                        AccessorModel texUAccessorModel = attributes.get("TEXCOORD_0");
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texUAccessorModel.getBufferViewModel().getBufferViewData(), GL15.GL_STATIC_DRAW);

                        normalBuffer = GL15.glGenBuffers();
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
                        AccessorModel normal = attributes.get("NORMAL");
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normal.getBufferViewModel().getBufferViewData(), GL15.GL_STATIC_DRAW);
                        break;
                    }
                }
            }
        }

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        //Pos
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posBuffer);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12, 0);
        GL20.glEnableVertexAttribArray(0);

        //Texture
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBuffer);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 8, 0);
        GL20.glEnableVertexAttribArray(1);
        //Normal
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
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

        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(4 + i, 4, GL_FLOAT, false, 64, i * 16);
            glEnableVertexAttribArray(4 + i);
            glVertexAttribDivisor(4 + i, 1);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        indirectBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, indirectBuffer);

        // Example data for a draw call
        int vertexCount = 96;
        int instanceCount = 1;
        int firstVertex = 0;
        int baseInstance = 0;

        // Create a ByteBuffer to hold the draw parameters
        ByteBuffer drawBuffer = BufferUtils.createByteBuffer(16);
        drawBuffer.putInt(vertexCount);
        drawBuffer.putInt(instanceCount);
        drawBuffer.putInt(firstVertex);
        drawBuffer.putInt(baseInstance);
        drawBuffer.flip();

        // Upload the draw parameters to the buffer

        GL15.glBufferData(GL40.GL_DRAW_INDIRECT_BUFFER, drawBuffer, GL15.GL_STATIC_DRAW);

        return new Vao(vao, GL11.GL_TRIANGLES, 96, false);
    }
}
