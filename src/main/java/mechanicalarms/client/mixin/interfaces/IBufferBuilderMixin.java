package mechanicalarms.client.mixin.interfaces;

public interface IBufferBuilderMixin {
    void putIntBulkData(int[] buffer);

    double getOffsetX();

    double getOffsetY();

    double getOffsetZ();
}
