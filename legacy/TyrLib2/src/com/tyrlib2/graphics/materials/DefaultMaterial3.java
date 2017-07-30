package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.renderer.VertexLayout;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * Default material for rendering 3D objects especially entities.
 * Supports:
 * - Lighting
 * - Per vertex coloring
 * - Skinning/skeletal animation
 * - Texturing
 * @author Sascha
 *
 */

public class DefaultMaterial3 extends LightedMaterial {
	
	/** Per vertex color of this object **/
	private Color[] colors;
	
	/** Per vertex normals of this object **/
	public static final int DEFAULT_NORMAL_OFFSET = 3;
	public static final int DEFAULT_NORMAL_SIZE = 3;
	protected int normalHandle;
	
	/** Texture information of this object **/
	public static final int DEFAULT_UV_OFFSET = 6;
	public static final int DEFAULT_UV_SIZE = 2;
	
	public static final int DIFFUSE_OFFSET = 8;
	public static final int DIFFUSE = 4;
	
	private int textureUniformHandle;
	private int textureCoordinateHandle;
	private int shadowTextureHandle;
	private int depthMVPHandle;
	private String textureName;
	private Texture texture;
	protected float repeatX;
	protected float repeatY;
	
	public static final int posOffset = 0;
	
	/** Contains the normal matrix **/
	private static float[] shadowMVP = new float[16];
	
	public static final String PER_PIXEL_PROGRAM_NAME = "TEXTURED_PPL";

	private boolean transparent;
	
	private static boolean wasAnimated = false;
	
	private int blendMode = TyrGL.GL_ONE_MINUS_SRC_ALPHA;

	private int modelMatrixHandle;
	
	/** Default vertex layout **/
	public final static VertexLayout DEFAULT_LAYOUT = new VertexLayout();
	
	/** Vertex layout used for meshs with baked in lighting information **/
	public final static VertexLayout BAKED_LIGHTING_LAYOUT = new VertexLayout();

	private static final float[] mvpMatrix = new float[16];
	
	static {
		DEFAULT_LAYOUT.setPos(VertexLayout.POSITION, 0);
		DEFAULT_LAYOUT.setSize(VertexLayout.POSITION, 3);
		DEFAULT_LAYOUT.setPos(VertexLayout.NORMAL, DEFAULT_NORMAL_OFFSET);
		DEFAULT_LAYOUT.setSize(VertexLayout.NORMAL, DEFAULT_NORMAL_SIZE);
		DEFAULT_LAYOUT.setPos(VertexLayout.UV, DEFAULT_UV_OFFSET);
		DEFAULT_LAYOUT.setSize(VertexLayout.UV, DEFAULT_UV_SIZE);
		
		BAKED_LIGHTING_LAYOUT.setPos(VertexLayout.POSITION, 0);
		BAKED_LIGHTING_LAYOUT.setSize(VertexLayout.POSITION, 3);
		BAKED_LIGHTING_LAYOUT.setPos(VertexLayout.NORMAL, DEFAULT_NORMAL_OFFSET);
		BAKED_LIGHTING_LAYOUT.setSize(VertexLayout.NORMAL, DEFAULT_NORMAL_SIZE);
		BAKED_LIGHTING_LAYOUT.setPos(VertexLayout.UV, DEFAULT_UV_OFFSET);
		BAKED_LIGHTING_LAYOUT.setSize(VertexLayout.UV, DEFAULT_UV_SIZE);
		BAKED_LIGHTING_LAYOUT.setPos(DIFFUSE, DIFFUSE_OFFSET);
		BAKED_LIGHTING_LAYOUT.setSize(DIFFUSE, 1);
	}
	
	public DefaultMaterial3() {
		
	}
	
	public DefaultMaterial3(Program program, String textureName, float repeatX, float repeatY, Color[] colors) {
		this.program = program;
		
		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, colors);
	}
	
	public DefaultMaterial3(String textureName, float repeatX, float repeatY, Color[] colors) {

		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
		
			program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
		
			if (colors == null) {
				colors = new Color[1];
				colors[0] = new Color(1,1,1,1);
			}
			
			if (textureName != null) {
				texture = TextureManager.getInstance().getTexture(textureName);
			}
		
		}
		
		setup(textureName, repeatX, repeatY, colors);

	}
	
	public DefaultMaterial3(String textureName, float repeatX,
			float repeatY, Color[] colors,
			boolean animated) {
		
		if (colors == null) {
			colors = new Color[1];
			colors[0] = new Color(1,1,1,1);
		}
		
		String add = "";
		
		this.animated = animated;
		
		if (animated) {
			add = "_ANIMATED";
		}
		
	
		program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME + add);


		if (textureName != null) {
			texture = TextureManager.getInstance().getTexture(textureName);
		}
		
		setup(textureName, repeatX, repeatY, colors);
		
	}

	protected void setup(String textureName, float repeatX, float repeatY, Color[] colors) {
		lighted = true;
		
		this.boneParam = "u_Bone";
		this.boneIndexParam = "a_BoneIndex";
		this.boneWeightParam = "a_BoneWeight";
		
		this.textureName = textureName;
		this.colors = colors; 
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		
		init(posOffset,3, "u_MVPMatrix", "a_Position");
		
		addVertexInfo(VertexLayout.NORMAL, DEFAULT_NORMAL_OFFSET, DEFAULT_NORMAL_SIZE);
		addVertexInfo(VertexLayout.UV, DEFAULT_UV_OFFSET, DEFAULT_UV_SIZE);
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			updateHandles();
		}
	}
	
	public void setLighted(boolean lighted) {
		this.lighted = lighted;
	}
	
	@Override
	public void updateHandles() {
		super.updateHandles();
		normalMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_NormalMatrix"); 
		textureUniformHandle = TyrGL.glGetUniformLocation(program.handle, "u_Texture");
		ambientHandle = TyrGL.glGetUniformLocation(program.handle, "u_Ambient");
		textureUniformHandle = TyrGL.glGetUniformLocation(program.handle, "u_Texture");
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
		textureCoordinateHandle = TyrGL.glGetAttribLocation(program.handle, "a_TexCoordinate");
		shadowTextureHandle = TyrGL.glGetUniformLocation(program.handle, "u_ShadowMap");
		depthMVPHandle = TyrGL.glGetUniformLocation(program.handle, "u_DepthMVP");
		modelMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_M");
	}
	
	@Override
	public void render(Mesh mesh, float[] modelMatrix) {
		super.render(mesh, modelMatrix);

		if (program.meshChange) {
			passMesh(mesh);
		}
		//passModelViewMatrix(modelMatrix);
	    
	   // SceneManager sceneManager = SceneManager.getInstance();
	   // float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
	   // Matrix.multiplyMM(normalMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
	    
        // Pass in the modelview matrix.
		//TyrGL.glUniformMatrix4fv(normalMatrixHandle, 1, false, normalMatrix, 0);

		
		if (modelMatrixHandle != -1) {
		
			Matrix.setLookAtM(	mvpMatrix, 
					0, 
					0,0,1, 
					0,0,0,
					1,0,0);
	
	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);
	        // Combine the rotation matrix with the projection and camera view
			TyrGL.glUniformMatrix4fv(modelMatrixHandle, 1, false, mvpMatrix, 0);
			
		}
		
		if (SceneManager.getInstance().getRenderer().isShadowsEnabled() && depthMVPHandle != -1) {
			  // Apply the projection and view transformation
			Matrix.multiplyMM(shadowMVP, 0, SceneManager.getInstance().getRenderer().getShadowVP(), 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
			TyrGL.glUniformMatrix4fv(depthMVPHandle, 1, false, shadowMVP, 0);
			
		    // Set the active texture unit to texture unit 2.
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE2);
	 
		    // Bind the texture to this unit.
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, SceneManager.getInstance().getRenderer().getShadowMapHandle());
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			TyrGL.glUniform1i(shadowTextureHandle, 2);
		} 
		
		int textureHandle = texture.getHandle();
        if (program.textureHandle != textureHandle) {
        	passTexture(textureHandle);
        }
	    
	    if (!animated && wasAnimated) {
	    	wasAnimated = false;
	    } else {
	    	wasAnimated = true;
	    }
	    
	    if (transparent) {
	    	Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
	    }
	    
	    
	}
	
	public void setBlendMode(int mode) {
		this.blendMode = mode;
	}
	
//	private void passModelViewMatrix(float[] modelMatrix) {
//	    SceneManager sceneManager = SceneManager.getInstance();
//	    float[] viewMatrix = sceneManager.getRenderer().getCamera().getViewMatrix();
//	    Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);  
//	    
//	    
//        // Pass in the modelview matrix.
//        TyrGL.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
//	}
	
	protected void passMesh(Mesh mesh)
	{	
		FloatBuffer vertexBuffer = mesh.getVertexBuffer();
		if (mesh.isUsingVBO()) {
		    // Pass in the normal information
			if (normalHandle != -1) {
			    TyrGL.glVertexAttribPointer(normalHandle, getNormalSize(), TyrGL.GL_FLOAT, false,
			    							getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, getNormalOffset() * OpenGLRenderer.BYTES_PER_FLOAT);
			 
			    TyrGL.glEnableVertexAttribArray(normalHandle);
			}
			
			if (textureCoordinateHandle != -1) {
		        // Pass in the texture coordinate information
		        TyrGL.glVertexAttribPointer(textureCoordinateHandle, getUVSize(), TyrGL.GL_FLOAT, false, 
		        		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, getUVOffset() * OpenGLRenderer.BYTES_PER_FLOAT);
		        TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
			}
			
		} else {
			if (getNormalSize() > 0) {
			    // Pass in the normal information
			    vertexBuffer.position(getNormalOffset());
			    TyrGL.glVertexAttribPointer(normalHandle, getNormalSize(), TyrGL.GL_FLOAT, false,
			    		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
			 
			    TyrGL.glEnableVertexAttribArray(normalHandle);
			}
		    
	        // Pass in the texture coordinate information
	        vertexBuffer.position(getUVOffset());
	        TyrGL.glVertexAttribPointer(textureCoordinateHandle, getUVSize(), TyrGL.GL_FLOAT, false, 
	        		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	        
	        TyrGL.glEnableVertexAttribArray(textureCoordinateHandle);
		}
	}

	protected void passTexture(int textureHandle) {
	    // Set the active texture unit to texture unit 0.
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);
	 
	    // Bind the texture to this unit.
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureHandle);
	 
	    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		TyrGL.glUniform1i(textureUniformHandle, 0);
		
		
	    program.textureHandle = textureHandle;
	    
	    OpenGLRenderer.setTextureFails(OpenGLRenderer.getTextureFails() + 1);
	}

	public Color[] getColors() {
		return colors;
	}
	
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
	
	public boolean isTransparent() {
		return transparent;
	}
	
	
	public float[] createVertexData(Vector3[] points, float[] uvCoords, short[] drawOrder) {
		float[] vertexData = super.createVertexData(points, drawOrder);
		
		int vertexCount = points.length;
		
		Vector3[] normals = new Vector3[points.length];
		for (int i = 0; i < vertexCount; ++i) {
			normals[i] = new Vector3();

			for (int j = 0; j < drawOrder.length; j += 3) {
				if (j + 2 < drawOrder.length) {
					if (drawOrder[j] == i || drawOrder[j+1] == i || drawOrder[j + 2] == i) {
						Vector3 u = points[drawOrder[j]].vectorTo(points[drawOrder[j+1]]);
						Vector3 v = points[drawOrder[j+1]].vectorTo(points[drawOrder[j+2]]);
						Vector3 normal = u.cross(v);
						normal.normalize();
						
						normals[i] = normals[i].add(normal);
					}
				}
			}
			normals[i].normalize();
		}
		
		int uvCoord = 0;
		
		for (int i = 0; i < vertexCount; i++) {
			
			int pos = i * getByteStride();
			
			int normalOffset = getNormalOffset();
			vertexData[pos + normalOffset  + 0] = normals[i].x;
			vertexData[pos + normalOffset + 1] = normals[i].y;
			vertexData[pos + normalOffset + 2] = normals[i].z;
			
			int uvOffset = getUVOffset();
			vertexData[pos + uvOffset + 0] = uvCoords[uvCoord] * repeatX;
			vertexData[pos + uvOffset + 1] = uvCoords[uvCoord+1] * repeatY;
			
			uvCoord += 2;
			
			if (uvCoord >= uvCoords.length) {
				uvCoord = 0;
				// Increase the coordinate numbers in order to prevent
				// weird clamping effects to occur in most cases
				for (int j = 0; j < uvCoords.length; ++j) {
					uvCoords[j] = uvCoords[j] + 1;
				}
			}
			
		}
		
		return vertexData;
	}
	
	/**
	 * Adds the colors to the vertex data.
	 * Repeats the colors if there are more vertices than colors
	 */
	
	public float[] createVertexData(Vector3[] points, short[] drawOrder) {
		// Assign some arbitary uvCoordinates for the textures
		// The default uvCoords assigned by this work for shapes
		// like planes, meshs, etc
		float[] uvCoords = {
			0.0f, 0.0f, 				
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
		};
		
		return createVertexData(points, uvCoords, drawOrder);
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	public int getNormalOffset() {
		return getInfoOffset(VertexLayout.NORMAL);
	}
	
	public int getUVOffset(){
		return getInfoOffset(VertexLayout.UV);
	}
	
	public int getUVSize() {
		return getInfoSize(VertexLayout.UV);
	}

	public int getNormalSize() {
		return getInfoSize(VertexLayout.NORMAL);
	}
	
	public void setNormalSize(int size) {
		vertexLayout.setSize(VertexLayout.NORMAL, size);
	}
	
	public void setNormalOffset(int offset) {
		vertexLayout.setPos(VertexLayout.NORMAL, offset);
	}
	
	public void setUVSize(int size) {
		vertexLayout.setSize(VertexLayout.UV, size);
	}
	
	public void setUVOffset(int offset) {
		vertexLayout.setPos(VertexLayout.UV, offset);
	}
	
	public Material copy() {
		DefaultMaterial3 material = new DefaultMaterial3(textureName, repeatX, repeatY, colors, animated);
		material.vertexLayout = vertexLayout.copy();
		return material;
	}
	
	public Material copy(boolean animated) {
		DefaultMaterial3 material = new DefaultMaterial3(textureName, repeatX, repeatY, colors, animated);
		material.vertexLayout = vertexLayout.copy();
		return material;
	}
	
	public void setTexture(Texture texture, String textureName) {
		this.texture = texture;
		this.textureName = textureName;
	}
	
	public void setTexture( String textureName) {
		this.textureName = textureName;
		this.texture = TextureManager.getInstance().getTexture(textureName);
	}
	
	public Vector2 getRepeat() {
		return new Vector2(repeatX, repeatY);
	}
	
	public boolean isAnimated() {
		return animated;
	}

	public void setVertexLayout(VertexLayout vertexLayout) {
		this.vertexLayout = vertexLayout;
	}
	
	
}
