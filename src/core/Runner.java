package core;

import java.io.IOException;

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
     *  Parse and Translate a Math Expression
     */
    public void expression(){
        emitLn("MOVE #" + getNum() + ",DO");
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
