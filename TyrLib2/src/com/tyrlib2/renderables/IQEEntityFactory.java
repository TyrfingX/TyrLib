package com.tyrlib2.renderables;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.Context;

import com.tyrlib2.materials.TexturedMaterial;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Texture;
import com.tyrlib2.renderer.TextureManager;

/**
 * Creates Entities by reading .iqe files
 * @author Sascha
 *
 */

public class IQEEntityFactory implements IEntityFactory {
	
	private static final String OBJECT = "mesh";
	private static final String VERTEX = "vp";
	private static final String UV_COORD = "vt";
	private static final String NORMAL = "vn";
	private static final String TRIANGLE = "fm";
	private static final String MATERIAL = "material";
	
	
	/** Holds the data for one triangle **/
	private class Triangle {
		short[] data = new short[3];
	}
	
	/** Contains data for creating a SubEntity **/
	private class SubEntityData {
		public String name;
		public String matName;
		public List<Triangle> triangles;
		private List<Vector3> pos;
		private List<Vector2> uv;
		private List<Vector3> normals;
		public SubEntityData(String name) {
			this.name = name;
			triangles = new ArrayList<Triangle>();
			pos = new ArrayList<Vector3>();
			uv = new ArrayList<Vector2>();
			normals = new ArrayList<Vector3>();
		}
	}
	
	
	/** Contains a SubEntity prototyp which contains data shared
	 * by all SubEntities of this type created with this factory
	 */
	private class SubEntityPrototype {
		public Mesh mesh;
		public TexturedMaterial material;
		public String name;
	}
	
	private List<SubEntityData> subEntityData;
	private SubEntityPrototype[] subEntityPrototypes;
	private SubEntityData currentDataBlock = null;
	private StringTokenizer tokenizer;
	private TexturedMaterial baseMaterial;
	private Map<String, TexturedMaterial> materials;

	
	public IQEEntityFactory(Context context, String fileName, TexturedMaterial baseMaterial) {
		
		this.baseMaterial = baseMaterial;
		
		subEntityData = new ArrayList<SubEntityData>();
		
		materials = new HashMap<String, TexturedMaterial>();
		
		// Due to efficiency reasons this class does not employ the file reader but rather parses the file directly
		try {
			InputStream inputStream = context.getResources().getAssets().open(fileName);
			
			// setup Bufferedreader
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			// Try to parse the file
			String line = null;
			while((line = in.readLine()) != null) {
				tokenizer = new StringTokenizer(line);
				if (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.equals(OBJECT)) {
						createNextSubEntity();
					} else if (token.equals(VERTEX)) {
						createVertex();
					} else if (token.equals(UV_COORD)) {
						createUVCoord();
					} else if (token.equals(NORMAL)) {
						createNormal();
					} else if (token.equals(TRIANGLE)) {
						createTriangle();
					} else if (token.equals(MATERIAL)) {
						createMaterial();
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load entity " + fileName + ".");
		}
		
		if (currentDataBlock != null) {
			subEntityData.add(currentDataBlock);
		}
		
		// now create the prototypes
		subEntityPrototypes = new SubEntityPrototype[subEntityData.size()];
		
		for (int i = 0; i < subEntityPrototypes.length; ++i) {
			SubEntityData data = subEntityData.get(i);
			SubEntityPrototype prototype = new SubEntityPrototype();
			subEntityPrototypes[i] = prototype;
			prototype.name = data.name;
			
			// Next create the vertex data
			int byteStride = baseMaterial.getByteStride();
			float[] vertexData = new float[byteStride * data.pos.size()];
			short[] drawOrder = new short[data.triangles.size() * 3];
			int bytePos = 0;
			
			for (int j = 0; j < data.pos.size(); ++j) {
				Vector3 pos = data.pos.get(j);
				vertexData[bytePos + baseMaterial.getPositionOffset() + 0] = pos.y;
				vertexData[bytePos + baseMaterial.getPositionOffset() + 1] = pos.x;
				vertexData[bytePos + baseMaterial.getPositionOffset() + 2] = pos.z;
				
				vertexData[bytePos + baseMaterial.getColorOffset() + 0] = 1;
				vertexData[bytePos + baseMaterial.getColorOffset() + 1] = 1;
				vertexData[bytePos + baseMaterial.getColorOffset() + 2] = 1;
				vertexData[bytePos + baseMaterial.getColorOffset() + 3] = 1;
				
				Vector3 normal = data.normals.get(j);
				vertexData[bytePos + baseMaterial.getNormalOffset() + 0] = normal.y;
				vertexData[bytePos + baseMaterial.getNormalOffset() + 1] = normal.x;
				vertexData[bytePos + baseMaterial.getNormalOffset() + 2] = normal.z;
				
				Vector2 uv = data.uv.get(j);
				vertexData[bytePos + baseMaterial.getUVOffset() + 0] = uv.x;
				vertexData[bytePos + baseMaterial.getUVOffset() + 1] = uv.y;
				
				bytePos += byteStride;
			}
			
			for (short j = 0; j < data.triangles.size(); ++j) {
				Triangle triangle = data.triangles.get(j);
				for (short k = 0; k < triangle.data.length; ++k) {
					drawOrder[j*3 + k] = triangle.data[k];
				}
			}
			
			Mesh mesh = new Mesh(vertexData, drawOrder);
			prototype.material = materials.get(data.matName);
			prototype.mesh = mesh;
		}
		
		// Clean up
		subEntityData.clear();
		currentDataBlock = null;
	}
	
	@Override
	public Entity create() {
		Entity entity = new Entity();
		for (int i = 0; i < subEntityPrototypes.length; ++i) {
			SubEntityPrototype prototype = subEntityPrototypes[i];
			SubEntity subEntity = new SubEntity(prototype.name, prototype.mesh, prototype.material);
			entity.addSubEntity(subEntity);
		}
		return entity;
	}

	
	/** 
	 * Creates a new subentity
	 */
	private void createNextSubEntity() {
		if (currentDataBlock != null) {
			subEntityData.add(currentDataBlock);
		}
		String name = tokenizer.nextToken();
		currentDataBlock = new SubEntityData(name);
	}
	
	/**
	 * Adds a vertex
	 */
	private void createVertex() {
		float x = Float.valueOf(tokenizer.nextToken());
		float y = Float.valueOf(tokenizer.nextToken());
		float z = Float.valueOf(tokenizer.nextToken());
		Vector3 vertex = new Vector3(x,y,z);
		currentDataBlock.pos.add(vertex);
	}
	
	/** 
	 * Creates a new UV Coordinate
	 */
	private void createUVCoord() {
		float u = Float.valueOf(tokenizer.nextToken());
		float v = Float.valueOf(tokenizer.nextToken());
		Vector2 uvCoord = new Vector2(u,v);
		currentDataBlock.uv.add(uvCoord);
	}
	
	/**
	 * Creates a new normal
	 */
	private void createNormal() {
		float x = Float.valueOf(tokenizer.nextToken());
		float y = Float.valueOf(tokenizer.nextToken());
		float z = Float.valueOf(tokenizer.nextToken());
		Vector3 normal = new Vector3(x,y,z);
		normal.normalize();
		currentDataBlock.normals.add(normal);
	}
	
	/** Creates a new triangle **/
	private void createTriangle() {
		Triangle triangle = new Triangle();
		for (int i = 0; i < 3; ++i) {
			short vertex = Short.valueOf(tokenizer.nextToken());
			triangle.data[i] = vertex;
		}
		currentDataBlock.triangles.add(triangle);
	}
	
	/** Create a new material **/
	private void createMaterial() {
		String matName = tokenizer.nextToken();
		matName = matName.substring(1, matName.length()-1); // get rid of the ""
		Texture texture = TextureManager.getInstance().getTexture(matName);
		TexturedMaterial mat = (TexturedMaterial) baseMaterial.copy();
		mat.setTexture(texture, matName);
		materials.put(matName, mat);
		currentDataBlock.matName = matName;
	}

}
