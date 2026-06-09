package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * similar to Ground class, represents the layer outside of the GameMap
 */
public class Outside  implements Drawable {

    private final int x;
    private final int y;

    public Outside(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.OUTSIDELAVA;
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
