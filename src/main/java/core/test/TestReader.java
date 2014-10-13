package main.java.core.test;

import java.io.*;

public class TestReader {

    private String fileName;
    private ReaderMode mode;
    private StringBuilder expression;
    private StringBuilder resultCode;
    private enum ReaderMode{
        EXPRESSION,
        RESULT_CODE
    }

    public TestReader(String fileName){
        this.fileName = fileName;
        this.resultCode = new StringBuilder();
        this.expression = new StringBuilder();
    }

    public void process() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void processLine(String line) {
        if(line.equals(ReaderMode.EXPRESSION.toString())){
            mode = ReaderMode.EXPRESSION;
        } else if(line.equals(ReaderMode.RESULT_CODE.toString())){
            mode = ReaderMode.RESULT_CODE;
        } else {
            if(mode == ReaderMode.EXPRESSION){
                expression.append(line);
                expression.append("\n");
            } else if(mode == ReaderMode.RESULT_CODE){
                resultCode.append(line);
                resultCode.append("\n");
            }
        }
    }

    public String getExpression() {
        return expression.toString();
    }

    public String getResultCode() {
        return resultCode.toString();
    }
}
