package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.CollectItem;
import de.tum.cit.ase.bomberquest.audio.GameScreenMusic;
import de.tum.cit.ase.bomberquest.audio.MenuScreenMusic;
import de.tum.cit.ase.bomberquest.map.*;
import de.tum.cit.ase.bomberquest.screen.*;

import de.tum.cit.ase.bomberquest.texture.Animations;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class BomberQuestGame extends Game {
    /**
     * Sprite Batch for rendering game elements.
     * This eats a lot of memory, so we only want one of these.
     */
    private SpriteBatch spriteBatch;


    private DifficultyChooser difficultyChooser;
    private DifficultyChooser.Difficulty difficulty;
    private SkinChooser skinChooser;
    private SkinChooser.Skin playerSkin;
    public Animations animations;


    /**
     * determines whether the map we load comes from the fileChooser or not. This is very important for the Voyage mode!
     */
    public boolean comingFromFileChooser = false;

    /** The game's UI skin. This is used to style the game's UI elements. */
    private Skin skin;
    
    /**
     * The file chooser for loading map files from the user's computer.
     * This will give you access to a {@link com.badlogic.gdx.files.FileHandle} object,
     * which you can use to read the contents of the map file as a String, and then parse it into a {@link GameMap}.
     */


    private NativeFileChooser fileChooser;
    public static FileHandle file = null;
    private GameScreen gameScreen;
    public boolean isHardMapCompleted;



    /**
     * Lets the user choose a .properties file.
     * This file is parsed to the file object of this class.
     */
    public void chooseFile() {
        NativeFileChooserConfiguration config = new NativeFileChooserConfiguration();
        config.title = "Choose a File";
       config.mimeFilter = "text/x-java-properties"; // filter for .properties files
        config.directory = new com.badlogic.gdx.files.FileHandle(System.getProperty("user.home"));

        fileChooser.chooseFile(config, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                BomberQuestGame.file = file;
                createMap();
                MenuScreenMusic.MENUSCREEN.stop();
                GameScreenMusic.GAMESCREENMUSIC.play(); // Play music
                goToGame();
            }

            @Override
            public void onCancellation() {
                GameScreenMusic.GAMESCREENMUSIC.stop();
                MenuScreenMusic.MENUSCREEN.play();
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });
    }


    /**
     * The map. This is where all the game objects are stored.
     * This is owned by {@link BomberQuestGame} and not by {@link GameScreen}
     * because the map should not be destroyed if we temporarily switch to another screen.
     */
    private GameMap map;


    /**
     * Constructor for BomberQuestGame.
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
       this.fileChooser = fileChooser;
       this.difficulty = DifficultyChooser.Difficulty.EASY;
       this.playerSkin = SkinChooser.Skin.STEVE;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     * During the class constructor, libGDX is not fully initialized yet.
     * Therefore this method serves as a second constructor for the game,
     * and we can use libGDX resources here.
     */
    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch();// Create SpriteBatch for rendering
        this.animations = new Animations(this);
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin
        MenuScreenMusic.MENUSCREEN.play(); // play the menu screen music
        goToMenu(); // Navigate to the menu screen
    }


    /**
     * creates the map for the game.
     * If file is null, it creates the map by choosing a default map.
     * If file is not null, it uses the FileHandle object from the FileChooser
     */
    public void createMap(){
        if (file == null){
            this.map = new GameMap(this, Gdx.files.internal("EasyMap.properties").file());
            map.getWorld().setContactListener(new ContactListener() {
                @Override
                // The contact listener recognizes the contact between different objects and can trigger methods, if a contact is recognized
                public void beginContact(Contact contact) {
                    Object objectA = contact.getFixtureA().getBody().getUserData();
                    Object objectB = contact.getFixtureB().getBody().getUserData();

                    if (objectA instanceof Exit && objectB instanceof Player){
                        map.allEnemiesDefeated();
                        if (map.getExit().isOpen()) {
                            BomberQuestGame.this.goToVictory();
                        }
                    }
                    if (objectA instanceof Player && objectB instanceof Exit){
                        if (map.getExit().isOpen()) {
                            BomberQuestGame.this.goToVictory();
                        }
                    }
                    if(objectA instanceof Player && objectB instanceof Enemy){
                        BomberQuestGame.this.goToGameOverScreen();
                    }
                    if(objectA instanceof Enemy && objectB instanceof Player){
                        BomberQuestGame.this.goToGameOverScreen();
                    }
                    if(objectA instanceof BlastPowerUp && objectB instanceof Player){
                        map.score.numberOfPowerUpsCollected();
                        map.increaseBombRange();
                        CollectItem.COLLECT_ITEM.play();
                        ((BlastPowerUp) objectA).setTriggered(true);
                    }
                    if(objectA instanceof BombPowerUp && objectB instanceof Player){
                        map.score.numberOfPowerUpsCollected();
                        map.increaseBombLimit();
                        CollectItem.COLLECT_ITEM.play();
                        ((BombPowerUp) objectA).setTriggered(true);
                    }
                    if(objectA instanceof SpeedPowerUp && objectB instanceof Player){
                        map.score.numberOfPowerUpsCollected();
                        map.getPlayer().setSpeed(map.getPlayer().getSpeed() + 0.5f);
                        CollectItem.COLLECT_ITEM.play();
                        ((SpeedPowerUp) objectA).setTriggered(true);
                    }
                    if(objectA instanceof Blast && objectB instanceof Player){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }

                    if(objectA instanceof Player && objectB instanceof Blast){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }


                }

                @Override
                public void endContact(Contact contact) {
                    Object objectA = contact.getFixtureA().getBody().getUserData();
                    Object objectB = contact.getFixtureB().getBody().getUserData();
                    if(objectA instanceof Blast && objectB instanceof Player){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }
                    if(objectA instanceof Player && objectB instanceof Blast){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }
                }

                @Override
                public void preSolve(Contact contact, Manifold manifold) {

                }

                @Override
                public void postSolve(Contact contact, ContactImpulse contactImpulse) {

                }
            });
        }
        if (file != null){
            this.map = new GameMap(this, file.file().getAbsoluteFile());
            map.getWorld().setContactListener(new ContactListener() {
                @Override
                // The contact listener recognizes the contact between different objects and can trigger methods, if a contact is recognized
                public void beginContact(Contact contact) {
                    Object objectA = contact.getFixtureA().getBody().getUserData();
                    Object objectB = contact.getFixtureB().getBody().getUserData();

                    if (objectA instanceof Exit && objectB instanceof Player){
                        if (map.getExit().isOpen()) {
                            BomberQuestGame.this.goToVictory();
                        }
                    }
                    if (objectA instanceof Player && objectB instanceof Exit){
                        if (map.getExit().isOpen()) {
                            BomberQuestGame.this.goToVictory();
                        }
                    }
                    if(objectA instanceof Player && objectB instanceof Enemy){
                        BomberQuestGame.this.goToGameOverScreen();
                    }
                    if(objectA instanceof Enemy && objectB instanceof Player){
                        BomberQuestGame.this.goToGameOverScreen();

                    }
                    if(objectA instanceof BlastPowerUp && objectB instanceof Player){
                        map.score.numberOfPowerUpsCollected();
                        map.increaseBombRange();
                        CollectItem.COLLECT_ITEM.play();
                        ((BlastPowerUp) objectA).setTriggered(true);
                    }
                    if(objectA instanceof BombPowerUp && objectB instanceof Player){
                        map.score.numberOfPowerUpsCollected();
                        map.increaseBombLimit();
                        CollectItem.COLLECT_ITEM.play();
                        ((BombPowerUp) objectA).setTriggered(true);
                    }
                    if(objectA instanceof SpeedPowerUp && objectB instanceof Player){
                        map.score.numberOfPowerUpsCollected();
                        map.getPlayer().setSpeed(map.getPlayer().getSpeed() + 0.5f);
                        CollectItem.COLLECT_ITEM.play();
                        ((SpeedPowerUp) objectA).setTriggered(true);
                    }
                    if(objectA instanceof Blast && objectB instanceof Player){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }

                }

                @Override
                public void endContact(Contact contact) {
                    Object objectA = contact.getFixtureA().getBody().getUserData();
                    Object objectB = contact.getFixtureB().getBody().getUserData();
                    if(objectA instanceof Blast && objectB instanceof Player){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }
                    if(objectA instanceof Player && objectB instanceof Blast){
                        BomberQuestGame.this.getMap().getPlayer().setDead(true);
                    }
                }

                @Override
                public void preSolve(Contact contact, Manifold manifold) {

                }

                @Override
                public void postSolve(Contact contact, ContactImpulse contactImpulse) {

                }
            });
        }
    }

    public String proceedToNextMap(){
        if (file == null){
            return "maps/MediumMap.properties";
        }
        if ((file.file().getAbsolutePath().equals("maps/map-1.properties"))){
            return "maps/MediumMap.properties";
        }
        if ((file.file().getAbsolutePath().equals("maps/map-2.properties"))){
            return "maps/MediumMap.properties";
        }
        if ((file.file().getAbsolutePath().equals("maps/MediumMap.properties"))){
            return "maps/HardMap.properties";
        }
        else {
            isHardMapCompleted = true;
            return "maps/HardMap.properties";
        }
    }
    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        gameScreen = new GameScreen(this);
        this.setScreen(gameScreen); // Set the current screen to GameScreen
    }

    /**
     * Switches to VictoryScreen
     */
    public void goToVictory(){
        this.setScreen(new VictoryScreen(this)); // Set the current screen to VictoryScreen
    }

    /**
     * Switchs to GameOverScreen
     */
    public void goToGameOverScreen(){
        this.setScreen(new GameOverScreen(this));
    }

    /**
     * Switches to ChooseSkinScreen
     */
    public void goToChooseSkinScreen(){
        this.setScreen(new ChooseSkinScreen(this));
    }


    /**
     * Switches to ChooseDifficultyScreen
     */
    public void goToChooseDifficultyScreen(){
        this.setScreen(new ChooseDifficultyScreen(this));
    }


    /** Returns the skin for UI elements. */
    public Skin getSkin() {
        return skin;
    }

    /** Returns the main SpriteBatch for rendering. */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    /** Returns the current map, if there is one. */
    public GameMap getMap() {
        return map;
    }
    
    /**
     * Switches to the given screen and disposes of the previous screen.
     * @param screen the new screen
     */
    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }

    /** Cleans up resources when the game is disposed. */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }


    public DifficultyChooser getDifficultyChooser() {
        return difficultyChooser;
    }

    public void setDifficultyChooser(DifficultyChooser difficultyChooser) {
        this.difficultyChooser = difficultyChooser;
    }

    public DifficultyChooser.Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyChooser.Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public SkinChooser.Skin getPlayerSkin() {
        return playerSkin;
    }

    public void setPlayerSkin(SkinChooser.Skin playerSkin) {
        this.playerSkin = playerSkin;
    }

    public boolean isHardMapCompleted() {
        return isHardMapCompleted;
    }

    public void setHardMapCompleted(boolean hardMapCompleted) {
        isHardMapCompleted = hardMapCompleted;
    }

    public boolean isComingFromFileChooser() {
        return comingFromFileChooser;
    }

    public void setComingFromFileChooser(boolean comingFromFileChooser) {
        this.comingFromFileChooser = comingFromFileChooser;
    }
}
