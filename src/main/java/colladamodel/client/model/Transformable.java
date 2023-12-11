package colladamodel.client.model;

import colladamodel.client.model.transform.Transform;

public interface Transformable {
    String getName();

    void setName(String name);

    void addTransform(Transform transform);
}
