package com.timblomenkamp.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.timblomenkamp.bomberquest.BomberQuestGame;
import com.timblomenkamp.bomberquest.audio.GameScreenMusic;
import com.timblomenkamp.bomberquest.audio.MenuScreenMusic;
import com.timblomenkamp.bomberquest.audio.Victory;
import com.timblomenkamp.bomberquest.map.GameMap;

public class VictoryScreen implements Screen {
    private final Stage stage;
    private BomberQuestGame game;
    private Texture backgroundImage;

    /**
     * Constructor for VictoryScreen. Sets up the camera, viewport, stage, and UI elements.
     * @param game The main game class, used to access global resources and methods.
     */
    public VictoryScreen(BomberQuestGame game) {
        backgroundImage = new Texture(Gdx.files.internal("texture/victoryScreen.png"));
        this.game = game;
        GameScreenMusic.GAMESCREENMUSIC.stop();
        Victory.VICTORY.play();
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view


        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // calculate the score and load the highscore from the map
        int score = game.getMap().score.calculateScore();
        int highScore =  game.getMap().score.getHighScoreFromFile();
        // transforming both values into a charsequence to use it as a string
        CharSequence charScore = String.valueOf(score);
        CharSequence charHighScore = String.valueOf(highScore);

        // Add a label as a title
        table.add(new Label("Victory Royale!", game.getSkin(), "title")).row();

        if (game.isHardMapCompleted && !game.comingFromFileChooser){
            table.add(new Label("You successfully completed the voyage!", game.getSkin(), "title")).row();
            table.add(new Label("Have fun playing your own maps!", game.getSkin(), "title")).padBottom(30).row();
        }

        // adding the scores to the user interface
        table.add(new Label("Your score:", game.getSkin(), "default")).padBottom(30).row();
        table.add(new Label(charScore, game.getSkin(), "title")).padBottom(80).row();
        table.add(new Label("Current/Previous High Score:", game.getSkin(), "default")).padBottom(30).row();
        table.add(new Label(charHighScore, game.getSkin(), "title")).padBottom(80).row();

        if (score > highScore){
            table.add(new Label("You achieved a new Highscore!", game.getSkin(), "default")).padBottom(30).row();
            game.getMap().score.updateHighScore(score);
        }

        // only displaying the button to procede to next map if the hard map is not completed yet and the player is not coming from the file chooser
        if (!(game.isHardMapCompleted) && !game.comingFromFileChooser){
            TextButton ProceedToNextMapButton = new TextButton("Proceed to next map", game.getSkin());
            table.add(ProceedToNextMapButton).width(450).row();
            ProceedToNextMapButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String filePath = game.proceedToNextMap();
                    FileHandle file = Gdx.files.internal(filePath);
                    game.file = file;
                    game.createMap();
                    game.animations.setSkinForPlayer();
                    MenuScreenMusic.MENUSCREEN.stop();
                    GameScreenMusic.GAMESCREENMUSIC.play(); // Play music
                    game.goToGame(); // Change to the game screen when button is pressed
                }
            });
        }


        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Go to menu!", game.getSkin());
        table.add(goToGameButton).width(450).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MenuScreenMusic.MENUSCREEN.play();
                game.setHardMapCompleted(true);
                game.setComingFromFileChooser(false);
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });


    }

    /**
     * The render method is called every frame to render the  VictoryScreen.
     * It clears the screen and draws the stage.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.250f); // Cap frame time to 250ms to prevent spiral of death        ScreenUtils.clear(Color.BLACK);
        ScreenUtils.clear(new Color(0x3CB371FF));
        stage.act(frameTime); // Update the stage
        stage.getBatch().setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.getViewport().apply();
        stage.act(frameTime); // Update the stage
        stage.draw(); // Draw the stage
    }

    /**
     * Resize the stage when the screen is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
