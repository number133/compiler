package main.java.test;

import main.java.second.MultiCompiler;
import org.junit.Test;

public class MultiCompilerTest extends CoreCompilerTest{

    public MultiCompilerTest(){
        compilerClass = MultiCompiler.class;
    }

    @Test
    public void testMultiChar(){
        runTest("multi/multiChar.txt", "multiChar");
    }
}
