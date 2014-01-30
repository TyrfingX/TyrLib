package com.tyrlib2.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.animation.Animation;
import com.tyrlib2.graphics.animation.AnimationFrame;
import com.tyrlib2.graphics.animation.Bone;
import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderables.SubEntity;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

public class IQMEntityFactory implements IEntityFactory {
	
	private static final int HEADER_TXT_SIZE = 16;
	private static final int HEADER_SIZE = 27*4;
	private static final int SUBENTITY_SIZE = 6*4;
	private static final int VERTEX_ARRAY_SIZE = 5*4;
	private static final int JOINT_SIZE = 12*4;
	private static final int POSE_SIZE = 22*4;
	private static final int ANIM_SIZE = 5*4;
	
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
	
	/** Contains data for creating a bone **/
	private class BoneData {
		public String name;
		public Vector3 pos;
		public Quaternion rot;
		public int parentIndex;
	}
	
	/** Contains data for creating an animation **/
	private class AnimationData {
		public String name;
		public AnimationFrame[] animationFrames;
		public float frameRate;
		public int flag;
	}
	
	private class PoseData {
	    int parent;
	    int channelmask; // mask of which 10 channels are present for this joint pose
	    float[] channeloffset = new float[10];
	    float[] channelscale = new float[10]; 
	    // channels 0..2 are translation <Tx, Ty, Tz> and channels 3..6 are quaternion rotation <Qx, Qy, Qz, Qw>
	    // rotation is in relative/parent local space
	    // channels 7..9 are scale <Sx, Sy, Sz>
	    // output = (input*scale)*rotation + translation
	}
	
	/** Contains data for creating a skeleton **/
	private class SkeletonData {
		public List<BoneData> bones;
		public PoseData[] poses;
		public List<AnimationData> animations;
		public SkeletonData() {
			bones = new ArrayList<BoneData>();
			animations = new ArrayList<AnimationData>();
		}
	}
	
	private class EntityData {
		SubEntityData[] subEntities;
		SkeletonData skeletonData;
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
	private int textPos = 1;
	private DefaultMaterial3 baseMaterial;
	private VertexArray[] vertexArrays;
	
	private Header header;
	
	private EntityPrototype entityPrototype;
	
	
	public IQMEntityFactory(String fileName, DefaultMaterial3 baseMaterial) {
		
		this.fileName = fileName;
		this.baseMaterial = baseMaterial;
		
		entityData = new EntityData();
		
		try {
			InputStream inputStream = Media.CONTEXT.openAsset(fileName);
			in = new BufferedInputStream(inputStream);
			
			readHeader();
			readTexts();
			readSubEntities();
			readVertexArrays();
			readVertices();
			readFaces();
			readAdjacency();
			readSkeleton();
			readAnimations();
			
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
			p.mesh.setVertexBones(data.boneData);
			p.material = (DefaultMaterial3) baseMaterial.copy(entityData.skeletonData.bones.size() > 0);
			Texture texture = TextureManager.getInstance().getTexture(data.material);
			p.material.setTexture(texture, data.material);
			
			if (entityData.skeletonData.bones.size() > 0) {
				p.material.setAnimated(true);
			}
		}
		
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
		
		// Now create a new skeleton
		Skeleton skeleton = new Skeleton();
		entity.setSkeleton(skeleton);
		
		for (int i = 0; i < entityData.skeletonData.bones.size(); ++i) {
			BoneData boneData = entityData.skeletonData.bones.get(i);
			Bone bone = new Bone(boneData.name);
			if (boneData.parentIndex != -1) {
				Bone parentBone = skeleton.getBone(boneData.parentIndex);
				parentBone.attachChild(bone);
			}
			bone.setPose(boneData.pos, boneData.rot);
			skeleton.addBone(bone);
		}
		
		for (int i = 0; i < entityData.skeletonData.animations.size(); ++i) {
			AnimationData animData = entityData.skeletonData.animations.get(i);
			Animation animation = new Animation(animData.name);
			skeleton.addAnimation(animation);
			for (int j = 0; j < animData.animationFrames.length; ++j) {
				animation.addFrame(animData.animationFrames[j]);
			}
			
		}
		
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
			pos++;
			pos++;
			int firstVertex = toInt(buffer, (pos++)*4);
			int numVertices = toInt(buffer, (pos++)*4);
			int firstTriangle = toInt(buffer, (pos++)*4);
			int numTriangles = toInt(buffer, (pos++)*4);
			
			subEntity.name = texts.get(textPos++);
			subEntity.material = texts.get(textPos++);
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
							subEntity.boneData[k * Mesh.BONE_BYTE_STRIDE + offset + m] = intVal;
						}
					}
					break;
				case 5: // This is a blendweight
					offset = Mesh.BONE_WEIGHT_OFFSET;
					for (int k = 0; k < subEntity.countVertices; ++k) {
						for (int m = 0; m < 4; ++m) {
							int intVal = buffer[pos++] & 0xff;
							float weight = intVal / 255.f;
							subEntity.boneData[k * Mesh.BONE_BYTE_STRIDE + offset + m] = weight;
						}
					}
					break;
				case 6: // This i a color
					noColor = false;
					break;
				default: //None of the above, we dont care about this data;
					break;
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

	private void readAdjacency() throws IOException {
		// Not needed to just jump over the data
		byte[] buffer = new byte[header.num_triangles*3*4];
		readBytes += in.read(buffer);
	}
	
	private void readSkeleton() throws IOException {
		entityData.skeletonData = new SkeletonData();
		
		if (header.ofs_joints == readBytes) {
			byte[] buffer = new byte[JOINT_SIZE * header.num_joints];
			readBytes += in.read(buffer);
			
			int pos = 0;
			
			for (int i = 0; i < header.num_joints; ++i) {
			
				BoneData bone = new BoneData();
				entityData.skeletonData.bones.add(bone);
				
				pos++;
				bone.name = texts.get(textPos++);
				bone.parentIndex = toInt(buffer, (pos++)*4);
				
				bone.pos = new Vector3();
				bone.pos.x = toFloat(buffer, (pos++)*4);
				bone.pos.y = toFloat(buffer, (pos++)*4);
				bone.pos.z = toFloat(buffer, (pos++)*4);
				
				bone.rot = new Quaternion();
				bone.rot.x = toFloat(buffer, (pos++)*4);
				bone.rot.y = toFloat(buffer, (pos++)*4);
				bone.rot.z = toFloat(buffer, (pos++)*4);
				bone.rot.w = toFloat(buffer, (pos++)*4);
				
				pos += 3; // Scale info not supported yet
			
			}
		}
		
		if (header.ofs_poses == readBytes) {
			// Dont know why the fuck I would need these...
			byte[] buffer = new byte[POSE_SIZE * header.num_poses];
			readBytes += in.read(buffer);
			
			int pos = 0;
			
			entityData.skeletonData.poses = new PoseData[header.num_poses];
			
			for (int i = 0; i < header.num_poses; ++i) {
				PoseData pose = new PoseData();
				entityData.skeletonData.poses[i] = pose;
				
				pose.parent = toInt(buffer, (pos++)*4);
				pose.channelmask = toInt(buffer, (pos++)*4);
				for (int j = 0; j < 10; ++j) {
					pose.channeloffset[j] = toFloat(buffer, (pos++)*4);
				}
				for (int j = 0; j < 10; ++j) {
					pose.channelscale[j] = toFloat(buffer, (pos++)*4);
				}
			}
		}
		
	}
	
	private void readAnimations() throws IOException {
		if (header.ofs_anims == readBytes) {
			for (int i = 0; i < header.num_anims; ++i) {
				byte[] buffer = new byte[ANIM_SIZE];
				readBytes += in.read(buffer);
				
				int pos = 0;
				
				AnimationData animData = new AnimationData();
				
				pos++;
				animData.name = texts.get(textPos++);
				int firstFrame = toInt(buffer, (pos++)*4);
				int numFrames = toInt(buffer, (pos++)*4);
				animData.animationFrames = new AnimationFrame[numFrames];
				animData.frameRate = toFloat(buffer, (pos++)*4);
				animData.flag = toInt(buffer, (pos++)*4);
				
				entityData.skeletonData.animations.add(animData);
			}
		}
		
		if (header.ofs_frames == readBytes) {
			for (int i = 0; i < header.num_anims; ++i) {
				AnimationData animData = entityData.skeletonData.animations.get(i);
				
				byte[] buffer = new byte[2 * animData.animationFrames.length * header.num_framechannels];
				readBytes += in.read(buffer);
				
				int pos = 0;
				float prevTime = 0;
				
				
				for (int j = 0; j < animData.animationFrames.length; ++j) {
					AnimationFrame frame = new AnimationFrame(header.num_joints);
					animData.animationFrames[j] = frame;
					frame.time = prevTime;
					prevTime +=  1 / animData.frameRate;		
					
					for (int k = 0; k < header.num_poses; ++k) {
						
						PoseData p = entityData.skeletonData.poses[k];
						Vector3 translate = new Vector3(p.channeloffset[0], p.channeloffset[1], p.channeloffset[2]);
						
						if((p.channelmask&0x01) != 0) translate.x += toShort(buffer, (pos++)*2) * p.channelscale[0];
			            if((p.channelmask&0x02) != 0) translate.y += toShort(buffer, (pos++)*2) * p.channelscale[1];
			            if((p.channelmask&0x04) != 0) translate.z += toShort(buffer, (pos++)*2) * p.channelscale[2];
						
			            Quaternion rotate = new Quaternion(p.channeloffset[3], p.channeloffset[4], p.channeloffset[5], p.channeloffset[6]);
			            
			            if((p.channelmask&0x08) != 0) rotate.x += toShort(buffer, (pos++)*2) * p.channelscale[3];
			            if((p.channelmask&0x10) != 0) rotate.y += toShort(buffer, (pos++)*2) * p.channelscale[4];
			            if((p.channelmask&0x20) != 0) rotate.z += toShort(buffer, (pos++)*2) * p.channelscale[5];
			            if((p.channelmask&0x40) != 0) rotate.w += toShort(buffer, (pos++)*2) * p.channelscale[6];
			            
			            if((p.channelmask&0x80) != 0) pos++;
			            if((p.channelmask&0x100) != 0) pos++;
			            if((p.channelmask&0x200) != 0) pos++;
		            
			            frame.bonePos[k] = translate;
			            frame.boneRot[k] = rotate;
			            
					}

					
				}
				
			}
		}
	}
	
	private int toInt(byte[] buffer, int pos) {
		int intVal = ((buffer[pos+3] & 0xff) << 24) | ((buffer[pos+2] & 0xff) << 16) | ((buffer[pos+1] & 0xff) << 8) | (buffer[pos+0] & 0xff);
		return intVal;
	}

	private int toShort(byte[] buffer, int pos) {
		int intVal = ((buffer[pos+1] & 0xff) << 8) | (buffer[pos+0] & 0xff);
		return intVal;
	}
	
	private float toFloat(byte[] buffer, int pos) {
		int intVal = toInt(buffer, pos);
		float floatVal = Float.intBitsToFloat(intVal);
		return floatVal;
	}

}
