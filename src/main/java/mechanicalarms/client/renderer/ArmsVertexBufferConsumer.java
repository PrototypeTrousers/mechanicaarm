package mechanicalarms.client.renderer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class ArmsVertexBufferConsumer implements IVertexConsumer {
    private final BufferBuilder renderer;
    private final int[] quadData = new int[28];
    private int v = 0;
    private final BlockPos offset = BlockPos.ORIGIN;


    ArmsVertexBufferConsumer(BufferBuilder bufferBuilder) {
        this.renderer = bufferBuilder;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return renderer.getVertexFormat();
    }

    @Override
    public void setQuadTint(int tint) {

    }

    @Override
    public void setQuadOrientation(EnumFacing orientation) {

    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {

    }

    @Override
    public void setTexture(TextureAtlasSprite texture) {

    }

    @Override
    public void put(int e, float... data) {
        VertexFormat format = getVertexFormat();
        if (e == format.getElementCount() - 1) {
            v++;
            if (v == 4) {
                renderer.addVertexData(quadData);
                renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
                v = 0;
            }
        }
    }


    public void repack(int[] from, float[] middleStep, int[] to, VertexFormat formatFrom, int v, int e) {
        int length = Math.min(4, middleStep.length);
        VertexFormatElement fromElement = formatFrom.getElement(e);
        int vertexStartFrom = v * formatFrom.getSize() + formatFrom.getOffset(e);
        int countFrom = fromElement.getElementCount();

        VertexFormat formatTo = renderer.getVertexFormat();
        VertexFormatElement elementTo = formatTo.getElement(e);
        VertexFormatElement.EnumType typeTo = elementTo.getType();
        int sizeTo = typeTo.getSize();

        for (int i = 0; i < length; i++) {
            if (i < countFrom) {
                int pos = vertexStartFrom + sizeTo * i;
                int index = pos >> 2;
                to[index] = from[index];
            }
        }
    }

    public void putBakedQuad(BakedQuad quad) {
        this.setTexture(quad.getSprite());
        this.setQuadOrientation(quad.getFace());
        if (quad.hasTintIndex()) {
            this.setQuadTint(quad.getTintIndex());
        }
        this.setApplyDiffuseLighting(quad.shouldApplyDiffuseLighting());
        VertexFormat formatFrom = renderer.getVertexFormat();
        VertexFormat formatTo = quad.getFormat();
        int[] eMap = LightUtil.mapFormats(formatFrom, formatTo);

        float[] data = new float[4];

        int countFrom = formatFrom.getElementCount();
        int countTo = formatTo.getElementCount();

        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < countFrom; e++) {
                if (eMap[e] != countTo) {
                    repack(quad.getVertexData(), data, quadData, formatFrom, v, eMap[e]);
                    this.put(e);
                } else {
                    this.put(e);
                }
            }
        }
    }
}
