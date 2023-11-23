package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import mechanicalarms.common.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Vao {

    public int vaoId;
    public int drawMode;
    public int vertexCount;
    public boolean useElements;

    public static int vboInstance;

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

    public static Vao setupVertices(int[][][] vertices){
        IModel im;
        try {
            im = OBJLoader.INSTANCE.loadModel(ClientProxy.arm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LinkedHashSet<OBJModel.Face> f = ((OBJModel) im).getMatLib().getGroups().get("arm1").getFaces();
        ByteBuffer data = GLAllocation.createDirectByteBuffer(240 * Vertex.BYTES_PER_VERTEX);
        int v = 0;
        for (OBJModel.Face face : f) {
            for (OBJModel.Vertex vertex : face.getVertices()){
                data.putFloat(vertex.getPos().x);
                data.putFloat(vertex.getPos().y);
                data.putFloat(vertex.getPos().z);
                //U,V
                data.putFloat(vertex.getTextureCoordinate().u);
                data.putFloat(vertex.getTextureCoordinate().v);
                //Normals don't need as much precision as tex coords or positions
                data.put((byte) Float.floatToIntBits(vertex.getNormal().x));
                data.put((byte) Float.floatToIntBits(vertex.getNormal().y));
                data.put((byte) Float.floatToIntBits(vertex.getNormal().z));
                //Neither do colors

                data.put((byte) 255);
                data.put((byte) 255);
                data.put((byte) 255);
                data.put((byte) 255);
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

        return new Vao(vao, GL11.GL_QUADS, v, false);
    }


}
