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

package colladamodel.client.model.collada;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import colladamodel.client.model.Face;
import colladamodel.client.model.Geometry;
import colladamodel.client.model.Model;
import colladamodel.client.model.animation.IAnimable;
import colladamodel.client.model.animation.KeyFrame;
import colladamodel.client.model.interpolation.BezierInterpolation;
import colladamodel.client.model.interpolation.Interpolation;
import colladamodel.client.model.interpolation.LinearInterpolation;

import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import colladamodel.client.model.animation.Animation;
import colladamodel.client.model.animation.AnimationSampler;
import colladamodel.client.model.transform.Matrix;
import colladamodel.client.model.transform.Rotation;
import colladamodel.client.model.transform.Scale;
import colladamodel.client.model.transform.Transform;
import colladamodel.client.model.transform.Translation;

public class ColladaAsset {

	public String xAxis;
	public String zAxis;
	public String yAxis;

	private Element root;
	private XPath xpath = XPathFactory.newInstance().newXPath();

	public ColladaAsset(Document doc) throws ModelLoaderRegistry.LoaderException {
		this.root = doc.getDocumentElement();

		String upAxis = getXPathString("asset/up_axis");
		if (upAxis.equals("X_UP")) {
			xAxis = "Z";
			yAxis = "X";
			zAxis = "Y";
		} else if (upAxis.equals("Y_UP")) {
			xAxis = "X";
			yAxis = "Y";
			zAxis = "Z";
		} else if (upAxis.equals("Z_UP")) {
			xAxis = "Y";
			yAxis = "Z";
			zAxis = "X";
		} else
			throw new ModelLoaderRegistry.LoaderException("Invalid up axis configuration");

	}

	public ColladaAsset() {
	}

	private String getXPathString(String path) throws ModelLoaderRegistry.LoaderException {
		return getXPathString(root, path);
	}

	private String getXPathString(Element node, String path) throws ModelLoaderRegistry.LoaderException {
		try {
			return (String) xpath.evaluate(path, node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new ModelLoaderRegistry.LoaderException(
					"Could not get the string for the path '" + path + "'", e);
		}
	}

	private Element getXPathElement(String path) throws ModelLoaderRegistry.LoaderException {
		return getXPathElement(root, path);
	}

	private Element getXPathElement(Element node, String path) throws ModelLoaderRegistry.LoaderException {
		try {
			return (Element) xpath.evaluate(path, node, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new ModelLoaderRegistry.LoaderException(
					"Could not get the element for the path '" + path + "'", e);
		}
	}

	private Collection<Element> getXPathElementList(String path) throws ModelLoaderRegistry.LoaderException {
		return getXPathElementList(root, path);
	}

	private Collection<Element> getXPathElementList(Element node, String path) throws ModelLoaderRegistry.LoaderException {
		try {
			LinkedList<Element> result = new LinkedList<>();

			NodeList nodeList = (NodeList) xpath.evaluate(path, node, XPathConstants.NODESET);

			// Create a List to store nodes and their children
			List<Node> nodesList = new ArrayList<>();

			// Iterate through the nodes and their children recursively
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node n = nodeList.item(i);
				addNodeAndChildrenToList(n, nodesList);
			}

			// Now, 'nodesList' contains all nodes and their children
			for (Node n : nodesList) {
				NodeList children = n.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node child = children.item(j);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						result.add((Element) child);
					}
				}
			}

			return result;


		} catch (XPathExpressionException e) {
			throw new ModelLoaderRegistry.LoaderException(
					"Could not get the node list for the path '" + path + "'",
					e);
		}
	}

	private static void addNodeAndChildrenToList(Node node, List<Node> nodesList) {
		nodesList.add(node);

		NodeList children = node.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				addNodeAndChildrenToList(child, nodesList);
			}
		}
	}

	private Collection<Element> getXmlChildren(Element node) {
		LinkedList<Element> result = new LinkedList<Element>();
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				result.add((Element) nodes.item(i));
			}
		}
		return result;
	}

	private String parseURL(String url) {
		return url.substring(1);
	}

	private String[] splitData(String data) {
		return data.trim().split("\\s+");
	}

	private int[] splitDataInt(String data) {
		String[] dataSplit = splitData(data);
		int[] ret = new int[dataSplit.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Integer.parseInt(dataSplit[i]);
		}
		return ret;
	}

	private double[] splitDataDouble(String data) {
		String[] dataSplit = splitData(data);
		double[] ret = new double[dataSplit.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Double.parseDouble(dataSplit[i]);
		}
		return ret;
	}

	public String getRootSceneId() throws ModelLoaderRegistry.LoaderException {
		return parseURL(getXPathString("scene/instance_visual_scene/@url"));
	}

	public Model getModel(String id) throws ModelLoaderRegistry.LoaderException {
		Element sceneElem = getXPathElement(String.format(
				"library_visual_scenes/visual_scene[@id='%s']", id));
		return parseScene(sceneElem);
	}

	private Model parseScene(Element sceneElem) throws ModelLoaderRegistry.LoaderException {
		Model model = new Model();
		for (Element nodeElem : getXPathElementList(sceneElem, "node")) {
			parseSceneNode(model, nodeElem);
		}
		return model;
	}

	private void parseSceneNode(Model model, Element nodeElem) {

		String nodeType = nodeElem.getAttribute("type");
		String nodeId = nodeElem.getAttribute("id");
		String nodeName = nodeElem.getNodeName();

		try {
			if (nodeName.equals("instance_geometry")) {
				String geomURL = nodeElem.getAttribute("url");
				Geometry geom = getGeometry(parseURL(geomURL));
				geom.setName(((Element)nodeElem.getParentNode()).getAttribute("id"));

				for (Element child : getXmlChildren(nodeElem)) {
					Transform trans = null;
					String transId = null;
					if (child.getTagName().equals("translate")) {
						trans = parseTranslation(child);
						transId = child.getAttribute("sid");
					} else if (child.getTagName().equals("rotate")) {
						trans = parseRotation(child);
						transId = child.getAttribute("sid");
					} else if (child.getTagName().equals("scale")) {
						trans = parseScale(child);
						transId = child.getAttribute("sid");
					} else if (child.getTagName().equals("matrix")) {
						// TODO:
						trans = parseMatrix(child);
						transId = child.getAttribute("sid");
					}

					if (trans != null)
						geom.addTransform(trans);
				}

				model.addGeometry(geom);
			}

		} catch (ModelLoaderRegistry.LoaderException e) {
			throw new RuntimeException(e);
		}

	}

	private Translation parseTranslation(Element transElem) throws ModelLoaderRegistry.LoaderException {
		double[] transData = splitDataDouble(transElem.getTextContent());
		if (transData.length != 3)
			throw new ModelLoaderRegistry.LoaderException("Invalid translate data");

		return new Translation(transElem.getAttribute("sid"), toMinecraftCoords(
				transData[0], transData[1], transData[2]));
	}

	private Rotation parseRotation(Element rotElem) throws ModelLoaderRegistry.LoaderException {
		double[] rotData = splitDataDouble(rotElem.getTextContent());
		if (rotData.length != 4)
			throw new ModelLoaderRegistry.LoaderException("Invalid rotate data");

		return new Rotation(rotElem.getAttribute("sid"), toMinecraftCoords(
				rotData[0], rotData[1], rotData[2]), rotData[3]);
	}

	private Scale parseScale(Element scaleElem) throws ModelLoaderRegistry.LoaderException {
		double[] scaleData = splitDataDouble(scaleElem.getTextContent());
		if (scaleData.length != 3)
			throw new ModelLoaderRegistry.LoaderException("Invalid scale data");

		return new Scale(scaleElem.getAttribute("sid"), toMinecraftCoords(
				scaleData[0], scaleData[1], scaleData[2]));
	}

	private Matrix parseMatrix(Element matrixElem) throws ModelLoaderRegistry.LoaderException {
		double[] matrixData = splitDataDouble(matrixElem.getTextContent());
		if (matrixData.length != 16)
			throw new ModelLoaderRegistry.LoaderException("Invalid matrix data");

		double tmp = matrixData[7];
		matrixData[7] = matrixData[11];
		matrixData[11] = -tmp;

		ByteBuffer matrixBytes = ByteBuffer.allocateDirect(16 * 8);
		matrixBytes.order(ByteOrder.nativeOrder());
		matrixBytes.clear();
		DoubleBuffer matrix = matrixBytes.asDoubleBuffer();
		matrix.clear();
		for (int j = 0; j < 4; j++) {
			matrix.put(matrixData[j]);
			matrix.put(matrixData[j + 4]);
			matrix.put(matrixData[j + 8]);
			matrix.put(matrixData[j + 12]);
		}
		return new Matrix(matrixElem.getAttribute("id"),
				toMinecraftCoords(matrix));
	}

	public Geometry getGeometry(String id) {
		Element geomElem = null;
		try {
			geomElem = getXPathElement(String.format(
					"library_geometries/geometry[@id='%s']", id));
			return parseGeometry(geomElem);
		} catch (ModelLoaderRegistry.LoaderException e) {
			throw new RuntimeException(e);
		}
	}

	private Geometry parseGeometry(Element geomElem) throws ModelLoaderRegistry.LoaderException {
		Geometry geom = new Geometry();
		for (Element meshElem : getXPathElementList(geomElem, "mesh")) {
			parseMesh(geom, meshElem);
		}
		return geom;
	}

	private void parseMesh(Geometry geom, Element meshElem) {
		try {
			for (Element child : getXmlChildren(meshElem)) {
				if (child.getTagName().equals("triangles")) {
					parseMeshTriangles(geom, meshElem, child);
				} else if (child.getNodeName().equals("polylist")) {
					parseMeshPolylist(geom, meshElem, child);
				} else if (child.getNodeName().equals("polygons")) {
					parseMeshPolygons(geom, meshElem, child);
				}
			}
		} catch (ModelLoaderRegistry.LoaderException e) {
            throw new RuntimeException(e);
        }
    }

	private void parseMeshTriangles(Geometry geom, Element meshElem,
			Element triElem) throws ModelLoaderRegistry.LoaderException {
		ColladaSource[] dataSrcs = parseMeshInputSources(meshElem, triElem);

		int count = Integer.parseInt(triElem.getAttribute("count"));
		int[] refs = splitDataInt(getXPathElement(triElem, "p")
				.getTextContent());
		if (refs.length != (count * 9))
			throw new ModelLoaderRegistry.LoaderException("Wrong number of data elements");

		for (int q = 0; q < count; q++) {
			Vector3f[] vertex = new Vector3f[3];
			Vector3f[] normal = new Vector3f[3];
			Vector3f[] texCoords = new Vector3f[3];
			for (int r = 0; r < 3; r++) {
				vertex[r] = toMinecraftCoords(dataSrcs[0].getVec3(refs[q * 9
						+ r * 3], "X", "Y", "Z"));
				normal[r] = toMinecraftCoords(dataSrcs[1].getVec3(refs[q * 9
						+ r * 3 + 1], "X", "Y", "Z"));
				texCoords[r] = dataSrcs[2].getVec2(refs[q * 9 + r * 3 + 2],
						"S", "T");
			}
			Face poly = new Face();
			poly.setVertex(vertex, normal, texCoords);
			geom.addFace(poly);
		}
	}

	private ColladaSource[] parseMeshInputSources(Element meshElem,
			Element defElem) {
		try {
			ColladaSource[] dataSrcs = new ColladaSource[3];
			String verticesId = parseURL(getXPathString(defElem,
					"input[@semantic='VERTEX']/@source"));
			String srcId = parseURL(getXPathString(meshElem,
					String.format("vertices[@id='%s']/input/@source", verticesId)));
			dataSrcs[0] = parseSource(getXPathElement(meshElem,
					String.format("source[@id='%s']", srcId)));

			String normalsId = parseURL(getXPathString(defElem,
					"input[@semantic='NORMAL']/@source"));
			dataSrcs[1] = parseSource(getXPathElement(meshElem,
					String.format("source[@id='%s']", normalsId)));

			String texcoordId = parseURL(getXPathString(defElem,
					"input[@semantic='TEXCOORD']/@source"));
			dataSrcs[2] = parseSource(getXPathElement(meshElem,
					String.format("source[@id='%s']", texcoordId)));
			return dataSrcs;
		} catch (ModelLoaderRegistry.LoaderException e) {
            throw new RuntimeException(e);
        }

    }

	private void parseMeshPolylist(Geometry geom, Element meshElem,
			Element polylistElem) throws ModelLoaderRegistry.LoaderException {
		ColladaSource[] dataSrcs = parseMeshInputSources(meshElem, polylistElem);

		int count = Integer.parseInt(polylistElem.getAttribute("count"));
		int[] vcount = splitDataInt(getXPathElement(polylistElem, "vcount")
				.getTextContent());
		int[] refs = splitDataInt(getXPathElement(polylistElem, "p")
				.getTextContent());
		if (vcount.length != count)
			throw new ModelLoaderRegistry.LoaderException("Wrong number of data elements");

		int p = 0;
		for (int q = 0; q < vcount.length; q++) {
			Vector3f[] vertex = new Vector3f[vcount[q]];
			Vector3f[] normal = new Vector3f[vcount[q]];
			Vector3f[] texCoords = new Vector3f[vcount[q]];
			for (int r = 0; r < vcount[q]; r++) {
				vertex[r] = toMinecraftCoords(dataSrcs[0].getVec3(refs[p * 3],
						"X", "Y", "Z"));
				normal[r] = toMinecraftCoords(dataSrcs[1].getVec3(
						refs[p * 3 + 1], "X", "Y", "Z"));
				texCoords[r] = dataSrcs[2].getVec2(refs[p * 3 + 2], "S", "T");
				p++;
			}
			Face poly = new Face();
			poly.setVertex(vertex, normal, texCoords);
			geom.addFace(poly);
		}
	}

	private void parseMeshPolygons(Geometry geom, Element meshElem,
			Element polyElem) throws ModelLoaderRegistry.LoaderException {
		ColladaSource[] dataSrcs = parseMeshInputSources(meshElem, polyElem);

		int count = Integer.parseInt(polyElem.getAttribute("count"));
		Collection<Element> polysData = getXPathElementList(polyElem, "p");
		if (polysData.size() != count)
			throw new ModelLoaderRegistry.LoaderException("Wrong number of data elements");

		for (Element pElem : polysData) {
			int[] refs = splitDataInt(pElem.getTextContent());
			Vector3f[] vertex = new Vector3f[refs.length / 3];
			Vector3f[] normal = new Vector3f[refs.length / 3];
			Vector3f[] texCoords = new Vector3f[refs.length / 3];
			for (int r = 0; r < refs.length / 3; r++) {
				vertex[r] = toMinecraftCoords(dataSrcs[0].getVec3(refs[r * 3],
						"X", "Y", "Z"));
				normal[r] = toMinecraftCoords(dataSrcs[1].getVec3(
						refs[r * 3 + 1], "X", "Y", "Z"));
				texCoords[r] = dataSrcs[2].getVec2(refs[r * 3 + 2], "S", "T");
			}
			Face poly = new Face();
			poly.setVertex(vertex, normal, texCoords);
			geom.addFace(poly);
		}

	}

	public ColladaSource parseSource(Element srcElem) throws ModelLoaderRegistry.LoaderException {
		String id = srcElem.getAttribute("id");

		Element data_array = getXPathElement(srcElem, "float_array");
		if (data_array == null) {
			data_array = getXPathElement(srcElem, "Name_array");
		}
		if (data_array == null)
			throw new ModelLoaderRegistry.LoaderException(
					"Could not find the data array for the source");

		int data_count;
		try {
			data_count = Integer.parseInt(data_array.getAttribute("count"));
		} catch (NumberFormatException ex) {
			throw new ModelLoaderRegistry.LoaderException(
					"Could not parse the count attribute of the <float_array>",
					ex);
		}

		float[] float_data = null;
		String[] name_data = null;
		if (data_array.getNodeName().equals("float_array"))
			float_data = new float[data_count];
		else if (data_array.getNodeName().equals("Name_array"))
			name_data = new String[data_count];

		int i = 0;
		String data_string = data_array.getTextContent();
		for (String val : splitData(data_string)) {
			if (data_array.getNodeName().equals("float_array"))
				float_data[i] = Float.parseFloat(val);
			else if (data_array.getNodeName().equals("Name_array"))
				name_data[i] = val;
			i++;
			if (i > data_count)
				throw new ModelLoaderRegistry.LoaderException("Too many values in the data");
		}
		if (i < data_count - 1)
			throw new ModelLoaderRegistry.LoaderException("Not enough values in the data");

		Element accessorNode = getXPathElement(srcElem,
				"technique_common/accessor");
		int count = 0;
		int stride = 1;
		try {
			count = Integer.parseInt(accessorNode.getAttribute("count"));
			if (!accessorNode.getAttribute("stride").isEmpty())
				stride = Integer.parseInt(accessorNode.getAttribute("stride"));
		} catch (NumberFormatException ex) {
			throw new ModelLoaderRegistry.LoaderException(
					"Could not parse the count attribute of the <float_array>",
					ex);
		}

		ColladaSource source = null;

		Collection<Element> paramElems = getXPathElementList(accessorNode,
				"param");
		if (stride == 1) {
			Element paramElem = paramElems.iterator().next();
			if (paramElem.getAttribute("type").equals("float4x4")) {
				float[][] float4x4_data = new float[count][16];
				for (int j = 0; j < count; j++) {
					for (int k = 0; k < 16; k++) {
						float4x4_data[j][k] = float_data[j * 16 + k];
					}

				}
				source = new ColladaSource(id, paramElem.getAttribute("name"),
						float4x4_data);
			} else if (paramElem.getAttribute("type").equals("name")) {
				source = new ColladaSource(id, paramElem.getAttribute("name"),
						name_data);
			}
		}
		if (source == null) {
			String[] params = new String[paramElems.size()];
			i = 0;
			for (Element paramElem : paramElems) {
				params[i++] = paramElem.getAttribute("name");
			}
			source = new ColladaSource(id, params, stride, float_data);
		}
		return source;
	}

	private Animation parseAnimation(Element animElem) throws ModelLoaderRegistry.LoaderException {
		List<IAnimable> children = new LinkedList<IAnimable>();
		Element channelNode = getXPathElement(animElem, "channel");
		if (channelNode != null) {
			String samplerId = parseURL(channelNode.getAttribute("source"));
			Element samplerElem = getXPathElement(animElem,
					String.format("sampler[@id='%s']", samplerId));
			HashMap<String, ColladaSource> sources = parseAnimationInputSources(
					animElem, samplerElem);

			ColladaSource outputSource = sources.get("OUTPUT");
			List<KeyFrame> frames = new LinkedList<KeyFrame>();
			for (int i = 0; i < sources.get("INPUT").getCount(); i++) {
				int frame = (int) Math.floor(sources.get("INPUT").getDouble(
						"TIME", i));
				String interpName = sources.get("INTERPOLATION")
						.getString(0, i);
				Interpolation interp = null;
				if (interpName.equals("LINEAR")) {

					interp = new LinearInterpolation();
				} else if (interpName.equals("BEZIER")) {
					if (i + 1 < sources.get("INPUT").getCount())
						interp = new BezierInterpolation(sources.get(
								"OUT_TANGENT").getDouble("Y", i), sources.get(
								"IN_TANGENT").getDouble("Y", i + 1));
					else
						interp = new LinearInterpolation();
				} else {
					throw new ModelLoaderRegistry.LoaderException(String.format(
							"Invalid interpolation method %s", interpName));
				}
				if (outputSource.getType() == ColladaSourceType.FLOAT) {
					KeyFrame keyFrame = new KeyFrame(frame,
							outputSource.getDouble(0, i), interp);
					frames.add(keyFrame);
				} else if (outputSource.getType() == ColladaSourceType.FLOAT4x4) {
					KeyFrame keyFrame = new KeyFrame(frame,
							outputSource.getFloat4x4(0, i), interp);
					frames.add(keyFrame);
				}
			}

			String[] targetParts = channelNode.getAttribute("target").split(
					"[/.]");
			AnimationSampler sampler = new AnimationSampler(targetParts[0],
					targetParts[1], (targetParts.length == 3) ? targetParts[2]
							: null, frames);
			children.add(sampler);
		}
		for (Element subAnimationElem : getXPathElementList(animElem,
				"animation")) {
			children.add(parseAnimation(subAnimationElem));
		}

		Animation animation = new Animation(animElem.getAttribute("id"),
				children);
		return animation;
	}

	private HashMap<String, ColladaSource> parseAnimationInputSources(
			Element animElem, Element samplerElem) throws ModelLoaderRegistry.LoaderException {
		HashMap<String, ColladaSource> sources = new HashMap<String, ColladaSource>();
		for (Element inputElem : getXPathElementList(samplerElem, "input")) {
			sources.put(
					inputElem.getAttribute("semantic"),
					parseSource(getXPathElement(animElem, String.format(
							"source[@id='%s']",
							parseURL(inputElem.getAttribute("source"))))));
		}
		return sources;
	}

	private String toMinecraftParam(String param) {
		if (param.equals(xAxis))
			return "X";
		else if (param.equals(yAxis))
			return "Y";
		else if (param.equals(zAxis))
			return "Z";
		else
			return param;
	}

	public Vector3f toMinecraftCoords(double x, double y, double z) {
		return toMinecraftCoords(new Vector3f((float) x, (float) y, (float) z));
	}

	public Vector3f toMinecraftCoords(Vector3f vec) {
		if (yAxis.equals("X"))
			return new Vector3f(vec.z, vec.x, vec.y);
		else if (yAxis.equals("Y"))
			return new Vector3f(vec.x, vec.y, vec.z);
		else if (yAxis.equals("Z"))
			return new Vector3f(vec.y, vec.z, vec.x);
		else
			return null;
	}

	public DoubleBuffer toMinecraftCoords(DoubleBuffer matrix) {
		return matrix;
	}
}
