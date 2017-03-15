package com.mozz.remoteview.token;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Yang Tao on 17/2/27.
 */
public class TokenTest {
    @Test
    public void value() throws Exception {
        String CodeExample = "{Script example}";
        Token t = Token.obtainToken(TokenType.Code, CodeExample, 0, 0);
        assertTrue(t.value() == CodeExample);
    }

    @Test
    public void stringValue() throws Exception {
        String CodeExample = "{Script example}";
        Token t = Token.obtainToken(TokenType.Code, CodeExample, 0, 0);
        assertTrue(t.stringValue().equals(CodeExample));
    }

    @Test
    public void intValue() throws Exception {
        String CodeExample = "{Script example}";
        Token t = Token.obtainToken(TokenType.Code, 123, 0, 0);
        assertTrue(t.intValue() == 123);

        Token t2 = Token.obtainToken(TokenType.Code, CodeExample, 0, 0);
        assertTrue(t2.intValue() == 0);
    }

    @Test
    public void doubleValue() throws Exception {
        String CodeExample = "{Script example}";
        Token t = Token.obtainToken(TokenType.Code, 123.3d, 0, 0);

        assertTrue(Double.compare(t.doubleValue(), 123.3d) == 0);

        Token t2 = Token.obtainToken(TokenType.Code, CodeExample, 0, 0);
        assertTrue(Double.compare(t2.doubleValue(), 0d) == 0);
    }

    @Test
    public void obtainToken() throws Exception {
        Token t = Token.obtainToken(TokenType.Code, 0, 0);

        assertTrue(t != null);
    }

    @Test
    public void obtainToken1() throws Exception {
        Token t = Token.obtainToken(TokenType.Code, "Lala", 0, 0);

        assertTrue(t != null);
    }

    @Test
    public void recycle() throws Exception {
        Token.recycleAll();

        Token t1 = Token.obtainToken(TokenType.Code, "Lala", 0, 0);
        t1.recycle();

        Token t2 = Token.obtainToken(TokenType.Code, 123, 1, 2);
        assertTrue(t2 == t1);
    }

}