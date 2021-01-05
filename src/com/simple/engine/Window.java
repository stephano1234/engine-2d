package com.simple.engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Window {

	private JFrame frame;
	private BufferedImage image;
	private Canvas canvas;
	private BufferStrategy bs;
	private Graphics graphics;
	
	public Window(Engine engine) {
		this.image = new BufferedImage(engine.getWidth(), engine.getHeight(), BufferedImage.TYPE_INT_RGB);
		this.canvas = new Canvas();
		Dimension d = new Dimension((int) (engine.getWidth() * engine.getScale()), (int) (engine.getHeight() * engine.getScale()));
		this.canvas.setPreferredSize(d);
		this.canvas.setMinimumSize(d);
		this.canvas.setMaximumSize(d);
		this.frame = new JFrame(engine.getTitle());
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		this.frame.add(canvas, BorderLayout.CENTER);
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		this.frame.setVisible(true);
		this.canvas.createBufferStrategy(2);
		this.bs = this.canvas.getBufferStrategy();
		this.graphics = this.bs.getDrawGraphics();
	}
	
	public void update() {
		this.graphics.drawImage(this.image, 0, 0, this.canvas.getWidth(), this.canvas.getHeight(), null);
		this.bs.show();
	}

	public BufferedImage getImage() {
		return image;
	}

	public Canvas getCanvas() {
		return canvas;
	}

}
