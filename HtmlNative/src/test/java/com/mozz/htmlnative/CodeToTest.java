package com.mozz.htmlnative;

/**
 * Created by Yang Tao on 17/2/27.
 */

public class CodeToTest {

    public static String codeScriptFirst = "\n" +
            "<script>\n" +
            "  helloworld{\n" +
            "\n" +
            "  }\n" +
            "\n" +
            "  viewLoaded{\n" +
            "\n" +
            "  }\n" +
            "</script>\n" +
            "\n" +
            "<template>\n" +
            "<LinearLayout src=\"http://i0.letvimg.com/lc02_live/201702/23/15/19/94d06876-a66a-4d58-9585-56ba5009346e.jpg\">\n" +
            "  <TextView id=\"text1\" text=\"hello world, nihao\" textColor=\"#0000ff\"/>\n" +
            "  <TextView id=\"text2\" text=\"nihao, wo shi yangtao\" onClick=\"helloworld222\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=\"MATCH_PARENT\" onClick=\"helloworld\" height=624 src=\"http://i0.letvimg.com/lc02_live/201702/23/15/19/94d06876-a66a-4d58-9585-56ba5009346e.jpg\"/>\n" +
            "  <LinearLayout>\n" +
            "    <TextView id=\"text3\" text=\">>>\"/>\n" +
            "    <LinearLayout>\n" +
            "      <TextView text=\">>>\"/>\n" +
            "      <LinearLayout>\n" +
            "        <TextView text=\">>>\"/>\n" +
            "        <ImageView background=\"#ff0000\" width=\"MATCH_PARENT\" height=200/>\n" +
            "      </LinearLayout>\n" +
            "    </LinearLayout>\n" +
            "  </LinearLayout>\n" +
            "  <TextView text=\"hello world, nihao\" background=\"#0000ff\" padding=30 textColor=\"#ffffff\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\"/>\n" +
            "  <ImageView id=\"text4\" background=\"#00aa00\" width=1200 height=20 alpha=0.3 paddingLeft=32/>\n" +
            "  <Button text=\"hello\" width=500 height=500 onClick=\"btn1\"/>\n" +
            "</LinearLayout>\n" +
            "</template>\n";

    public static String codeTemplateFirst = "\n" +
            "\n" +
            "<template>\n" +
            "<LinearLayout src=\"http://i0.letvimg.com/lc02_live/201702/23/15/19/94d06876-a66a-4d58-9585-56ba5009346e.jpg\">\n" +
            "  <TextView id=\"text1\" text=\"hello world, nihao\" textColor=\"#0000ff\"/>\n" +
            "  <TextView id=\"text2\" text=\"nihao, wo shi yangtao\" onClick=\"helloworld222\"/>\n" +
            "  <ImageView background=\"#00aa00\" width=\"MATCH_PARENT\" onClick=\"helloworld\" height=624 src=\"http://i0.letvimg.com/lc02_live/201702/23/15/19/94d06876-a66a-4d58-9585-56ba5009346e.jpg\"/>\n" +
            "  <LinearLayout>\n" +
            "    <TextView id=\"text3\" text=\">>>\"/>\n" +
            "    <LinearLayout>\n" +
            "      <TextView text=\">>>\"/>\n" +
            "      <LinearLayout>\n" +
            "        <TextView text=\">>>\"/>\n" +
            "        <ImageView background=\"#ff0000\" width=\"MATCH_PARENT\" height=200/>\n" +
            "      </LinearLayout>\n" +
            "    </LinearLayout>\n" +
            "  </LinearLayout>\n" +
            "  <TextView text=\"hello world, nihao\" background=\"#0000ff\" padding=30 textColor=\"#ffffff\"/>\n" +
            "  <TextView text=\"nihao, wo shi yangtao\"/>\n" +
            "  <ImageView id=\"text4\" background=\"#00aa00\" width=1200 height=20 alpha=0.3 paddingLeft=32/>\n" +
            "  <Button text=\"hello\" width=500 height=500 onClick=\"btn1\"/>\n" +
            "</LinearLayout>\n" +
            "</template>\n" +
            "\n" +
            "<script>\n" +
            "  helloworld{\n" +
            "\n" +
            "  }\n" +
            "\n" +
            "  viewLoaded{\n" +
            "\n" +
            "  }\n" +
            "</script>\n";

    public static String codeTemplateOnly = "<template>\n" +
            "  <scroller>\n" +
            "    <div background=\"#ffffff\" height=300 padding=50>\n" +
            "      \n" +
            "      <img src=\"http://i2.sinaimg.cn/dy/deco/2012/0613/yocc20120613img01/news_logo.png\" width=274 height=72/>\n" +
            "      \n" +
            "      <p text=\"\" fontSize=20 color=\"#000000\" paddingTop=30 paddingBottom=30>\n" +
            "        解放军军舰救渔民是浪费军力？军网回应(图)\n" +
            "      </p>\n" +
            "\n" +
            "      <p text=\"d\" paddingTop=20 paddingBottom=40 fontSize=12 color=\"#9d9d9d\">\n" +
            "        2017年03月01日19:43 中国政府网\n" +
            "      </p>\n" +
            "\n" +
            "      <p paddingTop=20 color=\"#272727\" lineHeight=80 text=\"\" fontSize=14>\n" +
            "        　原标题：安全问题频发 普京下令莫斯科两年内拆除所有“赫鲁晓夫楼” [观察者网编译]大家对这样的单元楼不陌生吧？”\n" +
            "      </p>\n" +
            "      <img src=\"http://n.sinaimg.cn/news/crawl/20170302/18ey-fycaahm6004808.jpg\"/>\n" +
            "\n" +
            "      <img src=\"http://n.sinaimg.cn/news/crawl/20170302/SVeR-fycaafm4691017.jpg\" paddingTop=80/>\n" +
            "      <p paddingTop=20 color=\"#272727\" lineHeight=80>\n" +
            "        这样的单元楼在中国很多地方，尤其是老工业基地随处可见。这种用预制板修建的单元楼最初源自苏联，因为在赫鲁晓夫时期大量修建，这种楼在苏联被称为“赫鲁晓夫楼”。对很多苏联加盟共和国的老百姓来说，“赫鲁晓夫楼”承载着他们的共产主义记忆和建设国家的热血青春。\n" +
            "      </p>\n" +
            "\n" +
            "      <img src=\"http://n.sinaimg.cn/news/crawl/20170302/74cl-fycaafm4691019.jpg\"/>\n" +
            "\n" +
            "      <p paddingTop=20 color=\"#000000\" lineHeight=80 fontSize=16 fontStyle=\"bold\">\n" +
            "        莫斯科将在两年内拆除所有“赫鲁晓夫楼\n" +
            "      </p>\n" +
            "      <p paddingTop=20 color=\"#000000\" lineHeight=80>\n" +
            "        不过，据克里姆林宫官方网站的消息，2月21日，俄罗斯总统普京要求莫斯科市长谢尔盖·苏比雅宁在两年内拆除莫斯科的所有“赫鲁晓夫楼”，原因是这些通常有五个单元的老楼虽然设计寿命为25年到50年，但已经难以满足目前的建筑安全标准。\n" +
            "      </p>\n" +
            "      <img src=\"http://n.sinaimg.cn/news/crawl/20170302/3dS6-fycaafm4691032.jpg\"/>\n" +
            "    </div>\n" +
            "  </scroller>\n" +
            "</template>\n";

    public static String codeScriptOnly = "<script>\n" +
            "  helloworld{\n" +
            "\n" +
            "  }\n" +
            "\n" +
            "  viewLoaded{\n" +
            "\n" +
            "  }\n" +
            "</script>";
}
