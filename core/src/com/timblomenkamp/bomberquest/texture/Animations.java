package com.timblomenkamp.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.timblomenkamp.bomberquest.BomberQuestGame;
import com.timblomenkamp.bomberquest.SkinChooser;

/**
 * Contains all animation constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Animations {
    private BomberQuestGame game;

    public Animations(BomberQuestGame game) {
        this.game = game;
    }

    /**
     * This methods sets the correct skin for the player. It sets the skin based on the enum the user selected.
     */
    public void setSkinForPlayer(){
        switch (game.getPlayerSkin()){
            case STEVE -> {
                CHARACTER_WALK_DOWN = new Animation<>(0.1f,
                        SpriteSheet.CHARACTER.at(1, 1),
                        SpriteSheet.CHARACTER.at(1, 2),
                        SpriteSheet.CHARACTER.at(1, 3),
                        SpriteSheet.CHARACTER.at(1, 4));
                CHARACTER_WALK_UP = new Animation<>(0.1f,
                        SpriteSheet.CHARACTER.at(3, 1),
                        SpriteSheet.CHARACTER.at(3, 2),
                        SpriteSheet.CHARACTER.at(3, 3),
                        SpriteSheet.CHARACTER.at(3, 4));
                CHARACTER_WALK_RIGHT = new Animation<>(0.1f,
                        SpriteSheet.CHARACTER.at(2, 1),
                        SpriteSheet.CHARACTER.at(2, 2),
                        SpriteSheet.CHARACTER.at(2, 3),
                        SpriteSheet.CHARACTER.at(2, 4));
                CHARACTER_WALK_LEFT = new Animation<>(0.1f,
                        SpriteSheet.CHARACTER.at(4, 1),
                        SpriteSheet.CHARACTER.at(4, 2),
                        SpriteSheet.CHARACTER.at(4, 3),
                        SpriteSheet.CHARACTER.at(4, 4)
                );
            }
            case ORIGINAL -> {
                CHARACTER_WALK_DOWN = new Animation<>(0.1f,
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(1, 4),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(1, 5),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(1, 6));
                CHARACTER_WALK_UP = new Animation<>(0.1f,
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(2, 4),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(2, 5),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(2, 6));
                CHARACTER_WALK_RIGHT = new Animation<>(0.1f,
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(2, 1),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(2, 2),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(2, 3));
                CHARACTER_WALK_LEFT = new Animation<>(0.1f,
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(1, 1),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(1, 2),
                        SpriteSheet.ORIGINAL_BOMBERMAN.at(1, 3));
            }
        }
    }



    // creating the different attributes for the different character animations. The actual animations are created above.
    public static Animation<TextureRegion> CHARACTER_WALK_DOWN;
    public static Animation<TextureRegion> CHARACTER_WALK_UP;
    public static Animation<TextureRegion> CHARACTER_WALK_RIGHT;
    public static Animation<TextureRegion> CHARACTER_WALK_LEFT;

    // The animation if the player dies
    public static final Animation<TextureRegion> CHARACTER_DIES = new Animation<>(1f,
            SpriteSheet.OBJECTS.at(3, 6),
            SpriteSheet.OBJECTS.at(3, 7),
            SpriteSheet.OBJECTS.at(3, 8),
            SpriteSheet.OBJECTS.at(3, 9)
    );




    //Enemy animation
    public static final Animation<TextureRegion> ENEMY_STAND = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(4, 8)
    );
    public static final Animation<TextureRegion> ENEMY_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(6, 7),
            SpriteSheet.ENEMY.at(6, 8),
            SpriteSheet.ENEMY.at(6, 9)
    );
    public static final Animation<TextureRegion> ENEMY_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(7, 7),
            SpriteSheet.ENEMY.at(7, 8),
            SpriteSheet.ENEMY.at(7, 9)
    );
    public static final Animation<TextureRegion> ENEMY_WALK_DOWN= new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(5, 7),
            SpriteSheet.ENEMY.at(5, 8),
            SpriteSheet.ENEMY.at(5, 9)
    );
    public static final Animation<TextureRegion> ENEMY_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(8, 7),
            SpriteSheet.ENEMY.at(8, 8),
            SpriteSheet.ENEMY.at(8, 9)
    );


    /**
     * The animation of the bomb
     */
    public static final Animation<TextureRegion> BOMB  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 1),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 2),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(4,3 )
    );


    //the different blast animations
    public static final Animation<TextureRegion> BLAST_UPPER_END  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(5, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(5, 8),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(10, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(10, 8)
    );

    public static final Animation<TextureRegion> BLAST_LOWER_END  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(9, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(9, 8),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(14, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(14, 8)
    );

    public static final Animation<TextureRegion> BLAST_RIGHT_END  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 10),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 10)
    );

    public static final Animation<TextureRegion> BLAST_LEFT_END  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 1),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 6),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 1),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 6)
    );

    public static final Animation<TextureRegion> BLAST_VERTICAL  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(6, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(6, 8),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(11, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(11, 8)
    );

    public static final Animation<TextureRegion> BLAST_HORIZONTAL  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 2),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 7),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 2),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 7)
    );

    public static final Animation<TextureRegion> BLAST_CENTER  = new Animation<>(1/3f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(7, 8),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 8)
    );






















}
