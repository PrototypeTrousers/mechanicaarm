package mechanicalarms.client.renderer;

import mechanicalarms.MechanicalArms;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Vao {

    public static int indirectBuffer;
    public static int boneBuffer;
    public static int lightBuffer;
    public static int vboInstance;

    public static int posBuffer;
    public static int normalBuffer;
    public static int texBuffer;

    public static ByteBuffer positionBuffer;
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
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        /*GltfModelReader reader = new GltfModelReader();
        GltfModel model;
        try {
            model = reader.readWithoutReferences(new BufferedInputStream(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(Tags.MODID, "models/block/arm.gltf")).getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);


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
                        positionBuffer = position.getBufferViewModel().getBufferViewData();
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, position.getBufferViewModel().getBufferViewData(), GL15.GL_STATIC_DRAW);
                        GL20.glVertexAttribPointer(0,
                                position.getElementType().getNumComponents(),
                                position.getComponentType(),
                                false,
                                position.getByteStride(),
                                position.getByteOffset());
                        GL20.glEnableVertexAttribArray(0);

                        AccessorModel texUAccessorModel = attributes.get("TEXCOORD_0");
                        texBuffer = GL15.glGenBuffers();
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBuffer);
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texUAccessorModel.getBufferViewModel().getBufferViewData(), GL15.GL_STATIC_DRAW);
                        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, true, 8, 0);
                        GL20.glEnableVertexAttribArray(1);


                        AccessorModel normal = attributes.get("NORMAL");
                        normalBuffer = GL15.glGenBuffers();
                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalBuffer);
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normal.getBufferViewModel().getBufferViewData(), GL15.GL_STATIC_DRAW);
                        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, true, 12, 0);
                        GL20.glEnableVertexAttribArray(2);
                        break;
                    }
                }
            }
        }

        */

        IModel im;
        try {
            im = OBJLoader.INSTANCE.loadModel(new ResourceLocation(MechanicalArms.MODID, "models/block/arm_basic.obj"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<OBJModel.Face> fl = new ArrayList<>();
        ((OBJModel) im).getMatLib().getGroups().values().forEach(g -> fl.addAll(g.getFaces()));
        ByteBuffer pos = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        ByteBuffer norm = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        ByteBuffer tex = GLAllocation.createDirectByteBuffer(2400000 * Vertex.BYTES_PER_VERTEX);
        int v = 0;
        for (OBJModel.Face face : fl) {
            for (OBJModel.Vertex vertex : face.getVertices()){
                pos.putFloat(vertex.getPos().x);
                pos.putFloat(vertex.getPos().y);
                pos.putFloat(vertex.getPos().z);
                //U,V
                tex.putFloat(vertex.getTextureCoordinate().u);
                tex.putFloat(vertex.getTextureCoordinate().v);
                //Normals don't need as much precision as tex coords or positions
                norm.put((byte) Float.floatToIntBits(vertex.getNormal().x));
                norm.put((byte) Float.floatToIntBits(vertex.getNormal().y));
                norm.put((byte) Float.floatToIntBits(vertex.getNormal().z));
                v++;
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

        for (int i = 0; i < 4; i++) {
            GL20.glVertexAttribPointer(4 + i, 4, GL11.GL_FLOAT, false, 16, i * 16);
            GL20.glEnableVertexAttribArray(4 + i);
            GL33.glVertexAttribDivisor(4 + i, 1);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        return new Vao(vao, GL11.GL_TRIANGLES, v, false);
    }
}
