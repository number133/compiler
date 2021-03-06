package main.java.second;

import main.java.core.*;
import main.java.core.Compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class MultiCompiler implements Compiler{

    /**
     * Lookahead Character
     */
    private char look;
    private InputStream codeInput;
    private OutputStream out;


    public MultiCompiler(InputStream codeInput){
        this.codeInput = codeInput;
        this.out = new ByteArrayOutputStream();
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
        if(look != x){
            expected("" + x);
        } else {
            getChar();
            skipWhite();
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
     * Recognize an Alphanumeric
     * @param c
     * @return
     */
    private boolean isAlNum(char c){
        return isAlpha(c) || isDigit(c);
    }

    /**
     *  Recognize White Space
     * @param c
     * @return
     */
    private boolean isWhite(char c){
        return Arrays.asList(' ', '\t').contains(c);
    }

    /**
     *  Skip Over Leading White Space
     */
    private void skipWhite(){
        while(isWhite(look)){
            getChar();
        }
    }

    /**
     * Get an Identifier
     * @return
     */
    private String getName(){
        String token = "";
        if(!isAlpha(look)){
            expected("Name");
        }
        while(isAlNum(look)){
            token = token + Character.toUpperCase(look);
            getChar();
        }
        skipWhite();
        return token;
    }

    /**
     * Get a Number
     * @return
     */
    private String getNum(){
        String value = "";
        if(!isDigit(look)){
            expected("Integer");
        }
        while(isDigit(look)){
            value = value + look;
            getChar();
        }
        skipWhite();
        return value;
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
            ident();
        } else{
            emitLn("MOVE #" + getNum() + ",DO");
        }
    }

    private void ident() {
        String name = getName();
        if(look == '('){
            match('(');
            match(')');
            emitLn("BSR " + name);
        } else {
            emitLn("MOVE " + name + "(PC),D0");
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
    private void expression(){
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

    private void assignment(){
        String name = getName();
        match('=');
        expression();
        emitLn("LEA " + name + "(PC),A0");
        emitLn("MOVE D0,(A0)");
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
    private void init(){
        getChar();
        skipWhite();
    }

    /**
     * main method
     */
    public void compile(){
        init();
        assignment();
        if(look != '\n'){
            expected("Newline");
        }
    }

    public OutputStream getOut() {
        return out;
    }

}
