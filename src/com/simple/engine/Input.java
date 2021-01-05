package com.simple.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

public class Input implements KeyListener, MouseListener, MouseMotionListener {

	private Engine engine;
	
	private static final int QTD_KEYS = 256;
	private boolean[] keys = new boolean[QTD_KEYS];
	private boolean[] keysLast = new boolean[QTD_KEYS];

	private static final int QTD_BUTTONS = 2;
	private boolean[] buttons = new boolean[QTD_BUTTONS];
	private boolean[] buttonsLast = new boolean[QTD_BUTTONS];

	private int mouseX;
	private int mouseY;
	
	public Input(Engine engine) {
		this.mouseX = 0;
		this.mouseY = 0;
		this.engine = engine;
		// attach window canvas to this input listener class
		this.engine.getWindow().getCanvas().addKeyListener(this);
		this.engine.getWindow().getCanvas().addMouseListener(this);
		this.engine.getWindow().getCanvas().addMouseMotionListener(this);
	}

	public void update() {
		this.keysLast = Arrays.copyOf(this.keys, QTD_KEYS);
		this.buttonsLast = Arrays.copyOf(this.buttons, QTD_BUTTONS);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// input event not implemented
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = (int) (e.getX() / this.engine.getScale());
		this.mouseY = (int) (e.getY() / this.engine.getScale());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// input event not implemented		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.buttons[e.getButton()] = false;		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// input event not implemented
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// input event not implemented
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// input event not implemented
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.keys[e.getKeyCode()] = false;
	}

	public boolean isKey(int keyCode) {
		return this.keys[keyCode];
	}

	public boolean isKeyUp(int keyCode) {
		return !this.keys[keyCode] && this.keysLast[keyCode];
	}

	public boolean isKeyDown(int keyCode) {
		return this.keys[keyCode] && !this.keysLast[keyCode];
	}

	public boolean isButton(int buttonCode) {
		return this.buttons[buttonCode];
	}

	public boolean isButtonUp(int buttonCode) {
		return !this.buttons[buttonCode] && this.buttonsLast[buttonCode];
	}

	public boolean isButtonDown(int buttonCode) {
		return this.buttons[buttonCode] && !this.buttonsLast[buttonCode];
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

}
