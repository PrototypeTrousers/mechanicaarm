/**
 * Copyright (c) 2014 Hea3veN
 * <p>
 * This file is part of lib-colladamodel.
 * <p>
 * lib-colladamodel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * lib-colladamodel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with lib-colladamodel.  If not, see <http://www.gnu.org/licenses/>.
 */

package colladamodel.client.model.collada;

import colladamodel.client.model.Model;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class ColladaModelLoader implements ICustomModelLoader {
    public static ColladaModelLoader INSTANCE = new ColladaModelLoader();

    private IResourceManager manager;

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getPath().endsWith(".dae");
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.manager = resourceManager;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws ModelLoaderRegistry.LoaderException {
        IResource res;
        try {
            res = Minecraft.getMinecraft().getResourceManager()
                    .getResource(modelLocation);
        } catch (IOException e) {
            throw new ModelLoaderRegistry.LoaderException("IO Exception reading model format",
                    e);
        }
        return LoadFromStream(res.getInputStream());
    }


    private Model LoadFromStream(InputStream stream) throws ModelLoaderRegistry.LoaderException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            return LoadFromXml(builder.parse(stream));
        } catch (IOException e) {
            throw new ModelLoaderRegistry.LoaderException("IO Exception reading model format",
                    e);
        } catch (ParserConfigurationException e) {
            throw new ModelLoaderRegistry.LoaderException(
                    "Xml Parser Exception reading model format", e);
        } catch (SAXException e) {
            throw new ModelLoaderRegistry.LoaderException(
                    "Xml Parsing Exception reading model format", e);
        }
    }

    private Model LoadFromXml(Document doc) throws ModelLoaderRegistry.LoaderException {
        ColladaAsset asset = null;
        try {
            asset = new ColladaAsset(doc);
        } catch (ModelLoaderRegistry.LoaderException e) {
            throw new RuntimeException(e);
        }
        return asset.getModel(asset.getRootSceneId());
    }

}
