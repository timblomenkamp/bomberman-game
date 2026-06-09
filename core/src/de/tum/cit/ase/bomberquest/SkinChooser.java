package de.tum.cit.ase.bomberquest;

/**
 * This class is used to let the user choose a skin he likes.
 */
public class SkinChooser {

    private Skin skin;

    /**
     * An enum to choose the different skins
     */
    public enum Skin{
        STEVE,
        ORIGINAL
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}
