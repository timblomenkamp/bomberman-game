package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.GameScreenMusic;
import de.tum.cit.ase.bomberquest.audio.MenuScreenMusic;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private Texture backgroundImage;
    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(BomberQuestGame game) {
        backgroundImage = new Texture(Gdx.files.internal("texture/menuScreen.png"));
        game.setHardMapCompleted(false);

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        Label title = new Label("Bomber Seal", game.getSkin(), "title");
        table.add(title).row();


        table.add(new Label("Will you escape?", game.getSkin())).padBottom(50).row();



        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            TextButton ResumeButton = new TextButton("Resume", game.getSkin());
            table.add(ResumeButton).width(350).row();
            ResumeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.getGameScreen().setPaused(false);
                    MenuScreenMusic.MENUSCREEN.stop();
                    GameScreenMusic.GAMESCREENMUSIC.play();
                    game.goToGame();
                }
            });
        }




        /// Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Play Voyage", game.getSkin());
        table.add(goToGameButton).width(350).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.file = null;
                game.createMap();
                game.animations.setSkinForPlayer();
                MenuScreenMusic.MENUSCREEN.stop();
                GameScreenMusic.GAMESCREENMUSIC.play(); // Play music
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });

        TextButton loadMapButton = new TextButton("Load new Map", game.getSkin());
        table.add(loadMapButton).width(350).row();
        loadMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.animations.setSkinForPlayer();
                game.setComingFromFileChooser(true);
                game.file = null;
                game.chooseFile(); // Executes Method chooseFile from class fileChooser
            }
        });


        TextButton chooseDifficultyButton = new TextButton("Choose Difficulty", game.getSkin());
        table.add(chooseDifficultyButton).width(350).row();
        chooseDifficultyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToChooseDifficultyScreen();
            }
        });

        TextButton chooseSkinButton = new TextButton("Choose Skin", game.getSkin());
        table.add(chooseSkinButton).width(350).row();
        chooseSkinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToChooseSkinScreen();
            }
        });

        table.add().height(40).row();

        TextButton exitButton = new TextButton("Quit", game.getSkin());
        table.add(exitButton).width(350).row();
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });




    }
    
    /**
     * The render method is called every frame to render the menu screen.
     * It clears the screen and draws the stage.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.250f); // Cap frame time to 250ms to prevent spiral of death        ScreenUtils.clear(Color.BLACK);
        ScreenUtils.clear(Color.BLACK);
        stage.act(frameTime); // Update the stage

        stage.getBatch().setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.getViewport().apply();
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
