package mechanicalarms.client.renderer;

import de.javagl.obj.*;
import mechanicalarms.MechanicalArms;
import mechanicalarms.common.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Vao {

    public int vaoId;
    public int drawMode;
    public int vertexCount;
    public boolean useElements;

    public static int vboInstance;
    public static int ebo;

    public static IntBuffer indices;

    public Vao(int vao, int mode, int length, boolean b) {
        this.vaoId = vao;
        this.drawMode = mode;
        this.vertexCount = length;
        this.useElements = b;
    }

    public void draw(){
        GL30.glBindVertexArray(vaoId);
        if(useElements){
            //Unsigned int because usually elements are specified as unsigned integer values
            GL11.glDrawElements(drawMode, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        } else {
            GL11.glDrawArrays(drawMode, 0, vertexCount);
        }
        GL30.glBindVertexArray(0);
    }

    public static Vao setupVertices(int[][][] vertices) {


        ResourceLocation file = ClientProxy.arm;
        Obj obj;
        try {
            Mouse.setGrabbed(false);
            Obj r = ObjReader.read(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic.obj")).getInputStream());
            obj = ObjUtils.convertToRenderable(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        indices = ObjData.getFaceVertexIndices(obj, 3);
        FloatBuffer verticex = ObjData.getVertices(obj);
        FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
        FloatBuffer normals = ObjData.getNormals(obj);


        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        FloatBuffer data = GLAllocation.createDirectFloatBuffer(indices.capacity() * 27);

        for (int i = 0; i < obj.getNumFaces(); i++) {
            ObjFace face = obj.getFace(i);
            for (int j = 0; j < face.getNumVertices(); j++) {
                FloatTuple vt = obj.getVertex(face.getVertexIndex(j));
                data.put(vt.getX());
                data.put(vt.getY());
                data.put(vt.getZ());

                FloatTuple tx = obj.getTexCoord(face.getTexCoordIndex(j));
                data.put(tx.getX());
                data.put(tx.getY());

                FloatTuple nm = obj.getNormal(face.getNormalIndex(j));
                data.put(nm.getX());
                data.put(nm.getY());
                data.put(nm.getZ());

                for (int k = 0; k < 4; k++) {
                    data.put((byte) 255);
                }
            }
        }

        data.rewind();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);

        //Pos
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Vertex.BYTES_PER_VERTEX, 0);
        GL20.glEnableVertexAttribArray(0);

        //Texture
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.BYTES_PER_VERTEX, 12);
        GL20.glEnableVertexAttribArray(1);
        //Normal
        GL20.glVertexAttribPointer(2, 3, GL11.GL_BYTE, true, Vertex.BYTES_PER_VERTEX, 20);
        GL20.glEnableVertexAttribArray(2);
        //Color
        GL20.glVertexAttribPointer(3, 4, GL11.GL_UNSIGNED_BYTE, true, Vertex.BYTES_PER_VERTEX, 23);
        GL20.glEnableVertexAttribArray(3);

        vboInstance = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboInstance);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 16, GL15.GL_DYNAMIC_DRAW);

        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(4 + i, 4, GL_FLOAT, false, 64, i * 16);
            glEnableVertexAttribArray(4 + i);
            glVertexAttribDivisor(4 + i, 1);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);

        return new Vao(vao, GL11.GL_QUADS, 1, false);
    }


}
