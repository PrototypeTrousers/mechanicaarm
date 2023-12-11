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

import colladamodel.client.model.transform.Transform;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class Geometry implements Transformable {

    private String name = null;
    private final Map<String, Transform> transforms = new LinkedHashMap<>();
    private final List<Transformable> children = new ArrayList<>();
    private final List<Face> faces = new LinkedList<>();
    private final Transformable parent;

    public Geometry(Transformable parent) {
        this.parent = parent;
    }

    public Transformable getParent() {
        return parent;
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

    public Transform getTransform(String name) {
        return transforms.get(name);
    }

    public void addFace(Face face) {
        faces.add(face);
    }

    public void render(Tessellator tessellator) {
        GL11.glPushMatrix();

        for (Transform trans : transforms.values()) {
            trans.apply();
        }

        for (Face face : faces) {
            //face.render(tessellator);
        }

        GL11.glPopMatrix();
    }

    public void renderAnimation(Tessellator tessellator, double frame) {
        // TODO: Refactor
//		GL11.glPushMatrix();
//
//		for (Transform trans : transforms) {
//			trans.applyAnimation(frame);
//		}
//
//		for (Face face : faces) {
//			face.render(tessellator);
//		}
//
//		GL11.glPopMatrix();
    }

    public double getAnimationLength() {
        // TODO: Refactor
        return 0;
//		double animationLength = 0;
//		for (Transform trans : transforms) {
//			if (trans.getAnimationLength() > animationLength)
//				animationLength = trans.getAnimationLength();
//		}
//		return animationLength;
    }

    public List<Face> getFaces() {
        return faces;
    }
}
