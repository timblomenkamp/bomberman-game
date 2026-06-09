package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.timblomenkamp.bomberquest.texture.Animations;
import com.timblomenkamp.bomberquest.texture.Drawable;
import com.timblomenkamp.bomberquest.texture.Textures;

public class Exit implements Drawable {


    /**
     * location of the exit, whether it is already opened or not <--> connected to whether all enemies are dead
     */
    private final float x;
    private final float y;
    private boolean isOpen;
    private float elapsedTime;

    /**
     * Create an exit at the given position.
     * @param world The Box2D world to add the exit's hitbox to.
     * @param x The X position.
     * @param y The Y position.
     */
    public Exit(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
        this.isOpen = false;
    }

    /**
     * Create a Box2D body for the exit.
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
        Body body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a polygon shape for the chest.
        PolygonShape box = new PolygonShape();
        // Make the polygon a square with a side length of 1 tile.
        box.setAsBox(0.4f, 0.4f);
        // Attach the shape to the body as a fixture.
        body.createFixture(box, 1.0f).setSensor(true);
        // We're done with the shape, so we should dispose of it to free up memory.
        box.dispose();
        // Set the chest as the user data of the body so we can look up the chest from the body later.
        body.setUserData(this);
    }


    /**
     * obligatory tick method to connect the logic of the exit to the elapsed time
     * @param frameTime
     */
    void tick(float frameTime){
        this.elapsedTime += frameTime;
    }


    /**
     * The texture of the exit depends on, if it is already unlocked or not.
     * @return the current animation of the exit
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        if (isOpen){
            return Textures.EXITOPENED;
        }
        return Textures.EXITCLOSED;
    }

    @Override
    public float getX(){
        return x;
    }

    @Override
    public float getY() {
        return y;
    }


    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;}
}
