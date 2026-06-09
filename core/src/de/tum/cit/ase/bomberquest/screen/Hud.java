package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.Bomb;
import de.tum.cit.ase.bomberquest.map.GameMap;

/**
 * A Heads-Up Display (HUD) that displays information on the screen.
 * It uses a separate camera so that it is always fixed on the screen.
 */
public class Hud {
    
    /** The SpriteBatch used to draw the HUD. This is the same as the one used in the GameScreen. */
    private final SpriteBatch spriteBatch;
    /** The font used to draw text on the screen. */
    private final BitmapFont font;
    /** The camera used to render the HUD. */
    private final OrthographicCamera camera;
    private BomberQuestGame game;
    private float countdownTime = 0;

    private ShapeRenderer shapeRender = new ShapeRenderer();
    
    public Hud(SpriteBatch spriteBatch, BitmapFont font, BomberQuestGame game) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();
        this.game = game;
    }
    
    /**
     * Renders the HUD on the screen.
     * This uses a different OrthographicCamera so that the HUD is always fixed on the screen.
     */
    public void render() {
        // Render from the camera's perspective
        spriteBatch.setProjectionMatrix(camera.combined);
        // Start drawing
        spriteBatch.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND); //Blending found at https://stackoverflow.com/questions/25735363/opengl-blending-and-lighting-in-libgdx
        shapeRender.setProjectionMatrix(camera.combined);
        shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        shapeRender.setColor(0.7f, 0.7f, 0.7f,0.7f);
        shapeRender.rect(5, Gdx.graphics.getHeight() - 220, 350, 214);
        shapeRender.end();
        spriteBatch.end();


        spriteBatch.begin();

        // Draw the HUD elements
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "Press Esc to Pause!", 10, Gdx.graphics.getHeight() - 10);
        font.draw(spriteBatch, "Bomb limit: " + game.getMap().getMaxBombs(), 10, Gdx.graphics.getHeight() - 100); // implement logic of remaining bombs
        font.draw(spriteBatch, "Bomb range: " + game.getMap().getBombRange(), 10, Gdx.graphics.getHeight() - 130);


        font.draw(spriteBatch, "Difficulty:", 10, Gdx.graphics.getHeight() - 190);

        switch(game.getDifficulty()){
            case EASY ->{
                font.setColor(Color.GREEN);
                font.draw(spriteBatch, "EASY", 192, Gdx.graphics.getHeight() - 190);
            }
            case MEDIUM -> {
                font.setColor(Color.ORANGE);
                font.draw(spriteBatch, "MEDIUM", 192, Gdx.graphics.getHeight() - 190);
            }
            case HARD -> {
                font.setColor(Color.RED);
                font.draw(spriteBatch, "HARD", 192, Gdx.graphics.getHeight() - 190);
            }
        }




        if(countdownTime > 60){
            font.setColor(Color.GREEN);
            font.draw(spriteBatch, "Time left: " + (int) countdownTime + "s", 10, Gdx.graphics.getHeight() - 40);
        }
        else{
            font.setColor(Color.RED);
            font.draw(spriteBatch, "Time left: " + (int) countdownTime + "s", 10, Gdx.graphics.getHeight() - 40);
        }

        if((int) GameMap.countEnemies() > 0) {
            font.setColor(Color.RED);
            font.draw(spriteBatch, "Enemies left: " + (int) GameMap.countEnemies(), 10, Gdx.graphics.getHeight() - 70);
        }
        else{
            font.setColor(Color.GREEN);
            font.draw(spriteBatch, "Enemies left: 0", 10, Gdx.graphics.getHeight() - 70);
        }

        if((int) GameMap.countEnemies() != 0){
            font.setColor(Color.WHITE);
            font.draw(spriteBatch, "Exit closed", 10, Gdx.graphics.getHeight() - 160);
        }
        else{
            font.setColor(Color.GREEN);
            font.draw(spriteBatch, "Exit opened", 10, Gdx.graphics.getHeight() - 160);
        }

        // Finish drawing
        spriteBatch.end();
    }
    
    /**
     * Resizes the HUD when the screen size changes.
     * This is called when the window is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }


    public void setCountdownTime(float countdownTime) {
        this.countdownTime = countdownTime;
    }
}
