package com.timblomenkamp.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {

    public static final TextureRegion GROUND = SpriteSheet.BASIC_TILES.at(3, 1);

    public static final TextureRegion OUTSIDELAVA = SpriteSheet.BASIC_TILES.at(10, 3);

    public static final TextureRegion EXITCLOSED = SpriteSheet.THINGS.at(1, 4);
    public static final TextureRegion EXITOPENED = SpriteSheet.BASIC_TILES.at(7, 3);
    public static final TextureRegion ENTRY = SpriteSheet.BASIC_TILES.at(2, 1);

    public static final TextureRegion CHEST = SpriteSheet.BASIC_TILES.at(5, 5);

    public static final TextureRegion INDESTRUCTIBLE_WALL = SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 4);
    public static final TextureRegion DESTRUCTIBLE_WALL = SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 5);

    public static final TextureRegion BOMB = SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 1);

    public static final TextureRegion BLASTPOWERUP = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 7);
    public static final TextureRegion BOMBPOWERUP = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 6);
    public static final TextureRegion SPEEDPOWERUP = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 4);

    
}
