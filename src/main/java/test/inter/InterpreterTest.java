package main.java.test.inter;

import main.java.core.CoreTest;
import main.java.inter.Interpreter;
import org.junit.Test;

public class InterpreterTest extends CoreTest {

    public InterpreterTest(){
        compilerClass = Interpreter.class;
    }

    @Test
    public void testInt(){
        runTest("inter/int.txt", "integer");
    }

    @Test
    public void testAdd(){
        runTest("inter/add.txt", "add");
    }

    @Test
    public void testSubtract(){
        runTest("inter/subtract.txt", "subtract");
    }

    @Test
    public void testMulti(){
        runTest("inter/multi.txt", "multiply");
    }

    @Test
    public void testDivide(){
        runTest("inter/divide.txt", "divide");
    }

    @Test
    public void testBraces(){
        runTest("inter/braces.txt", "braces");
    }

    @Test
    public void testInout(){
        runTest("inter/inout.txt", "inout");
    }
}
