package com.mozz.remoteview;

import org.junit.Test;

import java.io.EOFException;

import static org.junit.Assert.assertTrue;

/**
 * @author Yang Tao, 17/3/17.
 */
public class CharQueueTest {
    @Test
    public void pushAndPop() throws Exception {
        CharQueue charCache = new CharQueue(3);

        charCache.push('h');
        assertTrue(charCache.pop() == 'h');

        charCache.push('a');
        charCache.push('b');
        charCache.push('c');

        charCache.push('d');
        charCache.push('e');

        assertTrue(charCache.pop() == 'c');
        assertTrue(charCache.pop() == 'd');
        assertTrue(charCache.pop() == 'e');

        charCache.push('f');

        assertTrue(charCache.pop() == 'f');

        try {
            charCache.pop();
        } catch (EOFException e) {
            return;
        }

        throw new IllegalArgumentException("wrong");
    }

    @Test
    public void peek() throws Exception {
        CharQueue charQueue = new CharQueue(3);
        charQueue.push('1');
        charQueue.push('2');

        assertTrue(charQueue.peek(0) == '1');
        assertTrue(charQueue.peek(1) == '2');
        //
        assertTrue(charQueue.pop() == '1');
        assertTrue(charQueue.pop() == '2');

        charQueue.push('3');
        charQueue.push('4');
        charQueue.push('5');
        charQueue.push('6');

        assertTrue(charQueue.peek(0) == '4');
        assertTrue(charQueue.peek(1) == '5');
        assertTrue(charQueue.peek(2) == '6');

        try {
            char c = charQueue.peek(3);
        } catch (IllegalArgumentException e) {
            return;
        }

        throw new IllegalArgumentException();
    }

    @Test
    public void toStringTest() throws Exception {
        CharQueue charQueue = new CharQueue(3);

        charQueue.push('h');
        charQueue.push('e');
        charQueue.push('l');
        charQueue.push('l');
        charQueue.push('o');

        charQueue.push('w');
        charQueue.push('o');
        charQueue.push('r');
        charQueue.push('l');
        charQueue.push('d');

        assertTrue(charQueue.toString().equals("rld"));
    }


}