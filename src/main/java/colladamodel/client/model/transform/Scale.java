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

package colladamodel.client.model.transform;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Scale extends Transform {

    private final Vector3f vec;

    public Scale(String name, Vector3f vec) {
        super(name);
        this.vec = vec;
    }

    public Vector3f getVec() {
        return vec;
    }

    public void apply() {
        GL11.glScaled(vec.x, vec.y, vec.z);
    }
}
