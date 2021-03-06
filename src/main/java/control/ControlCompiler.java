package main.java.control;

import main.java.core.*;
import main.java.core.Compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ControlCompiler implements Compiler {
    /**
     * Lookahead Character
     */
    private char look;
    private int lCount;

    private InputStream codeInput;
    private OutputStream out;


    public ControlCompiler(InputStream codeInput){
        this.codeInput = codeInput;
        this.out = new ByteArrayOutputStream();
        this.lCount = 0;
    }

    /**
     * Read New Character From Input Stream
     */
    private void getChar() {
        try {
            look = (char) codeInput.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Report an Error
     * @param s
     */
    private void error(String s){
        try {
            out.write("\n".getBytes());
            out.write(("\t Error:" + s + ".\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Report Error and exit
     * @param s
     */
    private void abort(String s){
        error(s);
        throw new CompileException();
    }

    /**
     * Report What Was Expected
     * @param s
     */
    private void expected(String s){
        abort(s + " Expected");
    }

    /**
     * Match a Specific Input Character
     * @param x
     */
    private void match(char x){
        if(look == x){
            getChar();
        } else {
            expected("" + x);
        }
    }

    /**
     * Recognize an Alpha Character
     * @param c
     * @return
     */
    private boolean isAlpha(char c){
        return (Character.toUpperCase(c) >= 'A') && (Character.toUpperCase(c) <= 'Z');
    }

    /**
     * Recognize a Decimal Digit
     * @param c
     * @return
     */
    private boolean isDigit(char c){
        return (c >= '0') && (c <= '9');
    }

    /**
     * Get an Identifier
     * @return
     */
    private char getName(){
        if(!isAlpha(look)){
            expected("Name");
        }
        char result = Character.toUpperCase(look);
        getChar();
        return result;
    }

    /**
     * Get a Number
     * @return
     */
    private char getNum(){
        if(!isDigit(look)){
            expected("Integer");
        }
        char result = look;
        getChar();
        return result;
    }

    /**
     * Output a String with Tab
     * @param s
     */
    private void emit(String s){
        try {
            out.write(("\t" + s).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Output a String with Tab and CRLF
     * @param s
     */
    private void emitLn(String s){
        emit(s);
        try {
            out.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recognize and Translate an "Other"
     */
    private void other(){
        emitLn(String.valueOf(getName()));
    }

    /**
     *  Parse and Translate a Program
     */
    private void doProgram(){
        block("");
        if(look!='e'){
            expected("End");
        }
        emitLn("END");
    }

    /**
     * Recognize and Translate a Statement Block
     */
    private void block(String label){
        block: while(!Arrays.asList('e', 'l', 'u').contains(look)){
            switch (look){
                case 'i': {
                    doIf(label);
                    break;
                }
                case 'w': {
                    doWhile();
                    break;
                }
                case 'p': {
                    doLoop();
                    break;
                }
                case 'r': {
                    doRepeat();
                    break;
                }
                case 'f': {
                    doFor();
                    break;
                }
                case 'd': {
                    doDo();
                    break;
                }
                case 'b': {
                    doBreak(label);
                    break;
                }
                default:{
                    other();
                }
            }
        }
    }

    /**
     *  Generate a Unique Label
     * @return
     */
    private String newLabel(){
        String label = "L" + lCount;
        lCount++;
        return label;
    }

    /**
     * Post a Label To Output
     * @param l
     */
    private void postLabel(String l){
        try {
            out.write(l.getBytes());
            out.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Parse and Translate a Boolean Condition
     *  This version is a dummy
     */
    private void condition(){
        emitLn("<condition>");
    }

    /**
     * Recognize and Translate a BREAK
     * @param label
     */
    private void doBreak(String label){
        match('b');
        if(label!=null && !label.isEmpty()){
            emitLn("BRA " + label);
        } else {
            abort("No loop to break from");
        }
    }

    /**
     * Recognize and Translate an IF Construct
     */
    private void doIf(String label){
        String labe1, labe2;
        match('i');
        condition();
        labe1 = newLabel();
        labe2 = labe1;
        emitLn("BEQ " + labe1);
        block(label);
        if(look == 'l'){
            match('l');
            labe2 = newLabel();
            emitLn("BRA " + labe2);
            postLabel(labe1);
            block(label);
        }
        match('e');
        postLabel(labe2);
    }

    private void doWhile(){
        String labe1, labe2;
        match('w');
        labe1 = newLabel();
        labe2 = newLabel();
        postLabel(labe1);
        condition();
        emitLn("BEQ " + labe2);
        block(labe2);
        match('e');
        emitLn("BRA " + labe1);
        postLabel(labe2);
    }

    /**
     *  Parse and Translate a LOOP Statement
     */
    private void doLoop(){
        String labe1, labe2;
        match('p');
        labe1 = newLabel();
        labe2 = newLabel();
        postLabel(labe1);
        block(labe2);
        match('e');
        emitLn("BRA " + labe1);
        postLabel(labe2);
    }

    /**
     * Parse and Translate a REPEAT Statement
     */
    private void doRepeat(){
        String labe1, labe2;
        match('r');
        labe1 = newLabel();
        labe2 = newLabel();
        postLabel(labe1);
        block(labe2);
        match('u');
        condition();
        emitLn("BEQ " + labe1);
        postLabel(labe2);
    }

    /**
     *  Parse and Translate a FOR Statement
     */
    private void doFor(){
        String labe1, labe2;
        char name;
        match('f');
        labe1 = newLabel();
        labe2 = newLabel();
        name = getName();
        match('=');
        expression();
        emitLn("SUBQ #1,D0");
        emitLn("LEA " + name + "(PC),A0");
        emitLn("MOVE (D0),A0");
        expression();
        emitLn("MOVE D0,-(SP)");
        postLabel(labe1);
        emitLn("LEA " + name + "(PC),A0");
        emitLn("MOVE (A0),D0");
        emitLn("ADDQ #1,D0");
        emitLn("MOVE D0,(A0)");
        emitLn("CMP (SP),D0");
        emitLn("BGT " + labe2);
        block(labe2);
        match('e');
        emitLn("BRA " + labe1);
        postLabel(labe2);
        emitLn("ADDQ #2,SP");
    }

    /**
     *  Parse and Translate a DO Statement
     */
    private void doDo(){
        String labe1, labe2;
        match('d');
        labe1 = newLabel();
        labe2 = newLabel();
        expression();
        emitLn("SUBQ #1,D0");
        postLabel(labe1);
        emitLn("MOVE D0,-(SP)");
        block(labe2);
        emitLn("MOVE (SP)+,D0");
        emitLn("DBRA D0," + labe1);
        emitLn("SUBQ #2,SP");
        postLabel(labe2);
        emitLn("ADDQ #2,SP");
    }

    /**
     *  Parse and Translate an Expression
     *  This version is a dummy
     */
    private void expression(){
        emitLn("<expr>");
    }

    /**
     * Initialize
     */
    private void init(){
        getChar();
    }

    /**
     * main method
     */
    public void compile(){
        init();
        doProgram();
    }

    public OutputStream getOut() {
        return out;
    }
}
