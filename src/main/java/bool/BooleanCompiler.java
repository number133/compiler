package main.java.bool;

import main.java.core.*;
import main.java.core.Compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class BooleanCompiler implements Compiler {
    /**
     * Lookahead Character
     */
    private char look;

    private InputStream codeInput;
    private OutputStream out;


    public BooleanCompiler(InputStream codeInput){
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

    private boolean isAddop(char c){
        return Arrays.asList('+', '-').contains(c);
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
     *  Recognize a Boolean Literal
     */
    private boolean isBoolean(char c){
        return Arrays.asList('T', 'F').contains(Character.toUpperCase(c));
    }

    /**
     *  Recognize a Relop
     * @param c
     * @return
     */
    private boolean isRelop(char c){
        return Arrays.asList('=', '#', '<', '>').contains(c);
    }

    /**
     *  Recognize and Translate a Relational "Equals"
     */
    private void equals(){
        match('=');
        expression();
        emitLn("CMP (SP)+,D0");
        emitLn("SEQ D0");
    }

    /**
     *  Recognize and Translate a Relational "Not Equals"
     */
    private void notEquals(){
        match('#');
        expression();
        emitLn("CMP (SP)+,D0");
        emitLn("SNE D0");
    }

    /**
     * Recognize and Translate a Relational "Less Than"
     */
    private void less(){
        match('<');
        expression();
        emitLn("CMP (SP)+,D0");
        emitLn("SGE D0");
    }

    /**
     * Recognize and Translate a Relational "Greater Than"
     */
    private void greater(){
        match('>');
        expression();
        emitLn("CMP (SP)+,D0");
        emitLn("SLE D0");
    }

    /**
     *  Get a Boolean Literal
     * @return
     */
    private boolean getBoolean(){
        boolean result;
        if(!isBoolean(look)){
            expected("Boolean literal");
        }
        result = Character.toUpperCase(look) == 'T';
        getChar();
        return result;
    }

    /**
     * Parse and Translate a Relation
     */
    private void relation(){
        expression();
        if(isRelop(look)){
            emitLn("MOVE D0,-(SP)");
            switch (look){
                case '=':{
                    equals();
                    break;
                }
                case '#':{
                    notEquals();
                    break;
                }
                case '<':{
                    less();
                    break;
                }
                case '>':{
                    greater();
                    break;
                }
            }
            emitLn("TST D0");
        }
    }

    /**
     * Parse and Translate an Identifier
     */
    private void ident() {
        char name = getName();
        if(look == '('){
            match('(');
            match(')');
            emitLn("BSR " + name);
        } else {
            emitLn("MOVE " + name + "(PC),D0");
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

    /**
     *  Parse and Translate the First Math Factor
     */
    private void signedFactor(){
        if(look == '+'){
            getChar();
        }
        if(look == '-'){
            getChar();
            if(isDigit(look)){
                emitLn("MOVE #-" + getNum() + ",D0");
            } else {
                factor();
                emitLn("NEG D0");
            }
        } else {
            factor();
        }
    }

    /**
     * Recognize and Translate a Multiply
     */
    private void multiply(){
        match('*');
        factor();
        emitLn("MULS (SP)+,D0");
    }

    /**
     * Recognize and Translate a Divide
     */
    private void divide(){
        match('/');
        factor();
        emitLn("MOVE (SP)+,D1");
        emitLn("EXS.L D0");
        emitLn("DIVS D1,D0");
    }

    /**
     * Parse and Translate a Math Term
     */
    private void term(){
        signedFactor();
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
     *  Parse and Translate an Expression
     */
    private void expression(){
        term();
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
            }
        }
    }

    /**
     * Parse and Translate a Boolean Factor
     */
    private void boolFactor(){
        if(isBoolean(look)){
            if(getBoolean()){
                emitLn("MOVE #-1,D0");
            } else {
                emitLn("CLR D0");
            }
        } else {
            relation();
        }
    }

    /**
     * Parse and Translate a Boolean Factor with NOT
     */
    private void notFactor(){
        if(look == '!'){
            match('!');
            boolFactor();
            emitLn("EOR #-1,D0");
        } else {
            boolFactor();
        }
    }

    /**
     * Parse and Translate a Boolean Term
     */
    private void boolTerm(){
        notFactor();
        while (look == '&'){
            emitLn("MOVE D0,-(SP)");
            match('&');
            notFactor();
            emitLn("AND (SP)+,D0");
        }
    }

    /**
     * Parse and Translate a Boolean Expression
     */
    private void booleanExpression(){
        boolTerm();
        while(isOrOp(look)){
            emitLn("MOVE D0,-(SP)");
            switch (look){
                case '|':{
                    boolOr();
                    break;
                }
                case '~':{
                    boolXor();
                    break;
                }
            }
        }
    }

    /**
     * Recognize a Boolean Orop
     * @param c
     * @return
     */
    private boolean isOrOp(char c){
        return Arrays.asList('|', '~').contains(c);
    }

    /**
     *  Recognize and Translate a Boolean OR
     */
    private void boolOr(){
        match('|');
        boolTerm();
        emitLn("OR (SP)+,D0");
    }

    /**
     * Recognize and Translate an Exclusive Or
     */
    private void boolXor(){
        match('~');
        boolTerm();
        emitLn("EOR (SP)+,D0");
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
        booleanExpression();
    }

    public OutputStream getOut() {
        return out;
    }
}
