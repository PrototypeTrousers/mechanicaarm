package mechanicalarms.client.renderer;

import assimp.AiScene;
import assimp.Importer;
import colladamodel.client.model.Face;
import colladamodel.client.model.Geometry;
import colladamodel.client.model.Model;
import colladamodel.client.model.collada.ColladaModelLoader;
import mechanicalarms.MechanicalArms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.glBindBufferBase;

public class Vao {

    public static int indirectBuffer;
    public static int boneBuffer;
    public static int lightBuffer;
    public int vaoId;
    public int drawMode;
    public int vertexCount;
    public boolean useElements;
    public static ByteBuffer data = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);

    public static Model dae;

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


            AiScene scene = new Importer().readFile(FileSystems.getDefault().getPath("/mnt/ldata/git/mechanicalarms/src/main/resources/assets/mechanicalarms/models/block/dragon.dae"));
scene.getAnimations();
scene.getMeshes().get(0).getBones()


        try {
            dae = (Model) ColladaModelLoader.INSTANCE.loadModel(new ResourceLocation(MechanicalArms.MODID, "models/block/dragon.dae"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Face> faces = new ArrayList<>();
        for (Geometry g : dae.getGeometries().values()) {
            faces.addAll(g.getFaces());
        }
        //List<Face> faces = ((Model) dae).getGeometry("firstArm").getFaces();
        int v = 0;

        for (Face f : faces) {
            Vector3f[] vertex = f.getVertex();
            for (int i = 0; i < vertex.length; i++) {
                Vector3f vec = vertex[i];
                data.putFloat(vec.x);
                data.putFloat(vec.y);
                data.putFloat(vec.z);
                //U,V
                data.putFloat(f.getVertexTexCoord()[i].x);
                data.putFloat(f.getVertexTexCoord()[i].y);
                //Normals
                data.put((byte) Float.floatToIntBits(f.getVertexNormals()[i].x));
                data.put((byte) Float.floatToIntBits(f.getVertexNormals()[i].y));
                data.put((byte) Float.floatToIntBits(f.getVertexNormals()[i].z));
                data.put((byte) 0);
                v++;
            }
        }

        data.rewind();

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW);

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        //Pos
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Vertex.BYTES_PER_VERTEX, 0);
        GL20.glEnableVertexAttribArray(0);

        //Texture
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.BYTES_PER_VERTEX, 12);
        GL20.glEnableVertexAttribArray(1);
        //Normal
        GL20.glVertexAttribPointer(2, 3, GL11.GL_BYTE, true, Vertex.BYTES_PER_VERTEX, 20);
        GL20.glEnableVertexAttribArray(2);

        //Bone index
        GL20.glVertexAttribPointer(7, 1, GL11.GL_UNSIGNED_BYTE, false, Vertex.BYTES_PER_VERTEX, 23);
        GL20.glEnableVertexAttribArray(7);

        lightBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lightBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 2, GL15.GL_DYNAMIC_DRAW);

        //Light
        GL20.glVertexAttribPointer(3, 2, GL11.GL_UNSIGNED_BYTE, false, 2, 0);
        GL20.glEnableVertexAttribArray(3);
        GL33.glVertexAttribDivisor(3, 1);

        boneBuffer = GL15.glGenBuffers();
        glBindBufferBase(GL15.GL_ARRAY_BUFFER, 0, boneBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, boneBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 16, GL15.GL_DYNAMIC_DRAW);


        //3 bones transform as Quaternion
        for (int i = 0; i < 3; i++) {
            GL20.glVertexAttribPointer(i + 4, 4, GL11.GL_FLOAT, false, 64, i * 4);
            GL20.glEnableVertexAttribArray(i + 4);
            GL33.glVertexAttribDivisor(i + 4, 1);
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);


        indirectBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL40.GL_DRAW_INDIRECT_BUFFER, indirectBuffer);

        // Example data for a draw call
        int vertexCount = v;
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


        return new Vao(vao, GL11.GL_TRIANGLES, v, false);
    }


}
