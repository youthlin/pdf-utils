package com.youthlin.pdf.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author : youthlin.chen @ 2019-10-12 23:29
 */
public class FxUtil {
    public static void showAlertWithException(Exception e) {
        showAlertWithException(null, e);
    }

    public static void showAlertWithException(String info, Exception e) {
        Platform.runLater(() -> showAlert(buildAlert(info, e)));
    }

    public static void showAlert(String info) {
        Platform.runLater(() -> showAlert(buildAlert(info)));
    }

    public static void showAlert(Alert alert) {
        if (Platform.isFxApplicationThread()) {
            alert.show();
        } else {
            Platform.runLater(alert::show);
        }
    }

    /**
     * buildAlert 需要在 Fx 线程中调用
     */
    public static Alert buildAlert(String text, Exception ex) {
        if (text != null) {
            text = text + "\r\n" + ex.getLocalizedMessage();
        } else {
            text = ex.getLocalizedMessage();
        }
        // http://code.makery.ch/blog/javafx-dialogs-official/
        Alert alert = new Alert(Alert.AlertType.ERROR, text);
        addIcon(alert);
        alert.setHeaderText(__("Error"));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label(__("The exception stacktrace was:"));
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.add(label, 0, 1);
        expContent.add(textArea, 0, 2);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().expandedProperty().addListener((invalidationListener) -> Platform.runLater(() -> {
            alert.getDialogPane().requestLayout();
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.sizeToScene();
        }));
        return alert;
    }

    /**
     * buildAlert 需要在 Fx 线程中调用
     */
    public static Alert buildAlert(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, info);
        addIcon(alert);
        alert.setHeaderText(__("Note"));
        return alert;
    }

    private static void addIcon(Alert alert) {
        //        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);
    }
}
