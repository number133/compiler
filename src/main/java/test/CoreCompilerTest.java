package main.java.test;

import main.java.core.CoreCompiler;
import main.java.core.CoreTest;
import org.junit.Test;

public class CoreCompilerTest extends CoreTest{

    public CoreCompilerTest(){
        compilerClass = CoreCompiler.class;
    }

    @Test
    public void testOneDigit(){
        runTest("oneDigit.txt", "one digit");
    }

    @Test
    public void testNegativeDigit(){
        runTest("negativeDigit.txt", "negative digit");
    }

    @Test
    public void testAdd(){
        runTest("add.txt", "addition");
    }

    @Test
    public void testSubtract(){
        runTest("subtract.txt", "subtract");
    }

    @Test
    public void testMultiply(){
        runTest("multiply.txt", "multiply");
    }

    @Test
    public void testDivide(){
        runTest("divide.txt", "divide");
    }

    @Test
     public void testMix(){
        runTest("mix.txt", "mix");
    }

    @Test
    public void testBraces(){
        runTest("braces.txt", "braces");
    }

    @Test
    public void testVar(){
        runTest("var.txt", "var");
    }

    @Test
    public void testFunction(){
        runTest("func.txt", "function");
    }

    @Test
    public void testNewlineError(){
        runTest("newlineError.txt", "new line");
    }

}
