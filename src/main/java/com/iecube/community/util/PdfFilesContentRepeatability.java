package com.iecube.community.util;


import com.iecube.community.model.duplicate_checking.dto.Similarity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfFilesContentRepeatability {

    // 匹配所有的非汉字
    private static final Pattern NON_CHINESE_CHAR_PATTERN = Pattern.compile("[^\\p{IsHan}]");

    //分割字符的长度
    private static final Integer CharSize=32;

    /**
     * 获取pdf文件的文本内容
     * @param file
     * @return
     */
    private static String extractTextFromPDF(File file) {
        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 预处理文本内容，去除所有的非汉字
     * @param text
     * @return
     */
    private static String preprocessText(String text) {
        Matcher matcher = NON_CHINESE_CHAR_PATTERN.matcher(text);
        return matcher.replaceAll("").toLowerCase().trim();
    }

    private static String getFileTypeByExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "Unknown";
    }

    private static HashSet<String> getTextHashSet(String text){
        HashSet<String> hashSet = new HashSet<>();
        int startIndex = 0;
        while(startIndex<text.length()){
            int endIndex = Math.min(startIndex+CharSize, text.length());
            String content = text.substring(startIndex,endIndex);
            hashSet.add(content);
//            hashSet.add(content.hashCode());
            startIndex += CharSize;
        }
        return hashSet;
    }

    /**
     * fileA 对照 fileB， fileA的重复率为 double res
     * @param pdfFileA pdf文件 要计算重复率的文件
     * @param pdfFileB pdf文件 A 和 B 对比， A有百分之多少 是和B重复的
     * @return
     */
    public static Similarity getSimilarity(File pdfFileA, File pdfFileB) {
        Similarity similarity = new Similarity();
        // 判断文件是不是pdf文件
        if(!(getFileTypeByExtension(pdfFileA.getName()).equals("pdf") && getFileTypeByExtension(pdfFileA.getName()).equals("pdf"))){
            similarity.setSimilarity(0);
            return similarity;
        }
        String textA = preprocessText(extractTextFromPDF(pdfFileA));
        String textB = preprocessText(extractTextFromPDF(pdfFileB));
//        String textA = extractTextFromPDF(pdfFileA);
//        String textB = extractTextFromPDF(pdfFileB);
        HashSet<String> hashSet1 = getTextHashSet(textA);
        HashSet<String> hashSet2 = new HashSet<>();

        for(int i=0; i<CharSize; i++){
            HashSet<String> hashSetB = getTextHashSet(textB);
            hashSet2.addAll(hashSetB);
            if(textB!=null && textB.length()>1){
                textB = textB.substring(1);
            }

        }
        // 分母  所求文件的总长度
        HashSet<String> union = new HashSet<>(hashSet1);
        // 重复的内容 的 集合
        HashSet<String> intersection = new HashSet<>(hashSet1);
        intersection.retainAll(hashSet2);
        double res = (double) intersection.size() / (double) union.size() * 100;
        similarity.setContent(intersection.toString());
        similarity.setSimilarity(res);
        return similarity;
    }
}
