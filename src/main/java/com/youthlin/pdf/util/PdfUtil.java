package com.youthlin.pdf.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.youthlin.pdf.model.Bookmark;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author youthlin.chen
 * @date 2019-10-12 15:27
 */
public class PdfUtil {
    public static void addBookmark(String srcFile, String pass, Bookmark bookmark, String outFile) {
        try {
            addOutline(srcFile, pass, bookmark.toOutlines(), outFile);
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(__("Add bookmarks error"), e);
        }
    }

    private static void addOutline(String srcFile, String pass,
            List<HashMap<String, Object>> outlines, String outFile) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(srcFile, pass.getBytes());
        PdfStamper pdfStamper = new PdfStamper(reader, new FileOutputStream(outFile));
        pdfStamper.setOutlines(outlines);
        pdfStamper.close();
    }

}
