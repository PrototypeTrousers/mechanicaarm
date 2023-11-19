package mechanicalarms.client.renderer;

import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

import static java.lang.Float.floatToRawIntBits;

public class FixedFunctionVbo extends Vbo {

    public FixedFunctionVbo(int vbo, int drawMode, int numVertices) {
        super(vbo, drawMode, numVertices);
    }

    public static FixedFunctionVbo setupVbo(Vertex[] vertices){
        ByteBuffer data = GLAllocation.createDirectByteBuffer(vertices.length * Vertex.BYTES_PER_VERTEX);
        for(Vertex v : vertices){
            data.putFloat(v.x);
            data.putFloat(v.y);
            data.putFloat(v.z);
            data.putFloat(v.u);
            data.putFloat(v.v);
            //Normals don't need as much precision as tex coords or positions
            data.put((byte)((int)(v.normalX*127)&0xFF));
            data.put((byte)((int)(v.normalY*127)&0xFF));
            data.put((byte)((int)(v.normalZ*127)&0xFF));
            //Neither do colors
            data.put((byte)(v.r*255));
            data.put((byte)(v.g*255));
            data.put((byte)(v.b*255));
            data.put((byte)(v.a*255));
        }

        data.rewind();
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        FixedFunctionVbo vbo = new FixedFunctionVbo(vboId, GL11.GL_QUADS, vertices.length);
        return vbo;
    }

    public static FixedFunctionVbo setupVbo(int[][][] vertices){
        ByteBuffer data = GLAllocation.createDirectByteBuffer(240 * Vertex.BYTES_PER_VERTEX);
        int v = 0;

        for (int part = 0; part < vertices.length; part++) {
            int[][] quadData = vertices[part];
            for (int i = 0; i < quadData.length; i++) {
                int[] vertexData = quadData[i];
                for (int k = 0; k < 4; ++k) {
                    // Getting the offset for the current arm_shader.vert.
                    int vertexIndex = k * 7;
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex]));
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex + 1]));
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex + 2]));
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex + 4]));
                    data.putFloat(Float.intBitsToFloat(vertexData[vertexIndex + 5]));
                    //Normals don't need as much precision as tex coords or positions
                    data.put((byte) ((int) (0 * 127) & 0xFF));
                    data.put((byte) ((int) (0 * 127) & 0xFF));
                    data.put((byte) ((int) (0 * 127) & 0xFF));
                    //Neither do colors
                    data.put((byte) (vertexData[vertexIndex + 3] * 255));
                    data.put((byte) (vertexData[vertexIndex + 3] * 255));
                    data.put((byte) (vertexData[vertexIndex + 3] * 255));
                    data.put((byte) (vertexData[vertexIndex + 3] * 255));
                    v++;
                }
            }
        }

        data.rewind();
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        FixedFunctionVbo vbo = new FixedFunctionVbo(vboId, GL11.GL_QUADS, v);
        return vbo;
    }

    @Override
    public void draw() {
        preDraw();
        //Draws the currently bound array buffer with the specified draw mode (quads in this case), from arm_shader.vert 0 to the number of vertices (the whole model).
        GL11.glDrawArrays(drawMode, 0, numVertices);
        postDraw();
    }

    private void preDraw(){
        //Bind the buffer to the array buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        //Tells opengl that we specificed our positions as 3 numbers stored as 3 floats, with a stride of the size of a arm_shader.vert and an offset of 0.
        GL11.glVertexPointer(3, GL11.GL_FLOAT, Vertex.BYTES_PER_VERTEX, 0);
        //Enables the position attribute
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        //Tells opengl that the texcoords is 2 numbers, stored as floats, with an offset of 12 (position is the first 12 bytes)
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, Vertex.BYTES_PER_VERTEX, 12);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        //Tells opengl that the normal (which is always 3 numbers) is stored as signed bytes, with an offset of 20 (arm_shader.vert bytes + the 8 texcoord bytes come first)
        GL11.glNormalPointer(GL11.GL_BYTE, Vertex.BYTES_PER_VERTEX, 20);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        //Tells opengl that color is 4 numbers, stored as an unsigned byte, and has an offset of 23 (arm_shader.vert bytes + texcoord bytes + the 3 normal bytes)
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, Vertex.BYTES_PER_VERTEX, 23);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
    }

    private void postDraw(){
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
