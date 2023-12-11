package colladamodel.client.model;

import colladamodel.client.model.transform.Transform;

import java.util.LinkedHashMap;
import java.util.Map;

public class Bone implements Transformable {
    private String name;
    private final Map<String, Transform> transforms;

    public Bone(String name) {
        this.name = name;
        this.transforms = new LinkedHashMap<String, Transform>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addTransform(Transform transform) {
        this.transforms.put(transform.getName(), transform);
    }
}
