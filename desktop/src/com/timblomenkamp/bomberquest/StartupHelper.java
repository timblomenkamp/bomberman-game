/*
 * Adapted from the libGDX gdx-liftoff StartupHelper (Apache License 2.0).
 * On macOS, LWJGL/GLFW requires the JVM to be started with -XstartOnFirstThread.
 * IDE "run main()" launches usually omit this flag, so we relaunch the JVM with it.
 */
package com.timblomenkamp.bomberquest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public final class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    private StartupHelper() {
    }

    /**
     * Relaunches the JVM with -XstartOnFirstThread if required (only on macOS).
     *
     * @return true if a new JVM was started and the caller should return immediately from main()
     */
    public static boolean startNewJvmIfRequired() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("mac")) {
            return false;
        }

        long pid = ProcessHandle.current().pid();

        // Already started on the first thread (e.g. via the Gradle run task).
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        // We already attempted a restart; don't loop forever.
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                "Could not verify the JVM was started with -XstartOnFirstThread. Continuing anyway.");
            return false;
        }

        String separator = System.getProperty("file.separator");
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
        if (!new File(javaExecPath).exists()) {
            System.err.println(
                "A Java installation could not be found. Run the game with -XstartOnFirstThread manually.");
            return false;
        }

        ArrayList<String> jvmArgs = new ArrayList<>();
        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));

        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            Process process = new ProcessBuilder(jvmArgs)
                .redirectErrorStream(true)
                .start();
            try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }
        return true;
    }
}
