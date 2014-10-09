package core;

import java.io.IOException;
import java.util.Arrays;

public class Runner {

    /**
     * Lookahead Character
     */
    private char look;

    /**
     * Read New Character From Input Stream
     */
    public void getChar() {
        try {
            look = (char)System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Report an Error
     * @param s
     */
    public void error(String s){
        System.out.println();
        System.out.println("\0007 Error:" + s + ".");
    }

    /**
     * Report Error and exit
     * @param s
     */
    public void abort(String s){
        error(s);
        System.exit(1);
    }

    /**
     * Report What Was Expected
     * @param s
     */
    public void expected(String s){
        abort(s + " Expected");
    }

    /**
     * Match a Specific Input Character
     * @param x
     */
    public void match(char x){
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
    public boolean isAlpha(char c){
        return (Character.toUpperCase(c) >= 'A') && (Character.toUpperCase(c) <= 'Z');
    }

    /**
     * Recognize a Decimal Digit
     * @param c
     * @return
     */
    public boolean isDigit(char c){
        return (c >= '0') && (c <= '9');
    }

    /**
     * Get an Identifier
     * @return
     */
    public char getName(){
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
    public char getNum(){
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
    public void emit(String s){
        System.out.print("\t" + s);
    }

    /**
     * Output a String with Tab and CRLF
     * @param s
     */
    public void emitLn(String s){
        emit(s);
        System.out.println();
    }

    /**
     *  Parse and Translate a Math Factor
     */
    public void factor(){
        if(look == '('){
            match('(');
            expression();
            match(')');
        } else {
            emitLn("MOVE #" + getNum() + ",DO");
        }
    }

    /**
     * Parse and Translate a Math Term
     */
    public void term(){
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
        term();
        while(Arrays.asList('+', '-').contains(look)){
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
    public void add(){
        match('+');
        term();
        emitLn("ADD (SP)+,D0");
    }

    /**
     * Recognize and Translate a Subtract
     */
    public void subtract(){
        match('-');
        term();
        emitLn("SUB (SP)+,D0");
        emitLn("NEG D0");
    }

    /**
     * Recognize and Translate a Multiply
     */
    public void multiply(){
        match('*');
        factor();
        emitLn("MULS (SP)+,D0");
    }

    public void divide(){
        match('/');
        factor();
        emitLn("DIVS (SP)+,D0");
    }

    /**
     * Initialize
     */
    public void init(){
        getChar();
    }

    public static void main(String[] args){
        Runner runner = new Runner();
        runner.init();
        runner.expression();
    }
}
