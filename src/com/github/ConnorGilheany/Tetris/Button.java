package com.github.ConnorGilheany.Tetris;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Button extends Rectangle {

    private String label;
    private int labelX, labelY;

    private Color unpressedColor = Color.red;
    private Color pressedColor = Color.green;

    private boolean pressed = false;

    /**
     * Constructs a button on the screen
     *
     * @param x      The x location of the top left of the button
     * @param y      The y location of the top left of the button
     * @param width  The width of the button
     * @param height The height of the button
     * @param label  The string displayed on the button
     * @see Rectangle
     */
    public Button(int x, int y, int width, int height, String label) {
        super(x, y, width, height);
        this.label = label;
        calcLabelDimensions();
    }


    /**
     * Constructs a button on the screen
     *
     * @param x     The x location of the top left of the button
     * @param y     The y location of the top left of the button
     * @param label The string displayed on the button
     * @see Rectangle
     */
    public Button(int x, int y, String label) {
        this(x, y, 120, 80, label);
    }

    /**
     * Calculates the x and y position of the label
     */
    private void calcLabelDimensions() {
        labelY = (int) (this.y + this.height / 2 - 10);
        labelX = (int) (this.x + this.width / 2 - label.length() * 5);
    }

    /**
     * @return Whether the button is pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    public boolean checkClick(int x, int y) {
        if (this.contains(x, y)) {
            press();
            return true;
        }
        return false;
    }

    /**
     * @return the color that the button should be
     */
    private Color getColor() {
        return pressed ? pressedColor : unpressedColor;
    }

    /**
     * Draws the button onto the screen
     * @param g the graphics to draw with
     */
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillRect(x, y, width, height);
        g.setColor(Color.black);
        g.drawString(label, labelX, labelY);
    }


    /**
     * Presses or unpresses the button, depending on it's current state
     *
     * @return whether the button is now pressed
     */
    public boolean press() {
        pressed = !pressed;
        return pressed;
    }
}
