package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.timblomenkamp.bomberquest.texture.Drawable;
import com.timblomenkamp.bomberquest.texture.Textures;

/**
 * easiest class representing the ground underneath everything
 */
public class Ground implements Drawable {

    /**
     * ground coordinates
     */
    private final int x;
    private final int y;
    
    public Ground(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.GROUND;
    }
    
    @Override
    public float getX() {
        return x;
    }
    
    @Override
    public float getY() {
        return y;
    }
}
