package com.timblomenkamp.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ScreenUtils;
import com.timblomenkamp.bomberquest.BomberQuestGame;
import com.timblomenkamp.bomberquest.audio.BombPlacementSound;
import com.timblomenkamp.bomberquest.audio.GameScreenMusic;
import com.timblomenkamp.bomberquest.audio.GhostScreamSound;
import com.timblomenkamp.bomberquest.audio.MenuScreenMusic;
import com.timblomenkamp.bomberquest.map.*;
import com.timblomenkamp.bomberquest.texture.Drawable;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {
    
    /**
     * The size of a grid cell in pixels.
     * This allows us to think of coordinates in terms of square grid tiles
     * (e.g. x=1, y=1 is the bottom left corner of the map)
     * rather than absolute pixel coordinates.
     */
    public static final int TILE_SIZE_PX = 16;



    private boolean paused;
    
    /**
     * The scale of the game.
     * This is used to make everything in the game look bigger or smaller.
     */
    public static final int SCALE = 4;

    private final BomberQuestGame game;
    private final SpriteBatch spriteBatch;
    private final GameMap map;
    private final Hud hud;
    private final OrthographicCamera mapCamera;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(BomberQuestGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        this.hud = new Hud(spriteBatch, game.getSkin().getFont("font"), game);
        // Create and configure the camera for the game view
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
        this.paused = false;
    }
    
    /**
     * The render method is called every frame to render the game.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
            GameScreenMusic.GAMESCREENMUSIC.stop();
            MenuScreenMusic.MENUSCREEN.play();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            game.goToVictory();
        }

        //place the bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            map.addBomb();
        }
        
        // Clear the previous frame from the screen, or else the picture smears
        ScreenUtils.clear(Color.BLACK);
        
        // Cap frame time to 250ms to prevent spiral of death
        float frameTime = Math.min(deltaTime, 0.250f);


        if (!paused){
            // Update the map state
            map.tick(frameTime);

            //update the countdown
            if (map.getCountdownTime() > 0){
                map.setCountdownTime(map.getCountdownTime()-frameTime);
                if (map.getCountdownTime() <= 0){
                    game.goToGameOverScreen();
                }
            }

            //update countdownTime in the hud
            hud.setCountdownTime(map.getCountdownTime());
        }

        // Update the camera
        updateCamera();
        // Render the map on the screen
        renderMap();
        // Render the HUD on the screen
        hud.render();
    }
    
    /**
     * Updates the camera to match the current state of the game.
     * Currently, this just centers the camera at the origin.
     */
    private void updateCamera() {
        mapCamera.setToOrtho(false);

        float playerX = map.getPlayer().getX();
        float playerY = map.getPlayer().getY();

        mapCamera.position.x = playerX * TILE_SIZE_PX * SCALE;
        mapCamera.position.y = playerY * TILE_SIZE_PX * SCALE;
        mapCamera.update(); // This is necessary to apply the changes
    }


    /**
     * rendering the objects accessed via the getter of the GameMap
     * important to render with layer logic
     */
    private void renderMap() {
        // This configures the spriteBatch to use the camera's perspective when rendering
        spriteBatch.setProjectionMatrix(mapCamera.combined);

        spriteBatch.begin();

        for (Outside outside : map.getOutside()) {
            if(outside != null) {
                draw(spriteBatch, outside);
            }
        }
        for (Ground ground : map.getGround()) {
            draw(spriteBatch, ground);
        }

        draw(spriteBatch, map.getEntry()); //Entry

        for (BombPowerUp bombPowerUp : map.getBombPowerUps()){
            draw(spriteBatch,bombPowerUp);
        }
        for (BlastPowerUp up : map.getBlastPowerUps()) {
            draw(spriteBatch, up);
        }
        for (SpeedPowerUp up : map.getSpeedPowerUps()) {
            draw(spriteBatch, up);
        }

        draw(spriteBatch,map.getExit());

        for(DestructibleWall destructibleWall : map.getDestructibleWall()) {
            draw(spriteBatch, destructibleWall);}
        for(IndestructibleWall indestructibleWall : map.getIndestructibleWalls()) {
            draw(spriteBatch, indestructibleWall);
        }

        for(Enemy enemy : map.getEnemies()) {
            draw(spriteBatch, enemy);
        }
        for (Bomb bomb : map.getBombs()){
            draw(spriteBatch, bomb);
        }

        for (Blast blast : map.getBlasts()) {
            draw(spriteBatch,blast);
        }
        draw(spriteBatch, map.getPlayer());


        spriteBatch.end();
    }
    
    /**
     * Draws this object on the screen.
     * The texture will be scaled by the game scale and the tile size.
     * This should only be called between spriteBatch.begin() and spriteBatch.end(), e.g. in the renderMap() method.
     * @param spriteBatch The SpriteBatch to draw with.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        TextureRegion texture = drawable.getCurrentAppearance();
        // Drawable coordinates are in tiles, so we need to scale them to pixels
        float x = drawable.getX() * TILE_SIZE_PX * SCALE;
        float y = drawable.getY() * TILE_SIZE_PX * SCALE;
        // Additionally scale everything by the game scale
        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;
        spriteBatch.draw(texture, x, y, width, height);
    }
    
    /**
     * Called when the window is resized.
     * This is where the camera is updated to match the new window size.
     * @param width The new window width.
     * @param height The new window height.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false);
        hud.resize(width, height);
    }

    // Unused methods from the Screen interface
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
