package com.mozz.remoteview.parser;

import org.junit.Test;

/**
 * Created by Yang Tao on 17/2/21.
 */
public class SyntaxTreeTest {
    @Test
    public void addChild() throws Exception {
        RVModule rvModule = new RVModule();

        RVDomTree syntaxTree = new RVDomTree(rvModule, null, 0, 0);
        RVDomTree tree1 = syntaxTree.addChild("1", 0);
        RVDomTree tree11 = tree1.addChild("1-1", 0);
        tree11.addChild("1-1-1", 0);
        syntaxTree.addChild("2", 1);

        RVDomTree tree3 = syntaxTree.addChild("3", 2);
        tree3.addChild("3-1", 0);
        tree3.addChild("3-2", 1);

        syntaxTree.wholeTreeToString();
    }

}