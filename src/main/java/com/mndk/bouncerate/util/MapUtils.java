package com.mndk.bouncerate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {

    /**
     * <a href="https://stackoverflow.com/a/2581754">Source</a>
     */
    public static <K, V extends Comparable<? super V>> List<K> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        return list.stream().map(Map.Entry::getKey).toList();
    }
}
