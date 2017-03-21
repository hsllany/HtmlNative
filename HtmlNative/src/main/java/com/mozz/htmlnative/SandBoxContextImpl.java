package com.mozz.htmlnative;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.mozz.htmlnative.script.ScriptRunner;
import com.mozz.htmlnative.script.ScriptRunnerFactory;
import com.mozz.htmlnative.script.ScriptRunnerThread;

import java.util.Map;

/**
 * @author Yang Tao, 17/3/6.
 */
final class SandBoxContextImpl implements HNSandBoxContext {

    private static boolean DEBUG = false;

    private static final String TAG = HNSandBoxContext.class.getSimpleName();

    private static final int ViewContextTag = 0x3 << 24;

    private final Map<String, View> mViewWithId = new ArrayMap<>();

    private final VariablePoolImpl mPool = new VariablePoolImpl();

    private ScriptRunner mRunner;

    private final HNSegment mSegment;

    private final Context mContext;

    private SandBoxContextImpl(HNSegment segment, Context context) {
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
    public View put(String id, View view) {
        View before = mViewWithId.put(id, view);
        if (before != null) {
            Log.w(TAG, "Duplicated id " + id + ", before is " + before + ", current is " + view);
        }
        return before;
    }

    @Nullable
    @Override
    public View findViewById(@NonNull String id) {
        return mViewWithId.get(id);
    }

    @Override
    public boolean containsView(String id) {
        return mViewWithId.containsKey(id);
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
        return mViewWithId.toString();
    }

    public static HNSandBoxContext getViewContext(@NonNull FrameLayout v) {
        Object object = v.getTag(ViewContextTag);

        if (object != null && object instanceof HNSandBoxContext) {
            return (HNSandBoxContext) object;
        }

        return null;
    }

    @Override
    public void execute(final String script) {
        if (mRunner == null) {
            Log.d(TAG, "skip the script \"" + script + "\" because no script in module " +
                    mSegment);
            return;
        }

        ScriptRunnerThread.getInstance().runScript(this, this.mRunner, script);
    }

    @Override
    public void executeFun(String funName) {
        mRunner.runFunction(funName);
    }

    @NonNull
    static HNSandBoxContext create(@NonNull FrameLayout layout, HNSegment module, Context context) {
        HNSandBoxContext v = new SandBoxContextImpl(module, context);
        layout.setTag(ViewContextTag, v);
        return v;
    }

}
