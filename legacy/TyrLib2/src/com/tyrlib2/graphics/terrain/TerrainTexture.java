package com.tyrlib2.graphics.terrain;

import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;


/**
 * This class represents a texture used for rendering terrain.
 * @author Sascha
 *
 */

public class TerrainTexture {
	private Texture texture;
	
	/** optimal height region **/
	private float heightRegion;
	private float heightRange = 1;
	private float heightWeight;
	
	/** optimatl slope region **/
	private float slopeRegion;
	private float slopeRange = 1;
	private float slopeWeight;
	
	public TerrainTexture(Texture texture) {
		this.texture = texture;
	}
	
	public TerrainTexture(String textureName) {
		this.texture = TextureManager.getInstance().getTexture(textureName);
	}
	
	public void setHeightWeight(float heightRegion, float heightRange, float heightWeight) {
		this.heightRegion = heightRegion;
		this.heightRange = heightRange;
		this.heightWeight = heightWeight;
	}
	
	public void setSlopeWeight(float slopeRegion, float slopeRange, float slopeWeight) {
		this.slopeRegion = slopeRegion;
		this.slopeRange = slopeRange;
		this.slopeWeight = slopeWeight;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	/**
	 * Get how important this texture is for a specific height and slope
	 * @return
	 */
	public float getWeight(float height, float slope) {
		float weight = 0;
		float w = (float) (1.0 - ((Math.abs(height - heightRegion) / heightRange)));
		
			weight += w * heightWeight;

		
		w = (float) (1.0 - ((Math.abs(slope - slopeRegion) / slopeRange)));

			weight += w * slopeWeight;

		
		if (weight < 0) {
			weight = 0;
		}
		
		return weight;
	}
}
