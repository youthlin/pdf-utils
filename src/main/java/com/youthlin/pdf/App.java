package com.youthlin.pdf;

import com.youthlin.pdf.controller.MainController;
import com.youthlin.pdf.util.FxUtil;
import com.youthlin.utils.i18n.Translation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author youthlin.chen
 * @date 2019-10-12 15:25
 */
public class App extends Application {

    public static void main(String[] args) {
        Translation.setDft(Translation.getBundle("Message"));
        launch(App.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(FxUtil.ICON);
        primaryStage.setTitle(__("PDF Utils"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.stage = primaryStage;
        mainController.app = this;
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        FxUtil.setOnMouseScreen(primaryStage);
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }

}
