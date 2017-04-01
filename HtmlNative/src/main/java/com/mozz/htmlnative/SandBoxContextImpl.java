package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.mozz.htmlnative.script.ScriptRunner;
import com.mozz.htmlnative.script.ScriptRunnerFactory;
import com.mozz.htmlnative.view.HNViewGroup;

/**
 * @author Yang Tao, 17/3/6.
 */
final class SandBoxContextImpl implements HNSandBoxContext {

    private static boolean DEBUG = false;

    private static final String TAG = HNSandBoxContext.class.getSimpleName();

    private HNViewGroup mRootView;

    private final VariablePoolImpl mPool = new VariablePoolImpl();

    private ScriptRunner mRunner;

    private final HNSegment mSegment;

    private final Context mContext;

    private SandBoxContextImpl(HNSegment segment, Context context, HNViewGroup rootView) {
        mRootView = rootView;
        mSegment = segment;
        mContext = context;
    }

    @Override
    public Context getAndroidContext() {
        return mContext;
    }

    @Override
    public void addVariable(String string, Object object) {
        mPool.addVariable(string, object);
    }

    @Override
    public void updateVariable(String string, Object newValue) {
        mPool.updateVariable(string, newValue);
    }

    @Override
    public Object getVariable(String string) {
        return mPool.getVariable(string);
    }

    @Nullable
    public View saveId(String id, View view) {
        return mRootView.putViewWithId(id, view);
    }

    @Nullable
    @Override
    public View findViewById(@NonNull String id) {
        return mRootView.findViewById(id);
    }

    @Override
    public boolean containsView(String id) {
        return mRootView.containsView(id);
    }

    @Override
    public void onViewLoaded() {
        callCreated();
    }

    @Override
    public void onViewCreate() {
        // if there is script code in layout file, then initContextScriptRunner
        if (mSegment.mHasScriptEmbed) {
            mRunner = ScriptRunnerFactory.createRunner(mSegment.mScriptInfo.type(), this);
        }

        initVariablePool();

        callCreate();
    }

    private void initVariablePool() {

    }

    private void callCreate() {
        if (mSegment.mScriptInfo != null) {
            execute(mSegment.mScriptInfo.code());
        }
    }

    private void callCreated() {
    }

    public String allIdTag() {
        return mRootView.allIdTag();
    }

    @Override
    public void execute(final String script) {
        if (mRunner == null) {
            Log.d(TAG, "skip the script \"" + script + "\" because no script in module " +
                    mSegment);
            return;
        }

        HNScriptRunnerThread.runScript(this, this.mRunner, script);
    }

    @Override
    public void executeFun(String funName) {
        mRunner.runFunction(funName);
    }

    @NonNull
    static HNSandBoxContext createContext(@NonNull HNViewGroup layout, HNSegment module, Context context) {
        return new SandBoxContextImpl(module, context, layout);
    }

}
