package mechanicalarms.client.renderer;

public class Vertex {

    //Pos, tex, normal, color
    public static final int BYTES_PER_VERTEX = 5 * 4 + 3 + 64;

    public float x;
    public float y;
    public float z;
    public float u;
    public float v;
    public byte normalX;
    public byte normalY;
    public byte normalZ;

    // mat4 transform
}
