package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;


/**
 * A destructible wall is a wall the player can destrpy.
 */
public class DestructibleWall implements Drawable {


    /**
     * coordinates of the DestructibleWall
     */
    private final float x;
    private final float y;

    /**
     * Wall constructor
     * @param world The Box2D world to add the DestructibleWall's hitbox to.
     * @param x The X position.
     * @param y The Y position.
     */
    DestructibleWall(World world, float x, float y) {
       this.x = x;
       this.y = y;
       createHitbox(world);
    }


    /**
     * @return the texture of the wall
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.DESTRUCTIBLE_WALL;
    }

    private Body body;


    /**
     * creating the Hitbox of the DestructibleWall wall
     * @param world
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

    public float getX(){
        return x;
    }
    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }
}
