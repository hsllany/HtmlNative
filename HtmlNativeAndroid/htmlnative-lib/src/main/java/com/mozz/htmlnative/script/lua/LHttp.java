package com.mozz.htmlnative.script.lua;

import android.webkit.URLUtil;

import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.http.HttpRequest;
import com.mozz.htmlnative.http.HttpResponse;
import com.mozz.htmlnative.http.RequestCallback;
import com.mozz.htmlnative.script.ScriptRunner;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Map;

/**
 * @author Yang Tao, 17/5/29.
 */

class LHttp extends LuaTable implements ILApi {

    private ScriptRunner mRunner;

    LHttp(ScriptRunner runner) {
        super();
        mRunner = runner;

        set("get", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue url, final LuaValue callback) {
                if (LuaUtils.notNull(url)) {
                    if (!url.isstring()) {
                        return LuaValue.NIL;
                    }
                    String urlJ = url.tojstring();

                    if (URLUtil.isValidUrl(urlJ)) {

                        HNativeEngine.getHttpClient().send(new HttpRequest(urlJ, HttpRequest
                                .Method.GET, (byte[]) null, null), new RequestCallback() {

                            @Override
                            public void onResponse(final HttpResponse response) {
                                mRunner.postRun(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (callback == LuaValue.NIL) {
                                            return;
                                        }
                                        LuaValue responseFun = callback.get("onResponse");
                                        if (responseFun != null && responseFun.isclosure()) {
                                            responseFun.call(LResponse.wrap(response));

                                        }

                                    }
                                });
                            }
                        });

                    }
                }
                return LuaValue.NIL;
            }
        });

        set("post", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue url, LuaValue param, final LuaValue callback) {
                if (LuaUtils.notNull(url)) {
                    String urlJ = url.tojstring();
                    Map<String, String> paramsJ = null;
                    if (param instanceof LuaTable) {
                        paramsJ = LuaUtils.luaTableToMap((LuaTable) param);
                    }

                    if (URLUtil.isValidUrl(urlJ)) {
                        HNativeEngine.getHttpClient().send(new HttpRequest(urlJ, HttpRequest
                                .Method.POST, paramsJ, null), new RequestCallback() {

                            @Override
                            public void onResponse(final HttpResponse response) {
                                mRunner.postRun(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (callback == LuaValue.NIL) {
                                            return;
                                        }
                                        LuaValue responseFun = callback.get("onResponse");
                                        if (responseFun != null && responseFun.isclosure()) {
                                            responseFun.call(LResponse.wrap(response));
                                        }

                                    }
                                });
                            }
                        });
                    }
                }
                return LuaValue.NIL;
            }
        });
    }

    @Override
    public String apiName() {
        return "http";
    }

    private static class LResponse extends LObject {
        private String mHeader;
        private int mStatusCode;
        private String mBody;

        LResponse() {
            super();
            set("header", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    if (mHeader == null) {
                        return LuaValue.NIL;
                    }
                    return LuaString.valueOf(mHeader);
                }
            });

            set("body", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    if (mBody == null) {
                        return LuaValue.NIL;
                    }
                    return LuaString.valueOf(mBody);
                }
            });

            set("status", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaInteger.valueOf(mStatusCode);
                }
            });
        }

        @Override
        String onObjectClassName() {
            return "httpResponse";
        }

        public static LResponse wrap(HttpResponse response) {

            LResponse luaResponse = new LResponse();
            luaResponse.mStatusCode = response.getStatusCode();
            if (response.getHeader() != null) {
                luaResponse.mHeader = response.getHeader().toString();
            }
            luaResponse.mBody = response.getBodyAsString();
            return luaResponse;

        }
    }

}
