package ru.axxle.vilog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {
    List<ThreadView> threadViewList = new ArrayList<ThreadView>();
    long minY;
    long maxY;
    static int tWidth = 1000;
    static int tDistance = 100;
    static long shiftY = 1553156980000L; //TO DO как то нужно рассчитать
    static long vb_height;
    static long vb_y1;
    //параметры viewBox //TO DO как то нужно рассчитать

    public List<ThreadView> getThreadViewList() {
        return threadViewList;
    }
    public void setThreadViewList(List<ThreadView> threadViewList) {
        this.threadViewList = threadViewList;
    }


    public static void main(String[] args) {
        try {
            App app = new App();
            System.out.println(new File(".").getAbsolutePath());
            //TO DO
            //      ./app.jar
            // если есть папка ./INPUT - то считываем из нее файлы и выходной файл кладем в текущую папку - ./output.html
            // если такой папки нет - то создаем ее, и кладем файл кладем в текущую папку - ./Положите ваши файлы для анализа в папку INPUT.html
            String dirPath = "C:\\TESTNG_LOGS\\"; //TO DO
            List<LogView> logViewList = app.parseFilesFromDir(dirPath);
            app.threadViewList = ThreadView.fillThreadViews(logViewList);
            app.calcViewBoxParams(app.getThreadViewList());
            app.prepareToPrint(app.getThreadViewList());
            app.printHtmlFile(app.getThreadViewList());
        } catch (Exception e){
            System.out.println("ОШИБКА!!! : " + e.toString()); //TO DO
        }
    }

    private void calcViewBoxParams (List<ThreadView> tList) {
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (ThreadView threadView : tList) {
            List<LogView> logViews = threadView.getLogViewList();
            for (LogView log : logViews) {
                long init = log.getY0();
                if (init < min)
                    min = init;
                long destroy = log.getY1();
                if (destroy > max)
                    max = destroy;
            }
        }
        this.minY = min;
        this.maxY = max;
        App.shiftY = (min - (min % 100));
        App.vb_height = max - shiftY + 100;
        App.vb_y1 = max - shiftY + 100;
    }

    private static void prepareToPrint(List<ThreadView> list){
        for(int i = 0; i < list.size(); i++){
            ThreadView t = list.get(i);
            t.setX0(tDistance + ((tWidth + tDistance) * i) );
            t.setX1(tDistance + ((tWidth + tDistance) * i) + tWidth);
            t.prepareToPrint();
        }
    }

    private static List<LogView> parseFilesFromDir(String dirPath){
        File file = new File(dirPath);
        File[] files;
        if(file.isDirectory()){
            files = file.listFiles();
        } else {
            files = new File[]{file};
        }
        if(files == null) return null;
        List<LogView> logViewList = new ArrayList<LogView>();
        for(int i = 0; i < files.length; i++){
            File oneFile = files[i];
            LogView logView = parse(oneFile);
            logViewList.add(logView);
        }
        return logViewList;
    }

    private static LogView parse(File file){
        LogView logView = LogView.parse(file);
        return logView;
    }

    public static void printHtmlFile(List<ThreadView> tList){
        try(FileWriter writer = new FileWriter("output.html")) {
            writer.write(HTML_HEADER);
            writer.write(SVG_BOARD_HEADER);
            writer.write(SVG_HEADER_1 + App.vb_height + SVG_HEADER_2 + App.vb_y1 + SVG_HEADER_3);

            for (int i = tList.size()-1; i >= 0; i--) {
                ThreadView threadView = tList.get(i);
                List<LogView> logViewList = threadView.getLogViewList();
                int x0 = threadView.getX0();
                int x1 = threadView.getX1();
                for (int j = 0; j < logViewList.size(); j++) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(logViewList.get(j).printSvgRect(App.shiftY, x0, x1));
                    builder.append(logViewList.get(j).printSvgText(App.shiftY, x0));
                    writer.write(builder.toString());
                }
            }
            writer.write(SVG_FOOTER);
            writer.write(SVG_BOARD_FOOTER);
            writer.write(SVG_BOARD_JS);
            writer.write(HTML_FOOTER);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static String HTML_HEADER = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "\t<title>visual_testNG_logs</title>\n" +
            "\t<style> #v_body { margin:0 0 0 0; overflow:hidden !important; } </style>\n" +
            "\t\n" +
            "</head>\n" +
            "<body id=\"v_body\">";

    private static String SVG_BOARD_HEADER = "\r\n<svg version=\"1.1\"\n" +
            "     baseProfile=\"full\"\n" +
            "     xmlns=\"http://www.w3.org/2000/svg\"\n" +
            "     xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
            "     xmlns:ev=\"http://www.w3.org/2001/xml-events\"\n" +
            "\t width=\"1920\" height=\"908\" viewBox=\"0 0 1920 908\" id = \"svgboard\">";

    private static String SVG_HEADER_1 = "\r\n\t\t<svg x=\"0\" y=\"0\" width=\"40000\" height=\"";
    private static String SVG_HEADER_2 = "\" viewBox=\"0 0 40000 ";
    private static String SVG_HEADER_3 = "\">";

    private static String SVG_FOOTER = "\r\n\t\t</svg>";

    private static String SVG_BOARD_FOOTER = "\r\n</svg>";

    private static String HTML_FOOTER = "</body>\n" + "</html>";


    private static String SVG_BOARD_JS = "\r\n<script lang=\"JavaScript\">\n" +
            "var SVG_BOARD_ID = \"svgboard\";\t\n" +
            "var scaleRates = [\n" +
            "\t256,\n" +
            "\t128,\n" +
            "\t64,\n" +
            "\t32,\n" +
            "\t16,\n" +
            "\t8,\n" +
            "\t4,\n" +
            "\t3,\n" +
            "\t2.5,\n" +
            "\t2,\n" +
            "\t1.8,\n" +
            "\t1.5,\n" +
            "\t1.2,\n" +
            "\t1,\n" +
            "\t0.5,\n" +
            "\t0.25,\n" +
            "\t0.2,\n" +
            "\t0.15,\n" +
            "\t0.075\n" +
            "];\n" +
            "var singleScaleRateIndex = findInitScaleIndex(scaleRates);\n" +
            "var currentScaleIndex = singleScaleRateIndex;\n" +
            "function findInitScaleIndex(scaleRateArr) {\n" +
            "\tfor(var i = 0; i < scaleRateArr.length; i++){\n" +
            "\t\tif (scaleRateArr[i] == 1 ) {\n" +
            "\t\t\treturn i;\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "var TOOLBAR_HEIGHT = 30;\n" +
            "var initWidth=document.documentElement.clientWidth;\n" +
            "var initHeight=document.documentElement.clientHeight;\n" +
            "\n" +
            "var scaleValues = calcScaleValues(scaleRates);\n" +
            "/*Построение таблицы масштабирования*/\n" +
            "function calcScaleValues(scaleRateArr) {\n" +
            "\tvar newScaleValueArr = [];\n" +
            "\tfor (var i = 0; i < scaleRateArr.length; i++) {\n" +
            "\t\tvar w = 0;\n" +
            "\t\tvar h = 0;\n" +
            "\t\tif (scaleRateArr[i]<0) {\n" +
            "\t\t\tw = initWidth/(-scaleRateArr[i]);\n" +
            "\t\t\th = initHeight/(-scaleRateArr[i]);\n" +
            "\t\t}\n" +
            "\t\tif (scaleRateArr[i]>=0) {\n" +
            "\t\t\tw = initWidth*(scaleRateArr[i]);\n" +
            "\t\t\th = initHeight*(scaleRateArr[i]);\n" +
            "\t\t}\n" +
            "\t\tnewScaleValueArr.push([0, 0, w, h]);\n" +
            "\t}\n" +
            "\treturn newScaleValueArr;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "/*Считает значения для viewBox при перетаскивании мышкой*/\n" +
            "function calcMouseMove(mouseDownVBvalues, mouseDownXY, mouseMoveXY, currentScaleIndex) {\n" +
            "\tvar rate = scaleRates[currentScaleIndex];\n" +
            "\tvar diffX = mouseDownXY[0] - mouseMoveXY[0];\n" +
            "\tvar diffY = mouseDownXY[1] - mouseMoveXY[1];\n" +
            "\tvar castedDiffX = diffX*rate;\n" +
            "\tvar castedDiffY = diffY*rate;\n" +
            "\tvar shiftedDiffX = castedDiffX + mouseDownVBvalues[0];\n" +
            "\tvar shiftedDiffY = castedDiffY + mouseDownVBvalues[1];\n" +
            "\tvar values = scaleValues[currentScaleIndex];\n" +
            "\treturn [shiftedDiffX, shiftedDiffY, values[2], values[3]];\n" +
            "}\n" +
            "\n" +
            "\n" +
            "/*Обработчик событий прокрутки мыши*/\n" +
            "document.onwheel = function(e) {\n" +
            "\te = e || window.event;\n" +
            "\tvar delta = e.deltaY || e.detail || e.wheelDelta;\n" +
            "\tvar mouseX = e.clientX;\n" +
            "\tvar mouseY = e.clientY;\n" +
            "\tsetViewBoxValues(SVG_BOARD_ID, getScaleValues(delta, mouseX, mouseY));\n" +
            "};\n" +
            "\n" +
            "/*Обработка перетаскивания полотна доски*/\n" +
            "var svgBoardObject = document.getElementById(SVG_BOARD_ID);\n" +
            "svgBoardObject.onmousedown = function(event) {\n" +
            "\tvar mouseDownXY = [event.clientX, event.clientY];\n" +
            "\tvar mouseDownVBvalues = getViewBoxValues(SVG_BOARD_ID);\n" +
            "\tdocument.onmousemove = function(e) {\n" +
            "\t\tvar mouseMoveXY = [e.clientX, e.clientY];\n" +
            "\t\tvar moveViewBox = calcMouseMove(mouseDownVBvalues, mouseDownXY, mouseMoveXY, currentScaleIndex);\n" +
            "\t\tsetViewBoxValues(SVG_BOARD_ID, moveViewBox);\n" +
            "\t}\n" +
            "\tsvgBoardObject.onmouseup = function(e) {\n" +
            "\t\tdocument.onmousemove = null;\n" +
            "\t\tsvgBoardObject.onmouseup = null;\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "var getViewBoxValues = function (id) { \n" +
            "  var strArr = document.getElementById(id).getAttribute(\"viewBox\").split(\" \");\n" +
            "  var arr = [];\n" +
            "  for(var i = 0; i < strArr.length; i++){\n" +
            "\tarr[i] = parseInt(strArr[i], 10);\n" +
            "  }\n" +
            "  return arr;\n" +
            "}\n" +
            "\n" +
            "var setViewBoxValues = function (id, arr) { \n" +
            "  var svg = document.getElementById(id);\n" +
            "  svg.setAttribute(\"viewBox\", arr.join(' '));\n" +
            "}\n" +
            "\n" +
            "\n" +
            "function initBoard(boardId) { \n" +
            "\tvar board = document.getElementById(SVG_BOARD_ID);\n" +
            "\tinitWidth=document.documentElement.clientWidth;\n" +
            "\tinitHeight=document.documentElement.clientHeight;\n" +
            "\tboard.setAttribute('width', initWidth);\n" +
            "\tboard.setAttribute('height', initHeight);\n" +
            "\tsetViewBoxValues(SVG_BOARD_ID, [0, 0, initWidth, initHeight]);\n" +
            "}\n" +
            "\n" +
            "/*Самовызываемая функция*/\n" +
            "(function (){\n" +
            "\tinitBoard(SVG_BOARD_ID);\n" +
            "}());\n" +
            "\n" +
            "function castSingleRate(mouseX, mouseY, currentScaleIndex) {\n" +
            "\tvar rate = scaleRates[currentScaleIndex];\n" +
            "\tvar castedX = mouseX*rate;\n" +
            "\tvar castedY = mouseY*rate;\n" +
            "\treturn [castedX, castedY];\n" +
            "}\n" +
            "\t\n" +
            "/*Получение данных для масштабирования через viewBox*/\n" +
            "function getScaleValues(wheelDelta, mouseX, mouseY) {\n" +
            "\tvar k = wheelDelta < 0 ? 1 : -1;\n" +
            "\tvar prevScaleIndex = currentScaleIndex;\n" +
            "\tvar nextScaleIndex = prevScaleIndex + k;\n" +
            "\tif (prevScaleIndex < 0) {\n" +
            "\t\tprevScaleIndex = 0;\n" +
            "\t}\n" +
            "\tif (prevScaleIndex >= scaleValues.length-1) {\n" +
            "\t\tprevScaleIndex = scaleValues.length-1;\n" +
            "\t}\n" +
            "\tif (nextScaleIndex < 0) {\n" +
            "\t\tnextScaleIndex = 0;\n" +
            "\t}\n" +
            "\tif (nextScaleIndex >= scaleValues.length-1) {\n" +
            "\t\tnextScaleIndex = scaleValues.length-1;\n" +
            "\t}\n" +
            "\tvar viewBoxCurrent = getViewBoxValues(SVG_BOARD_ID);\n" +
            "\tvar newValues = scaleValues[nextScaleIndex];\n" +
            "\tvar lastRate = scaleRates[currentScaleIndex];\n" +
            "\tvar newRate = scaleRates[nextScaleIndex];\n" +
            "\t\n" +
            "\tvar shiftedArr = [\n" +
            "\t\tviewBoxCurrent[0] + (mouseX*lastRate - mouseX*newRate),\n" +
            "\t\tviewBoxCurrent[1] + (mouseY*lastRate - mouseY*newRate),\n" +
            "\t\tnewValues[2],\n" +
            "\t\tnewValues[3]\n" +
            "\t]\n" +
            "\tcurrentScaleIndex = nextScaleIndex;\n" +
            "\treturn shiftedArr;\n" +
            "}\n" +
            "</script>\n";
}
