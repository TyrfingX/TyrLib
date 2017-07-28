package com.tyrfing.games.tyrlib3.graphics.renderables;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.graphics.renderer.IBlendable;
import com.tyrfing.games.tyrlib3.graphics.renderer.Program;
import com.tyrfing.games.tyrlib3.graphics.renderer.RenderableSceneObject;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.renderer.Viewport;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.graphics.text.Font;
import com.tyrfing.games.tyrlib3.graphics.text.IGLText;
import com.tyrfing.games.tyrlib3.math.Quaternion;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.math.Vector3F;
import com.tyrfing.games.tyrlib3.util.Color;

/**
 * This class renders takes while taking formatting marks into account. These are:
 * 
 * \n					Create a new line
 * <#rrggbb>			The text followed by this tag will be colored in the color corresponding to the hex value of #rrggbb. Where rr is the red component
 * 						gg the green component and bb the blue component. No nesting allowed.
 * \#					This ends colored text.
 * <rVALUE>				Rotate the following text by VALUE. No nesting allowed
 * \r					End the rotation of the text.
 * <link=VALUE>			Creates a link calling the event VALUE upon clicking
 * \l					Ends a link
 * <img ATLAS REGION>	
 * @author Sascha
 *
 */

public class FormattedText2 extends RenderableSceneObject implements IBlendable {
	
	public static final Color LINK_COLOR = new Color(0.2f,0.2f,1,1);
	
	public enum ALIGNMENT {
		LEFT,
		CENTER,
		RIGHT
	}
	
	public static final float[] NO_ROTATION = Quaternion.fromAxisAngle(Vector3F.UNIT_Z, 0).toMatrix();
	
	public static class TextSection {
		public Color color;
		public String text;
		public int rotationValue;
		public float[] rotation;
		public float xOffset;
		public float yOffset;
		public String link;
		public Vector2F size;
		public Vector2F pos;
		public String atlasName;
		public String regionName;
		
		public TextSection(Color color, String text, int rotation, float xOffset, float yOffset, String link) {
			this.color = color;
			this.text = text;
			this.rotationValue = rotation;
			if (rotation != 0) {
				this.rotation = new float[16];
				Quaternion.fromAxisAngle(Vector3F.UNIT_Z, rotation).toMatrix(this.rotation);
			} else {
				this.rotation = NO_ROTATION;
			}
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.link = link;
		}
		
		public TextSection(Color color, String text, float xOffset, float yOffset, String link) {
			this.color = color;
			this.text = text;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.link = link;
			this.rotation = NO_ROTATION;
		}
	}
	
	private Color baseColor;
	private String text;
	private int baseRotation;
	private Font font;
	public List<TextSection> textSections;
	private ALIGNMENT alignment;
	private Vector2F size;
	private int insertionID;
	private float maxWidth;
	
	public FormattedText2(String text, int baseRotation, Color baseColor, Font font) {
		this.baseColor = baseColor;
		this.baseRotation = baseRotation;
		this.text = text;
		this.font = font;
		this.size = new Vector2F();
		this.alignment = ALIGNMENT.LEFT;
		parseText();
	}

	@Override
	public void render(float[] vpMatrix) {
		
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
		IGLText glText = font.glText;
		Vector3F pos = parent.getCachedAbsolutePosVector();
		for (int i = 0; i < textSections.size(); ++i) {
			TextSection section = textSections.get(i);
			if (section.atlasName == null && !section.text.equals("")) {
				glText.begin( section.color.r, section.color.g, section.color.b, section.color.a * (section.color != baseColor ? baseColor.a : 1), vpMatrix );
				if (alignment == ALIGNMENT.LEFT) {
					glText.draw( section.text, pos.x + section.xOffset, pos.y + section.yOffset - font.glText.getCharHeight(), section.rotation);   
				} else if (alignment == ALIGNMENT.CENTER) {
					glText.drawCX( section.text, pos.x + section.xOffset, pos.y + section.yOffset - font.glText.getCharHeight()); 
				} else if (alignment == ALIGNMENT.RIGHT) {
					float length = glText.getLength(section.text);
					glText.draw( section.text, pos.x + section.xOffset - length, pos.y + section.yOffset - font.glText.getCharHeight(), section.rotation); 
				}
				glText.end();
				//glText.end();
			} 
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
		String link = "";
		
		float length = 0;
		Viewport viewport = SceneManager.getInstance().getViewport();
		
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			
			if (c == '\\') {
				if (i < text.length() - 1) {
					char d = text.charAt(++i);
					if (d == '#') {
						// We want to end any special coloring we currently have
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						builder.setLength(0);
						
						color = baseColor;
						if (alignment == ALIGNMENT.CENTER) {
							xOffset += font.glText.getLength(sectionText) / 2;
						} else {
							xOffset += font.glText.getLength(sectionText);
						}
					} else if (d == 'l') {
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						builder.setLength(0);
						
						color = baseColor;
						if (alignment == ALIGNMENT.CENTER) {
							xOffset += font.glText.getLength(sectionText) / 2;
						} else {
							xOffset += font.glText.getLength(sectionText);
						}
						link = "";
					} else if (d == 'n') {
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						
						builder.setLength(0);
						if (alignment == ALIGNMENT.CENTER) {
							xOffset += font.glText.getLength(sectionText) / 2;
						} else {
							xOffset += font.glText.getLength(sectionText);
						}
						
						yOffset -= font.glText.getCharHeight() - font.glText.getDescent()/2 - font.glText.getAscent()/2;
						
						if (xOffset > maxX) {
							maxX = xOffset;
						}
						xOffset = 0;
						length = 0;
					} else {
						builder.append(c);
						builder.append(d);
					}

				}
			} else if (c == '\n') {
				// Line break here
				String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
				
				builder.setLength(0);
				if (alignment == ALIGNMENT.CENTER) {
					xOffset += font.glText.getLength(sectionText) / 2;
				} else {
					xOffset += font.glText.getLength(sectionText);
				}
				
				yOffset -= font.glText.getCharHeight() - font.glText.getDescent()/2 - font.glText.getAscent()/2;
				
				if (xOffset > maxX) {
					maxX = xOffset;
				}
				xOffset = 0;
				length = 0;
			} else if (c == '\r') {
				// We want to end any special rotation we currently have
				String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
				builder.setLength(0);
				
				rotation = baseRotation;
				xOffset += font.glText.getLength(sectionText);
			} else if (c == '<') {
				if (i < text.length() - 1) {
					char d = text.charAt(++i);
				
					if (d == '#') {
						
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						builder.setLength(0);
						if (alignment == ALIGNMENT.CENTER) {
							xOffset += font.glText.getLength(sectionText) / 2;
						} else {
							xOffset += font.glText.getLength(sectionText);
						}
						
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
						
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						builder.setLength(0);
						xOffset += font.glText.getLength(sectionText);
						d = text.charAt(++i);
						
						while(d != '>') {
							builder.append(d);
							d = text.charAt(++i);
						}
						
						rotation = Integer.parseInt(builder.toString());
						builder.setLength(0);
					} else if (d == 'l') {
						// This block of text is a link
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						builder.setLength(0);
						if (alignment == ALIGNMENT.CENTER) {
							xOffset += font.glText.getLength(sectionText)/ 2;
						} else {
							xOffset += font.glText.getLength(sectionText);
						}
						
						color = LINK_COLOR;
						++i; //i
						++i; //n
						++i; //k
						++i; //=
						
						d = text.charAt(++i);
						link = "";
						while(d != '>') {
							builder.append(d);
							d = text.charAt(++i);
						}
						
						link = builder.toString();
						builder.setLength(0);
					} else if (d == 'i') {
						String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
						if (alignment == ALIGNMENT.CENTER) {
							xOffset += font.glText.getLength(sectionText) / 2;
						} else {
							xOffset += font.glText.getLength(sectionText);
						}
						
						++i; //m
						++i; //g
						++i; //=
						
						d = text.charAt(++i);
						String atlasName = "";
						while(d != ' ') {
							atlasName += d;
							d = text.charAt(++i);
						}
						
						d = text.charAt(++i);
						String regionName = "";
						while(d != '>') {
							regionName += d;
							d = text.charAt(++i);
						}
						
						builder.setLength(0);
						addCurrentSection(builder, color, rotation, xOffset, yOffset, link, atlasName, regionName, 0);
						
						xOffset += font.glText.getCharHeight()*0.8f;
						
					} else {
						builder.append(c);
						builder.append(d);
					}
				}
			} else {
				length += font.glText.getLength(String.valueOf(c));
				builder.append(c);
				
				if ((xOffset + length) / viewport.getWidth() >= maxWidth && maxWidth > 0) {
					String line = builder.toString();
					builder.setLength(0);
					String[] words = line.split(" ");
					for (int j = 0; j < words.length - 1; ++j) {
						builder.append(words[j]);
						if (j != words.length - 2) {
							builder.append(" ");
						}
					}
					
					String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
					
					builder.setLength(0);
					if (alignment == ALIGNMENT.CENTER) {
						xOffset += font.glText.getLength(sectionText) / 2;
					} else {
						xOffset += font.glText.getLength(sectionText);
					}
					
					yOffset -= font.glText.getCharHeight() - font.glText.getDescent()/2 - font.glText.getAscent()/2;
					
					if (xOffset > maxX) {
						maxX = xOffset;
					}
					
					xOffset = 0;
					line = (words.length > 0) ? (words[words.length-1]) : "";
					if (c == ' ') line += " ";
					length = font.glText.getLength(line);
					builder.append(line);
				}
			}
			

		}
		
		String sectionText = addCurrentSection(builder, color, rotation, xOffset, yOffset, link, null, null, 0);
		xOffset += font.glText.getLength(sectionText);
		
		if (xOffset > maxX) {
			maxX = xOffset;
		}
		
		size.x = maxX;
		size.y = -yOffset + font.glText.getCharHeight() + font.glText.getDescent() / 2; 
	}
	
	private String addCurrentSection(StringBuilder builder, Color color, int rotation, float xOffset, float yOffset, String link, String atlasName, String regionName, int imgSize) {
		String sectionText = builder.toString();
		Viewport viewport = SceneManager.getInstance().getViewport();
		TextSection textSection = new TextSection(color, sectionText, rotation, xOffset, yOffset, link);
		if (alignment == ALIGNMENT.CENTER) {
			for (int j = 0; j < textSections.size(); ++j) {
				if (textSections.get(j).yOffset == yOffset) {
					textSections.get(j).xOffset -= font.glText.getLength(sectionText) / 2;
					if (textSections.get(j).pos != null) {
						textSections.get(j).pos.x -= font.glText.getLength(sectionText) / (2 * viewport.getWidth());
					}
				}
			}
		}
		
		if (link != null && !link.equals("")) {
			textSection.size = new Vector2F();
			textSection.size.x = font.glText.getLength(sectionText);
			textSection.size.y = font.glText.getCharHeight();
			textSection.size.x /= viewport.getWidth();
			textSection.size.y /= viewport.getHeight();
			
			textSection.pos = new Vector2F();
			textSection.pos.x = xOffset;
			if (alignment == ALIGNMENT.CENTER) {
				textSection.pos.x -= font.glText.getLength(sectionText)/2;
			}
			textSection.pos.y = -yOffset;
			textSection.pos.x /= viewport.getWidth();
			textSection.pos.y /= viewport.getHeight();
		}
		
		textSection.atlasName = atlasName;
		textSection.regionName = regionName;	
		
		if (atlasName != null) {
			textSection.size = new Vector2F();
			textSection.size.x = font.glText.getCharHeight()*0.7f;
			textSection.size.y = font.glText.getCharHeight()*0.7f;
			
			textSection.pos = new Vector2F();
			textSection.pos.x = xOffset + font.glText.getCharHeight()*0.15f;
			textSection.pos.y = -yOffset - textSection.size.y + font.glText.getCharHeight()*0.15f;
			textSection.pos.x /= viewport.getWidth();
			textSection.pos.y /= viewport.getHeight();
			textSection.size.x /= viewport.getWidth();
			textSection.size.y /= viewport.getHeight();
		}
		
		textSections.add(textSection);
		return sectionText;
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
		if (alignment != this.alignment) {
			this.alignment = alignment;
			parseText();
		}
	}
	
	public Vector2F getSize() {
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

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
	}
}
