package com.tyrfing.games.id17.mapgen.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

class DrawingPoint extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4548192757666570989L;
	private Ellipse2D point;
	
	public DrawingPoint() {
		point = new Ellipse2D.Double(0, 0, 10, 10);
		this.setOpaque(false);
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        Graphics2D g2d = (Graphics2D) g;
        g2d.fill(point);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(13, 13);
    }
}