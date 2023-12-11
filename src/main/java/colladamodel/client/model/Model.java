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

package colladamodel.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.*;
import java.util.function.Function;

public class Model implements IModel {

    private final Map<String, Geometry> geometries;
    private final Map<String, Bone> bones;

    public Model() {
        geometries = new HashMap<>();
        bones = new HashMap<>();
    }

    public void addGeometry(Geometry geom) {
        geometries.put(geom.getName(), geom);
    }

    public void addTransformable(Transformable transformable) {
        if (transformable instanceof Geometry geometry) {
            geometries.put(transformable.getName(), geometry);
        } else if ( transformable instanceof Bone bone) {
            bones.put(transformable.getName(), bone);
        }
    }

    public void addBone(Bone bone) {
        bones.put(bone.getName(), bone);
    }

    public Map<String, Bone> getBones() {
        return bones;
    }

    public Geometry getGeometry(String geomId) {
        return geometries.get(geomId);
    }

    public Map<String, Geometry> getGeometries() {
        return Collections.unmodifiableMap(geometries);
    }

    public void renderAll() {
        Tessellator tessellator = Tessellator.getInstance();

        for (Geometry geom : geometries.values()) {
            geom.render(tessellator);
        }
    }

    public void renderOnly(String... geometriesNames) {
        Tessellator tessellator = Tessellator.getInstance();
        for (String geometryName : geometriesNames) {
            geometries.get(geometryName).render(tessellator);
        }
    }

    public void renderPart(String partName) {
        Tessellator tessellator = Tessellator.getInstance();
        geometries.get(partName).render(tessellator);
    }

    public void renderAllExcept(String... excludedGroupNames) {
        Set<String> excludedSet = new HashSet<String>(
                Arrays.asList(excludedGroupNames));
        Tessellator tessellator = Tessellator.getInstance();
        for (Geometry geometry : geometries.values()) {
            if (!excludedSet.contains(geometry.getName()))
                geometry.render(tessellator);
        }

    }

    public void renderAnimationAll(double time) {
        Tessellator tessellator = Tessellator.getInstance();
        for (Geometry geom : geometries.values()) {
            geom.renderAnimation(tessellator, time);
        }
    }

    public void renderAnimationOnly(double time, String... geometriesNames) {
        Tessellator tessellator = Tessellator.getInstance();
        for (String geometryName : geometriesNames) {
            geometries.get(geometryName).renderAnimation(tessellator, time);
        }
    }

    public void renderAnimationPart(double time, String partName) {
        Tessellator tessellator = Tessellator.getInstance();
        geometries.get(partName).renderAnimation(tessellator, time);
    }

    public void renderAnimationAllExcept(double time,
                                         String... excludedGroupNames) {
        Set<String> excludedSet = new HashSet<String>(
                Arrays.asList(excludedGroupNames));
        Tessellator tessellator = Tessellator.getInstance();
        for (Geometry geometry : geometries.values()) {
            if (!excludedSet.contains(geometry.getName()))
                geometry.renderAnimation(tessellator, time);
        }
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return null;
    }
}
