package com.youthlin.pdf.util;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author : youthlin.chen @ 2019-10-12 23:29
 */
public class FxUtil {
    public static final Image ICON = new Image(FxUtil.class.getResourceAsStream("/icon.png"));
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void showAlertWithException(String info, Exception e) {
        Platform.runLater(() -> showAlert(buildAlert(info, e)));
    }

    public static void showAlert(String info) {
        Platform.runLater(() -> showAlert(buildAlert(info)));
    }

    private static void showAlert(Alert alert) {
        if (Platform.isFxApplicationThread()) {
            alert.show();
        } else {
            Platform.runLater(alert::show);
        }
    }

    /**
     * buildAlert 需要在 Fx 线程中调用
     */
    private static Alert buildAlert(String text, Exception ex) {
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
    private static Alert buildAlert(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, info);
        addIcon(alert);
        alert.setHeaderText(__("Note"));
        return alert;
    }

    public static void runBackground(String startText, String doneText, Runnable runnable) {
        MyDialog<ButtonType, GridPane> dialog = new MyDialog<>(new GridPane());
        dialog.getContextWrap().add(new ProgressIndicator(), 0, 0);
        dialog.getContextWrap().add(new Text(startText), 1, 0);
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.show();
        CompletableFuture.runAsync(runnable, EXECUTOR)
                .thenAccept(ignore -> Platform.runLater(() -> {
                    dialog.getContextWrap().getChildren().clear();
                    dialog.getContextWrap().add(new Text(doneText), 0, 0);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                }));
    }


    public static Dialog<String> buildPasswordDialog(String text) {
        // https://stackoverflow.com/a/53825771
        MyDialog<String, GridPane> dialog = new MyDialog<>();
        PasswordField passwordField = new PasswordField();
        dialog.getContextWrap().add(new Text(text), 0, 0, 2, 1);
        dialog.getContextWrap().add(new Label(__("Input password")), 0, 1);
        dialog.getContextWrap().add(passwordField, 1, 1);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
        Platform.runLater(passwordField::requestFocus);
        return dialog;
    }

    private static class MyDialog<T, N extends Node> extends Dialog<T> {
        private Node node;

        MyDialog() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setMaxWidth(Double.MAX_VALUE);
            grid.setAlignment(Pos.CENTER_LEFT);
            this.node = grid;
            init();
        }

        MyDialog(N node) {
            this.node = node;
            init();
        }

        private void init() {
            setTitle(__("Note"));
            getDialogPane().setContent(node);
            addIcon(this);
        }

        @SuppressWarnings("unchecked")
        private N getContextWrap() {
            return (N) node;
        }
    }

    private static void addIcon(Dialog dialog) {
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(ICON);
        setOnMouseScreen(stage);
    }

    public static void setOnMouseScreen(Window stage) {
        // https://stackoverflow.com/a/25734893
        stage.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> {
            Point point = MouseInfo.getPointerInfo().getLocation();
            if (point != null) {
                for (Screen screen : Screen.getScreens()) {
                    Rectangle2D visualBounds = screen.getVisualBounds();
                    if (visualBounds.contains(point.getX(), point.getY())) {
                        stage.setX(visualBounds.getMinX() + ((visualBounds.getMaxX() - visualBounds.getMinX()) / 2));
                        stage.setY(visualBounds.getMinY() + ((visualBounds.getMaxY() - visualBounds.getMinY()) / 2));
                        stage.centerOnScreen();
                        return;
                    }
                }
            }
        });
    }

}
