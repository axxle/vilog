package ru.axxle.vilog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LogView {
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");

    private String htmlId;
    private String fileName;
    private String threadName;
    private String initTimeStr;
    private String destroyTimeStr;

    private long y0;
    private long y1;
    private String color;

    public static LogView parse(File file){
        List<String> lines = new LinkedList<String>();
        LogView logView = new LogView();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logView.setFileName(file.getName());
        if(!lines.isEmpty()){
            logView.setThreadName(parseThreadName(lines));
            logView.setInitTimeStr(parseInitTimeStr(lines));
            logView.setDestroyTimeStr(parseDestroyTimeStr(lines));
            logView.setHtmlId(logView.getFileName().replace('.', '_'));
        }
        parseNumbers(logView);
        return logView;
    }

    private static String parseThreadName (List<String> lines) {
        //return lines.get(0).substring(30, 44);
        String s = lines.get(0);
        String ss = s.substring(30, 52);
        return ss;
    }

    private static void parseNumbers (LogView logView) {
        try {
            Date date = DATE_FORMAT.parse(logView.getInitTimeStr());
            logView.setY0(date.getTime());
            date = DATE_FORMAT.parse(logView.getDestroyTimeStr());
            logView.setY1(date.getTime());
        } catch(Exception e) { }
    }

    private static String parseInitTimeStr (List<String> lines) { //TO DO
        if (!lines.isEmpty())
            return lines.get(0).substring(0, 23);
        return "";
    }

    private static String parseDestroyTimeStr (List<String> lines) {
        if (!lines.isEmpty())
            return lines.get(lines.size()-1).substring(0, 23);
        return "";
    }


    public String printSvgRect(long shiftY, int x0, int x1){
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n\t\t\t")
                .append("<polygon points=\""+ x0 +",").append(y0-shiftY)
                .append(" " + x1 + ",").append(y0-shiftY)
                .append(" " + x1 + ",").append(y1-shiftY)
                .append(" " + x0 + ",").append(y1-shiftY)
                .append(" " + x0 + ",").append(y0-shiftY)
                .append("\" fill=\"").append(color).append("\" stroke=\"none\" />");
        return builder.toString();
    }

    public String printSvgText(long shiftY, int x0){
        StringBuilder builder = new StringBuilder();
        builder.append("\r\n\t\t\t")
                .append("<text x=\"" + x0 + "\" y=\"")
                .append((y0 + 100 - shiftY))
                .append("\"").append("font-size=\"100px\"").append("><tspan>")
                .append(fileName)
                .append("</tspan></text>");
        return builder.toString();
    }

    public String printSvgRect0(long shiftY){
        StringBuilder builder = new StringBuilder();
        builder.append("<polygon points=\"100,").append(y0-shiftY)
                .append(" 1100,").append(y0-shiftY)
                .append(" 1100,").append(y1-shiftY)
                .append(" 100,").append(y1-shiftY)
                .append(" 100,").append(y0-shiftY)
                .append("\" fill=\"").append(color).append("\" stroke=\"none\" />");
        return builder.toString();
    }

    public String printSvgText0(long shiftY){
        StringBuilder builder = new StringBuilder();
        builder.append("<text x=\"100\" y=\"")
                .append((y0 + 100 - shiftY))
                .append("\"").append("font-size=\"100px\"").append("><tspan>")
                .append(fileName)
                .append("</tspan></text>");
        return builder.toString();
    }

    public long getY0() {
        return y0;
    }

    public void setY0(long y0) {
        this.y0 = y0;
    }

    public long getY1() {
        return y1;
    }

    public void setY1(long y1) {
        this.y1 = y1;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getInitTimeStr() {
        return initTimeStr;
    }

    public void setInitTimeStr(String initTimeStr) {
        this.initTimeStr = initTimeStr;
    }

    public String getDestroyTimeStr() {
        return destroyTimeStr;
    }

    public void setDestroyTimeStr(String destroyTimeStr) {
        this.destroyTimeStr = destroyTimeStr;
    }

    public String getHtmlId() {
        return htmlId;
    }

    public void setHtmlId(String htmlId) {
        this.htmlId = htmlId;
    }
}
