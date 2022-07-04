package mechanicalarms.client.mixin.renderer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.ByteBuffer;
import java.util.List;

@Mixin(WorldVertexBufferUploader.class)
public class WorldVertexBufferUploaderMixin {

    /**
     * @author PrototypeTrousers
     * @reason
     */
    @Overwrite
    public void draw(BufferBuilder bufferBuilderIn) {
        if (bufferBuilderIn.getVertexCount() > 0) {
            VertexFormat vertexformat = bufferBuilderIn.getVertexFormat();
            int i = vertexformat.getSize();
            ByteBuffer bytebuffer = bufferBuilderIn.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();

            for (int j = 0; j < list.size(); ++j) {
                VertexFormatElement vertexformatelement = list.get(j);
                bytebuffer.position(vertexformat.getOffset(j));
                // moved to VertexFormatElement.preDraw
                vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
            }

            GlStateManager.glDrawArrays(bufferBuilderIn.getDrawMode(), 0, bufferBuilderIn.getVertexCount());
            int i1 = 0;

            for (int j1 = list.size(); i1 < j1; ++i1) {
                // moved to VertexFormatElement.postDraw
                list.get(i1).getUsage().postDraw(vertexformat, i1, i, bytebuffer);
            }
        }

        bufferBuilderIn.reset();
    }

}
