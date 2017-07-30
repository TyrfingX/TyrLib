package com.tyrfing.games.id17.mapgen.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

class DrawingLine extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4548192757666570989L;
	private Line2D[] lines;
	
	public DrawingLine(Line2D[] lines) {
		this.lines = lines;
		this.setOpaque(false);
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(	RenderingHints.KEY_ANTIALIASING,
                				RenderingHints.VALUE_ANTIALIAS_ON); 
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < lines.length; ++i) {
        	g2d.draw(lines[i]);
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}