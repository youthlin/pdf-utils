package com.youthlin.pdf.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.youthlin.pdf.model.Bookmark;
import com.youthlin.pdf.model.FileListItem;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author youthlin.chen
 * @date 2019-10-12 15:27
 */
@Slf4j
public class PdfUtil {
    public static final String ACTION = "Action";
    public static final String GOTO = "GoTo";
    public static final String XYZ = "XYZ";
    public static final String TITLE = "Title";
    public static final String PAGE = "Page";
    public static final String KIDS = "Kids";
    public static final String FILE_EXT = ".pdf";

    public static void addBookmark(String srcFile, byte[] pass, Bookmark bookmark, String outFile) {
        try {
            addOutline(srcFile, pass, bookmark.toOutlines(), outFile);
        } catch (IOException | DocumentException e) {
            FxUtil.showAlertWithException(__("Add bookmark error"), e);
        }
    }

    private static void addOutline(String srcFile, byte[] pass,
            List<HashMap<String, Object>> outlines, String outFile) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(srcFile, pass);
        PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(outFile));
        pdfStamper.setOutlines(outlines);
        pdfStamper.close();
    }

    public static Bookmark getBookmark(PdfReader reader, int height) {
        List<HashMap<String, Object>> outline = SimpleBookmark.getBookmark(reader);
        Bookmark bookmark = new Bookmark();
        if (outline != null) {
            for (HashMap<String, Object> map : outline) {
                bookmark.getBookmarkItems().add(fromMap(map, height));
            }
        }
        return bookmark;
    }

    @SuppressWarnings("unchecked")
    private static Bookmark.Item fromMap(Map<String, Object> map, int height) {
        Bookmark.Item item = new Bookmark.Item();
        Object title = map.get(TITLE);
        Object page = map.get(PAGE);
        if (title instanceof String && page instanceof String) {
            String p = (String) page;
            item.setTitle((String) title);
            item.setPage(Integer.parseInt(p.split("\\s")[0]));
            int index = p.indexOf(XYZ);
            if (index > 0) {
                p = p.substring(index + XYZ.length());
                String[] xyz = p.split("\\s");
                if (xyz.length == 3) {
                    item.setX(Strings.parseInt(xyz[0], 0));
                    item.setY(Strings.parseInt(xyz[1], height));
                    item.setZ(Strings.parseInt(xyz[2], 0));
                }
            }
        }
        if (item.getY() == 0) {
            item.setX(0);
            item.setY(height);
            item.setZ(0);
        }
        Object kids = map.get(KIDS);
        if (kids instanceof List) {
            List<Bookmark.Item> childItems = new ArrayList<>(((List) kids).size());
            item.setChildren(childItems);
            for (Map<String, Object> child : (List<Map<String, Object>>) kids) {
                childItems.add(fromMap(child, height));
            }
        }
        return item;
    }

    public static void addPassword(PdfReader reader, byte[] password,
            String outFile) throws IOException, DocumentException {
        PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(outFile));
        pdfStamper.setEncryption(password, password, PdfWriter.ALLOW_COPY, false);
        pdfStamper.close();
    }

    public static void removePassword(PdfReader reader, String outFile) throws IOException, DocumentException {
        PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(outFile));
        pdfStamper.close();
    }

    public static void merge(File outFile, List<FileListItem> list,
            boolean fileNameAsBookmark) {
        try {
            Document document = new Document();
            PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(outFile));
            document.open();
            Bookmark bookmark = new Bookmark();
            int index = 1;
            for (FileListItem item : list) {
                PdfReader pdfReader = new PdfReader(item.getFullPath(), item.getPass());
                int pages = pdfReader.getNumberOfPages();
                for (int i = 1; i <= pages; i++) {
                    PdfImportedPage page = pdfCopy.getImportedPage(pdfReader, i);
                    pdfCopy.addPage(page);
                }
                if (fileNameAsBookmark) {
                    int height = (int) pdfReader.getPageSize(1).getHeight();
                    bookmark.getBookmarkItems().add(buildBookmark(item, index, height));
                }
                index += pages;
                pdfReader.close();
            }
            if (fileNameAsBookmark) {
                pdfCopy.setOutlines(bookmark.toOutlines());
            }
            document.close();
        } catch (Exception e) {
            FxUtil.showAlertWithException(__("Merge pdf files error."), e);
        }
    }

    private static Bookmark.Item buildBookmark(FileListItem fileItem, int page, int height) {
        Bookmark.Item item = new Bookmark.Item();
        String title = fileItem.getName();
        if (title.endsWith(FILE_EXT)) {
            title = title.substring(0, title.length() - FILE_EXT.length());
        }
        item.setTitle(title);
        item.setPage(page);
        item.setY(height);
        return item;
    }

}
