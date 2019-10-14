package com.youthlin.pdf.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

/**
 * @author : youthlin.chen @ 2019-10-14 22:15
 */
@Data
@AllArgsConstructor
public class FileListItem {
    private String fullPath;
    private String name;
    private byte[] pass;

    public File toFile() {
        return new File(fullPath);
    }

    public static FileListItem fromFile(File file) {
        return new FileListItem(file.getAbsolutePath(), file.getName(), null);
    }
}
