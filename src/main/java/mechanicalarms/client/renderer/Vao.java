package mechanicalarms.client.renderer;

import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

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
        ByteBuffer data = GLAllocation.createDirectByteBuffer(240 * Vertex.BYTES_PER_VERTEX);
        int v = 0;

        for (int part = 0; part < vertices.length; part++) {
            int[][] quadData = vertices[part];
            for (int i = 0; i < quadData.length; i++) {
                int[] vertexData = quadData[i];
                for (int k = 0; k < 4; ++k) {
                    // Getting the offset for the current arm_shader.vert.
                    int vertexIndex = k * 7;
                    //POS X,Y,Z
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex]));
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex + 1]));
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex + 2]));
                    //U,V
                    data.putFloat(vertexData[vertexIndex + 4]);
                    data.putFloat(vertexData[vertexIndex + 5]);
                    //Normals don't need as much precision as tex coords or positions
                    data.put((byte) ((int) (1 * 127) & 0xFF));
                    data.put((byte) ((int) (1 * 127) & 0xFF));
                    data.put((byte) ((int) (1 * 127) & 0xFF));
                    //Neither do colors
                    data.put((byte) ((vertexData[vertexIndex + 3] >> 16) & 0xFF));
                    data.put((byte) ((vertexData[vertexIndex + 3] >> 8) & 0xFF));
                    data.put((byte) (vertexData[vertexIndex + 3] & 0xFF));
                    data.put((byte) ((vertexData[vertexIndex + 3] >> 24) & 0xFF));
                    data.putFloat(1);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(1);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(1);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(0);
                    data.putFloat(1);
                    v++;
                }
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
