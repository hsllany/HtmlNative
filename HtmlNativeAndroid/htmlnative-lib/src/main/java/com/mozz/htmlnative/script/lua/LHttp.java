package com.mozz.htmlnative.script.lua;

import android.webkit.URLUtil;

import com.mozz.htmlnative.HNativeEngine;
import com.mozz.htmlnative.http.Http;
import com.mozz.htmlnative.http.HttpRequest;
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

                        HNativeEngine.getHttpClient().send(new HttpRequest(urlJ, Http.Method.GET)
                                , new Http.RequestCallback() {

                            @Override
                            public void onResponse(final Http.Response response) {
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
                        HNativeEngine.getHttpClient().send(new HttpRequest(urlJ, paramsJ, Http
                                .Method.POST), new Http.RequestCallback() {

                            @Override
                            public void onResponse(final Http.Response response) {
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

    static class LResponse extends LuaTable implements Http.Response {
        private String mHeader;
        private int mStatusCode;
        private String mBody;

        LResponse() {
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
        public void setHeader(String header) {
            mHeader = header;
        }

        @Override
        public String header() {
            return mHeader;
        }

        @Override
        public void setBody(String body) {
            mBody = body;
        }

        @Override
        public String body() {
            return mBody;
        }

        @Override
        public void setStatusCode(int code) {
            mStatusCode = code;
        }

        @Override
        public int statusCode() {
            return mStatusCode;
        }

        public static LResponse wrap(Http.Response response) {
            if (response instanceof LResponse) {
                return (LResponse) response;
            } else {
                LResponse luaResponse = new LResponse();
                luaResponse.setStatusCode(response.statusCode());
                luaResponse.setHeader(response.header());
                luaResponse.setBody(response.body());
                return luaResponse;
            }
        }
    }

}
