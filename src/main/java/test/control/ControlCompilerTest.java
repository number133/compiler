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

    @Test
    public void testWhile(){
        runTest("control/while.txt", "while");
    }

    @Test
    public void testLoop(){
        runTest("control/loop.txt", "loop");
    }

    @Test
    public void testRepeat(){
        runTest("control/repeat.txt", "repeat");
    }
}
