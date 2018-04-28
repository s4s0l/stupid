package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public interface Convertable {
    /**
     * Convert an object to a boolean.
     *
     * @return boolean representation of the object
     */
    boolean toBool();

    /**
     * Convert an object to an integer.
     *
     * @return integer representation of given object
     */
    int toInt();

    /**
     * Convert an object to a double.
     *
     * @return the double representation of the argument
     */
    double toDouble();

    /**
     * Convert an object to a string.
     * Java already has semantics for converting (.toString()) but
     * it is nice to still have it here anyway.
     */
    String toStupidString();
}
