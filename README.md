# Bomber Seal

Hello wandering Seal, welcome to Bomber Seal! We're thrilled you've stumbled upon our game. 
Hopefully, you've honed your skills at the Seal Training Center because now it's time to prove yourself 
in this final trial: the Bomber Simulator. If you manage to escape from the cursed map, you will earn the
coveted Seal Certificate, proving you're worthy of your sealhood.

## Shortcut for the Corrector 
If you are not interested in winning every level at every difficulty (more on that later) in every game you can just press "P" in order to switch to the 
win screen immediately. This is based on the same logic of having defeated all enemies and running through the opened exit. :) 

## How to play the game?
To start the game, just press the play button in the upper right corner. You will then be redirected to the menu 
screen. If you press the button „Play voyage“, the game will launch with a default map and the voyage starts. After 
completing one map, you can proceed to the next map (more on that bonus feature below). If you press the button 
„Choose file“, you may choose a file ending with „properties“.  After that, the map is being created and you will 
be redirected to the GameScreen. If you press „Quit“, the game stops and the window closes.

You also have the possibility to choose a skin or choose a difficulty (More on that below).

To place bombs, press space. If you press escape, you will get to the game menu and the game will be paused. This time, you have the option to 
resume the game. When you choose this option, the game will continue at the point in time where you paused it.

If you die or win the game, you will have the option to return to the menu screen and choose if you want to start 
the game, load a map or quit the game.

(FYI the game looks best in fullscreen) 


## How is the code structured?
All imported objects are created in the map package and have its own class. The GameMap class is the central 
administration for all these objects. Therefore, if a game map is being deleted or overridden, the progress of the 
game is lost and you can start again. This ensures that you can easily start a new game if you lost or have won by 
creating a new GameMap instead of having to create a new instance of BomberQuestGame.
The objects such as enemies, bombs and walls are stored in the GameMap class as ArrayLists. We decided to use lists,
because they are more intuitive and allow us to easily remove and add objects to them. After an object has been
destroyed (e.g. a destructible wall), the object has to be removed from the respective list and its body has to be
destroyed. This happens in various methods, such as the removeMarkedBodies() method.

The difficulty chooser and skin chooser are instances of the BomberQuestGame class. This ensures that the difficulty 
and the skin is only changed if the game gets completely restarted and not if you choose a new map. The score is an 
instance of the map class, because we needed access to many attributes from this class such as the timer. It was also 
meaningful because it gets computed after completing one map.

If the user selects a valid file, the file handle returned by the FileChooser is transferred to the local 
attribute "file“. The constructor of the GameMap class accesses the attribute „file“. We decided to not create the 
GameMap in the method create() in the BomberQuestGame class, but create an own method, just to create the map, the 
createMap() method. This ensures that the map is only created when we want it to be created. If the file is null 
(e.g. the user did not choose a valid file or presses the button „Go to game“) the method loads the default map. 
Else, it accepts the FileHandle which is stored in the file attribute, converts it to a proper file and creates 
the map. 

Creating the map is the very core of our program. In our GameMap class we built the map with the command buildGameMap(). This method calls the method buildHashMapWithFile()
which reads out a passed file and transforms it from the file format to a string and after that into a HashMap (using MapKey - more on that in a second). This HashMap is taken 
and the method iterates through every position of it, placing the respective objects at the necessary positions. Accordingly, every "value" (between 0 and 6) is passed as 
an instance at a respective coordinate to the respective list of the object, which is an attribute to the GameMap. Later when drawing everything we iterate trough each list 
in the right order to draw every assignment out. 

... and what is a MapKey? A MapKey is a class whose only purpose is to serve as a key in a HashMap in the GameMap class. 
To make things easier to follow we changed any file given over a String into a HashMap. Because every object is based on two coordinates we decided to 
just make the Key to the HashMap also two-dimensional. Therefore, we created the MapKey class whose instances are solely based on two coordinates in order to 
form Keys. This MapKeys are utilized extremely often in order to most importantly place objects into the map. Here we often have a relationship between two coordinates 
and a value which makes the HashMap come in extremely handy.

Lastly of course we implemented all minimum requirements and stuck to the upper limit of a maximum of 8 bomb/ blast power-ups. From the point you collected 8 
onwards the counter and bomb can't improve anymore.


## Implementations which go beyond the minimum requirements

### Consecutive maps (Voyage mode)
Everybody knows that the best games always have a progressive nature which enables you to really feel your improvements. Therefore, we implemented a game mode we like to call "Voyage". When Being inside, of the Menu screen next to the option of
loading an own map you can have this incredible experience. When starting the Voyage you start with an easy map only containing 16 enemies. After completion, you get the option to continue with the next part of the voyage (= next map). Having completed this second map which is
fairly harder than its predecessor you again get the option to continue the voyage. When continuing you find yourself in the last, hardest level. With over 50 enemies and no indestructible walls, you are outnumbered and potentially have to look for the exit for a very long time.
After successful completion of all three maps in a consecutive manner you get to a custom Victory screen which congratulates you. This marks the end of an exciting journey through our own hand made maps.

### Custom maps
For our Voyage mode we created 3 custom maps with increasing difficulty level. These are hand-crafted and structured in a very meaningful way.

### Enemy Movement
Our enemies use advanced intelligence to make the game more fun by following you around. Every enemy is initialised with an
instance of a player which allows them to follow the player from the second  on the game is started. Even though these highly
intelligent enemies are able to spot you from several miles away they won’t be able to follow you too fast unless you get close. If your
distance to the enemy falls under an internally set threshold of x tiles (which is set by your difficulty preferences) the enemies get aggressive and stop slowly drifting towards
you but instead hunt you with massively increased movement speed, making the game a real challenge. From this point on every step
into a corner could be fatal. 

### Enemy Animation
Of course the enemies’ intelligence is also visible at any point in time. They run on advanced animations which make them look into
your direction constantly. Their getCurrentAppearance() method always looks at the distance of the enemy to the player in horizontal
and vertical direction and returns the animation matching the direction the player is in. Via the tick method of the enemy class
connected to the tick method of the GameMap class this animation is displayed on your screen making the gaming experience extra fun.

### Score
For the sake of a little addiction factor, we have implemented a score. It is calculated by factors like time left and power-ups collected.  

### High score mechanism 
But what would a score be without a mechanism keeping track of the high score? This high score is not only retained when the player loads a new map,
but also when he closes and reopens the game. The new score is saved in a separate text file if it is higher than the previous score.
The game is able to read the file, and write integer into it. The player is also shown whether he has set a new high score or not.
You can even push the high score via git, and it gets displayed on the new device.

### Custom difficulty levels
Wouldn't it be boring to only play at a fixed level of difficulty? That's exactly what we thought and what made us come up with custom difficulty settings. When opening the Game you find yourself inside the game Menu where you can find an option to set the difficulty of your liking.
You get passed to a new screen with same themed custom background in which you can pick and choose between EASY, MEDIUM and HARD. Accordingly the game time and sensitivity of the ghosts is adjusted. The harder option you go for the less time you have and the more aggressive the enemies are. The enemies' default
movement speed is increased each time, and they switch to their aggressive mode faster. But don't worry forgetting what mode you picked: Depending on your selection the current difficulty mode is also displayed in the HUD.

### Selecting Skins in the Menu
When you want to switch things up between games you have the possibility to change your skin from the young boy we like to call Steve to the normal bomberman skin and vice versa. Of course next to the selection button you already get a little preview of what the skin looks like in case you are not familiar with the original bomberman. ;)

### Speed power-up
In order to make the game more dynamic we implemented another custom power-up - the speed power-up. Because we want the logic to apply on an given map we
set them up in a completely random way. Similar to the Random game map entrance we filtered every destructible wall and out of those selected a random amount between 1 and 5. Each speed power-up increased the player's movement
speed by 0.5f. Whenever you load a new GameMap there will be up to 5 powerful speed power-ups hidden underneath destructible walls. But be patient - if you get unlucky there might be only one.

### Random Game Entrance
In the buildGameMap() method automatically implements a technique which is able to assign the Entrance to a random tile 
on the map on which per default nothing is placed. To implement this logic we made use of several methods working together. 
First of all we implemented the createMapWithGround() method. To keep things dynamic it utilises the findMapSize() method in 
order to calculate the maximum coordinate of a passed map. By for loop which iterates over each coordinate until this 
maximum coordinate is hit there is a mechanism which checks whether there is already a value between 0 and 6 assigned to the
“key of the map”. If not this means it is a possible spawn location of an Entrance - with value 7 for every coordinate that
is below the maximum coordinate and not referenced in the file provided. Therefore the returned complete map (with value 7 for
ground tiles) is needed for further processing. This processing is taking place in the filterGround() method, which filters 
the complete map by values = 7. Correspondingly a “pure” map with a variety of coordinates and only 7 as the value is returned. 
This map is now utilised in the buildGameMap() method which under the condition that the entrance has not been initialised 
yet picks a random index out this map and creates the respective entrance corresponding to the key of the map. Lastly the
players spawning is set to the location of the entrance ensuring the player to spawn randomly. This complex mechanism of 
randomising coordinates which are technically not given to you allows us to take all kinds of .properties files without resulting
in a crash of the game. ;)

### Customised HUD
For better readability of the individual lines of information in the HUD we put a custom square with grey transparent grey colouring to not only
match the graphics of the map but creating a contrast which makes the lines easier to read. Additionally we coloured the relevant constraining lines of code 
intuitively: The time countdown is green when there is plenty of time left and turns red when the timer falls below 60 seconds. also the enemy count is red unless
you killed all enemies, if so it turns green. Lastly of course there is an explicit indication of whether the exit is open or not. When the exit is closed it is written in red and when it is opened it switches to green.

### Player friendly control system 
Because the arrow keys can lead to an uncomfortable gaming experience we implemented another control system based on "W" "A" "S" "D". Feel free to switch at any time in the game. You don't even need 
to change any setting. If you wish you can even play with a mixture of "WASD" and the arrow keys.

### Game design
Because a black screen wouldn't invite you to play the game at all we implemented stunning background images along the way. For example there is a Menu background, a background for the screen in which you choose difficulty, a background for the screen when you have won, and a background for when you lost.   
Each of these images was carefully generated and adjusted to our liking and makes the game look way more aesthetically pleasing.

## Sources
- If we have been inspired by code from the internet, we added the necessary sources in the JavaDoc description of the respective method
- The sounds and music are coming from opengameart.com and pixabay.com
- All images from the screen (such as that from the menu screen) are generated by DALL-E 3