package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.timblomenkamp.bomberquest.texture.Animations;
import com.timblomenkamp.bomberquest.texture.Drawable;

/**
 * If there is a contact between an enemy and the player, the player dies.
 */
public class Enemy implements Drawable {

    private float elapsedTime;
    private Body hitbox; //make final
    private Player player;
    private Body body;
    float velocity = 0.01f;


    /**
     * @param world The Box2D world to add the body to.
     * @param x The x position of the enemy
     * @param y The y position of the enemy
     * @param player the player
     */
    public Enemy(World world, float x, float y, Player player) { //davor GameMap map
        this.player = player;
        this.hitbox = createHitbox(world, x, y);
    }


    /**
     * Creates Hitbox of Enemy - similar to the one of player
     * @param world The Box2D world to add the Enemy's hitbox to.
     * @param startX The X position.
     * @param startY The Y position.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Dynamic bodies are affected by forces and collisions.
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position of the body.
        bodyDef.position.set(startX, startY);
        // Create the body in the world using the body definition.
        body = world.createBody(bodyDef);
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


    // For this method I found one discussion on Reddit particularly helpful: https://www.reddit.com/r/libgdx/comments/pm6545/how_to_make_an_enemy_follow_the_player/?utm_source=chatgpt.com&rdt=61372
    /**
     * tick method called repetitively in GameMap class
     * @param frameTime
     */
    public void tick(float frameTime) {
        this.elapsedTime += frameTime;

        float playerX = player.getX();
        float playerY = player.getY();

        float enemyX = this.hitbox.getPosition().x;
        float enemyY = this.hitbox.getPosition().y;

        float xDirection = playerX - enemyX;
        float yDirection = playerY - enemyY;
        float distance = (float) Math.sqrt(xDirection * xDirection + yDirection * yDirection);
        if (distance != 0) {
            xDirection /= distance;
            yDirection /= distance;
        }
        this.hitbox.setLinearVelocity(xDirection * velocity, yDirection * velocity);
    }


    /**
     * Setter method of the speed of enemy
     * @param velocity
     */
    public void setVelocity(float velocity){
        this.velocity = velocity;
    }


    /**
     * returns enemies animation, depends on the Players position which is initialised when calling
     * the enemies constructor -> animation depends on the player's position as the enemy moves towards
     * the enemy at any time and should look at the enemy at any time.
     */
    @Override
    public TextureRegion getCurrentAppearance() {

        float xDiff = player.getX() - this.getX();
        float yDiff = player.getY() - this.getY();

        if (Math.abs(xDiff) > Math.abs(yDiff)) {
            if (xDiff < 0) {
                return Animations.ENEMY_WALK_LEFT.getKeyFrame(this.elapsedTime, true);
            } else {
                return Animations.ENEMY_WALK_RIGHT.getKeyFrame(this.elapsedTime, true);
            }
        } else {
            if (yDiff < 0) {
                return Animations.ENEMY_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
            } else {
                return Animations.ENEMY_WALK_UP.getKeyFrame(this.elapsedTime, true);
            }
        }
    }

    @Override
    public float getX() {
        return hitbox.getPosition().x;
    }
    @Override
    public float getY() {
        return hitbox.getPosition().y;
    }

    public Body getBody() {
        return body;
    }
}
