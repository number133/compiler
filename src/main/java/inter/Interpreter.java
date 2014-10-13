package main.java.inter;

import main.java.core.*;
import main.java.core.Compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Interpreter implements Compiler {
    /**
     * Lookahead Character
     */
    private char look;
    private InputStream in;
    private OutputStream out;

    public Interpreter(InputStream in){
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
    private int getNum(){
        if(!isDigit(look)){
            expected("Integer");
        }
        int result = 0;
        while(isDigit(look)){
            result = result * 10 + Integer.valueOf(Character.toString(look));
            getChar();
        }
        return result;
    }

    private int factor(){
        int value = 0;
        if(look == '('){
            match('(');
            value = expression();
            match(')');
        } else {
            value = getNum();
        }
        return value;
    }

    /**
     * Parse and Translate a Math Term
     */
    private int term(){
        int value = factor();
        while(Arrays.asList('*', '/').contains(look)){
            switch (look){
                case '*': {
                    match('*');
                    value = value * factor();
                    break;
                }
                case '/': {
                    match('/');
                    value = value / factor();
                    break;
                }
            }
        }
        return value;
    }

    /**
     *  Parse and Translate an Expression
     */
    private int expression(){
        int value = 0;
        if(isAddop(look)){
            value = 0;
        } else {
            value = term();
        }
        while(isAddop(look)){
            switch (look){
                case '+': {
                    match('+');
                    value = value + term();
                    break;
                }
                case '-': {
                    match('-');
                    value = value - term();
                }
            }
        }
        return value;
    }

    private boolean isAddop(char c){
        return Arrays.asList('+', '-').contains(c);
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
        try {
            getOut().write(
                    (String.valueOf(expression()) + "\n").getBytes()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(look != '\n'){
            expected("Newline");
        }
    }

    public OutputStream getOut() {
        return out;
    }
}
