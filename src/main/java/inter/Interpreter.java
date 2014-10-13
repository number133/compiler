package main.java.inter;

import main.java.core.*;
import main.java.core.Compiler;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Interpreter implements Compiler {
    /**
     * Lookahead Character
     */
    private char look;
    private Map<Character, Integer> table = new HashMap<Character, Integer>();

    private InputStream codeInput;
    private BufferedReader consoleInput;
    private OutputStream out;

    public Interpreter(InputStream codeInput, InputStream consoleInput){
        this.codeInput = codeInput;
        this.consoleInput = new BufferedReader(new InputStreamReader(consoleInput));
        this.out = new ByteArrayOutputStream();
    }

    private void initTable(){
        for(char c = 'A'; c <= 'Z'; c++){
            table.put(c, 0);
        }
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
     * Recognize and Skip Over a Newline
     */
    private void newLine(){
        if(look == '\n'){
            getChar();
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

    /**
     * Parse and Translate an Assignment Statement
     */
    private void assignment(){
        char name = getName();
        match('=');
        table.put(name, expression());
    }

    private int factor(){
        int value = 0;
        if(look == '('){
            match('(');
            value = expression();
            match(')');
        } else if(isAlpha(look)){
            value = table.get(getName());
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
     * Input Routine
     */
    private void input(){
        match('?');
        try {
            table.put(getName(), Integer.valueOf(consoleInput.readLine()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Output Routine
     */
    private void output(){
        match('!');
        try {
            out.write((table.get(getName()).toString() + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize
     */
    private void init(){
        initTable();
        getChar();
    }

    /**
     * main method
     */
    public void compile(){

        init();
        do {
            switch (look){
                case '?':{
                    input();
                    break;
                }
                case '!':{
                    output();
                    break;
                }
                default:{
//                    try {
                        assignment();
//                        getOut().write(
//                                (String.valueOf(table.get('A')) + "\n").getBytes()
//                        );
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
            newLine();
        } while(look != '.');
    }

    public OutputStream getOut() {
        return out;
    }
}
