package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class Entrance implements Drawable {


    /**
     * location of the Entrance
     */
    private final float x;
    private final float y;


    public Entrance(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //Here we don't need a hitbox as there is no contact to be recognised


    //The usual helper methods

    @Override
    public TextureRegion getCurrentAppearance() {return Textures.ENTRY;}

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
