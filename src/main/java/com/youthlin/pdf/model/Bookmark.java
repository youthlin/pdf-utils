package com.youthlin.pdf.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.youthlin.pdf.util.PdfUtil;
import javafx.scene.control.TreeItem;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Item {
        private String title;
        private int page;
        private int x;
        private int y;
        private int z;
        private List<Item> children;

        private HashMap<String, Object> toOutline() {
            HashMap<String, Object> map = new HashMap<>();
            map.put(PdfUtil.TITLE, title);
            map.put(PdfUtil.ACTION, PdfUtil.GOTO);
            map.put(PdfUtil.PAGE, String.format("%d %s %d %d %d", page, PdfUtil.XYZ, x, y, z));
            if (children != null && !children.isEmpty()) {
                map.put(PdfUtil.KIDS, children.stream().map(Item::toOutline).collect(Collectors.toList()));
            }
            return map;
        }

        public TreeItem<TreeTableBookmarkItem> toTreeItem() {
            TreeItem<TreeTableBookmarkItem> item = new TreeItem<>();
            item.setValue(new TreeTableBookmarkItem(title, page, x, y, z));
            if (children != null) {
                for (Item child : children) {
                    item.getChildren().add(child.toTreeItem());
                }
            }
            return item;
        }

        public static Bookmark.Item ofTreeItem(TreeItem<TreeTableBookmarkItem> treeItem) {
            Bookmark.Item bookmarkItem = new Bookmark.Item();
            bookmarkItem.setTitle(treeItem.getValue().getTitle());
            bookmarkItem.setPage(treeItem.getValue().getPage());
            bookmarkItem.setX(treeItem.getValue().getX());
            bookmarkItem.setY(treeItem.getValue().getY());
            bookmarkItem.setZ(treeItem.getValue().getZ());
            if (!treeItem.getChildren().isEmpty()) {
                bookmarkItem.setChildren(new ArrayList<>(treeItem.getChildren().size()));
            }
            for (TreeItem<TreeTableBookmarkItem> child : treeItem.getChildren()) {
                bookmarkItem.getChildren().add(ofTreeItem(child));
            }
            return bookmarkItem;
        }
    }

    public List<HashMap<String, Object>> toOutlines() {
        return bookmarkItems.stream()
                .map(Item::toOutline)
                .collect(Collectors.toList());
    }

}
