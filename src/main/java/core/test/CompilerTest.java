package main.java.core.test;

import static junit.framework.Assert.assertEquals;

import main.java.core.*;
import main.java.core.Compiler;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CompilerTest {
    InputStream in;
    TestReader testReader;
    main.java.core.Compiler compiler;

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

    public void runTest(String fileName, String error){
        testReader = new TestReader(fileName);
        testReader.process();
        in = new ByteArrayInputStream(testReader.getExpression().getBytes());
        compiler = new Compiler(in);
        compiler.init();
        try {
            compiler.expression();
        } catch(CompileException e){
            assertEquals(error, testReader.getResultCode(), compiler.getOut().toString());
        }
        assertEquals(error, testReader.getResultCode(), compiler.getOut().toString());
    }
}
