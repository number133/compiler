package main.java.test.bool;

import main.java.bool.BooleanCompiler;
import main.java.core.CoreTest;
import org.junit.Test;

public class BooleanCompilerTest extends CoreTest{

    public BooleanCompilerTest(){
        compilerClass = BooleanCompiler.class;
    }

    @Test
    public void testOr(){
        runTest("bool/or.txt", "or");
    }

    @Test
    public void testXor(){
        runTest("bool/xor.txt", "xor");
    }

    @Test
    public void testNot(){
        runTest("bool/not.txt", "not");
    }

    @Test
    public void testAnd(){
        runTest("bool/and.txt", "and");
    }

    @Test
    public void testExp(){
        runTest("bool/exp.txt", "exp");
    }
}
