package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.timblomenkamp.bomberquest.texture.Animations;
import com.timblomenkamp.bomberquest.texture.Drawable;

/**
 * The class blast is used, to show the blast when the bomb explodes. This is necessary because each blast has a different animation.
 */
public class Blast implements Drawable {

    private float x;
    private float y;
    private Bomb bomb;
    private boolean shouldBeRemoved;



    public Blast(World world, float x, float y, Bomb bomb) {
        this.x = x;
        this.y = y;
        this.bomb = bomb;
        createHitbox(world);
        this.shouldBeRemoved = false;
    }

    private Body body;

    /* creating the Hitbox of the Blast */
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

    /**
     * This method calculates the animation of the blast.
     * Each blast does not have the same animation,
     * therefore it needs to be calculated by using the coordinates of the bomb, the range and the position of the blast
     * @return the animation of the blast.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        if (bomb.getX() == this.x && bomb.getY() == this.y){
            return Animations.BLAST_CENTER.getKeyFrame(bomb.getElapsedTime(), true);
        }
        if (bomb.getX() - bomb.getGameMap().getBombRange() == this.x && bomb.getY() == this.y){
            return Animations.BLAST_LEFT_END.getKeyFrame(bomb.getElapsedTime(), true);
        }
        if (bomb.getX() + bomb.getGameMap().getBombRange() == this.x && bomb.getY() == this.y){
            return Animations.BLAST_RIGHT_END.getKeyFrame(bomb.getElapsedTime(), true);
        }
        if (bomb.getX() == this.x && bomb.getY() - bomb.getGameMap().getBombRange() == this.y){
            return Animations.BLAST_LOWER_END.getKeyFrame(bomb.getElapsedTime(), true);
        }
        if (bomb.getX() == this.x && bomb.getY() + bomb.getGameMap().getBombRange() == this.y){
            return Animations.BLAST_UPPER_END.getKeyFrame(bomb.getElapsedTime(), true);
        }
        if (bomb.getX() == this.x){
            return Animations.BLAST_VERTICAL.getKeyFrame(bomb.getElapsedTime(), true);
        }
        if (bomb.getY() == this.y){
            return Animations.BLAST_HORIZONTAL.getKeyFrame(bomb.getElapsedTime(), true);
        }
        return Animations.BLAST_VERTICAL.getKeyFrame(bomb.getElapsedTime(), true);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public boolean ShouldBeRemoved() {
        return shouldBeRemoved;
    }

    public void setShouldBeRemoved(boolean shouldBeRemoved) {
        this.shouldBeRemoved = shouldBeRemoved;
    }

    public Body getBody() {
        return body;
    }
}
