package colladamodel.client.model.animation;

import java.util.List;

public class Animation implements IAnimable {
    private final String name;
    private final List<IAnimable> children;

    public Animation(String name, List<IAnimable> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }
}
