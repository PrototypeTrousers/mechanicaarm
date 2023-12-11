package colladamodel.client.model;

import colladamodel.client.model.collada.ColladaModelLoader;
import colladamodel.client.model.collada.IModelAnimationCustom;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.HashMap;
import java.util.Map;

public class ModelManager implements IResourceManagerReloadListener {

    private static final Map<ResourceLocation, IModelAnimationCustom> models = new HashMap<ResourceLocation, IModelAnimationCustom>();

    public ModelManager() {
    }

    @Override
    public void onResourceManagerReload(IResourceManager var1) {
        for (ResourceLocation resource : models.keySet()) {
            try {
                models.put(resource, (IModelAnimationCustom) ColladaModelLoader.INSTANCE
                        .loadModel(resource));
            } catch (ModelLoaderRegistry.LoaderException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public IModelAnimationCustom getModel(ResourceLocation resource) {
        if (!models.containsKey(resource)) {
            try {
                models.put(resource, (IModelAnimationCustom) ColladaModelLoader.INSTANCE
                        .loadModel(resource));
            } catch (ModelLoaderRegistry.LoaderException e) {
                throw new RuntimeException(e);
            }
        }
        return models.get(resource);
    }

}
