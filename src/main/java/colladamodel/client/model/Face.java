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

package colladamodel.client.model;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Face {

	private Vector3f[] vertex;
	private Vector3f[] vertexNormals;
	private Vector3f[] vertexTexCoord;

	public Face() {
		vertex = null;
		vertexNormals = null;
		vertexTexCoord = null;
	}

//	public void render(Tessellator tessellator) {
//
//		tessellator.startDrawing(GL11.GL_POLYGON);
//
//		Vector3f faceNormal = calculateFaceNormal();
//		tessellator.setNormal((float) -faceNormal.x,
//				(float) -faceNormal.y, (float) -faceNormal.z);
//
//		float averageU = 0F;
//		float averageV = 0F;
//
//        for (Vector3f vector3f : vertexTexCoord) {
//            averageU += vector3f.x;
//            averageV += vector3f.y;
//        }
//
//		averageU = averageU / vertexTexCoord.length;
//		averageV = averageV / vertexTexCoord.length;
//
//		float offsetU, offsetV;
//
//		for (int i = 0; i < vertex.length; i++) {
//			offsetU = 0.0005F;
//			offsetV = 0.0005F;
//
//			if (vertexTexCoord[i].x > averageU) {
//				offsetU = -offsetU;
//			}
//			if (vertexTexCoord[i].y > averageV) {
//				offsetV = -offsetV;
//			}
//
//
//			tessellator.getBuffer().addVertexWithUV(vertex[i].x, vertex[i].y,
//					vertex[i].z, vertexTexCoord[i].x + offsetU, 1
//							- vertexTexCoord[i].y - offsetV);
//		}
//
//		tessellator.draw();
//	}

	private Vector3f calculateFaceNormal() {
		float sumX = 0;
		float sumY = 0;
		float sumZ = 0;
		for (int i = 0; i < vertexNormals.length; i++) {
			sumX += vertexNormals[i].x;
			sumY += vertexNormals[i].y;
			sumZ += vertexNormals[i].z;
		}
		return new Vector3f(sumX / vertexNormals.length, sumY
				/ vertexNormals.length, sumZ / vertexNormals.length);
	}

	public void setVertex(Vector3f[] vertex, Vector3f[] normal, Vector3f[] texCoords) {
		this.vertex = vertex;
		this.vertexNormals = normal;
		this.vertexTexCoord = texCoords;
	}
}
