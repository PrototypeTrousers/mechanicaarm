/**
 * 
 * Copyright (c) 2014 Hea3veN
 * 
 *  This file is part of lib-colladamodel.
 *
 *  lib-colladamodel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  lib-colladamodel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with lib-colladamodel.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package colladamodel.client.model.transform;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Rotation extends Transform {

	private Vector3f vec;
	private double angle;

	public Rotation(String name, Vector3f vec, double angle) {
		super(name);
		this.vec = vec;
		this.angle = angle;
	}

	public Vector3f getVec() {
		return vec;
	}

	public void apply() {
		GL11.glRotated(angle, vec.x, vec.y, vec.z);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}
}
