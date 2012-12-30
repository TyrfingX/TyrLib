package com.tyrlib2.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.tyrlib.animation.Skeleton;
import com.tyrlib2.materials.DefaultMaterial3;
import com.tyrlib2.renderables.Entity;
import com.tyrlib2.renderables.SubEntity;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Texture;
import com.tyrlib2.renderer.TextureManager;

public class IQMEntityFactory implements IEntityFactory {
	
	private static final int HEADER_TXT_SIZE = 16;
	private static final int HEADER_SIZE = 27*4;
	private static final int SUBENTITY_SIZE = 6*4;
	private static final int VERTEX_ARRAY_SIZE = 5*4;
	
	private class Header {
	    int version;
	    int fileSize;
	    int flags;
	    int num_text, ofs_text;
	    int num_meshes, ofs_meshes;
	    int num_vertexarrays, num_vertexes, ofs_vertexarrays;
	    int num_triangles, ofs_triangles, ofs_adjacency;
	    int num_joints, ofs_joints;
	    int num_poses, ofs_poses;
	    int num_anims, ofs_anims;
	    int num_frames, num_framechannels, ofs_frames, ofs_bounds;
	    int num_comment, ofs_comment;
	    int num_extensions, ofs_extensions; 
	    
	    public Header(byte[] buffer) {
	    	
	    	int pos = 0;
	    	
	    	version = toInt(buffer, (pos++)*4);
	    	fileSize = toInt(buffer, (pos++)*4);
	    	flags = toInt(buffer, (pos++)*4);
	    	
	    	num_text = toInt(buffer, (pos++)*4);
	    	ofs_text = toInt(buffer, (pos++)*4);
	    	
	    	num_meshes = toInt(buffer, (pos++)*4);
	    	ofs_meshes = toInt(buffer, (pos++)*4);
	    	
	    	num_vertexarrays = toInt(buffer, (pos++)*4);
	    	num_vertexes = toInt(buffer, (pos++)*4);
	    	ofs_vertexarrays = toInt(buffer, (pos++)*4);
	    	
	    	num_triangles = toInt(buffer, (pos++)*4);
	    	ofs_triangles = toInt(buffer, (pos++)*4);
	    	ofs_adjacency = toInt(buffer, (pos++)*4);
	    	
	    	num_joints = toInt(buffer, (pos++)*4);
	    	ofs_joints = toInt(buffer, (pos++)*4);
	    	
	    	num_poses = toInt(buffer, (pos++)*4);
	    	ofs_poses = toInt(buffer, (pos++)*4);
	    	
	    	num_anims = toInt(buffer, (pos++)*4);
	    	ofs_anims = toInt(buffer, (pos++)*4);
	    	
	    	num_frames = toInt(buffer, (pos++)*4);
	    	num_framechannels = toInt(buffer, (pos++)*4);
	    	ofs_frames = toInt(buffer, (pos++)*4);
	    	ofs_bounds = toInt(buffer, (pos++)*4);
	    	
	    	num_comment = toInt(buffer, (pos++)*4);
	    	ofs_comment = toInt(buffer, (pos++)*4);
	    	
	    	num_extensions = toInt(buffer, (pos++)*4);
	    	ofs_extensions = toInt(buffer, (pos++)*4);
	    }
	}
	
	private class SubEntityData {
		float[] vertexData;
		float[] boneData;
		short[] triangleData;
		String material;
		String name;
		
		int firstVertexIndex;
		int firstTriangleIndex;
		
		int countVertices;
		int countTriangles;
	}
	
	private class EntityData {
		SubEntityData[] subEntities;
	}
	
	private class SubEntityPrototype {
		Mesh mesh;
		DefaultMaterial3 material;
		String name;
	}
	
	private class EntityPrototype {
		SubEntityPrototype[] subEntityPrototypes;
	}
	
	private class VertexArray {
		int type; // 0 = POSITION, 1 = TEXCOORD, 2 = NORMAL, 3 = TANGENT, 4 = BLENDINDEXES, 5 = BLENDWEIGHS, 6 = COLOR
		int flags;
		int format;
		int size;
		int offset;
	}
	
	private String fileName;
	private BufferedInputStream in;
	private int readBytes;
	private EntityData entityData;
	private List<String> texts;
	private DefaultMaterial3 baseMaterial;
	private VertexArray[] vertexArrays;
	
	private Header header;
	
	private EntityPrototype entityPrototype;
	
	
	public IQMEntityFactory(Context context, String fileName, DefaultMaterial3 baseMaterial) {
		
		this.fileName = fileName;
		this.baseMaterial = baseMaterial;
		
		entityData = new EntityData();
		
		try {
			InputStream inputStream = context.getResources().getAssets().open(fileName);
			in = new BufferedInputStream(inputStream);
			
			readHeader();
			readTexts();
			readSubEntities();
			readVertexArrays();
			readVertices();
			readFaces();
			
		} catch (IOException e) {
			throw new RuntimeException("Failed to load entity " + fileName + ".");
		}
		
		entityPrototype = new EntityPrototype();
		entityPrototype.subEntityPrototypes = new SubEntityPrototype[entityData.subEntities.length];
		
		for (int i = 0; i < entityData.subEntities.length; ++i) {
			SubEntityData data = entityData.subEntities[i];
			SubEntityPrototype p = new SubEntityPrototype();
			entityPrototype.subEntityPrototypes[i] = p;
			
			p.mesh = new Mesh(data.vertexData, data.triangleData, data.countVertices);
			p.material = (DefaultMaterial3) baseMaterial.copy();
			Texture texture = TextureManager.getInstance().getTexture(data.material);
			p.material.setTexture(texture, data.material);
		}
		
		entityData = null;
	}
	
	@Override
	public Entity create() {
		Entity entity = new Entity();
		for (int i = 0; i < entityPrototype.subEntityPrototypes.length; ++i) {
			// Use the prototypes to create the actual sub entities
			SubEntityPrototype prototype = entityPrototype.subEntityPrototypes[i];
			SubEntity subEntity = new SubEntity(prototype.name, prototype.mesh, prototype.material);
			entity.addSubEntity(subEntity);
		}
		
		Skeleton skeleton = new Skeleton();
		entity.setSkeleton(skeleton);
		
		return entity;
	}
	
	private void readHeader() throws IOException {
		byte[] buffer = new byte[HEADER_TXT_SIZE];
		readBytes += in.read(buffer);
		
		String headerText = new String(buffer, "US-ASCII");
		
		if (!headerText.equals("INTERQUAKEMODEL\0")) throw new RuntimeException("File " + fileName + " is not a valid IQM file!");
	
		buffer = new byte[HEADER_SIZE];
		readBytes += in.read(buffer);
		header = new Header(buffer);
		
	}
	
	private void readSubEntities() throws IOException {
		entityData.subEntities = new SubEntityData[header.num_meshes];
		
		for (int i = 0; i < header.num_meshes; ++i) {
			// create all subentities
			
			SubEntityData subEntity = new SubEntityData();
			byte[] buffer = new byte[SUBENTITY_SIZE];
			readBytes += in.read(buffer);
			
			int pos = 0;
			int nameTextIndex = toInt(buffer, (pos++)*4);
			int materialTextIndex = toInt(buffer, (pos++)*4);
			int firstVertex = toInt(buffer, (pos++)*4);
			int numVertices = toInt(buffer, (pos++)*4);
			int firstTriangle = toInt(buffer, (pos++)*4);
			int numTriangles = toInt(buffer, (pos++)*4);
			
			subEntity.name = texts.get(nameTextIndex);
			subEntity.material = texts.get(nameTextIndex+1);
			subEntity.firstTriangleIndex = firstTriangle;
			subEntity.firstVertexIndex = firstVertex;
			subEntity.countTriangles = numTriangles;
			subEntity.countVertices = numVertices;
			
			subEntity.vertexData = new float[numVertices * baseMaterial.getByteStride()];
			subEntity.triangleData = new short[numTriangles * 3];
			subEntity.boneData = new float[numVertices * Mesh.BONE_BYTE_STRIDE];
			
			entityData.subEntities[i] = subEntity;
		}
	}
	
	private void readVertexArrays() throws IOException {
		
		vertexArrays = new VertexArray[header.num_vertexarrays];
		
		for (int i = 0; i < header.num_vertexarrays; ++i) {
			byte[] buffer = new byte[VERTEX_ARRAY_SIZE];
			readBytes += in.read(buffer);
			
			vertexArrays[i] = new VertexArray();
			int pos = 0;
			vertexArrays[i].type = toInt(buffer, (pos++)*4);
			vertexArrays[i].flags = toInt(buffer, (pos++)*4);
			vertexArrays[i].format = toInt(buffer, (pos++)*4);
			vertexArrays[i].size = toInt(buffer, (pos++)*4);
			vertexArrays[i].offset = toInt(buffer, (pos++)*4);
		}
		

	}
	
	private void readVertices() throws IOException {
		
		boolean noColor = true;
		
		for (int i = 0; i < header.num_vertexarrays; ++i) {
			
			VertexArray vertexArray = vertexArrays[i];
			
			for (int j = 0; j < entityData.subEntities.length; ++j) {
				SubEntityData subEntity = entityData.subEntities[j];
				// These are the vertices belonging to this subentity
				
				int componentSize = getVertexArrayComponentSize(vertexArray);
				
				byte[] buffer = new byte[subEntity.countVertices*vertexArray.size*componentSize];
				readBytes += in.read(buffer);
				
				int pos = 0;
				int byteStride = baseMaterial.getByteStride();
				int offset;
				
				switch (vertexArray.type) {
				case 0: // This is a position
					offset = baseMaterial.getPositionOffset();
					for (int k = 0; k < subEntity.countVertices; ++k) {
						subEntity.vertexData[k*byteStride + offset + 0] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 1] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 2] = toFloat(buffer, (pos++)*4);
					}
					
					break;
				case 1: // This is a texture coordinate
					offset = baseMaterial.getUVOffset();
					for (int k = 0; k < subEntity.countVertices; ++k) {
						subEntity.vertexData[k*byteStride + offset + 0] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 1] = toFloat(buffer, (pos++)*4);
					}
					
					break;
				case 2: // This is a normal
					offset = baseMaterial.getNormalOffset();
					for (int k = 0; k < subEntity.countVertices; ++k) {
						subEntity.vertexData[k*byteStride + offset + 0] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 1] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 2] = toFloat(buffer, (pos++)*4);
					}
					break;
				case 4: // This is a blendindex
					offset = Mesh.BONE_INDEX_OFFSET;
					for (int k = 0; k < subEntity.countVertices; ++k) {
						for (int m = 0; m < 4; ++m) {
							int intVal = buffer[pos++] & 0xff;
							float index = Float.intBitsToFloat(intVal);
							subEntity.boneData[k * Mesh.BONE_BYTE_STRIDE + offset + m] = index;
						}
					}
					break;
				case 5: // This is a blendweight
					offset = Mesh.BONE_WEIGHT_OFFSET;
					for (int k = 0; k < subEntity.countVertices; ++k) {
						for (int m = 0; m < 4; ++m) {
							int intVal = buffer[pos++] & 0xff;
							float weight = Float.intBitsToFloat(intVal);
							weight /= 255;
							subEntity.boneData[k * Mesh.BONE_BYTE_STRIDE + offset + m] = weight;
						}
					}
					break;
				case 6: // This i a color
					noColor = false;
					offset = baseMaterial.getColorOffset();
					for (int k = 0; k < subEntity.countVertices; ++k) {
						subEntity.vertexData[k*byteStride + offset + 0] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 1] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 2] = toFloat(buffer, (pos++)*4);
						subEntity.vertexData[k*byteStride + offset + 3] = toFloat(buffer, (pos++)*4);
					}
					break;
				default: //None of the above, we dont care about this data;
					break;
				} 						
			
			}
		}
		
		if (noColor) {
			int offset = baseMaterial.getColorOffset();
			for (int j = 0; j < entityData.subEntities.length; ++j) {
				SubEntityData subEntity = entityData.subEntities[j];
				int byteStride = baseMaterial.getByteStride();
				for (int k = 0; k < subEntity.countVertices; ++k) {
					subEntity.vertexData[k*byteStride + offset + 0] = 1;
					subEntity.vertexData[k*byteStride + offset + 1] = 1;
					subEntity.vertexData[k*byteStride + offset + 2] = 1;
					subEntity.vertexData[k*byteStride + offset + 3] = 1;
				}
			}
		}
	}
	
	private void readFaces() throws IOException {
		if (readBytes == header.ofs_triangles) {
			for (int j = 0; j < entityData.subEntities.length; ++j) {
				SubEntityData subEntity = entityData.subEntities[j];
				byte[] buffer = new byte[subEntity.countTriangles*3*4];
				readBytes += in.read(buffer);
				
				int pos = 0;
				
				for (int i = 0; i < subEntity.countTriangles; ++i) {
					for (int k = 0; k < 3; ++k) {
						int intVal = toInt(buffer, (pos++)*4);
						short index = (short) intVal;
						subEntity.triangleData[i*3 + 2 - k] = index;
					}
				}
			}
		}
	}
		
	private int getVertexArrayComponentSize(VertexArray array) {
		int componentSize = -1;
	    
	    switch (array.format) {
	    case 0: //BYTE
	    case 1: //UBYTE
	    	componentSize = 1;
	    	break;
	    case 2: //SHORT
	    case 3: //USHORT
	    	componentSize = 2;
	    	break;
	    case 4: //INT
	    case 5: //UINT
	    case 6: //HALF ??
	    case 7: //FLOAT
	    	componentSize = 4;
	    	break;
	    case 8: //DOUBLE
	    	componentSize = 8;
	    	break;
	    }  
	    
	    return componentSize;
	}
	
	private void readTexts() throws IOException {
		
		texts = new ArrayList<String>();
		
		byte[] buffer = new byte[header.num_text];

		String text = "";
		

		int textStart = 0;
		int textEnd = 0;
		
		readBytes += in.read(buffer);
		
		while(textEnd < header.num_text) {
		
			while(textEnd < header.num_text && buffer[textEnd] != '\0') {
				textEnd++;
			}
			
			text += new String(buffer, textStart, textEnd-textStart, "US-ASCII");
			
			if (textEnd < header.num_text) {
				if (buffer[textEnd] == '\0') {
					// We found a text
					texts.add(text);
					text = "";
					textStart = textEnd + 1;
					textEnd = textStart;
				}
			}
		
		}	

	}
	
	private int toInt(byte[] buffer, int pos) {
		int intVal = ((buffer[pos+3] & 0xff) << 24) | ((buffer[pos+2] & 0xff) << 16) | ((buffer[pos+1] & 0xff) << 8) | (buffer[pos+0] & 0xff);
		return intVal;
	}
	
	private float toFloat(byte[] buffer, int pos) {
		int intVal = toInt(buffer, pos);
		float floatVal = Float.intBitsToFloat(intVal);
		return floatVal;
	}

}
