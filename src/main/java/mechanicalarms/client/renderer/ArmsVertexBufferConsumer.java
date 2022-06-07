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
    VertexFormat format = DefaultVertexFormats.BLOCK;


    ArmsVertexBufferConsumer(BufferBuilder bufferBuilder) {
        this.renderer = bufferBuilder;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return DefaultVertexFormats.BLOCK;
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
    public void put(int element, float... data) {

    }

    public void put(int e, int... data) {
        renderer.addVertexData(quadData);
        renderer.putPosition(offset.getX(), offset.getY(), offset.getZ());
    }

    public void repack(int[] from, int[] to, VertexFormat formatFrom, VertexFormat formatTo, int v, int e) {
        int length = Math.min(to.length, 4);

        VertexFormatElement element = formatFrom.getElement(e);
        int vertexStart = v * formatFrom.getSize() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.EnumType fromType = element.getType();

        VertexFormatElement elementTo = formatTo.getElement(e);
        VertexFormatElement.EnumType toType = elementTo.getType();

        int fromSize = fromType.getSize();

        int toSize = toType.getSize();
        int toMask = (256 << (8 * (toSize - 1))) - 1;

        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + fromSize * i;
                int index = pos >> 2;
                int offset = pos & 3;

                int posTo = vertexStart + toSize * i;
                int indexTo = posTo >> 2;
                int offsetTo = posTo & 3;

                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + fromSize - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= -1;

                to[indexTo] &= ~(toMask << (offsetTo * 8));
                to[indexTo] |= (((bits & toMask) << (offsetTo * 8)));
            } else {
                to[i] = 0;
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
        VertexFormat formatFrom = quad.getFormat();
        int[] eMap = LightUtil.mapFormats(formatFrom, this.format);
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < 4; e++) {
                if (eMap[e] != 5) {
                    repack(quad.getVertexData(), quadData, formatFrom, this.format, this.v, eMap[e]);
                    if (eMap[e] == 3) {
                        this.v++;
                        if (this.v == 4) {
                            this.put(e, quadData);
                            this.v = 0;
                        }
                    }
                } else {
                    this.put(e);
                }
            }
        }
    }
}
