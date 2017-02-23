package com.mozz.remoteview.parser.token;


public final class Token {
    private Type mType;

    private Object mValue;

    private Token next;

    // for token pool
    private static Token sPool;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 20;
    private static final Object sPoolSync = new Object();

    private Token(Type type, Object value) {
        mType = type;
        mValue = value;
    }

    @Override
    public String toString() {
        String value = mValue == null ? "" : ":" + mValue;
        return "[" + mType.toString() + "]" + value;
    }

    public Type type() {
        return mType;
    }

    public Object value() {
        return mValue;
    }

    public String stringValue() {
        return (String) mValue;
    }

    public int intValue() {
        return (int) mValue;
    }

    public double doubleValue() {
        return (double) mValue;
    }

    public static Token obtainToken(Type type, Object value) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Token t = sPool;
                sPool = t.next;
                t.next = null;
                sPoolSize--;

                t.mType = type;
                t.mValue = value;
                return t;
            }

            return new Token(type, value);
        }
    }

    public static Token obtainToken(Type type) {
        return obtainToken(type, null);
    }

    public void recycle() {
        recycleUnchecked();
    }

    private void recycleUnchecked() {
        mType = Type.Unknown;
        mValue = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }


}
