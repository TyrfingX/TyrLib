package com.tyrlib2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

/**
 * Creates Entities by reading .iqe files
 * @author Sascha
 *
 */

public class IQEEntityFactory implements IEntityFactory {
	
	private static final String OBJECT = "mesh";		// Begin a new sub entity
	private static final String VERTEX = "vp";			// Begin a new vertex and set the pos
	private static final String UV_COORD = "vt";		// Set the UV Texture for the current vertex
	private static final String NORMAL = "vn";			// Set the normal vector for the current vertex
	private static final String TRIANGLE = "fm";		// Make a triangle
	private static final String BONE_BINDING = "vb";	// Binds bones to a vertex
	private static final String MATERIAL = "material";	// Use a new material
	private static final String BONE = "joint";			// Create a new bone
	private static final String BONE_POSE = "pq";		// Set a bone pose
	private static final String ANIMATION = "animation";// Begin a new animation
	private static final String FRAMERATE = "framerate";// Set the framerate of the current animation
	private static final String FRAME = "frame";		// Begin a new animation frame
	
	public static int MAX_BONES_PER_VERTEX = 4;
	
	
	/** Holds the data for one triangle **/
	private static class Triangle {
		short[] data = new short[3];
	}
	
	
	/** Contains the data for binding a vertex to a bone **/ 
	private static class BoneBinding {
		float boneIndex;
		float boneWeight;
	}
	
	/** Contains per vertex data **/
	private static class VertexData {
		public Vector3 pos;
		public Vector2 uv;
		public Vector3 normal;
		public List<BoneBinding> boneBindings;
		
		public VertexData() {
			boneBindings = new ArrayList<BoneBinding>();
		}
	}
	
	/** Contains data for creating a SubEntity **/
	private static class SubEntityData {
		public String name;
		public String matName;
		public List<Triangle> triangles;
		private List<VertexData> vertexData;
		public SubEntityData(String name) {
			this.name = name;
			triangles = new ArrayList<Triangle>();
			vertexData = new ArrayList<VertexData>();
		}
	}
	
	/** Contains data for creating a bone **/
	private static class BoneData {
		public String name;
		public Vector3 pos;
		public Quaternion rot;
		public int parentIndex;
	}
	
	/** Contains data for creating an animation **/
	private static class AnimationData {
		public String name;
		public List<AnimationFrame> animationFrames;
		public float frameRate;
		public AnimationData() {
			animationFrames = new ArrayList<AnimationFrame>();
		}
	}
	
	/** Contains data for creating a skeleton **/
	private static class SkeletonData {
		public List<BoneData> bones;
		public List<AnimationData> animations;
		public SkeletonData() {
			bones = new ArrayList<BoneData>();
			animations = new ArrayList<AnimationData>();
		}
	}
	

	
	/** Contains a SubEntity prototyp which contains data shared
	 * by all SubEntities of this type created with this factory
	 */
	private static class SubEntityPrototype {
		public Mesh mesh;
		public DefaultMaterial3 material;
		public String name;
	}
	
	private List<SubEntityData> subEntityData;
	private SubEntityPrototype[] subEntityPrototypes;
	private SubEntityData currentDataBlock = null;
	private StringTokenizer tokenizer;
	private DefaultMaterial3 baseMaterial;
	private Map<String, DefaultMaterial3> materials;
	private SkeletonData skeletonData;
	private BufferedReader in;

	
	public IQEEntityFactory(String fileName, DefaultMaterial3 baseMaterial) {
		
		this.baseMaterial = baseMaterial;
		
		subEntityData = new ArrayList<SubEntityData>();
		skeletonData = new SkeletonData();
		
		materials = new HashMap<String, DefaultMaterial3>();
		
		// Due to efficiency reasons this class does not employ the file reader but rather parses the file directly
		try {
			InputStream inputStream = Media.CONTEXT.openAsset(fileName);
			
			// setup Bufferedreader
			in = new BufferedReader(new InputStreamReader(inputStream));

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
					} else if (token.equals(BONE_BINDING)) {
						createBoneBinding();
					} else if (token.equals(TRIANGLE)) {
						createTriangle();
					} else if (token.equals(BONE)) {
						createBone();
					} else if (token.equals(BONE_POSE)) {
						createBonePose();
					} else if (token.equals(MATERIAL)) {
						createMaterial();
					} else if (token.equals(ANIMATION)) {
						createAnimations();
					} 
				}
			}
			
			inputStream.close();
			in.close();
		} catch (IOException e) {
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
			float[] vertexData = new float[byteStride * data.vertexData.size()];
			short[] drawOrder = new short[data.triangles.size() * 3];
			float[] boneData = new float[data.vertexData.size() * Mesh.BONE_BYTE_STRIDE];
			int bytePos = 0;
			int boneBytePos = 0;
			
			for (int j = 0; j < data.vertexData.size(); ++j) {
				VertexData vertex = data.vertexData.get(j);
				Vector3 pos = vertex.pos;
				vertexData[bytePos + baseMaterial.getPositionOffset() + 0] = pos.x;
				vertexData[bytePos + baseMaterial.getPositionOffset() + 1] = pos.y;
				vertexData[bytePos + baseMaterial.getPositionOffset() + 2] = pos.z;
				
				Vector3 normal = vertex.normal;
				vertexData[bytePos + baseMaterial.getNormalOffset() + 0] = normal.x;
				vertexData[bytePos + baseMaterial.getNormalOffset() + 1] = normal.y;
				vertexData[bytePos + baseMaterial.getNormalOffset() + 2] = normal.z;
				
				Vector2 uv = vertex.uv;
				vertexData[bytePos + baseMaterial.getUVOffset() + 0] = uv.x;
				vertexData[bytePos + baseMaterial.getUVOffset() + 1] = uv.y;
				
				bytePos += byteStride;

				int offset = 0;
				for (int k = 0; k < vertex.boneBindings.size(); ++k) {
					BoneBinding binding = vertex.boneBindings.get(k);
					boneData[boneBytePos + offset] = binding.boneIndex;
					offset++;
				}
				
				offset = Mesh.MAX_BONES_PER_VERTEX;
				
				for (int k = 0; k < vertex.boneBindings.size(); ++k) {
					BoneBinding binding = vertex.boneBindings.get(k);
					boneData[boneBytePos + offset] =  binding.boneWeight;
					offset++;
				}
				
				boneBytePos += Mesh.BONE_BYTE_STRIDE;
			}
			
			for (short j = 0; j < data.triangles.size(); ++j) {
				Triangle triangle = data.triangles.get(j);
				for (short k = 0; k < triangle.data.length; ++k) {
					drawOrder[j*3 + 2 - k] = triangle.data[k];
				}
			}
			
			Mesh mesh = new Mesh(vertexData, drawOrder, data.vertexData.size());
			mesh.setVertexBones(boneData);
			prototype.material = materials.get(data.matName);
			prototype.material.setAnimated(true);
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
			// Use the prototypes to create the actual sub entities
			SubEntityPrototype prototype = subEntityPrototypes[i];
			SubEntity subEntity = new SubEntity(prototype.name, prototype.mesh, prototype.material.copy(true));
			entity.addSubEntity(subEntity);
		}
		
		// Now create a new skeleton
		Skeleton skeleton = new Skeleton();
		entity.setSkeleton(skeleton);
		for (int i = 0; i < skeletonData.bones.size(); ++i) {
			BoneData boneData = skeletonData.bones.get(i);
			Bone bone = new Bone(boneData.name);
			if (boneData.parentIndex != -1) {
				Bone parentBone = skeleton.getBone(boneData.parentIndex);
				parentBone.attachChild(bone);
			}
			bone.setPose(boneData.pos, boneData.rot);
			skeleton.addBone(bone);
		}
		
		for (int i = 0; i < skeletonData.animations.size(); ++i) {
			AnimationData animData = skeletonData.animations.get(i);
			Animation animation = new Animation(animData.name);
			skeleton.addAnimation(animation);
			animation.addAllFrames(animData.animationFrames);
			
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
		VertexData vertexData = new VertexData();
		vertexData.pos = new Vector3(x,y,z);
		currentDataBlock.vertexData.add(vertexData);
	}
	
	/** 
	 * Creates a new UV Coordinate
	 */
	private void createUVCoord() {
		float u = Float.valueOf(tokenizer.nextToken());
		float v = Float.valueOf(tokenizer.nextToken());
		currentDataBlock.vertexData.get(currentDataBlock.vertexData.size()-1).uv = new Vector2(u,v);
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
		currentDataBlock.vertexData.get(currentDataBlock.vertexData.size()-1).normal = normal;
	}
	
	/**
	 * Binds bones to a vertex/a vertex to bones
	 */
	private void createBoneBinding() {
		VertexData vertex = currentDataBlock.vertexData.get(currentDataBlock.vertexData.size()-1);
		while(tokenizer.hasMoreElements()) {
			BoneBinding binding = new BoneBinding();
			binding.boneIndex = Byte.valueOf(tokenizer.nextToken());
			binding.boneWeight = Float.valueOf(tokenizer.nextToken());
			vertex.boneBindings.add(binding);
		}
		
		// Renormalize
		float total = 0;
		for (int i = 0; i < vertex.boneBindings.size(); ++i) {
			total += vertex.boneBindings.get(i).boneWeight;
		}
		
		if (total != 0) {
			for (int i = 0; i < vertex.boneBindings.size(); ++i) {
				vertex.boneBindings.get(i).boneWeight /= total;
			}
		}
		
		// Kick out the weakest bone bindings
		while(vertex.boneBindings.size() > Mesh.MAX_BONES_PER_VERTEX) {
			float min = 1;
			int minIndex = 0;
			for (int i = 0; i < vertex.boneBindings.size(); ++i) {
				float weight = vertex.boneBindings.get(i).boneWeight;
				if (weight < min) {
					minIndex = i;
					min = weight;
				}
			}
			
			vertex.boneBindings.remove(minIndex);
		}
		
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
	
	/** Create a new bone **/
	private void createBone() {
		String boneName = tokenizer.nextToken();
		BoneData bone = new BoneData();
		bone.name = boneName;
		int parentIndex = Integer.valueOf(tokenizer.nextToken());
		bone.parentIndex = parentIndex;		
		
		skeletonData.bones.add(bone);
	}
	
	private void createBonePose() {
		BoneData bone = skeletonData.bones.get(skeletonData.bones.size()-1);
		
		float x = Float.valueOf(tokenizer.nextToken());
		float y = Float.valueOf(tokenizer.nextToken());
		float z = Float.valueOf(tokenizer.nextToken());
		bone.pos = new Vector3(x,y,z);
		
		
		float rotX = Float.valueOf(tokenizer.nextToken());
		float rotY = Float.valueOf(tokenizer.nextToken());
		float rotZ = Float.valueOf(tokenizer.nextToken());
		float angle = Float.valueOf(tokenizer.nextToken());
		bone.rot = new Quaternion(rotX,rotY,rotZ,angle);
		
		if (rotY == 0 && rotX == 0 && rotZ == 0) {
			bone.rot.w = 1;
		}
		
		//float scaleX = Float.valueOf(tokenizer.nextToken());
		//float scaleY = Float.valueOf(tokenizer.nextToken());
		//float scaleZ = Float.valueOf(tokenizer.nextToken());
		//bone.scale = new Vector3(scaleX,scaleY,scaleZ);
	}
	
	/** Create a new material **/
	private void createMaterial() {
		String matName = tokenizer.nextToken();
		matName = matName.substring(1, matName.length()-1); // get rid of the ""
		Texture texture = TextureManager.getInstance().getTexture(matName);
		DefaultMaterial3 mat = (DefaultMaterial3) baseMaterial.copy();
		mat.setTexture(texture, matName);
		materials.put(matName, mat);
		currentDataBlock.matName = matName;
	}
	
	/** Create all animations **/
	private void createAnimations() throws IOException {
		if (tokenizer.hasMoreElements()) {
			createAnimation();
		}
	}
	
	/** Create a new animation **/
	private void createAnimation() throws IOException {
		String animName = tokenizer.nextToken();
		animName = animName.substring(1, animName.length()-1); // get rid of the ""
		AnimationData animData = new AnimationData();
		animData.name = animName;
		skeletonData.animations.add(animData);
		
		// parse the data for this animation
		
		String line =  in.readLine();
		tokenizer = new StringTokenizer(line);
		String token = tokenizer.nextToken();
		if (token.equals(FRAMERATE)) {
			animData.frameRate = Float.valueOf(tokenizer.nextToken());
		}
		
		in.readLine(); // Empty line
		int currentBone = 0;
		
		AnimationFrame animFrame = null;
		
		while((line = in.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			if (tokenizer.hasMoreElements()) {
				token = tokenizer.nextToken();
				if (token.equals(BONE_POSE)) {
					float x = Float.valueOf(tokenizer.nextToken());
					float y = Float.valueOf(tokenizer.nextToken());
					float z = Float.valueOf(tokenizer.nextToken());
					animFrame.bonePos[currentBone] = new Vector3(x,y,z);
					
					float rotX = Float.valueOf(tokenizer.nextToken());
					float rotY = Float.valueOf(tokenizer.nextToken());
					float rotZ = Float.valueOf(tokenizer.nextToken());
					float angle = Float.valueOf(tokenizer.nextToken());
					
					
					animFrame.boneRot[currentBone] = new Quaternion(rotX,rotY,rotZ,angle);
					
					if (rotY == 0 && rotX == 0 && rotZ == 0) {
						animFrame.boneRot[currentBone].w = 1;
					}

					
					currentBone++;
				} else if (token.equals(FRAME)) {
					currentBone = 0;
					animFrame = new AnimationFrame(skeletonData.bones.size());
					animFrame.time += animData.animationFrames.size() * 1 / animData.frameRate;
					animData.animationFrames.add(animFrame);
				}else if (token.equals(ANIMATION)) {
					break;
				}
			}
		}
	}

}
