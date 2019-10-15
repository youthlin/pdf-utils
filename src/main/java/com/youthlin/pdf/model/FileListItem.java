package com.youthlin.pdf.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileListItem that = (FileListItem) o;
        return Objects.equals(fullPath, that.fullPath) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullPath, name);
    }
}
