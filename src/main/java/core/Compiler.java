package main.java.core;

import java.io.OutputStream;

public interface Compiler {

    public void compile();

    public OutputStream getOut();
}
