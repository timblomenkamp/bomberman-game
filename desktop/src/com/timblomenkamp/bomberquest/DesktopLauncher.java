package com.timblomenkamp.bomberquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.desktop.DesktopFileChooser;
//import games.spooky.gdx.nativefilechooser.DefaultNativeFileChooser;

import java.io.File;

/**
 * The DesktopLauncher class is the entry point for the desktop version of the Bomber Quest game.
 * It sets up the game window and launches the game using LibGDX framework.
 */
public class DesktopLauncher {

	/**
	 * The main method sets up the configuration for the game window and starts the application.
	 *
	 * @param arg Command line arguments (not used in this application)
	 */
	public static void main(String[] arg) {
		// On macOS, LWJGL requires the JVM to run with -XstartOnFirstThread.
		// Relaunch with that flag if needed (e.g. when started directly from an IDE).
		if (StartupHelper.startNewJvmIfRequired()) {
			return;
		}

		// Configuration for the game window
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Bomber Quest"); // Set the window title

		// Get the display mode of the current monitor
		Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
		// Set the window size to 80% of the screen width and height
		config.setWindowedMode(
				Math.round(0.8f * displayMode.width),
				Math.round(0.8f * displayMode.height)
		);
		config.useVsync(true); // Enable vertical sync
		config.setForegroundFPS(60); // Set the foreground frames per second

//		BomberQuestGame game = new BomberQuestGame();
//
//		// Inject the DesktopFileChooser
//		game.setFileChooser(new DesktopFileChooser(new DefaultNativeFileChooser()));

		// Ensure working directory is assets/ so LibGDX can find internal files,
		// regardless of whether the game is launched via Gradle or directly from an IDE.
		java.io.File assetsDir = new java.io.File("assets");
		if (assetsDir.exists() && assetsDir.isDirectory()) {
			System.setProperty("user.dir", assetsDir.getAbsolutePath());
		}

		NativeFileChooser fileChooser = new DesktopFileChooser();

		// Launch the game
		new Lwjgl3Application(new BomberQuestGame(fileChooser)); //import file here?
	}
}
