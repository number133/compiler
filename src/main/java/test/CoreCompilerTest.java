package main.java.test;

import static junit.framework.Assert.assertEquals;

import main.java.core.*;
import main.java.core.Compiler;
import main.java.core.CoreCompiler;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CoreCompilerTest {
    InputStream in;
    TestReader testReader;
    Compiler compiler;
    Class compilerClass;

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

    public void runTest(String fileName, String error){
        testReader = new TestReader(fileName);
        testReader.process();
        in = new ByteArrayInputStream(testReader.getExpression().getBytes());
        Constructor constructor = compilerClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        try {
            compiler = (Compiler)constructor.newInstance(in);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            compiler.compile();
        } catch(CompileException e){
            assertEquals(error, testReader.getResultCode(), compiler.getOut().toString());
        }
        assertEquals(error, testReader.getResultCode(), compiler.getOut().toString());
    }
}
