package com.iecube.community.util.LogParsing;

import com.google.gson.Gson;
import com.iecube.community.model.resource.entity.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LogParser {
    /**
     *
     * @param
     * @return
     */
    public static List parse(List<String> dataList){
        List result = parseCategoriesAndData(dataList);
        return result;
    }

    public static String operationsTime(List<String> dataList){
        List<OperationCount> operationCountList = new ArrayList<>();
        List<String> operationList = new ArrayList<>();
        for(String item : dataList){
            Pattern patternPanelName = Pattern.compile("\\] (.*?)\\-");
            Matcher matcherPanelName = patternPanelName.matcher(item);
            if(matcherPanelName.find()){
                String operation = matcherPanelName.group(1);
                operationList.add(operation);
            }
        }
        // 创建一个HashMap来统计每个元素的出现次数
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String element : operationList) {
            frequencyMap.put(element, frequencyMap.getOrDefault(element, 0) + 1);
        }
        // 打印每个元素及其出现次数
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            OperationCount operationCount = new OperationCount();
            operationCount.setName(entry.getKey());
            operationCount.setCount(entry.getValue());
            operationCountList.add(operationCount);
        }
        Gson gson = new Gson();
        return gson.toJson(operationCountList);
    }

    public static List<String> parseLog(String filePath) {
        List<String> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "GBK"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String res = line.replaceAll("([Ii])(-)([Vv])", "IV");
//                System.out.println(res);
                if(!res.contains("程序启动")){
                    dataList.add(res);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     *
     * @param dataList 日志按行装换为列表
     * @return 一个列表，list[0]是 JSON：categories， list[1]是 JSON：data
     */
    private static List parseCategoriesAndData(List<String> dataList) {
        List<String> color = new ArrayList<>();
        color.add("#5470c6");
        color.add("#91cc75");
        color.add("#fac858");
        color.add("#ee6666");
        color.add("#73c0de");
        color.add("#3ba272");
        color.add("#fc8452");
        color.add("#9a60b4");
        color.add("#ea7ccc");
        //该日志中的面板
        List<String> panels = new ArrayList<>();
        // 该日志中的操作信息
        List deviceLogDataList = new ArrayList<>();
        for(int i=0; i<dataList.size(); i++){
            String item = dataList.get(i);
            DeviceLogData data = new DeviceLogData();
            Pattern patternPanelName = Pattern.compile("\\] (.*?)\\-");
            Matcher matcherPanelName = patternPanelName.matcher(item);
            Pattern timePattern = Pattern.compile("\\[(.*?)\\]");
            Integer index=0;
            if (matcherPanelName.find()) {
                String panelName = matcherPanelName.group(1); // 使用 group(1) 获取匹配到的内容，而不是包含括号的整个匹配项
//                System.out.println(panelName);
                data.setName(panelName);
                if(!panels.contains(panelName)){
                    panels.add(panelName);
                    index = panels.indexOf(panelName);
                }else {
                    index = panels.indexOf(panelName);
                }
            }
            List valueList  = new ArrayList<>();
            // index
            valueList.add(index);
            //开始时间
            Matcher MatcherStartTime = timePattern.matcher(item);
            String startTime = "";
            if (MatcherStartTime.find()){
                startTime = MatcherStartTime.group(1);
            }
            valueList.add(startTime);
            // 结束时间
            if(i+1<dataList.size()){
                Matcher MatcherEndTime = timePattern.matcher(dataList.get(i+1));
                String endTime = "";
                if(MatcherEndTime.find()){
                    endTime = MatcherEndTime.group(1);
                }
                valueList.add(endTime);
            }else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try{
                    Date startDate = dateFormat.parse(startTime);
                    long endTimestamp = startDate.getTime()+1000;
                    String endDate = dateFormat.format(new Date(endTimestamp));
                    valueList.add(endDate);
                }catch (ParseException e) {
                    log.error("日期字符串格式不正确：" + e.getMessage());
                }
            }
            // 操作详情
            Pattern detailPattern = Pattern.compile("-.*");
            Matcher detailMatcher = detailPattern.matcher(item);

            if (detailMatcher.find()) {
                valueList.add(detailMatcher.group().replaceAll("-",""));
            }
            data.setValue(valueList);
            deviceLogDataList.add(data);
            // itemStyle
            ItemStyle itemStyle = new ItemStyle();
            itemStyle.setColor(color.get(index));
            data.setItemStyle(itemStyle);
        }
        Gson gson = new Gson();
        String categories = gson.toJson(panels);
        String data = gson.toJson(deviceLogDataList);
        List categoriesAndData = new ArrayList();
        categoriesAndData.add(categories);
        categoriesAndData.add(data);
        return categoriesAndData;
    }

}
