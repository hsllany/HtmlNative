package com.mozz.remoteview.parser;

import com.mozz.remoteview.parser.reader.StringCodeReader;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParserTest {
    @Test
    public void process() throws Exception {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost/testLayout.xml")
                .build();

        Response r = client.newCall(request).execute();
        parserDebugger(r.body().string());
    }

    private void parserDebugger(String code) {

        System.out.println("code is \n" + code);

        StringCodeReader reader = new StringCodeReader(code);
        Parser parser = new Parser(reader);

        try {
            SyntaxTree rootTree = parser.process();
            rootTree.wholeTreeToString();

        } catch (SyntaxError sytaxError) {
            sytaxError.printStackTrace();
        }

    }

}