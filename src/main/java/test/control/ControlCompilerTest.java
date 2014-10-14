package main.java.test.control;

import main.java.control.ControlCompiler;
import main.java.core.CoreTest;
import org.junit.Test;

public class ControlCompilerTest extends CoreTest{

    public ControlCompilerTest(){
        compilerClass = ControlCompiler.class;
    }

    @Test
    public void testDumb(){
        runTest("control/dumb.txt", "dumb");
    }
}
