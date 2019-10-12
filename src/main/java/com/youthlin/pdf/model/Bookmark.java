package com.youthlin.pdf.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youthlin.chen
 * @date 2019-10-12 15:45
 */
@Data
public class Bookmark {
    private final List<Item> bookmarkItems = new ArrayList<>();

    @Data
    public static class Item {
        private String title;
        private int pageIndex;
        private List<Item> children;

        private HashMap<String, Object> toOutline() {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Title", title);
            map.put("Action", "GoTo");
            map.put("Page", String.format("%d XYZ", pageIndex));
            if (children != null && !children.isEmpty()) {
                map.put("Kids", children.stream().map(Item::toOutline).collect(Collectors.toList()));
            }
            return map;
        }
    }

    public List<HashMap<String, Object>> toOutlines() {
        return bookmarkItems.stream()
                .map(Item::toOutline)
                .collect(Collectors.toList());
    }

}
