package com.tyrfing.games.id17.gui.charts;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.IPaint.TextAlign;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Rect;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class PieChart {
	
	private static class DataSet {
		float value;
		Color color;
		String label;
		
		public DataSet(float value, Color color, String label) {
			this.value = value;
			this.color = color;
			this.label = label;
		}
	}
	
	private ICanvas canvas;
	private IDrawableBitmap bitmap;
	
	private List<DataSet> dataSets = new ArrayList<DataSet>();
	private float total;
	
	private int width;
	private int height;
	
	public PieChart(int width, int height) {
		
		this.width = width;
		this.height = height;
		
	    bitmap = Media.CONTEXT.createBitmap(width, height);
	    canvas = Media.CONTEXT.createCanvas(bitmap);
	}
	
	public void addDataSet(float value, Color color, String label) {
		dataSets.add(new DataSet(value, color, label));
		total += value;
	}
	
	public Texture build(String textureName) {
		
		Rect rect = new Rect(width*0.03f, height*0.03f, width*0.95f, height*0.97f );

		int startDegree = 0;

		for (int i = 0; i < dataSets.size(); ++i)  {
			DataSet dataSet = dataSets.get(i);
			int degree = (int) (360 * dataSet.value / total);
		
			if (i == dataSets.size() - 1) {
				degree = 360 - startDegree;
			}
			
			IPaint paint = Media.CONTEXT.createPaint(canvas);
			paint.setColor(dataSet.color);
			
			canvas.drawArc(rect, -startDegree, -degree, true, paint);

			startDegree += degree;
		}
		
		startDegree = 0;
		
		
		for (int i = 0; i < dataSets.size(); ++i)  {
			DataSet dataSet = dataSets.get(i);
			int degree = (int) (360 * dataSet.value / total);
			
			IPaint textPaint = Media.CONTEXT.createPaint(canvas);
			textPaint.setColor(Color.BLACK);
			textPaint.setTextSize(EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.PC_TARGET ? 50 : 30);
			
			float deg = degree/2.f + startDegree;
			if (deg == 90.0f) {
				deg = 90.1f;
			} else if (deg == 270.0f) {
				deg = 270.1f;
			}
			
			double m = Math.tan(Math.toRadians(deg));
			
			int signX = 1;
			int signY = -1;
	
			if (deg >= 90 && deg <= 270) {
				signX = -1;
				signY = 1;
				textPaint.setTextAlign(TextAlign.RIGHT);
			}
			
			
			float r = (float) Math.sqrt((0.375f*width/2)*(0.375f*width/2) + (0.375f*height/2)*(0.375f*height/2));
			
			Vector2 offset = new Vector2(signX * r, signY * (float) m * r);
			offset.normalize();
			offset = offset.multiply(0.85f*r);
			
			float[] widths = new float[dataSet.label.length()];
			textPaint.getTextWidths(dataSet.label.toCharArray(), 0, dataSet.label.length(), widths);
			float totalWidth = 0;
			for (int j = 0; j < widths.length; ++j) {
				totalWidth += widths[j];
			}
			
			if (offset.x <= 0) {
				totalWidth *= -1;
			}
			
			canvas.drawText(dataSet.label, offset.x + width*0.5f + 4 - totalWidth/2, offset.y + height*0.5f + 4, textPaint);
			
			startDegree += degree;
		}
		
		blurfast();
		
		return TextureManager.getInstance().createTexture(textureName, bitmap.toBitmap());
	}
	
	void blurfast() {
		/*
		Bitmap tmp = Bitmap.createScaledBitmap(bitmap, (int)(width/1.1), (int)(height/1.1), true);
		bitmap.recycle();
		bitmap = Bitmap.createScaledBitmap(tmp, width, height, true);
		tmp.recycle();
		*/
	}
}
