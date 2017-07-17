package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.mozz.htmlnative.script.ScriptFactory;
import com.mozz.htmlnative.script.ScriptRunner;
import com.mozz.htmlnative.view.HNRootView;

/**
 * @author Yang Tao, 17/3/6.
 */
final class HNSandBoxContextImpl implements HNSandBoxContext {

    private static final String TAG = HNSandBoxContext.class.getSimpleName();

    private HNRootView mRootView;

    private final VariablePoolImpl mPool = new VariablePoolImpl();

    private ScriptRunner mRunner;

    private final HNSegment mSegment;

    private final Context mContext;

    private HNSandBoxContextImpl(HNSegment segment, Context context, HNRootView rootView) {
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
    public View registerId(String id, View view) {
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
        if (mSegment.hasSetScript()) {
            mRunner = ScriptFactory.createRunner(mSegment.getScriptInfo().type(), this);
            if (mRunner != null) {
                mRunner.attach(this);
                mRunner.onLoad();
            }
        }

        initVariablePool();
        callCreate();
    }

    private void initVariablePool() {

    }

    private void callCreate() {
        if (mSegment.hasSetScript()) {
            execute(mSegment.getScriptInfo().code());
        }
    }

    private void callCreated() {
    }

    public String allIdTag() {
        return mRootView.allIdTag();
    }

    @Override
    public HNSegment getSegment() {
        return mSegment;
    }

    @Override
    public void postInScriptThread(Runnable r) {
        HNScriptRunnerThread.post(r);
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
    public void executeFun(final String funName) {
        HNScriptRunnerThread.post(new Runnable() {
            @Override
            public void run() {
                mRunner.runFunction(funName);
            }
        });

    }

    @Override
    public void executeUIFun(final String funName) {
        HNScriptRunnerThread.postAtFront(new Runnable() {
            @Override
            public void run() {
                mRunner.runFunction(funName);
            }
        });
    }

    @NonNull
    static HNSandBoxContext createContext(@NonNull HNRootView layout, HNSegment module, Context
            context) {
        return new HNSandBoxContextImpl(module, context, layout);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mRunner != null) {
            mRunner.onUnload();
        }
    }
}
