package com.tyrlib2.graphics.renderer;

import com.tyrlib2.math.Vector2;


public class TextureRegion {

   //--Members--//
   public float u1, v1;                               // Top/Left U,V Coordinates
   public float u2, v2;                               // Bottom/Right U,V Coordinates

   //--Constructor--//
   // D: calculate U,V coordinates from specified texture coordinates
   // A: texWidth, texHeight - the width and height of the texture the region is for
   //    x, y - the top/left (x,y) of the region on the texture (in pixels)
   //    width, height - the width and height of the region on the texture (in pixels)
   public TextureRegion(float texWidth, float texHeight, float x, float y, float width, float height)  {
      this.u1 = x / texWidth;                         // Calculate U1
      this.v1 = y / texHeight;                        // Calculate V1
      this.u2 = this.u1 + ( width / texWidth );       // Calculate U2
      this.v2 = this.v1 + ( height / texHeight );     // Calculate V2
   }
   
   public TextureRegion(TextureRegion other) {
	   this.u1 = other.u1;
	   this.v1 = other.v1;
	   this.u2 = other.u2;
	   this.v2 = other.v2;
   }
   
   public TextureRegion(float u1, float v1, float u2, float v2) {
	   this.u1 = u1;
	   this.v1 = v1;
	   this.u2 = u2;
	   this.v2 = v2;
   }
   
   public TextureRegion(Vector2 texSize, Vector2 min, Vector2 size) {
	   this(texSize.x, texSize.y, min.x, min.y, size.x, size.y);
   }
   
   public TextureRegion() {
	   u1 = 0;
	   v1 = 0;
	   u2 = 1;
	   v2 = 1;
   }
}
