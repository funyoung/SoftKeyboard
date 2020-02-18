package com.pattern;

/**
 * The template interface of visitor pattern.
 *
 * @author funyoung
 */
public interface Visitor<T> {
    /**
     * The abstract method to be invoked while traversing.
     * @return a list of object.
     */
    T visit();
}
