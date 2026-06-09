package de.tum.cit.ase.bomberquest.map;


/**
 * Importand helper class of the GameMap which is used to give HashMaps a 2 dimensional key
 */
public class MapKey {

    /**
     * Coordinates of the MapKey
     */
    private int x;
    private int y;

    public MapKey(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }



    // inspired by https://stackoverflow.com/questions/27581/what-issues-should-be-considered-when-overriding-equals-and-hashcode-in-java
    /**
     * in combination with hashCode() needed to make different MapKey objects
     * with same coordinate values comparable (-> equallity changed from "das Selbe" to "das Gleiche"
     * @param
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapKey)) return false;
        MapKey other = (MapKey) o;
        return this.x == other.x && this.y == other.y;
    }
    @Override
    public int hashCode() {
        // oder return Objects.hash(x, y);
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}
