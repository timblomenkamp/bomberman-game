package com.timblomenkamp.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.timblomenkamp.bomberquest.BomberQuestGame;
import com.timblomenkamp.bomberquest.Score;
import com.timblomenkamp.bomberquest.audio.BombPlacementSound;
import com.timblomenkamp.bomberquest.audio.GhostScreamSound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import java.util.*;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {
    // A static block is executed once when the class is referenced for the first time.
    static {
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    /**
     * The time step for the physics simulation.
     * This is the amount of time that the physics simulation advances by in each frame.
     * It is set to 1/refreshRate, where refreshRate is the refresh rate of the monitor, e.g., 1/60 for 60 Hz.
     */
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;

    /** The number of velocity iterations for the physics simulation. */
    private static final int VELOCITY_ITERATIONS = 6;

    /** The number of position iterations for the physics simulation. */
    private static final int POSITION_ITERATIONS = 2;

    /**The accumulated time since the last physics step.
     * We use this to keep the physics simulation at a constant rate even if the frame rate is variable.
     */
    private float physicsTime = 0;

    /** The game, in case the map needs to access it. */
    private  BomberQuestGame game; //used to be final

    /** The Box2D world for physics simulation. */
    private final World world;


    /**
     * Objects: player, entry, exit
     * 2-dimensional arrays: Ground, Outside
     * Lists: both types of walls, bombs, powerups, blasts
     */
    private Player player;
    private Exit exit;
    private Entrance entrance;

    private Outside[][] outside;

    private Ground[][] ground;
    private static List<DestructibleWall> destructibleWalls;
    private static List<IndestructibleWall> indestructibleWalls;
    private List<Bomb> bombs;
    private static List<Enemy> enemies;
    private List<BlastPowerUp> blastPowerUps;
    private List<BombPowerUp> bombPowerUps;
    private List<SpeedPowerUp> speedPowerUps;
    private static List<Blast> blasts;
    public Score score;

    //Attributes to make things dynamic
    public float enemyStandardSpeed = 0.01f;
    public float enemyAggressionSpeed = 0.5f;
    public int enemyAggressionDistance = 3;
    private float countdownTime = 300f;


    /**
     * The maximum number of bombs the player can place at the same time
     */
    private int maxBombs;
    /**
     * The range of the bombs
     */
    private int bombRange;
    /**
     * The death time is used to ensure that the screen only returns to the menu screen, if the death animation is over
     */
    private float deathTime = 0f;



    //Lists of different objects to remove e.g. if they are dead or destroyed
    private static List<DestructibleWall> wallBodiesToRemove;
    public static List<Enemy> enemiesToRemove = new ArrayList<>();
    List<Blast> blastsToRemove = new ArrayList<>();

    /**
     * chosen file as String format, Hashmap for comfortable creation of map a
     */
    public static String propertiesAsString;
    private Map<MapKey, Integer> mapKeyIntegerValueMap;

    /**
     * file initialised in the constructor when creation of map instance happens
     */
    private File passedFile;


    /**
     * Constructor for initialising relevant attributes
     * buildGameMapWithAssignmentMap() method called which in combination with
     * createMapFromFile() creates map from passed file initialised right here
     * @param game from BomberQuestGame
     * @param file from native file chooser
     */
    public GameMap(BomberQuestGame game, File file) {
        this.passedFile = file;
        this.game = game;
        world = new World(Vector2.Zero, true);
        this.score = new Score(this);

        bombs = new ArrayList<>();
        bombPowerUps = new ArrayList<>();
        blastPowerUps = new ArrayList<>();
        speedPowerUps = new ArrayList<>();
        enemies = new ArrayList<>();
        enemiesToRemove = new ArrayList<>();
        blasts= new ArrayList<>();
        indestructibleWalls = new ArrayList<>();
        destructibleWalls = new ArrayList<>();
        wallBodiesToRemove = new ArrayList<>();
        maxBombs = 1;
        bombRange = 1;

        mapKeyIntegerValueMap = createHashMapFromFile();
        setParametersByDifficulty();
        buildGameMap();
    }


    /**
     * This methods sets different parameters based on the difficulty. The harder the difficulty, the harder the game gets.
     */
   public void setParametersByDifficulty(){

        switch(game.getDifficulty()){

            case EASY -> {
                enemyStandardSpeed = 0.05f;
                enemyAggressionSpeed = 0.5f;
                enemyAggressionDistance = 3;
                countdownTime = 300f;
            }
            case MEDIUM -> {
                enemyStandardSpeed = 0.075f;
                enemyAggressionSpeed = 0.75f;
                enemyAggressionDistance = 4;
                countdownTime = 240f;
            }
            case HARD -> {
                enemyStandardSpeed = 0.1f;
                enemyAggressionSpeed = 1.25f;
                enemyAggressionDistance = 6;
                countdownTime = 180f;
            }
            default -> {
                enemyStandardSpeed = 0.05f;
                enemyAggressionSpeed = 0.5f;
                enemyAggressionDistance = 3;
                countdownTime = 300f;
            }
        }
   }



    /**
     * Method enabling player to place bombs, but only if he has the permission to do so
     */
    public void addBomb() {
        if (checkBombPlacementPermission()== true){
            for (Bomb bomb : bombs) {
                if (bomb.getX() == player.getX() && bomb.getY() == player.getY()) {
                    return;
                }
            }
            int roundedX = Math.round(player.getX());
            int roundedY = Math.round(player.getY());
            BombPlacementSound.BOMB_PLACEMENT_SOUND.play();
            Bomb bomb = new Bomb(world, roundedX, roundedY);
            bombs.add(bomb);
        }
    }


    /**
     * Increases the bomb range by 1, if not exceeding the limit of 8
     */
    public void increaseBombRange(){
        if (bombRange < 8) {
            bombRange += 1;
        }
    }


    /**
     * Method starting the process of creating the GameMap from the passed file
     * takes passed file given in the constructor automatically, as it makes use of the initialised passedFile attribute
     * takes file -> puts it into a String via a String builder -> String split into substrings via different splitting criteria
     * This method is inspired by https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file and https://stackoverflow.com/questions/51288154/parse-a-string-of-key-value-to-a-map?utm_source=chatgpt.com
     * with refinements
     * @return hashmap wth MapKey as key and value between 0 and 6 for map creation
     */
    /**
     * Robustly reads a map file's contents, trying multiple strategies so it works
     * regardless of the working directory (Gradle run vs IDE direct launch).
     */
    private String readMapFile(File file) {
        String path = file.getPath();
        // 1) User-selected files are absolute on disk -> read directly.
        if (file.isAbsolute() && file.exists()) {
            try {
                return new String(java.nio.file.Files.readAllBytes(file.toPath()));
            } catch (Exception ignored) {}
        }
        // 2) Bundled maps: try classpath first (works from any working directory).
        try {
            com.badlogic.gdx.files.FileHandle h = Gdx.files.classpath(path);
            if (h.exists()) return h.readString();
        } catch (Exception ignored) {}
        // 3) Fall back to internal (relative to working directory).
        try {
            com.badlogic.gdx.files.FileHandle h = Gdx.files.internal(path);
            if (h.exists()) return h.readString();
        } catch (Exception ignored) {}
        // 4) Last resort: try reading the raw path from disk.
        try {
            if (file.exists()) {
                return new String(java.nio.file.Files.readAllBytes(file.toPath()));
            }
        } catch (Exception ignored) {}
        System.err.println("Could not load map file '" + path + "'. Working directory: "
                + System.getProperty("user.dir"));
        return null;
    }

    public Map<MapKey, Integer> createHashMapFromFile(){
        propertiesAsString = readMapFile(passedFile);

        Map<MapKey, Integer> assignmentMap = new HashMap<>();

        if(propertiesAsString != null) {
            String[] rows = propertiesAsString.split("\n");
            for (String row : rows) {

                row = row.trim();

                if (row.isEmpty() || row.startsWith("#")) {
                    continue;
                }

                String[] parts = row.split("=");

                if (parts.length == 2) {
                    String[] coordinates = parts[0].split(",");
                    if (coordinates.length == 2) {
                        int x = Integer.parseInt(coordinates[0].trim());
                        int y = Integer.parseInt(coordinates[1].trim());

                        int value = Integer.parseInt(parts[1].trim());

                        MapKey key = new MapKey(x, y);
                        assignmentMap.put(key, value);
                    }
                }
            }
        }
        return assignmentMap;
    }


    /**
     * core of the GameMap class, handles the actual creation of the GameMap
     * takes the HashMap returned in the createHashMapFromFile() method and reads it
     * -> reads out coordinates and created the necessary instance of it + adds it into the corresponding List initialised in the constructor
     * return type void bc only assigns internally and called in the constructor
     */
    public void buildGameMap() {

        // map initialised
        Map<MapKey, Integer> mapAsMap = createHashMapFromFile();
        // store location of enemies bc depends on player being initialised (initialised when entry initialised with value 2)
        List<MapKey> storeEnemies = new ArrayList<>();

        if (mapAsMap == null) {
            throw new IllegalStateException("The map has not been initialized.");
        } else {

            // created outside layer of the map in order to not be a void
            this.outside = new Outside[findMapSize().getX() + 24][findMapSize().getY() + 24];
            for (int i = 0; i < outside.length; i++) {
                for (int j = 0; j < outside[i].length; j++) {
                    this.outside[i][j] = new Outside(i - 12, j - 12);
                }
            }



            // create ground layer underneath the actual map assignments
            this.ground = new Ground[findMapSize().getX()][findMapSize().getY()];
            for (int i = 0; i < ground.length; i++) {
                for (int j = 0; j < ground[i].length; j++) {
                this.ground[i][j] = new Ground(i, j);
                }
            }

            for (Map.Entry<MapKey, Integer> entry : mapAsMap.entrySet()) { // takes each entry in the map (key + value)
                MapKey key = entry.getKey();  // bound to key instance

                // coordinates of key taken out for easier handling
                int x = key.getX();
                int y = key.getY();

                if (entry != null) {
                    if(mapAsMap.get(key) == 0) { // place indestructible wall
                        indestructibleWalls.add(new IndestructibleWall(world, x, y));
                    }
                    else if (mapAsMap.get(key) == 1) { // place destructible wall
                        destructibleWalls.add(new DestructibleWall(world, x, y));
                    }
                    else if (mapAsMap.get(key) == 2) { // place map entry and bind player spawn location to it
                        this.entrance = new Entrance(x, y);
                        player = new Player(world, entrance.getX(), entrance.getY());
                    }
                    else if(mapAsMap.get(key) == 3){ // store enemy with location in the enemy store in order to not have issues with player being initialised or not
                        storeEnemies.add(new MapKey(x, y));
                    }
                    else if (mapAsMap.get(key) == 4) { // create/ place exit
                        this.exit = new Exit(world, x, y);
                    }
                    else if(mapAsMap.get(key) == 5){ // create/ place bomb power-up
                        bombPowerUps.add(new BombPowerUp(world, x, y));
                        destructibleWalls.add(new DestructibleWall(world, x, y));
                    }
                    else if(mapAsMap.get(key) == 6){ // create/ place blast radius power-up
                        blastPowerUps.add(new BlastPowerUp(world, x, y));
                        destructibleWalls.add(new DestructibleWall(world, x, y));
                    }
                }
            }

            //from this point on handling inherent exceptions from the file provided

            // if the entrance is not given in file (no value 2) it would not exist and the player would not have been initialised as well
            // -> making the entrance random and binding the player's spawn location to it
            if (entrance == null) {
                List<Map.Entry<MapKey, Integer>> entryList = new ArrayList<>(filterGround().entrySet()); //Player can only spawn at ground -> filter all ground fields out with filterGround() method
                if (entryList.isEmpty()) {
                    throw new IllegalArgumentException("No ground tiles available for player spawn in map");
                }
                int index = new Random().nextInt(entryList.size()); // picks a random index of all ground coordinates
                Map.Entry<MapKey, Integer> randomMapKey = entryList.get(index); // gets the corresponding index

                entrance = new Entrance(randomMapKey.getKey().getX(), randomMapKey.getKey().getY()); // creates entrance at the coordinates corresponding to the random pick of ground tiles
                player = new Player(world, entrance.getX(), entrance.getY()); // player's spawn bound to the very same location
            }

            // if the exit isn't given in file it should be assigned to a location of a random destructible wall
            if(this.exit == null){
                Set keys = filterDestructableWalls().keySet(); // operation to filter out the destructible walls
                MapKey key = getRandomKeyFromSet(keys); // gets random MapKey by utilizing helper method

                this.exit = new Exit(world, key.getX(), key.getY()); // creates exit at random destructible wall
            }

            // Enemies assigned to list at this point bc now we can guarantee that the player has been initialised
            // previously there would have been trouble if and enemy was created before the player
            for(MapKey key : storeEnemies){
                int x = key.getX();
                int y = key.getY();

                enemies.add(new Enemy(world, x, y, player));
            }

            //randomly spawn between 1 and 5 speed power-ups underneath any destructible wall
            Map<MapKey, Integer> walls = filterDestructableWalls(); // Hashmap of real walls which were given in the file, not the ones artificially added due to a power-up
            MapKey exitKey = new MapKey((int) exit.getX(),(int) exit.getY());
            walls.remove(exitKey); // deleting the wall under which the exit lies

            int numberOfPowerUps = (int) (Math.random() * 5) + 1;

            if(numberOfPowerUps > destructibleWalls.size()){
                numberOfPowerUps = destructibleWalls.size();
            }

            // random assignment of speed power-ups
            for(int i=0; i <= numberOfPowerUps - 1; i++){
                List<Map.Entry<MapKey, Integer>> entryList = new ArrayList<>(walls.entrySet()); //destructible walls without exit one
                int wallIndex = new Random().nextInt(entryList.size()); // picks a random index of all wall coordinates
                Map.Entry<MapKey, Integer> randomMapEntry = entryList.get(wallIndex);

                speedPowerUps.add(new SpeedPowerUp(world, randomMapEntry.getKey().getX(), randomMapEntry.getKey().getY()));
                walls.remove(randomMapEntry.getKey());
            }
        }
    }


    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */
    public void tick(float frameTime) {
        this.player.tick(frameTime);

        for(Enemy enemy : enemies){
            enemy.setVelocity(enemyStandardSpeed);
            float distanceX = enemy.getX() - player.getX();
            float distanceY = enemy.getY() - player.getY();

            if(Math.abs(distanceX) <= enemyAggressionDistance && Math.abs(distanceY) <= enemyAggressionDistance){
                enemy.setVelocity(enemyAggressionSpeed);
            }
            enemy.tick(frameTime);
        }
        exit.tick(frameTime);

        List<Bomb> explodedBombs = new ArrayList<>();

        for (Bomb bomb : bombs) {
            bomb.updateTimer(frameTime);
            bomb.tick(frameTime, this);
            if(bomb.explosionOver()){
                explodedBombs.add(bomb);
                blastsToRemove.addAll(bomb.getBlasts());
            }
        }
        bombs.removeAll(explodedBombs);
        gameOver(frameTime);
        this.allEnemiesDefeated();
        doPhysicsStep(frameTime);
    }

    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
        removeMarkedBodies();
    }

    /**
     * This method removes the marked bodies (i.e. objects which are in the different maps).
     * This is necessary to ensure that also the bodies of the objects are deleted, if the objects are destroyed.
     */
    private void removeMarkedBodies(){
        //remove all bodies of destructible walls which should disappear
        for (DestructibleWall destructibleWall1 : wallBodiesToRemove){
            world.destroyBody(destructibleWall1.getBody());
        }
        wallBodiesToRemove.clear();

        //remove all bodies of enemies which should disappear
        for(Enemy e : enemiesToRemove){
            GhostScreamSound.GHOST_SCREAM_SOUND.play();
            world.destroyBody(e.getBody());
        }
        enemiesToRemove.clear();

        //remove all bodies of blast power ups which should disappear
        List<BlastPowerUp> blastPowerUpsToRemove = new ArrayList<>();
        for (BlastPowerUp blastPowerUp : blastPowerUps){
            if (blastPowerUp.isTriggered()){
                blastPowerUpsToRemove.add(blastPowerUp);
            }
        }
        for (BlastPowerUp blastPowerUp : blastPowerUpsToRemove){
            world.destroyBody(blastPowerUp.getBody());
        }
        blastPowerUps.removeAll(blastPowerUpsToRemove);

        //remove all bodies of bomb power ups which should disappear
        List<BombPowerUp> bombPowerUpsToRemove = new ArrayList<>();
        for (BombPowerUp bombPowerUp : bombPowerUps){
            if (bombPowerUp.isTriggered()){
                bombPowerUpsToRemove.add(bombPowerUp);
            }
        }
        for (BombPowerUp bombPowerUp : bombPowerUpsToRemove){
            world.destroyBody(bombPowerUp.getBody());
        }
        bombPowerUps.removeAll(bombPowerUpsToRemove);

        //remove all bodies of speed power ups which should disappear
        List<SpeedPowerUp> speedPowerUpsToRemove = new ArrayList<>();
        for (SpeedPowerUp up : speedPowerUps){
            if (up.isTriggered()){
                speedPowerUpsToRemove.add(up);
            }
        }
        for (SpeedPowerUp up : speedPowerUpsToRemove){
            world.destroyBody(up.getBody());
        }
        speedPowerUps.removeAll(speedPowerUpsToRemove);

        //remove all bodies of blasts which should disappear
        for (Blast blast : blastsToRemove){
            world.destroyBody(blast.getBody());
        }
        blasts.removeAll(blastsToRemove);
        blastsToRemove.clear();
    }

    /**
     * This method checks if the game is over. if so, the user is not allowed to do any inputs.
     * After a certain time (if the animation of the player dying is over) the screen switches to the gameover screen.
     * @param frameTime the time between the frames
     */
    public void gameOver(float frameTime){
        if (player.isDead()){
            deathTime += frameTime;
            player.setInputAllowed(false);
        }
        if (player.isDead() && deathTime >= 2f){
            game.goToGameOverScreen();
        }
    }
    // Helpermethods

    /**
     * This method checks, if there exists a destructible wall at the coordinates given as parameters
     * @param x The x coordinate where should be checked if a destructible wall is located at this x coordinate
     * @param y The y coordinate where should be checked if a destructible wall is located at this y coordinate
     * @return the destructible wall, which is located at the coordinates given as parameters to the method.
     */
    public static DestructibleWall findDestructibleWall(int x, int y){
        for (DestructibleWall destructibleWall1 : destructibleWalls){
            int wallXRounded = Math.round(destructibleWall1.getX());
            int wallYRounded = Math.round(destructibleWall1.getY());
            if (wallXRounded == x  && wallYRounded == y){
                return destructibleWall1;
            }
        }
        return null;
    }

    /**
     * This method checks, if there exists an indestructible wall at the coordinates given as parameters
     * @param x The x coordinate where should be checked if a destructible wall is located at this x coordinate
     * @param y The y coordinate where should be checked if a destructible wall is located at this y coordinate
     * @return the indestructible wall, which is located at the coordinates given as parameters to the method.
     */
    public static IndestructibleWall findIndestructibleWall(int x, int y){
        for (IndestructibleWall indestructibleWall : indestructibleWalls){
            int wallXRounded = Math.round(indestructibleWall.getX());
            int wallYRounded = Math.round(indestructibleWall.getY());
            if (wallXRounded == x  && wallYRounded == y){
                return indestructibleWall;
            }
        }
        return null;
    }

    /**
     * @return true if all enemies are defeated and false if not
     */
    public boolean allEnemiesDefeated(){
        if (enemies.isEmpty()){
            this.exit.setOpen(true);
            return true;
        }
        return false;
    }

    /**
     * @return the size of the enemies list
     */
    public static int countEnemies(){
        return enemies.size();
    }

    /**
     * This method destroys the destructible wall, which was given as a paramteter to the method
     * @param destructibleWall2 the destructible wall which sould be destroyed
     */
    public static void destroyDestructibleWall(DestructibleWall destructibleWall2){
        destructibleWalls.remove(destructibleWall2);
        wallBodiesToRemove.add(destructibleWall2);
    }

    /**
     * returns an enemy if there is one at a requested position
     */
    public static Enemy findEnemyOnField(int x, int y){
        for(Enemy e : enemies){
            int enemyRoundedX = Math.round(e.getX());
            int enemyRoundedY = Math.round(e.getY());

            if(enemyRoundedX == x && enemyRoundedY == y){
                return e;
            }
        }
        return null;
    }

    /**
     * @param enemy the enemy which should be destroyed
     */
    public static void destroyEnemy(Enemy enemy){
        GhostScreamSound.GHOST_SCREAM_SOUND.play();
        enemies.remove(enemy);
        enemiesToRemove.add(enemy);
    }

    /**
     * @return map with 7 as ground coordinates
     */
    public Map<MapKey, Integer> createMapWithGround(){
        Map<MapKey, Integer> fullMap = createHashMapFromFile();

        for (int x = 0; x <= findMapSize().getX(); x++) {
            for (int y = 0; y <= findMapSize().getY(); y++) {
                MapKey testKey = new MapKey(x, y);
                fullMap.putIfAbsent(testKey, 7);
            }
        }
        return fullMap;
    }

    /**
     * helper method to filter a random MapKey from a given Set of MapKeys
     */
    public MapKey getRandomKeyFromSet(Set<MapKey> set){
        if (set == null || set.isEmpty()) {
            throw new IllegalArgumentException("Cannot select random key from empty set");
        }
        int index = new Random().nextInt(set.size());
        int currIndex = 0;
        for(MapKey key : set){
            if(currIndex == index){
                return key;
            }
            currIndex++;
        }
        return null;
    }

    /**
     * finds the maximum map size, needed for Ground & Outside operations
     */
    public MapKey findMapSize(){
        int xIndex = 0;
        int yIndex = 0;
        mapKeyIntegerValueMap = createHashMapFromFile();
        for(MapKey key : mapKeyIntegerValueMap.keySet()) {
            int x = key.getX();
            int y = key.getY();

            if (xIndex < x) {
                xIndex = x;
            }
            if (yIndex < y) {
                yIndex = y;
            }
        }
        return new MapKey(xIndex, yIndex);
    }



    /**
     * sample method for how to filter a hashmap for specified  values
     * All filter methods are inspired by https://stackoverflow.com/questions/33459961/how-to-filter-a-map-by-its-values-in-java-8
     */
    public Map<MapKey, Integer> filterSpaces(){
        Map<MapKey, Integer> mapAsMap = createHashMapFromFile();

        Map<MapKey, Integer> filteredMap = mapAsMap.entrySet().stream().filter(entry -> {Integer value = entry.getValue(); return value != 0 && value != 1 && value != 4;}) // filter after values
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return filteredMap;
    }

    /**
     * filters out the destructible walls from the file provided
     * All filter methods are inspired by https://stackoverflow.com/questions/33459961/how-to-filter-a-map-by-its-values-in-java-8
     */
    public Map<MapKey, Integer> filterDestructableWalls(){
        Map<MapKey, Integer> mapAsMap = createHashMapFromFile();
        Map<MapKey, Integer> filteredMap = mapAsMap.entrySet().stream().filter(entry -> entry.getValue().equals(1)) // filter after values
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return filteredMap;
    }

    /**
     * filters all coordinates which are only a ground by using the complete map created with createFullMapAsMap()
     * and returns the new pure ground map
     * All filter methods are inspired by https://stackoverflow.com/questions/33459961/how-to-filter-a-map-by-its-values-in-java-8
     */
    public Map<MapKey, Integer> filterGround(){
        Map<MapKey, Integer> mapAsMapFull = createMapWithGround();
        Map<MapKey, Integer> filteredMap = mapAsMapFull.entrySet().stream().filter(entry -> entry.getValue().equals(7)) // filter after values
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return filteredMap;
    }


    /**
     * @return true if the player is allowed to place a bomb, false if not
     * The player is not allowed to place a bomb, if the limit of maxBombs is reached or the player wants to place more than 8 bombs at the same time.
     */
    public boolean checkBombPlacementPermission(){
        if (maxBombs > bombs.size()){
            return true;
        }
        return false;
    }

    /**
     * Increase the bomb limit by 1
     */
    public void increaseBombLimit(){
        if (maxBombs < 8){
            maxBombs += 1;
        }
    }


    // all necessary getters and setters
    public List<Outside> getOutside() {
        return Arrays.stream(outside).flatMap(Arrays::stream).toList();
    }
    

    public List<Ground> getGround() {
        return Arrays.stream(ground).flatMap(Arrays::stream).toList();
    }
    public List<DestructibleWall> getDestructibleWall() {
        return destructibleWalls;
    }
    public List<IndestructibleWall> getIndestructibleWalls() {
        return indestructibleWalls;
    }
    public Player getPlayer() {
        return player;
    }
    public List<Enemy> getEnemies() {
        return enemies;
    }
    public List<Bomb> getBombs() {
        return bombs;
    }
    public Exit getExit() {
        return exit;
    }
    public World getWorld() {
        return world;
    }
    public Entrance getEntry() {
        return entrance;
    }
    public List<BlastPowerUp> getBlastPowerUps() {
        return blastPowerUps;
    }
    public List<BombPowerUp> getBombPowerUps() {
        return bombPowerUps;
    }
    public List<Blast> getBlasts() {
        return blasts;
    }
    public int getBombRange() {
        return bombRange;
    }
    public void setBombRange(int bombRange) {
        this.bombRange = bombRange;
    }
    public int getMaxBombs() {
        return maxBombs;
    }
    public void setMaxBombs(int maxBombs) {
        this.maxBombs = maxBombs;
    }
    public float getCountdownTime() {
        return countdownTime;
    }
    public void setCountdownTime(float countdownTime) {
        this.countdownTime = countdownTime;
    }
    public List<SpeedPowerUp> getSpeedPowerUps() {
        return speedPowerUps;
    }
}
