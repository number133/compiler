package main.java.test.control;

import main.java.control.ControlCompiler;
import main.java.core.CoreTest;
import org.junit.Test;

public class ControlCompilerTest extends CoreTest{

    public ControlCompilerTest(){
        compilerClass = ControlCompiler.class;
    }

    @Test
    public void testIfElse(){
        runTest("control/ifElse.txt", "if/else");
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

    @Test
    public void testFor(){
        runTest("control/for.txt", "for");
    }

    @Test
    public void testDo(){
        runTest("control/do.txt", "do");
    }

    @Test
    public void testBreak(){
        runTest("control/break.txt", "break");
    }
}
