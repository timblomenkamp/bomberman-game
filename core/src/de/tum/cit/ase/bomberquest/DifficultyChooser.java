package de.tum.cit.ase.bomberquest;

/**
 * This class is used to let the user choose the difficulty
 */
public class DifficultyChooser {


    /**
     * An enum to choose the different levels of difficulties
     */
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
