package com.iecube.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSplitExample {

    public static void main(String[] args) {
//        String input = "文本字符。。。/n<iecube>特殊文本字符</iecube>/n 文本字符。。。。/n<iecube>特殊文本字符</iecube>/n 文本字符。。。 /n<iecube>特殊文本字符</iecube>/n 文本字符。。。";
        String input = "<iecube>特殊文本字符</iecube>/n";

        // 正则表达式定义
        String regex = "/n<iecube>(.*?)</iecube>/n";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 应用正则表达式进行匹配
        Matcher matcher = pattern.matcher(input);

        // 开始切割文本
        int lastEnd = 0;
        while (matcher.find()) {
            // 打印两个特殊文本字符之间的文本
            String textBefore = input.substring(lastEnd, matcher.start());
            System.out.println("文本字符段落：" + textBefore);

            // 打印特殊文本字符
            String specialText = matcher.group(1);
            System.out.println("特殊文本字符：" + specialText);

            // 更新下一个段落的起始位置
            lastEnd = matcher.end();
        }

        // 打印最后一个特殊文本字符后的文本
        if (lastEnd < input.length()) {
            String lastText = input.substring(lastEnd);
            System.out.println("文本字符段落：" + lastText);
        }
    }
}
