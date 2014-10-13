package main.java.test.multi;

import main.java.second.MultiCompiler;
import main.java.test.CoreCompilerTest;
import org.junit.Test;

public class MultiCompilerTest extends CoreCompilerTest {

    public MultiCompilerTest(){
        compilerClass = MultiCompiler.class;
    }

    @Test
    public void testMultiChar(){
        runTest("multi/multiChar.txt", "multiChar");
    }

    @Test
    public void testSpace(){
        runTest("multi/space.txt", "space");
    }
}
