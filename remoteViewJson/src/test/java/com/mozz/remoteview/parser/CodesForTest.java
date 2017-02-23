package com.mozz.remoteview.parser;

/**
 * Created by Yang Tao on 17/2/23.
 */

public class CodesForTest {

    public static String CodeSimple = "<a><b></b></a><c></c>";

    public static String CodeComplicated = "<ScrollView>\n" +
            "<LinearLayout background=\"#000000\">\n" +
            "  <TextView text=\"hello world, nihao\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\" onClick=\"lua\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=\"MATCH_PARENT\" height=624 src=\"http://www.w3school.com.cn/i/site_photoqe.jpg\"/>\n" +
            "  <LinearLayout>\n" +
            "    <TextView text=\">>>\"/>\n" +
            "    <LinearLayout>\n" +
            "      <TextView text=\">>>\"/>\n" +
            "      <LinearLayout>\n" +
            "        <TextView text=\">>>\"/>\n" +
            "        <ImageView background=\"#ff0000\" width=\"MATCH_PARENT\" height=200/>\n" +
            "      </LinearLayout>\n" +
            "    </LinearLayout>\n" +
            "  </LinearLayout>\n" +
            "  <TextView text=\"hello world, nihao\" background=\"#0000ff\" paddingTop=30 textColor=\"#ffffff\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=1200 height=20 alpha=0.3 paddingLeft=32/>\n" +
            "</LinearLayout>\n" +
            "</ScrollView>\n" +
            "\n" +
            "\n" +
            "me1{\n" +
            "  print ('hello world');\n" +
            "}\n" +
            "\n" +
            "me2{\n" +
            "  \n" +
            "}";

    public static String CodeFunction1 = "<ScrollView>\n" +
            "<LinearLayout background=\"#000000\">\n" +
            "  <TextView text=\"hello world, nihao\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\" onClick=\"lua\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=\"MATCH_PARENT\" height=624 src=\"http://www.w3school.com.cn/i/site_photoqe.jpg\"/>\n" +
            "  <LinearLayout>\n" +
            "    <TextView text=\">>>\"/>\n" +
            "    <LinearLayout>\n" +
            "      <TextView text=\">>>\"/>\n" +
            "      <LinearLayout>\n" +
            "        <TextView text=\">>>\"/>\n" +
            "        <ImageView background=\"#ff0000\" width=\"MATCH_PARENT\" height=200/>\n" +
            "      </LinearLayout>\n" +
            "    </LinearLayout>\n" +
            "  </LinearLayout>\n" +
            "  <TextView text=\"hello world, nihao\" background=\"#0000ff\" paddingTop=30 textColor=\"#ffffff\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=1200 height=20 alpha=0.3 paddingLeft=32/>\n" +
            "</LinearLayout>\n" +
            "</ScrollView>\n" +
            "\n" +
            "\n" +
            "me1{\n" +
            "  print ('hello world');\n" +
            "}\n" +
            "\n" +
            "me2{\n" +
            "  hello world\n" +
            "}";

    public static String CodeFunction3 = "me3{\n" +
            "  print ('hello world');\n" +
            "  if(a) b = 3;\n" +
            "}\n" +
            "\n" +
            "<ScrollView>\n" +
            "<LinearLayout background=\"#000000\">\n" +
            "  <TextView text=\"hello world, nihao\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\" onClick=\"lua\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=\"MATCH_PARENT\" height=624 src=\"http://www.w3school.com.cn/i/site_photoqe.jpg\"/>\n" +
            "  <LinearLayout>\n" +
            "    <TextView text=\">>>\"/>\n" +
            "    <LinearLayout>\n" +
            "      <TextView text=\">>>\"/>\n" +
            "      <LinearLayout>\n" +
            "        <TextView text=\">>>\"/>\n" +
            "        <ImageView background=\"#ff0000\" width=\"MATCH_PARENT\" height=200/>\n" +
            "      </LinearLayout>\n" +
            "    </LinearLayout>\n" +
            "  </LinearLayout>\n" +
            "  <TextView text=\"hello world, nihao\" background=\"#0000ff\" paddingTop=30 textColor=\"#ffffff\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=1200 height=20 alpha=0.3 paddingLeft=32/>\n" +
            "</LinearLayout>\n" +
            "</ScrollView>\n" +
            "\n" +
            "\n" +
            "me1{\n" +
            "  print ('hello world');\n" +
            "}\n" +
            "\n" +
            "me2{\n" +
            "  hello world\n" +
            "}\n";
}
