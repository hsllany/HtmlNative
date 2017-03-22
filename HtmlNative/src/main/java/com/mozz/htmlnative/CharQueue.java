package com.mozz.htmlnative;

import java.io.EOFException;

/**
 * @author Yang Tao, 17/3/17.
 */

final class CharQueue {

    private final char[] cache;

    private int nextInsertPosition;
    private int startPosition = 0;
    private int endPosition;

    private int count;

    public static boolean sDebug = false;

    public CharQueue(int compacity) {
        cache = new char[compacity];
    }

    public void push(char c) {
        cache[nextInsertPosition] = c;
        moveStartNext();
        count++;
        if (count > cache.length) {
            count = cache.length;
            moveEndNext();
        }

        if (sDebug) {
            log("push");
        }
    }

    private void moveEndNext() {
        endPosition++;
        startPosition++;

        if (endPosition >= cache.length) {
            endPosition = 0;
        }

        if (startPosition >= cache.length) {
            startPosition = 0;
        }
    }

    private void moveStartNext() {
        nextInsertPosition++;

        if (nextInsertPosition >= cache.length) {
            nextInsertPosition = 0;
        }
    }

    public char pop() throws EOFException {
        if (count == 0) {
            throw new EOFException();
        } else {
            char c = cache[endPosition];
            moveEndNext();
            count--;
            if (count < 0) {
                count = 0;
            }
            if (sDebug) {
                log("pop");
            }
            return c;
        }


    }

    public char peek(int index) {
        if (index >= size()) {
            throw new IllegalArgumentException("wrong");
        }

        int trueIndex = startPosition - (cache.length - index);
        if (trueIndex < 0) {
            trueIndex += cache.length;
        }

        if (sDebug) {
            log("peek");
        }

        return cache[trueIndex];
    }

    public int size() {
        return count;
    }

    @Override
    public String toString() {
        if (size() == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size(); i++) {
                sb.append(peek(i));
            }

            return sb.toString();
        }
    }

    private void log(String action) {
        System.out.println("CharQueue:" + "after " + action + ", start=" + startPosition + ", " +
                "nextInsert=" +
                nextInsertPosition + ", end=" + endPosition);
    }
}
