package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.timblomenkamp.bomberquest.texture.Drawable;
import com.timblomenkamp.bomberquest.texture.Textures;

/**
 * By collecting speed powerups, the player moves faster
 */
public class SpeedPowerUp implements Drawable {

    private final float x;
    private final float y;
    private boolean triggered;
    private Body body;

    /**
     * Create a BlastPowerUp at the given position.
     * @param world The Box2D world to add the BlastPowerUp hitbox to.
     * @param x The X position.
     * @param y The Y position.
     * */
    public SpeedPowerUp(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
        this.triggered = false;
    }

    /**
     * Create a BlastPowerUp body for the chest.
     * @param world The Box2D world to add the body to.
     */
    private void createHitbox(World world) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Static bodies never move, but static bodies can collide with them.
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set the initial position of the body.
        bodyDef.position.set(this.x, this.y);
        // Create the body in the world using the body definition.
        body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a polygon shape for the chest.
        PolygonShape box = new PolygonShape();
        // Make the polygon a square with a side length of 1 tile.
        box.setAsBox(0.1f, 0.1f);
        // Attach the shape to the body as a fixture.
        body.createFixture(box, 1.0f).setSensor(true);
        // We're done with the shape, so we should dispose of it to free up memory.
        box.dispose();
        // Set the chest as the user data of the body so we can look up the chest from the body later.
        body.setUserData(this);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.SPEEDPOWERUP;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public boolean isTriggered() {return triggered;}

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
