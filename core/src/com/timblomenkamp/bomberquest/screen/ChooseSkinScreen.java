package com.timblomenkamp.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.timblomenkamp.bomberquest.DifficultyChooser;
import com.timblomenkamp.bomberquest.SkinChooser;
import com.timblomenkamp.bomberquest.texture.Textures;


public class ChooseSkinScreen implements Screen {

    private final Stage stage;
    private BomberQuestGame game;
    private Texture backgroundImage;
    private Texture SkinSteveTexture;
    private Texture SkinOriginalTexture;
    private Label SkinLabel;
    /**
     * Constructor for ChooseSkinScreen. Sets up the camera, viewport, stage, and UI elements.
     * Creates textures for the different skins.
     * @param game The main game class, used to access global resources and methods.
     */
    public ChooseSkinScreen(BomberQuestGame game) {
        this.game = game;
        backgroundImage = new Texture(Gdx.files.internal("texture/ChooseDifficulty.png"));
        SkinSteveTexture = new Texture(Gdx.files.internal("skins/SkinSteveFront.png"));
        SkinOriginalTexture = new Texture(Gdx.files.internal("skins/SkinOriginalBombermanFront.png"));

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view
        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        table.add(new Label("Choose your skin!", game.getSkin(), "title")).padBottom(80).row();
        SkinLabel = new Label("You choose: " + game.getPlayerSkin().toString(), game.getSkin(), "default");
        table.add(SkinLabel).padBottom(80).row();

        // The button for choosing the skin "Steve"
        TextButton chooseSkinSteveButtom = new TextButton("Choose Steve", game.getSkin());
        table.add(chooseSkinSteveButtom).width(300).row();
        chooseSkinSteveButtom.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setPlayerSkin(SkinChooser.Skin.STEVE);
                SkinLabel.setText("You choose: " + game.getPlayerSkin().toString());
            }
        });

        // The button for choosing the original Bomberman skin
        TextButton chooseSkinOriginalButton = new TextButton("Choose Original", game.getSkin());
        table.add(chooseSkinOriginalButton).width(300).row();
        chooseSkinOriginalButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setPlayerSkin(SkinChooser.Skin.ORIGINAL);
                SkinLabel.setText("You choose: " + game.getPlayerSkin().toString());
            }
        });

        table.add().height(40).row();

        //Button to return to the menu
        TextButton GoToMenuButton = new TextButton("Go to menu", game.getSkin());
        table.add(GoToMenuButton).width(300).row();
        GoToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });


    }
    
    /**
     * The render method is called every frame to render the ChooseSkin screen.
     * It clears the screen and draws the stage.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.250f); // Cap frame time to 250ms to prevent spiral of death        ScreenUtils.clear(Color.BLACK);
        ScreenUtils.clear(Color.BLACK);
        stage.act(frameTime);
        stage.getBatch().setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float x = (Gdx.graphics.getWidth()/100) * 65;
        float y = (Gdx.graphics.getHeight()/100) * 38;
        float width = 80;
        float height = 80;
        if (game.getPlayerSkin() != null && game.getPlayerSkin().equals(SkinChooser.Skin.ORIGINAL)) {
            stage.getBatch().draw(SkinOriginalTexture, x, y, width, height);
        } else if (game.getPlayerSkin() != null) {
            stage.getBatch().draw(SkinSteveTexture, x, y, width, height);
        }
        stage.getBatch().end();
        stage.getViewport().apply();
        stage.draw(); // Draw the stage// Update the stage
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
