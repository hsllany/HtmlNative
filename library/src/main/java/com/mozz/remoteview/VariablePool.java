package com.mozz.remoteview;

/**
 * @author Yang Tao, 17/3/9.
 */

interface VariablePool {
    void addVariable(String string, Object object);

    void updateVariable(String string, Object newValue);

    Object getVariable(String string);

}
