package com.tyrlib2.graphics.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.tyrlib2.graphics.lighting.DirectionalLight;
import com.tyrlib2.graphics.lighting.Light;
import com.tyrlib2.graphics.lighting.LightingType;
import com.tyrlib2.graphics.lighting.PointLight;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.particles.ComplexParticleSystem;
import com.tyrlib2.graphics.particles.IParticleSystemFactory;
import com.tyrlib2.graphics.particles.ParticleSystem;
import com.tyrlib2.graphics.particles.SimpleParticleSystem;
import com.tyrlib2.graphics.particles.XMLParticleSystemFactory;
import com.tyrlib2.graphics.renderables.Box;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderables.FormattedText2;
import com.tyrlib2.graphics.renderables.Image2;
import com.tyrlib2.graphics.renderables.Line2;
import com.tyrlib2.graphics.renderables.Rectangle2;
import com.tyrlib2.graphics.renderables.Skybox;
import com.tyrlib2.graphics.renderables.Text2;
import com.tyrlib2.graphics.renderer.Camera;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.main.OpenGLActivity;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;
import com.tyrlib2.util.IEntityFactory;
import com.tyrlib2.util.IQEEntityFactory;
import com.tyrlib2.util.IQMEntityFactory;

/**
 * This singleton class manages the creation and destruction of Scene objects.
 * These objects should always be created via the SceneManager.
 * @author Sascha
 *
 */

public class SceneManager {
	
	private static SceneManager instance;
	private OpenGLRenderer renderer;
	
	private List<Light> lights;
	
	/** Factories for creating entities **/
	private Map<String, IEntityFactory> entityFactories;
	
	/** Factories for creating particle systems **/
	private Map<String, IParticleSystemFactory> particleSystemFactories;
	
	/** Loaded fonts **/
	private Map<String, Font> fonts;
	
	/** Global ambient illumination **/
	private Color ambientLight;
	
	private Map<String, TextureAtlas> atlases;
	
	private Font activeFont;
	
	public SceneManager() {
		lights = new ArrayList<Light>();
		
		// Per default completely slightly light the scene so that
		// no further setup needs to be done to actually see something
		ambientLight = new Color(0.5f,0.5f,0.5f,0);
		
		entityFactories = new HashMap<String, IEntityFactory>();
		particleSystemFactories = new HashMap<String, IParticleSystemFactory>();
		
		fonts = new HashMap<String, Font>();
		
		atlases = new HashMap<String, TextureAtlas>();
	}
	
	public static SceneManager getInstance() {
		if (instance == null) {
			instance = new SceneManager();
		}
		
		return instance;
	}
	
	public void destroy() {
		renderer.destroy();
		lights.clear();
		renderer = null;
		instance = null;
	}
	
	public void setRenderer(OpenGLRenderer renderer) {
		this.renderer = renderer;
	}
	
	/**
	 * Get the renderer
	 * @return	The renderer
	 */
	
	public OpenGLRenderer getRenderer() {
		return renderer;
	}
	
	/**
	 * Creates a new camera object
	 * @param lookAt	The look direction of the camera
	 * @param up		???
	 * @param node		The parent node
	 * @return			A new camera
	 */
	
	public Camera createCamera(Vector3 lookDirection, Vector3 up, SceneNode node) {
		Camera camera = new Camera(up);
		node.attachSceneObject(camera);
		camera.setLookDirection(lookDirection);
		return camera;
	}
	
	public Light createLight(Light.Type type) {
		
		Light light = null;
		
		switch(type) {
		case POINT_LIGHT:
			light = new PointLight();
			renderer.addRenderable((PointLight) light);
			break;
		case DIRECTIONAL_LIGHT:
			light = new DirectionalLight();
			break;
		default:
			break;
		}
		
		if (light != null) {
			lights.add(light);
		}
		
		return light;
	}
	
	/**
	 * Get the number of created lights
	 * @return	The number of created lights
	 */
	
	public int getLightCount() {
		return lights.size();
	}
	
	/**
	 * Get a light object
	 * @param index	Index of the light
	 * @return	The light object
	 */
	
	public Light getLight(int index) {
		return lights.get(index);
	}
	
	public SceneNode getRootSceneNode() {
		return renderer.getRootSceneNode();
	}
	
	/**
	 * Change the color of the ambient light
	 * @param color	The new color of the global ambient light
	 */
	
	public void setAmbientLight(Color color) {
		this.ambientLight = color;
	}
	
	
	/**
	 * Get the global ambient illumination
	 * @return	The global ambient illumination
	 */
	public Color getAmbientLight() {
		return ambientLight;
	}
	
	
	public void addFrameListener(IFrameListener frameListener) {
		if (renderer != null) {
			renderer.addFrameListener(frameListener);
		}
	}
	
	public void removeFrameListener(IFrameListener frameListener) {
		if (renderer != null) {
			renderer.removeFrameListener(frameListener);
		}
	}
	
	public Box createBox(Material material, Vector3 min, Vector3 max) {
		Box box = new Box(material, min, max);
		renderer.addRenderable(box);
		return box;
	}
	
	public void destroyRenderable(IRenderable renderable) {
		if (renderable instanceof SceneObject) {
			((SceneObject) renderable).detach();
		}
		renderer.removeRenderable(renderable);
	}
	
	public void destroyRenderable(IRenderable renderable, int channel) {
		if (renderable instanceof SceneObject) {
			((SceneObject) renderable).detach();
		}
		renderer.removeRenderable(renderable, channel);
	}
	
	/**
	 * Create an entity from a file source
	 * @param context	The context for loading the file
	 * @param path		The path to the file
	 * @return			The newly created entity
	 */
	public Entity createEntity(Context context, String path) {
		if (entityFactories.containsKey(path)) {
			Entity entity = entityFactories.get(path).create();
			renderer.addRenderable(entity);
			return entity;
		}
		
		IEntityFactory factory = null;
		
		if (path.endsWith("iqe")) {
			DefaultMaterial3 mat = new DefaultMaterial3(context, null, 1, 1, LightingType.PER_PIXEL, null);
			factory = new IQEEntityFactory(context, path, mat);
			entityFactories.put(path, factory);
		} else if (path.endsWith("iqm")) {
			DefaultMaterial3 mat = new DefaultMaterial3(context, null, 1, 1, LightingType.PER_PIXEL, null);
			factory = new IQMEntityFactory(context, path, mat);
			entityFactories.put(path, factory);
		} else {
			throw new RuntimeException("Format for loading entity " +  path + " not supported!");
		}
		
		Entity entity = factory.create();
		renderer.addRenderable(entity);
		
		return entity;
	}
	
	public ParticleSystem createParticleSystem(Context context, String path) {
		if (particleSystemFactories.containsKey(path)) {
			ParticleSystem particleSystem = particleSystemFactories.get(path).create();
			renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL);
			return particleSystem;
		}
		
		IParticleSystemFactory factory = null;
		
		if (path.endsWith("xml")) {
			factory = new XMLParticleSystemFactory(path, context);
			particleSystemFactories.put(path, factory);
		} else {
			throw new RuntimeException("Format for loading particle system " +  path + " not supported!");
		}
		
		ParticleSystem particleSystem = factory.create();
		renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL);
		
		return particleSystem;
	}
	
	public ParticleSystem createParticleSystem(int maxParticles) {
		ComplexParticleSystem particleSystem = new ComplexParticleSystem(maxParticles);
		renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL);
		return particleSystem;
	}
	
	public ParticleSystem createSimpleParticleSystem(int maxParticles) {
		SimpleParticleSystem particleSystem = new SimpleParticleSystem(maxParticles);
		renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL);
		return particleSystem;
	}
	
	public Rectangle2 createRectangle2(Vector2 size, Color color) {
		Rectangle2 rect = new Rectangle2(size, color);
		renderer.addRenderable(rect, OpenGLRenderer.OVERLAY_CHANNEL);
		return rect;
	}
	
	public Line2 createLine2(Vector2 from, Vector2 to, Color color, int thickness) {
		Line2 line = new Line2(from, to, color, thickness);
		renderer.addRenderable(line, OpenGLRenderer.OVERLAY_CHANNEL);
		return line;
	}
	
	public Image2 createImage2(Vector2 size, String textureName) {
		Image2 image = new Image2(size, textureName);
		renderer.addRenderable(image, OpenGLRenderer.OVERLAY_CHANNEL);
		return image;
	}
	
	public void loadFont(String name, int size, Context context) {
		loadFont(name, name, size, context);
	}
	
	public void loadFont(String source, String name, int size, Context context) {
		GLText glText = new GLText(context.getAssets());
		glText.load( source, size, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
		glText.setScale(1);
		activeFont = new Font(source, name, glText);
		fonts.put(name, activeFont);
	}
	
	public Font getFont(String name) {
		return fonts.get(name);
	}
	
	public void recreateFonts(Context context) {
		for (String fontName : fonts.keySet()) {
			Font font = fonts.get(fontName);
			GLText glText = new GLText(context.getAssets());
			glText.load(font.source, font.glText.getSize(), 2, 2 );
			font.glText = glText;
		}
	}
	
	public Text2 createText2(String text, int rotation, Color color) {
		Text2 text2 = new Text2(text, rotation, color, activeFont);
		renderer.addRenderable(text2, OpenGLRenderer.OVERLAY_CHANNEL);
		return text2;
	}
	
	public FormattedText2 createFormattedText2(String text, int rotation, Color color) {
		FormattedText2 text2 = new FormattedText2(text, rotation, color, activeFont);
		renderer.addRenderable(text2, OpenGLRenderer.OVERLAY_CHANNEL);
		return text2;
	}
	
	public Camera getActiveCamera() {
		return renderer.getCamera();
	}
	
	public Viewport getViewport() {
		return renderer.getViewport();
	}
	
	public Vector2 getViewportSize() {
		return new Vector2(renderer.getViewport().getWidth(), renderer.getViewport().getHeight());
	}
	
	public int getViewportWidth() {
		return getViewport().getWidth();
	}
	
	public int getViewportHeight() {
		return getViewport().getHeight();
	}
	
	public float getViewportRatio() {
		return getViewport().getRatio();
	}

	public void performSceneQuery(ISceneQuery query) {
		renderer.getOctree(OpenGLRenderer.DEFAULT_CHANNEL).query(query);
	}

	public void addTextureAtlas(String name, TextureAtlas atlas) {
		atlases.put(name, atlas);
	}
	
	public void removeTextureAtlas(String name) {
		atlases.remove(name);
	}
	
	public TextureAtlas getTextureAtlas(String name) {
		return atlases.get(name);
	}
	
	public Skybox createSkybox(Context context, String texture, Vector3 extents) {
		Skybox.enableSkyboxes(OpenGLActivity.CONTEXT);
		Skybox skybox = new Skybox(texture, extents.multiply(-1), extents);
		SceneManager.getInstance().getRenderer().addRenderable(skybox, OpenGLRenderer.BACKGROUND_CHANNEL);
		
		return skybox;
	}
}
