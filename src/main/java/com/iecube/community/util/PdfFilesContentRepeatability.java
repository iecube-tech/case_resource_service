package com.iecube.community.util;


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
    private static final Integer CharSize=16;

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
            System.out.println(text);
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

    private static HashSet<Integer> getTextHashSet(String text){
        HashSet<Integer> hashSet = new HashSet<>();
        int startIndex = 0;
        while(startIndex<text.length()){
            int endIndex = Math.min(startIndex+CharSize, text.length());
            String content = text.substring(startIndex,endIndex);
            char[] charArray = content.toCharArray();
            for (char c : charArray){
                hashSet.add((int) c);
            }
            startIndex += CharSize;
        }
        return hashSet;
    }

    public double getSimilarity(File pdfFileA, File pdfFileB) {
        // 判断文件是不是pdf文件
        if(!(getFileTypeByExtension(pdfFileA.getName()).equals("pdf") && getFileTypeByExtension(pdfFileA.getName()).equals("pdf"))){
            return 0;
        }
        String textA = preprocessText(extractTextFromPDF(pdfFileA));
        String textB = preprocessText(extractTextFromPDF(pdfFileB));
//        String textA = extractTextFromPDF(pdfFileA);
//        String textB = extractTextFromPDF(pdfFileB);
        HashSet<Integer> hashSet1 = getTextHashSet(textA);
        HashSet<Integer> hashSet2 = getTextHashSet(textB);

        // 分母   两个文本hash后 长度 之和
        HashSet<Integer> union = new HashSet<>(hashSet1);
        union.addAll(hashSet2);

        // 重复的内容 的 集合
        HashSet<Integer> intersection = new HashSet<>(hashSet1);
        intersection.retainAll(hashSet2);

        double similarity = (double) intersection.size() / (double) union.size() * 100;
        return similarity;
    }
}
