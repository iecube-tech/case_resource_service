package com.iecube.community.util.random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    /**
     * 在指定整数区间内生成指定数量的不重复随机整数
     * @param min 区间最小值（包含）
     * @param max 区间最大值（包含）
     * @param count 要生成的随机数数量
     * @return 包含不重复随机整数的列表
     * @throws IllegalArgumentException 当参数不合法时抛出异常
     */
    public static List<Integer> generateUniqueRandomNumbers(int min, int max, int count) {
        // 1. 基础参数校验
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }

        // 2. 关键校验：可生成的不重复数总数 = max - min + 1
        int totalAvailableNumbers = max - min + 1;
        if (count > totalAvailableNumbers) {
            throw new IllegalArgumentException(
                    String.format("无法生成%d个不重复的随机数，区间[%d, %d]内仅包含%d个整数",
                            count, min, max, totalAvailableNumbers)
            );
        }

        // 3. 使用Set自动去重，循环生成直到达到指定数量
        Set<Integer> uniqueNumbers = new HashSet<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        while (uniqueNumbers.size() < count) {
            // 生成[min, max]区间的随机数
            int randomNum = random.nextInt(min, max + 1);
            uniqueNumbers.add(randomNum); // Set自动忽略重复值
        }

        // 4. 将Set转换为List返回
        return new ArrayList<>(uniqueNumbers);
    }
}
