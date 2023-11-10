package util;

/**
 * Class acts as an abstract wrapper for Java's handling of time.
 */
public class Time {
    public static float timeStarted = System.nanoTime();

    public static float getTime() { return (float) ((System.nanoTime() - timeStarted) * 1E-9); }
}
