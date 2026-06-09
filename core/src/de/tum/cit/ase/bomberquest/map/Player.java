package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

import java.sql.Time;
import java.util.List;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 */
public class Player implements Drawable {
    
    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;
    
    /** The Box2D hitbox of the player, used for position and collision detection. */
    private final Body hitbox;

    /**
     * List of bombs the player has placed
     */

    public boolean inputAllowed;
    private float speed;


    /**
     * The constructor of the player class.
     * @param world The Box2D world to add the body to.
     * @param x The x position of the player
     * @param y The y position of the player
     */
    public Player(World world, float x, float y) {
        this.hitbox = createHitbox(world, x, y);
        this.hitbox.setUserData(this);
        this.dead = false;
        this.inputAllowed = true;
        this.speed = 2;
    }



    /**
     * Creates a Box2D body for the player.
     * This is what the physics engine uses to move the player around and detect collisions with other bodies.
     * @param world The Box2D world to add the body to.
     * @param startX The initial X position.
     * @param startY The initial Y position.
     * @return The created body.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Dynamic bodies are affected by forces and collisions.
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position of the body.
        bodyDef.position.set(startX, startY);
        // Create the body in the world using the body definition.
        Body body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a circle shape for the player.
        CircleShape circle = new CircleShape();
        // Give the circle a radius of 0.3 tiles (the player is 0.6 tiles wide).
        circle.setRadius(0.3f);
        // Attach the shape to the body as a fixture.
        // Bodies can have multiple fixtures, but we only need one for the player.
        body.createFixture(circle, 1.0f);
        // We're done with the shape, so we should dispose of it to free up memory.
        circle.dispose();
        // Set the player as the user data of the body so we can look up the player from the body later.
        body.setUserData(this);
        return body;
    }
    
    /**
     * Move the player around in a circle by updating the linear velocity of its hitbox every frame.
     * This doesn't actually move the player, but it tells the physics engine how the player should move next frame.
     * @param frameTime the time since the last frame.
     */
    private String WalkingDirection;
    private String WalkingDirectionWithWASD;
    private boolean dead;

    public void tick(float frameTime) {
        this.elapsedTime += frameTime;
        float xVelocity = 0;
        float yVelocity = 0;
        WalkingDirection = "STOP";
        WalkingDirectionWithWASD = "STOP";

        if(inputAllowed) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)) {
                yVelocity = speed;
                WalkingDirection = "UP";
                WalkingDirectionWithWASD = "UP";
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)) {
                yVelocity = -speed;
                WalkingDirection = "DOWN";
                WalkingDirectionWithWASD = "DOWN";
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)) {
                xVelocity = speed;
                WalkingDirection = "RIGHT";
                WalkingDirectionWithWASD = "RIGHT";
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)) {
                xVelocity = -speed;
                WalkingDirection = "LEFT";
                WalkingDirectionWithWASD = "LEFT";
            }
            this.hitbox.setLinearVelocity(xVelocity, yVelocity);
        }
    }

    private String lastPosition = "";

    /**
     * @return the animation of the player, depending on the players walking direction and if he is walking or not
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        // Get the frame of the walk down animation that corresponds to the current time.
        if (isDead() == true){
            return Animations.CHARACTER_DIES.getKeyFrame(this.elapsedTime,true);
        }
        if (WalkingDirection == "UP" || WalkingDirectionWithWASD == "UP"){
            lastPosition = "UP";
            return Animations.CHARACTER_WALK_UP.getKeyFrame(this.elapsedTime, true);
        }if (WalkingDirection == "DOWN"|| WalkingDirectionWithWASD == "DOWN"){
            lastPosition = "DOWN";
            return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
        }if (WalkingDirection == "RIGHT"|| WalkingDirectionWithWASD == "RIGHT"){
            lastPosition = "RIGHT";
            return Animations.CHARACTER_WALK_RIGHT.getKeyFrame(this.elapsedTime, true);
        }if (WalkingDirection == "LEFT" || WalkingDirectionWithWASD == "LEFT"){
            lastPosition = "LEFT";
            return Animations.CHARACTER_WALK_LEFT.getKeyFrame(this.elapsedTime, true);
        }
         if (WalkingDirection == "STOP" || WalkingDirectionWithWASD == "STOP"){
            if (lastPosition == "UP"){
                return Animations.CHARACTER_WALK_UP.getKeyFrame(this.elapsedTime, false);
            }if (lastPosition == "DOWN"){
                return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, false);
            }if (lastPosition == "RIGHT"){
                return Animations.CHARACTER_WALK_RIGHT.getKeyFrame(this.elapsedTime, false);
            } if (lastPosition == "LEFT"){
                return Animations.CHARACTER_WALK_LEFT.getKeyFrame(this.elapsedTime, false);
            }
        }
        return Animations.CHARACTER_WALK_DOWN.getKeyFrame(0, false);
    }


    @Override
    public float getX() {
        // The x-coordinate of the player is the x-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().x;
    }
    
    @Override
    public float getY() {
        // The y-coordinate of the player is the y-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().y;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isInputAllowed() {
        return inputAllowed;
    }

    public void setInputAllowed(boolean inputAllowed) {
        this.inputAllowed = inputAllowed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
