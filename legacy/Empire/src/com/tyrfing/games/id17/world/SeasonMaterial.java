package com.tyrfing.games.id17.world;

import java.nio.FloatBuffer;
import java.util.Random;

import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.renderer.VertexLayout;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class SeasonMaterial extends DefaultMaterial3 implements IUpdateable {
	
	public static final float FULL = 1.0f;
	public static final float NONE = 0.5f;
	public static final float UNEXPLORED = 0.0f;
	
	private String winTextureName;
	private Texture winTexture;
	private int winTextureHandle;
	private int bumpMapTextureHandle;
	private int winterHandle;
	private int timeHandle;
	private WorldChunk chunk;
	private int ownerHandle;
	private int viewDirectionHandle;
	private int diffuseHandle;
	
	private boolean fogged;
	
	private float windTime;
	private float wind;

	private Program fogProgram;
	private Program seasonProgram;
	private Program strategicProgram;
	private int modelMatrixHandle;
	private int colorStrategic;
	
	private static final float[] mvpMatrix = new float[16];
	private boolean strategic;
	private Color color;
	
	
	/** GRASS SETTINGS **/
	public float grassHeight;
	private int currentLayerHandle;
	private int grassHeightHandle;
	public int countLayers = 1;
	private int furTextureHandle;
	
	
	/** OREN NAYAR LIGHTING **/
	private int lightDirHandle;
	private int camPosHandle;
	
	public SeasonMaterial(Program program, WorldChunk chunk, String winTextureName, String textureName, float repeatX, float repeatY, Color[] colors, boolean water) {
		super(program, textureName, repeatX, repeatY, colors);
		
		this.seasonProgram = program;
		this.winTextureName = winTextureName;
		this.chunk = chunk;
		
		if (water)  {
			this.setTransparent(false);
			this.setBlendMode(TyrGL.GL_ONE_MINUS_SRC_ALPHA);
			vertexLayout.setSize(VertexLayout.NORMAL, 2);
			vertexLayout.setPos(VertexLayout.UV, 5);
		} else {
			vertexLayout.setPos(DIFFUSE, DIFFUSE_OFFSET);
			vertexLayout.setSize(DIFFUSE, 1);
		}
		

		this.lighted = false;
		
		World.getInstance().getUpdater().addItem(this);
		
		fogProgram = ProgramManager.getInstance().getProgram("TERRAIN_FOG");
		strategicProgram = ProgramManager.getInstance().getProgram("TERRAIN_STRATEGIC");
		
		updateHandles();
	}
	
	public void setGrassSettings(float grassHeight, int countLayers) {
		this.grassHeight = grassHeight;
		this.countLayers = countLayers;
		
		setTransparent(true);
		setBlendMode(TyrGL.GL_ONE_MINUS_SRC_ALPHA);
		
		nodepth = true;
	}
	
	@Override
	public void updateHandles() {
		super.updateHandles();
		winTexture = TextureManager.getInstance().getTexture(winTextureName);
		winTextureHandle = TyrGL.glGetUniformLocation(program.handle, "u_WinTexture");
		bumpMapTextureHandle = TyrGL.glGetUniformLocation(program.handle, "u_BumpMap");
		winterHandle = TyrGL.glGetUniformLocation(program.handle, "u_Winter");
		timeHandle = TyrGL.glGetUniformLocation(program.handle, "u_Time");
		ownerHandle = TyrGL.glGetUniformLocation(program.handle, "u_Owner");
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightPos");
		viewDirectionHandle = TyrGL.glGetUniformLocation(program.handle, "u_CamPos");
		modelMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_M");
		colorStrategic = TyrGL.glGetUniformLocation(program.handle, "u_Color");
		diffuseHandle = TyrGL.glGetAttribLocation(program.handle, "a_Diffuse");
		currentLayerHandle = TyrGL.glGetUniformLocation(program.handle, "u_CurrentLayer");
		grassHeightHandle = TyrGL.glGetUniformLocation(program.handle, "u_GrassHeight");
		furTextureHandle = TyrGL.glGetUniformLocation(program.handle, "u_FurTexture");
		lightDirHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightDir");
		camPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_CamPos");
	}
	
	public void setFogged(boolean state) {
		this.fogged = state;
		
		if (fogged) {
			this.program = fogProgram;
		} else {
			this.program = seasonProgram;
		}
		
		updateHandles();
	}
	
	public void setStrategic(boolean state, Color color) {
		this.strategic = state;
		this.color = color;
		
		if (strategic) {
			this.program = strategicProgram;
		} else {
			this.program = seasonProgram;
		}
		
		updateHandles();
	}
	
	
	public void setWind(float wind) {
		this.wind = wind;
	}
	
	@Override
	protected void passTexture(int textureHandle) {
		super.passTexture(textureHandle);

		if (!fogged && !strategic) {
		    // Set the active texture unit to texture unit 1.
			TyrGL.glActiveTexture(TyrGL.GL_TEXTURE1);
			
		    // Bind the texture to this unit.
			TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, winTexture.getHandle());
		 
		    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
			TyrGL.glUniform1i(winTextureHandle, 1);
			
			if (bumpMapTextureHandle != -1) {
			    // Set the active texture unit to texture unit 1.
				TyrGL.glActiveTexture(TyrGL.GL_TEXTURE3);
				
			    // Bind the texture to this unit.
				TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, TextureManager.getInstance().getTexture("BUMP_MAP_TEST").getHandle());
			 
			    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
				TyrGL.glUniform1i(bumpMapTextureHandle, 3);
			}
			
			if (furTextureHandle != -1) {
				Texture furTexture = TextureManager.getInstance().getTexture("TILES_FUR");
				
			    // Set the active texture unit to texture unit 1.
				TyrGL.glActiveTexture(TyrGL.GL_TEXTURE4);
				
			    // Bind the texture to this unit.
				TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D,furTexture.getHandle());
			 
			    // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 1.
				TyrGL.glUniform1i(furTextureHandle, 4);
			}
		}
		
	}
	
	
	@Override
	public void render(Mesh mesh, float[] modelMatrix) {
		
		super.render(mesh, modelMatrix);
		
		if (camPosHandle != -1) {
			Vector3 camPos = SceneManager.getInstance().getActiveCamera().getAbsolutePos();
			TyrGL.glUniform3f(camPosHandle, camPos.x, camPos.y, camPos.z);
			Light light = SceneManager.getInstance().getLight(0);
			float[] lightDir = light.getLightVector();
			TyrGL.glUniform3f(lightDirHandle, lightDir[0], lightDir[1], lightDir[2]);
		}
		
		if (currentLayerHandle != -1) {
			TyrGL.glUniform1f(currentLayerHandle, (this.iteration+1)/(this.repeatRender+1));
			TyrGL.glUniform1f(grassHeightHandle, grassHeight*this.iteration);
		}
		
		if (lightPosHandle != -1) {
			Light light = SceneManager.getInstance().getLight(0);
			TyrGL.glUniform3f(lightPosHandle, light.getLightVector()[0], light.getLightVector()[1], light.getLightVector()[2]);
		}
		
		if (viewDirectionHandle != -1) {
			Camera camera = SceneManager.getInstance().getActiveCamera();
			TyrGL.glUniform3f(viewDirectionHandle, camera.getLookDirection().x, camera.getLookDirection().y, camera.getLookDirection().z);
		}
		
		if (strategic) {
			TyrGL.glUniform4f(colorStrategic, color.r, color.g, color.b, color.a);
			int heightHandle = TyrGL.glGetUniformLocation(strategicProgram.handle, "u_Height");
			TyrGL.glUniform1f(heightHandle, -0.001f);
			int alphaHandle = TyrGL.glGetUniformLocation(strategicProgram.handle, "u_Alpha");
			TyrGL.glUniform1f(alphaHandle, 1.0f);
		}
		
		if (!fogged && !strategic) {
			Color ambient = SceneManager.getInstance().getAmbientLight();
			
			TyrGL.glUniform4f(ambientHandle, 0.75f+super.color.r,0.75f+super.color.g, 0.75f+super.color.b, ambient.a);
			TyrGL.glUniform1f(winterHandle, World.getInstance().getWinter());
			TyrGL.glUniform1f(ownerHandle, chunk.getOwnerValue());
			TyrGL.glUniform1f(timeHandle, windTime);
		} 
		
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
	
	@Override
	protected void passMesh(Mesh mesh)
	{	
		super.passMesh(mesh);
		FloatBuffer vertexBuffer = mesh.getVertexBuffer();
		if (mesh.isUsingVBO()) {
		    // Pass in the normal information
			if (diffuseHandle != -1) {
			    TyrGL.glVertexAttribPointer(diffuseHandle, 1, TyrGL.GL_FLOAT, false,
			    							getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, DIFFUSE_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
			 
			    TyrGL.glEnableVertexAttribArray(diffuseHandle);
			}
			
		} else {
			if (diffuseHandle != -1) {
			    // Pass in the normal information
			    vertexBuffer.position(DIFFUSE_OFFSET);
			    TyrGL.glVertexAttribPointer(diffuseHandle, 1, TyrGL.GL_FLOAT, false,
			    		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
			 
			    TyrGL.glEnableVertexAttribArray(diffuseHandle);
			}
		}
	}
	
	@Override
	public void onUpdate(float time) {
		windTime += (2-World.getInstance().getWinter())/2 * time * wind * World.getInstance().getPlaySpeed();
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public static float[] createVertexData(int byteStride, int positionOffset, int normalOffset, int uvOffset, float[] vertexData, int startPos, float[] points, float[] uvCoords, short[] drawOrder) {
		// Populate the vertex data
		
		int vertexCount = points.length/3;
		
		int uvCoord = 0;
		int repeat = 1;
		
		for (int i = 0; i < vertexCount; i++) {
			
			int pos = i * byteStride + startPos;
			
			vertexData[pos + positionOffset + 0] = points[i*3];
			vertexData[pos + positionOffset + 1] = points[i*3+1];
			vertexData[pos + positionOffset + 2] = points[i*3+2];
			
			vertexData[pos + normalOffset] = 0;
			
			vertexData[pos + uvOffset + 0] = uvCoords[uvCoord]*repeat;
			vertexData[pos + uvOffset + 1] = uvCoords[uvCoord+1]*repeat;
			
			uvCoord += 2;
			
			if (uvCoord >= uvCoords.length) {
				uvCoord = 0;
				// Increase the coordinate numbers in order to prevent
				// weird clamping effects to occur in most cases
				
				++repeat;
			}
			
		}
		
		return vertexData;
	}
	
	public static Texture createFurTexture(String baseTextureName, float density) {
		
		Texture texture = TextureManager.getInstance().getTexture(baseTextureName);
		
	    //read the width and height of the texture
	    int width = (int) texture.getSize().x;
	    int height = (int) texture.getSize().y;
		
	    IDrawableBitmap bitmap = Media.CONTEXT.createBitmap(width, height);
	    ICanvas canvas = Media.CONTEXT.createCanvas(bitmap);	

	    int totalPixels = width * height;

	    //random number generator
	    Random rand = new Random();

	    //initialize all pixels to transparent black
	    for (int x = 0; x < width; ++x) {
	    	for (int y = 0; y < height; ++y) {
	    		canvas.setRGB(x, y, new Color(0,0,0,0));
	    	}
	    }

	    //compute the number of opaque pixels = nr of hair strands
	    int nrStrands = (int)(density * totalPixels);

	    //fill texture with opaque pixels
	    for (int i = 0; i < nrStrands; i++)
	    {
	        int x, y;
	        //random position on the texture
	        x = rand.nextInt(width);
	        y = rand.nextInt(height);
	        //put color (which has an alpha value of 255, i.e. opaque)
	        canvas.setRGB(x, y, new Color(rand.nextFloat(),0,0,1));
	    }
	    
	    return TextureManager.getInstance().createTexture(baseTextureName + "_FUR", bitmap.toBitmap());
	}
	
}
