package com.darkxell.client.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.darkxell.client.launchable.Launcher;
import com.darkxell.client.ui.MainUiUtility;

public class StateManager {

	// ATTRIBUTES

	private AbstractState currentState;

	private BufferedImage internalBuffer;
	private int displayWidth = (int) (256 * 1.6);
	private int displayHeight = (int) (192 * 1.6);

	public byte backgroundID = 1;

	public void onKeyPressed(KeyEvent e, short key) {
		if (this.currentState != null)
			this.currentState.onKeyPressed(key);
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			Launcher.chatbox.send();
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			Launcher.chatbox.textfield.pressLeft();
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			Launcher.chatbox.textfield.pressRight();
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			Launcher.chatbox.textfield.pressDelete();
	}

	// KEY AND MOUSE EVENTS

	public void onKeyReleased(KeyEvent e, short key) {
		if (this.currentState != null)
			this.currentState.onKeyReleased(key);
	}

	public void onKeyTyped(KeyEvent e) {
		if (e.getKeyChar() != '\b')
			Launcher.chatbox.textfield.insertString(e.getKeyChar() + "");
	}

	public void onMouseClick(int x, int y) {
		if (this.currentState != null)
			this.currentState.onMouseClick(x, y);
	}

	public void onMouseMove(int x, int y) {
		if (this.currentState != null)
			this.currentState.onMouseMove(x, y);
	}

	public void onMouseRightClick(int x, int y) {
		if (this.currentState != null)
			this.currentState.onMouseRightClick(x, y);
	}

	// RENDER AND UPDATE

	public void render(Graphics2D g, int width, int height) {
		if (width == 0)
			return;
		if (internalBuffer == null)
			internalBuffer = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
		// Displays the game on the buffer
		Graphics2D g2 = internalBuffer.createGraphics();
		g2.clearRect(0, 0, displayWidth, displayHeight);
		if (this.currentState != null)
			this.currentState.render(g2, displayWidth, displayHeight);
		g2.dispose();
		// Calculates various values to draw the components to the window
		int gamewidth = (int) (0.6 * height * displayWidth / displayHeight <= 0.6 * width
				? 0.6 * height * displayWidth / displayHeight : 0.6 * width);
		int gameheight = gamewidth * displayHeight / displayWidth;
		int gamex = (int) (width * 0.35);
		int gamey = (int) (height * 0.35);
		int mapsize = (int) (gamewidth / 2 >= height * 0.25 ? height * 0.25 : gamewidth / 2);
		int mapx = (int) (width * 0.95) - mapsize < gamex + gamewidth - mapsize ? (int) (width * 0.95) - mapsize
				: gamex + gamewidth - mapsize;
		int mapy = (int) (height * 0.05);
		int chatwidth = (int) (width * 0.25);
		int chatheight = (int) (height * 0.9);
		int chatx = (int) (width * 0.05);
		int chaty = (int) (height * 0.05);
		// Draws the background
		MainUiUtility.drawBackground(g, width, height, backgroundID);
		// Draw the outlines
		MainUiUtility.drawBoxOutline(g, gamex, gamey, gamewidth, gameheight);
		MainUiUtility.drawBoxOutline(g, mapx, mapy, mapsize, mapsize);
		MainUiUtility.drawBoxOutline(g, chatx, chaty, chatwidth, chatheight);
		// draws the components insides
		g.drawImage(internalBuffer, gamex, gamey, gamewidth, gameheight, null);
		g.translate(chatx, chaty);
		Shape clp = g.getClip();
		g.setClip(new Rectangle(0, 0, chatwidth, chatheight));
		Launcher.chatbox.render(g, chatwidth, chatheight);
		g.setClip(clp);
		g.translate(-chatx, -chaty);
		g.setColor(Color.BLACK);
		g.fillRect(mapx, mapy, mapsize, mapsize);
	}

	public synchronized void update() {
		if (this.currentState != null)
			this.currentState.update();
	}

	// GETTERS,SETTERS AND UTILITY

	/**
	 * Sets the resolution of the internal display. Bigger means a zoomed out
	 * game. Defaults at [256*192]*2 (Official DS resolution * 2).
	 */
	public void setInternalDisplaySize(int w, int h) {
		displayHeight = h;
		displayWidth = w;
		internalBuffer = null;
	}

	public void randomizeBackground() {
		this.backgroundID = (byte) ((new Random().nextInt(7) + 1) % 7);
	}

	public AbstractState getCurrentState() {
		return this.currentState;
	}

	public void setState(AbstractState state) {
		if (this.currentState != null)
			this.currentState.onEnd();
		this.currentState = state;
		this.currentState.onStart();
	}

}
