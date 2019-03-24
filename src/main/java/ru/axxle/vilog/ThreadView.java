package ru.axxle.vilog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadView {
    private static final Pattern THREAD_NUMBER_PATTERN= Pattern.compile("-\\d*\\]");
    private String threadName;
    private Integer threadNumber;
    List<LogView> logViewList = new ArrayList<LogView>();
    private int x0;
    private int x1;
    private String[] colors = new String [] {"#30D5C8", "#98FF98"};

    public void prepareToPrint(){
        Collections.sort(this.logViewList, new Comparator() {
                    public int compare(Object o1, Object o2)
                    {
                        LogView l1 = (LogView)o1;
                        LogView l2 = (LogView)o2;
                        int v = 0;
                        if (l1.getY0() < l2.getY0())
                            v = -1;
                        if (l1.getY0() > l2.getY0())
                            v = 1;
                        return v;
                    }
                }
        );
        setColors(this.logViewList);
    }

    private void setColors (List<LogView> list) {
        for (int i = 0; i < list.size(); i++) {
            LogView logView = list.get(i);
            int colorIndex = i % this.colors.length;
            logView.setColor(this.colors[colorIndex]);
        }
    }

    public static List<ThreadView> fillThreadViews (List<LogView> logViewList) {
        List<ThreadView> threadViewList = createEmptyThreadViews (logViewList);
        Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i < threadViewList.size(); i++){
            ThreadView t = threadViewList.get(i);
            map.put(t.getThreadName(), i);
        }
        for(LogView logView : logViewList){
            threadViewList.get(
                    map.get(logView.getThreadName())
            ).getLogViewList()
                    .add(logView);
        }
        return threadViewList;
    }

    private static List<ThreadView> createEmptyThreadViews (List<LogView> logViewList) {
        Map<String, Integer> threadViewMap = new HashMap<String, Integer>();
        for(LogView logView : logViewList){
            String threadName = logView.getThreadName();
            Integer n = threadViewMap.get(threadName);
            if(n != null)
                continue;
            Integer threadNumber = 20;
            try {
                Matcher matcher = THREAD_NUMBER_PATTERN.matcher(threadName);
                if(matcher.find()) {
                    String sub = threadName.substring(matcher.start()+1, matcher.end()-1);
                    threadNumber = Integer.parseInt(sub);
                }
            } catch (Exception e) {
                System.out.println(threadName + " : " + e.toString());
            }
            threadViewMap.put(threadName, threadNumber);
        }
        List<ThreadView> emptyThreadViews = new ArrayList<ThreadView>();
        for(Map.Entry entry: threadViewMap.entrySet()) {
            String key = (String) entry.getKey();
            Integer value = (Integer) entry.getValue();
            ThreadView emptyThreadView = new ThreadView();
            emptyThreadView.setThreadName(key);
            emptyThreadView.setThreadNumber(value);
            emptyThreadViews.add(emptyThreadView);
        }
        Collections.sort(emptyThreadViews, new Comparator() {
                    public int compare(Object o1, Object o2)
                    {
                        ThreadView t1 = (ThreadView)o1;
                        ThreadView t2 = (ThreadView)o2;
                        int v = 0;
                        if (t1.getThreadNumber() < t2.getThreadNumber())
                            v = -1;
                        if (t1.getThreadNumber() > t2.getThreadNumber())
                            v = 1;
                        return v;
                    }
                }
        );
        return emptyThreadViews;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Integer getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(Integer threadNumber) {
        this.threadNumber = threadNumber;
    }

    public List<LogView> getLogViewList() {
        return logViewList;
    }

    public void setLogViewList(List<LogView> logViewList) {
        this.logViewList = logViewList;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }
}
