package com.mozz.remoteview.token;


public final class Token {

    private long startColumn;

    private long line;

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

    public void setValue(Object value) {
        mValue = value;
    }

    public String stringValue() {
        return (String) mValue;
    }

    public int intValue() {
        if (mValue instanceof Integer)
            return (int) mValue;
        else
            return 0;
    }

    public double doubleValue() {
        if (mValue instanceof Double || mValue instanceof Float)
            return (double) mValue;
        else
            return 0d;
    }

    public boolean booleanValue() {
        if (mValue instanceof Boolean) {
            return (boolean) mValue;
        } else {
            return false;
        }
    }

    public static Token obtainToken(Type type, Object value, long line, long column) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Token t = sPool;
                sPool = t.next;
                t.next = null;
                sPoolSize--;

                t.mType = type;
                t.mValue = value;
                t.line = line;
                t.startColumn = column;
                return t;
            }

            return new Token(type, value);
        }
    }

    public static Token obtainToken(Type type, long line, long column) {
        return obtainToken(type, null, line, column);
    }

    static void recycleAll() {
        synchronized (sPoolSync) {
            sPoolSize = 0;
            sPool = null;
        }
    }

    public void recycle() {
        recycleUnchecked();
    }

    private void recycleUnchecked() {
        mType = Type.Unknown;
        mValue = null;
        startColumn = -1;
        line = -1;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            Token compare = (Token) obj;

            return compare.mValue.equals(mValue) && compare.mType.equals(mType) &&
                    compare.line == line && compare.startColumn == startColumn;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int r = 17;
        r = 31 * r + (int) startColumn;
        r = 31 * r + (int) line;
        r = 31 * r + mValue.hashCode();
        r = 31 * r + mType.hashCode();
        return r;
    }

    public long getStartColumn() {
        return startColumn;
    }

    public long getLine() {
        return line;
    }
}
