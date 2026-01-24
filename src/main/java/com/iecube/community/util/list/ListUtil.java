package com.iecube.community.util.list;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ListUtil {

    /**
     * 检查List中所有元素的指定属性值是否全部相同
     * @param list 要检查的集合
     * @param propertyExtractor 提取元素属性的函数（指定要检查的属性）
     * @param <T> 集合元素类型
     * @param <V> 属性值类型
     * @return 所有元素属性值都相同返回true，否则返回false
     */
    public static <T, V> boolean isAllAttributesSame(List<T> list, Function<T, V> propertyExtractor) {
        // 处理空列表或只有一个元素的情况
        if (list == null || list.size() <= 1) {
            return true;
        }

        // 获取第一个元素的属性值作为基准
        V baseValue = propertyExtractor.apply(list.get(0));

        // 遍历剩余元素，逐一对比属性值
        for (int i = 1; i < list.size(); i++) {
            T currentElement = list.get(i);
            V currentValue = propertyExtractor.apply(currentElement);

            // 对比当前值与基准值（处理null值情况）
            if (!Objects.equals(baseValue, currentValue)) {
                return false;
            }
        }
        // 所有元素属性值都相同
        return true;
    }
}
