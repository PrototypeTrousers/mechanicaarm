package colladamodel.client.model;

import org.lwjgl.util.vector.Matrix4f;

public class Bone {
    private final String name;
    private Matrix4f transform;

    Bone(String name, Matrix4f transform) {
        this.name = name;
        this.transform = transform;
    }

    public String getName() {
        return name;
    }
}
