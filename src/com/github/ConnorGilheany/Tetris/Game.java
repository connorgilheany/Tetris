package com.github.ConnorGilheany.Tetris;

import com.github.ConnorGilheany.Tetris.Map.Map;
import com.github.ConnorGilheany.Tetris.Pieces.GamePiece;
import com.github.ConnorGilheany.Tetris.Pieces.RandomGenerator;
import com.github.ConnorGilheany.Tetris.controller.AIController;
import com.github.ConnorGilheany.Tetris.controller.Controller;
import com.github.ConnorGilheany.Tetris.controller.PlayerController;
import org.newdawn.slick.*;

import java.io.IOException;
import java.util.HashMap;

public class Game extends BasicGame {

    public final int AI_SPEED = 3000;//1000
    public int TICK_TIME = 500;
    private Map map;
    private Controller controller;
    private boolean AI_CONTROLLER;

    private GamePiece currentPiece;
    private GamePiece nextPiece;

    private java.util.Map<String, Button> buttons = new HashMap<>();

    private boolean paused = false;
    private boolean fast = false;


    private int points = 0;
    private int totalDelta = 0;


    /**
     * Initializes window setup with Slick2D, sets shutdown hook
     * @param title the title of the window
     */
    public Game(String title) {
        super(title);

        try {
            AppGameContainer agc = new AppGameContainer(this, 1000, 750, false);
            agc.setTargetFrameRate(60);
            agc.setUpdateOnlyWhenVisible(false);
            agc.start();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    save();
                }
            }));

        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /**
     * initialization method overridden from Slick2D's BasicGame class
     * @param gc the GameContainer object
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gc) throws SlickException {
        setUpButtons();
        setController();
        map = new Map();
        if (currentPiece == null)
            nextGamePiece();
    }

    /**
     * Creates/initializes the buttons to be placed on the screen
     */
    private void setUpButtons() {
        buttons.put("AI_CONTROLLER", new Button(680, 300, "AI"));
        buttons.put("fast", new Button(680, 420, "Fast"));
        AI_CONTROLLER = buttons.get("AI_CONTROLLER").press(); //Automatically starts in AI mode
        fast = buttons.get("fast").isPressed();
    }

    /**
     * Sets controller object to the correct type: AI or player
     */
    private void setController() {
        if (AI_CONTROLLER)
            controller = new AIController();
        else
            controller = new PlayerController();
        controller.init();
    }

    /**
     * The main rendering method, overridden from Slick2D's BasicGame
     * @param gc the GameContainer object holding the game, from Slick2D
     * @param g the Graphics object, from Slick2D
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {

        currentPiece.drawTarget(map, g);
        map.render(g);
        map.renderNextPiece(g, nextPiece);
        currentPiece.draw(map, g);

        if (AI_CONTROLLER)
            ((AIController) controller).drawStats(g);

        drawButtons(g);

        g.setColor(Color.white);
        g.drawString("Points: " + points, 50, 30);

        if (paused)
            g.drawString("PAUSED", 50, 250);
    }

    private void drawButtons(Graphics g) {
        for (Button b : buttons.values()) {
            b.draw(g);
        }
    }

    /**
     * The update method, which is run every few milliseconds
     * @param gc the GameContainer object holding the game, from Slick2D
     * @param delta milliseconds since last update
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        map.setScreenSize(gc.getWidth(), gc.getHeight());
        totalDelta += delta;

        if (gc.getInput().isKeyPressed(Input.KEY_EQUALS))
            TICK_TIME -= 10;
        if (gc.getInput().isKeyPressed(Input.KEY_MINUS))
            TICK_TIME += 10;
        if (gc.getInput().isKeyPressed(Input.KEY_C))
            clearData();

        if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            if (checkMouseInput(gc.getInput())) {
                {
                    boolean temp = AI_CONTROLLER;
                    AI_CONTROLLER = buttons.get("AI_CONTROLLER").isPressed();
                    if (temp != AI_CONTROLLER)
                        swapInputs();
                }
                fast = buttons.get("fast").isPressed(); //Keeps fast tied to it's button
            }
        }

        if (!AI_CONTROLLER)
            controller.doInput(this, gc);

        else if(fast || totalDelta > TICK_TIME) {
            tick();
            totalDelta = 0;
        }
    }


    /**
     * @param in the input object given by Slick
     * @return whether one or more buttons has been pressed
     */
    private boolean checkMouseInput(Input in) {
        boolean pressed = false;
        for (Button button : buttons.values()) {
            if (button.checkClick(in.getMouseX(), in.getMouseY())) pressed = true;
        }
        return pressed;
    }

    /**
     * Run upon quit; saves AI stats and genes to disc
     */
    private void save() {
        if (AI_CONTROLLER) {
            try {
                ((AIController) controller).save();
                ((AIController) controller).saveBestEverGenes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes AI data
     */
    private void clearData() {
        if (AI_CONTROLLER) {
            end();
            ((AIController) controller).clearData();
        }
    }

    /**
     * Checks AI button and swaps between player/AI controls accordingly
     */
    private void swapInputs() {
        AI_CONTROLLER = buttons.get("AI_CONTROLLER").isPressed();
        setController();
    }

    /**
     * Run when the game should perform it's next cycle
     * Appropriately decides next action by checking status of AI_CONTROLLEr and fast variables
     */
    public void tick() {
        if (!paused) {
            if (AI_CONTROLLER) {
                if (fast) {
                    fastAITick();
                } else {
                    controller.doInput(this, null);
                    checkPieceGrounded();
                }
            } else {
                checkPieceGrounded();

            }
        }

    }

    /**
     * Checks if the current piece is on the ground, freezes it and checks row completion if so
     */
    private void checkPieceGrounded() {
        if (!currentPiece.move(map, 0, 1)) {
            freezePiece();
            checkCompletion();
        }
    }

    /**
     * Game cycle that is performed when the fast AI is on.
     * Called by tick()
     */
    private void fastAITick() {
        for (int i = 0; i < AI_SPEED; i++) {
            controller.doInput(this, null);
            checkPieceGrounded();
        }
    }

    /**
     * Run after inserting a piece, checks whether the newly placed piece completed rows
     */
    private void checkCompletion() {
        int completed = map.checkCompletion();
        rowsCompleted(completed);
    }

    /**
     * Adds point values based on number of rows completed
     * @param completed the number of rows that have been completed
     */
    private void rowsCompleted(int completed) {
        switch (completed) {
            case 1:
                addPoints(40);
                break;
            case 2:
                addPoints(100);
                break;
            case 3:
                addPoints(300);
                break;
            case 4:
                addPoints(1200);
                break;
        }

    }

    /**
     * freezes the current piece in place permanently, generating a new piece afterwards
     */
    public void freezePiece() {
        currentPiece.cement(map);
        nextGamePiece();

    }

    /**
     * Creates a new game piece for the controller to place
     */
    private void nextGamePiece() {
        if (nextPiece == null)
            nextPiece = new GamePiece(this);
        currentPiece = nextPiece;
        nextPiece = new GamePiece(this);
    }

    /**
     * @return the piece currently being controlled
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * @return the tetris board
     */
    public Map getMap() {
        return map;
    }

    /**
     * Ends the current game and starts a new one
     */
    public void end() {
        //System.out.println("Ending game");
        RandomGenerator.newGame();
        controller.endGame(this);
        nextPiece = null;
        currentPiece = null;
        map.clear();
        points = 0;
        nextGamePiece();

    }

    /**
     * Toggles whether the game is paused
     */
    public void togglePause() {
        paused = !paused;
    }

    /**
     * @param amount the amount of points to be added
     */
    public void addPoints(int amount) {
        points += amount;
    }

    /**
     * @return the number of points scored in the current game
     */
    public int getPoints() {
        return points;
    }

}
