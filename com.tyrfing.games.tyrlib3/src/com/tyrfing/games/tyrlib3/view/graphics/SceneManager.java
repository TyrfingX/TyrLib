package com.tyrfing.games.tyrlib3.view.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.tyrlib3.main.Media;
import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.graphics.VertexLayout;
import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.ISceneQuery;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneObject;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.util.BackgroundWorker;
import com.tyrfing.games.tyrlib3.view.graphics.lighting.DirectionalLight;
import com.tyrfing.games.tyrlib3.view.graphics.lighting.Light;
import com.tyrfing.games.tyrlib3.view.graphics.lighting.PointLight;
import com.tyrfing.games.tyrlib3.view.graphics.materials.DefaultMaterial3;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;
import com.tyrfing.games.tyrlib3.view.graphics.particles.ComplexParticleSystem;
import com.tyrfing.games.tyrlib3.view.graphics.particles.IParticleSystemFactory;
import com.tyrfing.games.tyrlib3.view.graphics.particles.ARenderableParticleSystem;
import com.tyrfing.games.tyrlib3.view.graphics.particles.SimpleParticleSystem;
import com.tyrfing.games.tyrlib3.view.graphics.particles.XMLParticleSystemFactory;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.BoundedRenderable;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Box;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Entity;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.FormattedText2;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.IRenderable;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Image2;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Line2;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Rectangle2;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Skybox;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.Text2;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.GameLoop;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.view.graphics.text.Font;
import com.tyrfing.games.tyrlib3.view.graphics.text.GLText;
import com.tyrfing.games.tyrlib3.view.graphics.texture.TextureAtlas;

/**
 * This singleton class manages the creation and destruction of Scene objects.
 * These objects should always be created via the SceneManager.
 * @author Sascha
 *
 */

public class SceneManager {
	
	private static SceneManager instance;
	private GameLoop renderer;
	
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
	
	public void setRenderer(GameLoop renderer) {
		this.renderer = renderer;
	}
	
	/**
	 * Get the renderer
	 * @return	The renderer
	 */
	
	public GameLoop getRenderer() {
		return renderer;
	}
	
	/**
	 * Creates a new camera object
	 * @param lookAt	The look direction of the camera
	 * @param up		???
	 * @param node		The parent node
	 * @return			A new camera
	 */
	
	public Camera createCamera(Vector3F lookDirection, Vector3F up, SceneNode node) {
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
	
	public Box createBox(Material material, Vector3F min, Vector3F max) {
		Box box = new Box(material, min, max);
		renderer.addRenderable(box);
		return box;
	}
	
	public void destroyRenderable(IRenderable renderable) {
		if (renderable instanceof SceneObject) {
			((SceneObject) renderable).detach();
		}
		if (renderable instanceof BoundedRenderable) {
			renderer.removeRenderable((BoundedRenderable)renderable);
		} else {
			renderer.removeRenderable(renderable);
		}
	}
	
	public void destroyRenderable(IRenderable renderable, int channel) {
		if (renderable instanceof SceneObject) {
			((SceneObject) renderable).detach();
		}
		renderer.removeRenderable(renderable, channel);
		if (renderable instanceof BoundedRenderable) {
			renderer.removeRenderable((BoundedRenderable)renderable, channel);
		} else {
			renderer.removeRenderable(renderable, channel);
		}
	}
	
	
	/**
	 * Create an entity from a file source
	 * @param context	The context for loading the file
	 * @param path		The path to the file
	 * @param useVBO	Whether to use a VBO or not
	 * @return			The newly created entity
	 */
	
	public Entity createEntity(String path, boolean useVBO) {
		return createEntity(path, useVBO, -1, DefaultMaterial3.DEFAULT_LAYOUT);
	}
	
	/**
	 * Create an entity from a file source with static lighting information
	 * baked into the vertex data
	 * @param context	The context for loading the file
	 * @param path		The path to the file
	 * @param useVBO	Whether to use a VBO or not
	 * @param light		Index of the light for ligthing information
	 * @return			The newly created entity
	 */
	
	public Entity createEntity(String path, boolean useVBO, int lightIndex) {
		return createEntity(path, useVBO, lightIndex, DefaultMaterial3.BAKED_LIGHTING_LAYOUT);
	}
	
	/**
	 * Create an entity from a file source with static lighting information
	 * baked into the vertex data
	 * @param context	The context for loading the file
	 * @param path		The path to the file
	 * @param useVBO	Whether to use a VBO or not
	 * @param light		Index of the light for ligthing information
	 * @param layout	Layout of the vertex information
	 * @return			The newly created entity
	 */
	
	public Entity createEntity(String path, boolean useVBO, int lightIndex, VertexLayout layout) {
		
		Light light = lightIndex >= 0 ? getLight(lightIndex) : null;
		
		if (entityFactories.containsKey(path)) {
			Entity entity = entityFactories.get(path).create();
			renderer.addRenderable(entity);
			return entity;
		}
		
		IEntityFactory factory = null;
		
		if (path.endsWith("iqe")) {
			DefaultMaterial3 mat = new DefaultMaterial3(null, 1, 1, null);
			factory = new IQEEntityFactory(path, mat);
			entityFactories.put(path, factory);
		} else if (path.endsWith("iqm")) {
			factory = new IQMEntityFactory(path, layout, useVBO, light);
			entityFactories.put(path, factory);
		} else {
			throw new RuntimeException("Format for loading entity " +  path + " not supported!");
		}
		
		Entity entity = factory.create();
		renderer.addRenderable(entity);
		
		return entity;
	}
	
	public ARenderableParticleSystem createParticleSystem(String path) {
		return createParticleSystem(path, OpenGLRenderer.TRANSLUCENT_CHANNEL_1);
	}
	
	public ARenderableParticleSystem createParticleSystem(String path, int channel) {
		if (particleSystemFactories.containsKey(path)) {
			ARenderableParticleSystem particleSystem = (ARenderableParticleSystem) particleSystemFactories.get(path).create();
			if (particleSystem.isScreenSpace()) {
				renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.OVERLAY_CHANNEL);
			} else {
				renderer.addRenderable((BoundedSceneObject)particleSystem, channel);
			}
			return particleSystem;
		}
		
		IParticleSystemFactory factory = null;
		
		if (path.endsWith("xml")) {
			factory = new XMLParticleSystemFactory(path);
			particleSystemFactories.put(path, factory);
		} else {
			throw new RuntimeException("Format for loading particle system " +  path + " not supported!");
		}
		
		ARenderableParticleSystem particleSystem = (ARenderableParticleSystem) factory.create();
		if (particleSystem.isScreenSpace()) {
			renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.OVERLAY_CHANNEL);
		} else {
			renderer.addRenderable((BoundedSceneObject)particleSystem, channel);
		}
		
		return particleSystem;
	}
	
	public ARenderableParticleSystem createParticleSystem(int maxParticles) {
		ComplexParticleSystem particleSystem = new ComplexParticleSystem(maxParticles, false);
		renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
		return particleSystem;
	}
	
	public ARenderableParticleSystem createSimpleParticleSystem(int maxParticles) {
		SimpleParticleSystem particleSystem = new SimpleParticleSystem(maxParticles);
		renderer.addRenderable((BoundedSceneObject)particleSystem, OpenGLRenderer.TRANSLUCENT_CHANNEL_2);
		return particleSystem;
	}
	
	public Rectangle2 createRectangle2(Vector2F size, Color color) {
		Rectangle2 rect = new Rectangle2(size, color);
		renderer.addRenderable(rect, OpenGLRenderer.OVERLAY_CHANNEL);
		return rect;
	}
	
	public Line2 createLine2(Vector2F from, Vector2F to, Color color, int thickness) {
		Line2 line = new Line2(from, to, color, thickness);
		renderer.addRenderable(line, OpenGLRenderer.OVERLAY_CHANNEL);
		return line;
	}
	
	public Image2 createImage2(Vector2F size, String textureName) {
		Image2 image = new Image2(size, textureName);
		renderer.addRenderable(image, OpenGLRenderer.OVERLAY_CHANNEL);
		return image;
	}
	
	public void loadFont(String name, int size) {
		loadFont(name, name, size);
	}
	
	public void loadFont(String source, String name, int size) {
		GLText.init();
		activeFont = new Font(source, name, Media.CONTEXT.createTextRenderer(source, size));
		activeFont.glText.toTexture();
		fonts.put(name, activeFont);
	}
	
	public void backgroundLoadFont(String name, int size) {
		backgroundLoadFont(name, name, size);
	}
	
	public void backgroundLoadFont(final String source, final String name, final int size) {
		GLText.init();
		final Font font = new Font(source, name);
		fonts.put(name, font);
		
		BackgroundWorker.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				font.glText = Media.CONTEXT.createTextRenderer(source, size);
				renderer.queueEvent(new Runnable() {
					@Override
					public void run() {
						font.glText.toTexture();
						System.out.println("Background loaded Font: " + name);
					}
				});
			}
		});
	}
	
	public Font getFont(String name) {
		return fonts.get(name);
	}
	
	public void recreateFonts() {
		for (String fontName : fonts.keySet()) {
			Font font = fonts.get(fontName);
			font.glText = Media.CONTEXT.createTextRenderer(font.source, font.glText.getSize());
			font.glText.toTexture();
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

	public void setViewport(Viewport viewport) {
		renderer.setViewport(viewport);
	}
	
	public Vector2F getViewportSize() {
		return new Vector2F(renderer.getViewport().getWidth(), renderer.getViewport().getHeight());
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
	
	public Skybox createSkybox(String texture, Vector3F extents) {
		Skybox.enableSkyboxes();
		Skybox skybox = new Skybox(texture, extents.multiply(-1), extents);
		SceneManager.getInstance().getRenderer().addRenderable(skybox, OpenGLRenderer.BACKGROUND_CHANNEL);
		
		return skybox;
	}

	public void removeParticleSystemFactory(String path) {
		particleSystemFactories.remove(path);
	}
}
