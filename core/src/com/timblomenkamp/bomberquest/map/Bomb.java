package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.timblomenkamp.bomberquest.BomberQuestGame;
import com.timblomenkamp.bomberquest.audio.BombExplosion;
import com.timblomenkamp.bomberquest.audio.GameOverSound;
import com.timblomenkamp.bomberquest.audio.GhostScreamSound;
import com.timblomenkamp.bomberquest.texture.Animations;
import com.timblomenkamp.bomberquest.texture.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Bomb implements Drawable {

    private final float x;
    private final float y;
    private float elapsedTime;
    private float timeRemaining = 3f;
    public static int range = 1;
    private List<Blast> blasts;
    public boolean exploded = false;
    private boolean shouldDisappear = false;
    private GameMap gameMap;
    private World world;
    private boolean soundPlayed;


    public Bomb(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
        this.blasts = new ArrayList<>();
        this.world = world;
        this.soundPlayed = false;
    }

    /**
     * This method updated the timer by accepting the parameter deltaTime.
     * If the time remaining is smaller or equal 0, a new blast is created, exploded is set to true and the sound of the explosion is played.
     * @param deltaTime the difference between the time of two frames
     */
    public void updateTimer(float deltaTime){
            timeRemaining -= deltaTime;
            if (timeRemaining <= 0) {
                newBlastsInAllDirections(); // creates a new blast
                exploded = true;
                if (soundPlayed == false){ // checks if the sound was played before. This ensures that the sound of the bomb explosion is only player once
                    BombExplosion.BOMB_EXPLOSION.play(); // play the sound
                    soundPlayed = true;
                }

            }
    }

    /**
     * This methods updates the elapsed time to use it for the animations.
     * Also accepts a gameMap, to have access to the current gameMap in this class.
     * @param frameTime the difference between the time of two frames
     * @param gameMap the gameMap of the current game
     */
    void tick(float frameTime, GameMap gameMap){
        this.elapsedTime += frameTime;
        this.gameMap = gameMap;
    }

    private void createHitbox(World world) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Static bodies never move, but static bodies can collide with them.
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position of the body.
        bodyDef.position.set(this.x, this.y);
        // Create the body in the world using the body definition.
        Body body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a polygon shape for the chest.
        PolygonShape box = new PolygonShape();
        // Make the polygon a square with a side length of 1 tile.
        box.setAsBox(0.5f, 0.5f);
        // Attach the shape to the body as a fixture.
        body.createFixture(box, 1.0f).setSensor(true);
        // We're done with the shape, so we should dispose of it to free up memory.
        box.dispose();
        // Set the chest as the user data of the body so we can look up the chest from the body later.
        body.setUserData(this);
    }


    /**
     * Creates new blasts in all four directions.
     */
    private void newBlastsInAllDirections(){
        int bombX = Math.round(x);
        int bombY = Math.round(y);
        // if the enemy is on the field where the bomb explodes, he dies
        if (GameMap.findEnemyOnField(bombX,bombY) != null) {
            GameMap.destroyEnemy(GameMap.findEnemyOnField(bombX, bombY));
        }
        // if the player is on the field where the bomb explodes, he dies
        int playerX = Math.round(gameMap.getPlayer().getX());
        int playerY = Math.round(gameMap.getPlayer().getY());
        if (playerX == bombX && playerY == bombY){
            gameMap.getPlayer().setDead(true);
        }

        createBlastsInDirections(0,1);
        createBlastsInDirections(0,-1);
        createBlastsInDirections(1,0);
        createBlastsInDirections(-1,0);
    }

    /**
     *This methods creates the actual blasts.It accepts two parameters, the x direction and y direction.
     * The method iterates with a for loop, until the maximum bomb range is reached.
     * The blastCoordinate is the rounded coordinate of the bomb + the direction of the blast multiplied with the range.
     * After completion of this method, all blasts of one direction have been added to blastList of the respective bomb.
     * @param directionX The X direction, in which the blast should burst
     * @param directionY The Y direction, in which the blast should burst
     *
     */
    private void createBlastsInDirections(int directionX, int directionY){
        int bombX = Math.round(x);
        int bombY = Math.round(y);


        for (int i = 1; i <= gameMap.getBombRange(); i++){//iterates with a for loop, until the maximum range is reached
            int blastCoordinateX = bombX + (directionX * i);
            int blastCoordinateY = bombY + (directionY * i);


            if (GameMap.findEnemyOnField(blastCoordinateX,blastCoordinateY) != null){// if an enemy is on the field, the method destroyEnemy is called
                GameMap.destroyEnemy(GameMap.findEnemyOnField(blastCoordinateX,blastCoordinateY));
                gameMap.allEnemiesDefeated();
                blasts.add(new Blast(world, blastCoordinateX,blastCoordinateY,this));
            }
            if (GameMap.findDestructibleWall(blastCoordinateX, blastCoordinateY) != null){// destroys the destructible wall
                GameMap.destroyDestructibleWall(GameMap.findDestructibleWall(blastCoordinateX, blastCoordinateY));
                blasts.add(new Blast(world, blastCoordinateX,blastCoordinateY,this));
            }
            if (GameMap.findIndestructibleWall(blastCoordinateX, blastCoordinateY) != null){//If there is an indestructible wall, no more blasts are being created
                break;
            }
            else  blasts.add(new Blast(world, blastCoordinateX,blastCoordinateY,this));

        }
        gameMap.getBlasts().addAll(blasts); // adds all blasts to the gamemap
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        if (exploded){
            return Animations.BLAST_CENTER.getKeyFrame(elapsedTime,true);
        }
        return Animations.BOMB.getKeyFrame(this.elapsedTime, true);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public float getRange() {
        return range;
    }

    public boolean isExploded() {
        return exploded;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public List<Blast> getBlasts() {
        return blasts;
    }

    public boolean explosionOver() {
        return (timeRemaining <= 0 && elapsedTime >= 4f);
    }


    public GameMap getGameMap() {
        return gameMap;
    }
}
