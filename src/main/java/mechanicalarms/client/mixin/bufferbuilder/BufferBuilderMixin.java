package mechanicalarms.client.mixin.bufferbuilder;

import mechanicalarms.client.mixin.interfaces.IBufferBuilderMixin;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin implements IBufferBuilderMixin {
    @Shadow
    private IntBuffer rawIntBuffer;
    @Shadow
    private int vertexCount;
    @Shadow
    private VertexFormat vertexFormat;

    @Override
    public void putIntBulkData(int[] buffer) {
        this.growBuffer(buffer.length * 4 + 28);//Forge, fix MC-122110
        this.rawIntBuffer.position(this.getBufferSize());
        this.rawIntBuffer.put(buffer);
        this.vertexCount += buffer.length / 7;
    }

    @Shadow
    private void growBuffer(int increaseAmount) {

    }

    @Shadow
    private int getBufferSize() {
        return 0;
    }

}
