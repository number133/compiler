package main.java.core;

import java.io.*;
import java.util.Arrays;

public class Compiler {

    /**
     * Lookahead Character
     */
    private char look;
    private InputStream in;
    private OutputStream out;


    public Compiler(InputStream in){
        this.in = in;
        this.out = new ByteArrayOutputStream();
    }

    /**
     * Read New Character From Input Stream
     */
    private void getChar() {
        try {
            look = (char)in.read();
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
     *  Parse and Translate a Math Factor
     */
    private void factor(){
        if(look == '('){
            match('(');
            expression();
            match(')');
        } else if(isAlpha(look)) {
            emitLn("MOVE " + getName() + "(PC),D0");
        } else{
            emitLn("MOVE #" + getNum() + ",DO");
        }
    }

    /**
     * Parse and Translate a Math Term
     */
    private void term(){
        factor();
        while(Arrays.asList('*', '/').contains(look)){
            emitLn("MOVE D0,-(SP)");
            switch (look){
                case '*': {
                    multiply();
                    break;
                }
                case '/': {
                    divide();
                    break;
                }
                default: {
                    expected("Mulop");
                }
            }
        }
    }

    /**
     *  Parse and Translate an Expression
     */
    public void expression(){
        if(isAddop(look)){
            emitLn("CLR D0");
        } else {
            term();
        }
        while(isAddop(look)){
            emitLn("MOVE D0,-(SP)");
            switch (look) {
                case '+': {
                    add();
                    break;
                }
                case '-': {
                    subtract();
                    break;
                }
                default: {
                    expected("Addop");
                }
            }
        }
    }

    /**
     * Recognize and Translate an Add
     */
    private void add(){
        match('+');
        term();
        emitLn("ADD (SP)+,D0");
    }

    /**
     * Recognize and Translate a Subtract
     */
    private void subtract(){
        match('-');
        term();
        emitLn("SUB (SP)+,D0");
        emitLn("NEG D0");
    }

    /**
     * Recognize and Translate a Multiply
     */
    private void multiply(){
        match('*');
        factor();
        emitLn("MULS (SP)+,D0");
    }

    private void divide(){
        match('/');
        factor();
        emitLn("DIVS (SP)+,D0");
    }

    private boolean isAddop(char c){
        return Arrays.asList('+', '-').contains(c);
    }

    /**
     * Initialize
     */
    public void init(){
        getChar();
    }

    public OutputStream getOut() {
        return out;
    }
}
