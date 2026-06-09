package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.timblomenkamp.bomberquest.texture.Drawable;
import com.timblomenkamp.bomberquest.texture.Textures;



public class IndestructibleWall implements Drawable{

    /**
     * Coordinates of the IndestructibleWall
     */
    private final float y;
    private final float x;

    IndestructibleWall(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
    }


    /**
     * Creating the hitbox of the IndestructibleWall
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
        PolygonShape wall = new PolygonShape();
        // Make the polygon a square with a side length of 1 tile.
        wall.setAsBox(0.5f, 0.5f);
        // Attach the shape to the body as a fixture.
        body.createFixture(wall, 1.0f);
        // We're done with the shape, so we should dispose of it to free up memory.
        wall.dispose();
        // Set the chest as the user data of the body so we can look up the chest from the body later.
        body.setUserData(this);
    }

    public TextureRegion getCurrentAppearance() {
        return Textures.INDESTRUCTIBLE_WALL;
    }

    public float getX(){
        return x;
    }

    public float getY() {
        return y;
    }
}
