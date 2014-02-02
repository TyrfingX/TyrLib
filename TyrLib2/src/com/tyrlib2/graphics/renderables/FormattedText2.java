package com.tyrlib2.graphics.renderables;

import java.util.ArrayList;
import java.util.List;

import com.tyrlib2.graphics.renderer.IBlendable;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.graphics.text.IGLText;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * This class renders takes while taking formatting marks into account. These are:
 * 
 * \n				Create a new line
 * <#rrggbb>		The text followed by this tag will be colored in the color corresponding to the hex value of #rrggbb. Where rr is the red component
 * 					gg the green component and bb the blue component. No nesting allowed.
 * \#				This ends colored text.
 * <rVALUE>			Rotate the following text by VALUE. No nesting allowed
 * \r				End the rotation of the text.
 * @author Sascha
 *
 */

public class FormattedText2 extends SceneObject implements IRenderable, IBlendable {
	
	public enum ALIGNMENT {
		LEFT,
		CENTER
	}
	
	private class TextSection {
		Color color;
		String text;
		int rotationValue;
		float[] rotation = new float[16];
		float xOffset;
		float yOffset;
		
		public TextSection(Color color, String text, int rotation, float xOffset, float yOffset) {
			this.color = color;
			this.text = text;
			this.rotationValue = rotation;
			Quaternion.fromAxisAngle(new Vector3(0,0,1), rotation).toMatrix(this.rotation);
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}
	}
	
	private Color baseColor;
	private String text;
	private int baseRotation;
	private Font font;
	private List<TextSection> textSections;
	private ALIGNMENT alignment;
	private Vector2 size;
	
	public FormattedText2(String text, int baseRotation, Color baseColor, Font font) {
		this.baseColor = baseColor;
		this.baseRotation = baseRotation;
		this.text = text;
		this.font = font;
		this.size = new Vector2();
		this.alignment = ALIGNMENT.LEFT;
		
		parseText();
	}

	@Override
	public void render(float[] vpMatrix) {
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
		IGLText glText = font.glText;
		Vector3 pos = parent.getCachedAbsolutePosVector();
		for (int i = 0; i < textSections.size(); ++i) {
			TextSection section = textSections.get(i);
			glText.begin( section.color.r, section.color.g, section.color.b, section.color.a, vpMatrix );
			if (alignment == ALIGNMENT.LEFT) {
				glText.draw( section.text, pos.x + section.xOffset, pos.y + section.yOffset - font.glText.getCharHeight(), section.rotation);   
			} else if (alignment == ALIGNMENT.CENTER) {
				glText.drawC( section.text, pos.x + section.xOffset, pos.y + section.yOffset - font.glText.getCharHeight(), section.rotation); 
			}
			glText.end();
		}
		Program.blendDisable();
		
		Program.resetCache();
	}
	
	private void parseText() {
		textSections = new ArrayList<TextSection>();
		float xOffset = 0;
		float yOffset = 0;
		
		float maxX = 0;
		
		StringBuilder builder = new StringBuilder();
		Color color = baseColor;
		int rotation = baseRotation;
		
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			
			if (c == '\\') {
				if (i < text.length() - 1) {
					char d = text.charAt(++i);
					if (d == '#') {
						// We want to end any special coloring we currently have
						String sectionText = builder.toString();
						TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset);
						textSections.add(textSection);
						builder = new StringBuilder();
						
						color = baseColor;
						xOffset += font.glText.getLength(sectionText);
					} else {
						builder.append(c);
						builder.append(d);
					}

				}
			} else if (c == '\n') {
				// Line break here
				String sectionText = builder.toString();
				TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset);
				textSections.add(textSection);
				builder = new StringBuilder();
				xOffset += font.glText.getLength(sectionText);
				
				yOffset -= font.glText.getCharHeight() - font.glText.getDescent();
				
				if (xOffset > maxX) {
					maxX = xOffset;
				}
				xOffset = 0;
			} else if (c == '\r') {
				// We want to end any special rotation we currently have
				String sectionText = builder.toString();
				TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset);
				textSections.add(textSection);
				builder = new StringBuilder();
				
				rotation = baseRotation;
				xOffset += font.glText.getLength(sectionText);
			} else if (c == '<') {
				if (i < text.length() - 1) {
					char d = text.charAt(++i);
				
					if (d == '#') {
						
						String sectionText = builder.toString();
						TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset);
						textSections.add(textSection);
						builder = new StringBuilder();
						xOffset += font.glText.getLength(sectionText);
						
						// We want a different color for our text here
						String redHex = "" + text.charAt(i+1) + text.charAt(i+2); 
						int red = Integer.parseInt(redHex, 16);
						i += 2;
						
						String greenHex = "" + text.charAt(i+1) + text.charAt(i+2); 
						int green = Integer.parseInt(greenHex, 16);
						i += 2;
						
						String blueHex = "" + text.charAt(i+1) + text.charAt(i+2); 
						int blue = Integer.parseInt(blueHex, 16);
						i += 2;
						
						++i;
						
						color = new Color(red / 255.f, green / 255.f, blue / 255.f, 1);
					} else if (d == 'r') {
						// We want a different rotation for our text here
						
						String sectionText = builder.toString();
						TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset);
						textSections.add(textSection);
						builder = new StringBuilder();
						xOffset += font.glText.getLength(sectionText);
						d = text.charAt(++i);
						
						String rotationText = "";
						while(d != '>') {
							rotationText += d;
							d = text.charAt(++i);
						}
						
						rotation = Integer.parseInt(rotationText);
						
					} else {
						builder.append(c);
						builder.append(d);
					}
				}
			} else {
				builder.append(c);
			}
			

		}
		
		String sectionText = builder.toString();
		TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset);
		textSections.add(textSection);
		xOffset += font.glText.getLength(sectionText);
		
		if (xOffset > maxX) {
			maxX = xOffset;
		}
		
		size.x = maxX;
		size.y = -yOffset + font.glText.getCharHeight();
	}
	
	public void setText(String text) {
		this.text = text;
		parseText();
	}
	
	public String getText() {
		return text;
	}

	public ALIGNMENT getAlignment() {
		return alignment;
	}

	public void setAligment(ALIGNMENT alignment) {
		this.alignment = alignment;
	}
	
	public Vector2 getSize() {
		return size;
	}
	
	public void setBaseColor(Color baseColor) {
		this.baseColor.r = baseColor.r;
		this.baseColor.g = baseColor.g;
		this.baseColor.b = baseColor.b;
		this.baseColor.a = baseColor.a;
	}
	
	public void setBaseRotation(int baseRotation) {
		this.baseRotation = baseRotation;
		parseText();
	}
	
	public int getBaseRotation() {
		return baseRotation;
	}
	
	public Color getBaseColor() {
		return baseColor;
	}

	public Font getFont() {
		return font;
	}

	@Override
	public float getAlpha() {
		return baseColor.a;
	}

	@Override
	public void setAlpha(float alpha) {
		baseColor.a = alpha;
		parseText();
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
}
