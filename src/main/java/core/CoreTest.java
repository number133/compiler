package main.java.core;

import main.java.test.TestReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.Assert.assertEquals;

public abstract class CoreTest {
    InputStream in;
    TestReader testReader;
    Compiler compiler;
    protected Class compilerClass;

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
