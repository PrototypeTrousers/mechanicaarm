package colladamodel.client.model;

import colladamodel.client.model.transform.Transform;

import java.util.LinkedHashMap;
import java.util.Map;

public class Bone implements Transformable {
    private String name;
    Transformable parent;
    private final Map<String, Transform> transforms = new LinkedHashMap<>();

    public Bone(String name, Transformable parent) {
        this.name = name;
        this.parent = parent;
    }
    public Bone(String name) {
        this.name = name;
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
