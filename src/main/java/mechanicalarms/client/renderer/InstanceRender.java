package mechanicalarms.client.renderer;

import java.util.Map;

public class InstanceRender {

    static InstanceRender INSTANCE = new InstanceRender();

    Map<InstanceableModel, InstanceData> modelInstanceData;

    public void schedule(InstanceableModel item) {
        modelInstanceData.put(item, null);
    }


    public class InstanceData {

    }
}
