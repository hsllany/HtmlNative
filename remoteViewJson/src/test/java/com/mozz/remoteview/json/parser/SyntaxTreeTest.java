package com.mozz.remoteview.json.parser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Yang Tao on 17/2/21.
 */
public class SyntaxTreeTest {
    @Test
    public void addChild() throws Exception {
        SyntaxTree syntaxTree = new SyntaxTree("Root", null, 0, 0);
        SyntaxTree tree1 = syntaxTree.addChild("1", 0);
        SyntaxTree tree11 = tree1.addChild("1-1", 0);
        tree11.addChild("1-1-1", 0);
        syntaxTree.addChild("2", 1);

        SyntaxTree tree3 = syntaxTree.addChild("3", 2);
        tree3.addChild("3-1", 0);
        tree3.addChild("3-2", 1);

        syntaxTree.wholeTreeToString();
    }

}