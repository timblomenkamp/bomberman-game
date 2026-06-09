package com.timblomenkamp.bomberquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.timblomenkamp.bomberquest.map.GameMap;

public class Score {
    private GameMap map;
    private int numberOfPowerUpsCollected;

    /**
     * The constructor of the class, which initialises the highScore.
     * The map is needed to calculate the current score.
     * @param map the current gameMap
     */
    public Score(GameMap map) {
        this.map = map;
        this.numberOfPowerUpsCollected = 0;
    }

    /**
     * Used to track the number of powerUps which the player collected.
     */
    public void numberOfPowerUpsCollected(){
        numberOfPowerUpsCollected += 1;
    }

    /**
     * Used to calculate the highScore based on time and collected PowerUps.
     * @return the score the player achieved
     */
   public int calculateScore (){
       int timeInt = Math.round(map.getCountdownTime());
       int result = (300 - (300 - timeInt)) + numberOfPowerUpsCollected * 3;
       return result;
   }


    /**
     * This method reads an integer (the highscore) from a file. This is necessary because else, each time you restart your IDE, the score would be lost.
     * This method is inspired by https://stackoverflow.com/questions/37283273/libgdx-filehandle-reading-and-writing-more-than-one-line
     * @return the high score read form the file
     */
    public int getHighScoreFromFile() {
        FileHandle file = Gdx.files.local("core/src/Highscore");
        if (file.exists()) {
            try {
                return Integer.parseInt(file.readString());
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * This method overrides the current highscore and substitutes it with the new score, given as a parameter to the method.
     * This method is inspired by https://stackoverflow.com/questions/37283273/libgdx-filehandle-reading-and-writing-more-than-one-line
     * @param score the score you want to keep as a new highscore.
     */
    public void saveHighScore(int score) {
        FileHandle file = Gdx.files.local("core/src/Highscore");
        file.writeString(String.valueOf(score), false);
    }

    /**
     * This method checks, if the player achieved a new highscore. If yes, the method calls saveHighScore, to store the score as a new highscore.
     * @param newScore the new score the player achieved.
     */
    public void updateHighScore(int newScore) {
        int currentHighScore = getHighScoreFromFile();
        if (newScore > currentHighScore) {
            saveHighScore(newScore);
        }
    }
}
