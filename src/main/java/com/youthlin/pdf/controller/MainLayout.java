package com.youthlin.pdf.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;
import com.youthlin.pdf.App;
import com.youthlin.pdf.model.Bookmark;
import com.youthlin.pdf.model.TreeTableBookmarkItem;
import com.youthlin.pdf.util.FxUtil;
import com.youthlin.pdf.util.Jsons;
import com.youthlin.pdf.util.PdfUtil;
import com.youthlin.pdf.util.Strings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.youthlin.utils.i18n.Translation.__;
import static com.youthlin.utils.i18n.Translation._f;

/**
 * @author youthlin.chen
 * @date 2019-10-12 20:11
 */
@Slf4j
public class MainLayout implements Initializable {
    public Button open;
    public Label fileName;
    public Button save;
    public TreeTableView<TreeTableBookmarkItem> treeTableView;
    public Label titleLabel;
    public TextField titleInput;
    public Label pageLabel;
    public Button addButton;
    public Button addChildButton;
    public TextField pageInput;
    public Button editButton;
    public Button deleteButton;
    public TextArea jsonTextArea;
    public Label statusLabel;
    public Button transFromJson;
    public Button resetAll;
    public GridPane grid;
    public Tab bookmarkTab;
    public Tab mergeTab;
    public Tab aboutTab;
    public Tab passTab;
    public Button selectSrc;
    public Label srcFile;
    public PasswordField passwordField;
    public Label passwordLabel;
    public Button updatePassword;
    public ListView listView;
    private byte[] password;
    private Bookmark bookmark;
    private static final String FILENAME_TIP = __("Click left button to open a PDF file");
    public Stage stage;
    public App app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        open.setText(__("Open File"));
        fileName.setText(FILENAME_TIP);
        save.setText(__("Write Bookmarks"));
        titleLabel.setText(__("Title"));
        pageLabel.setText(__("Page"));
        addButton.setText(__("Add Item"));
        addChildButton.setText(__("Add Child Item"));
        editButton.setText(__("Update"));
        deleteButton.setText(__("Delete"));
        resetAll.setText(__("Reset"));
        transFromJson.setText(__("Trans From Input Json"));
        statusLabel.setText(__("Ready"));
        initTree();
    }

    private void initTree() {
        treeTableView.setRoot(new TreeItem<>());
        treeTableView.setShowRoot(false);
        TreeTableColumn<TreeTableBookmarkItem, String> titleColumn = new TreeTableColumn<>(__("Title"));
        TreeTableColumn<TreeTableBookmarkItem, Integer> pageColumn = new TreeTableColumn<>(__("Page"));
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        titleColumn.setSortable(false);
        pageColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("page"));
        pageColumn.setSortable(false);
        pageColumn.setPrefWidth(50);
        pageColumn.setMinWidth(50);
        pageColumn.setMaxWidth(50);
        ObservableList<TreeTableColumn<TreeTableBookmarkItem, ?>> columns = treeTableView.getColumns();
        columns.add(titleColumn);
        columns.add(pageColumn);
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("select changed: {} {} {}", observable, oldValue, newValue);
            if (newValue != null) {
                titleInput.setText(newValue.getValue().getTitle());
                pageInput.setText(String.valueOf(newValue.getValue().getPage()));
            } else {
                titleInput.setText("");
                pageInput.setText("");
            }
        });
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTableView.getRoot().addEventHandler(TreeItem.TreeModificationEvent.ANY, event -> updateJson());
    }

    private void updateJson() {
        bookmark = new Bookmark();
        for (TreeItem<TreeTableBookmarkItem> child : treeTableView.getRoot().getChildren()) {
            bookmark.getBookmarkItems().add(Bookmark.Item.ofTreeItem(child));
        }
        jsonTextArea.setText(Jsons.toJsonPretty(bookmark.getBookmarkItems()));
    }

    public void onOpenButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(__("Select a PDF file"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(__("PDF Files"), "*.pdf"));
        File file = fileChooser.showOpenDialog(stage);
        log.info("选择的文件:{}", file);
        if (file != null) {
            openFile(file.getAbsolutePath(), null);
        }
    }

    private void openFile(String fileName, byte[] pass) {
        try {
            PdfReader pdfReader = new PdfReader(fileName, pass);
            bookmark = PdfUtil.getBookmark(pdfReader);
            if (bookmark != null) {
                FxUtil.showAlert(
                        __("The pdf file already has bookmarks, if you continue to edit and saved to a new file, you may lost some bookmark infos(such as font style: bold, italic) on the new file."));
                buildTree(bookmark);
            }
            pdfReader.close();
            log.info("正常打开文件");
            this.fileName.setText(fileName);
            this.password = pass;
            if (pass != null) {
                statusLabel.setText(__("Pdf file opened(with password)."));
            } else {
                statusLabel.setText(__("Pdf file opened."));
            }
        } catch (BadPasswordException e) {
            password = null;
            Dialog<String> dialog = new Dialog<>();
            dialog.setContentText(__("Input password"));
            dialog.showAndWait().ifPresent(s -> openFile(fileName, s.getBytes()));
        } catch (IOException e) {
            this.password = null;
            this.fileName.setText(FILENAME_TIP);
            FxUtil.showAlertWithException(__("Can not open this file."), e);
        }
    }

    private void buildTree(Bookmark bookmark) {
        TreeItem<TreeTableBookmarkItem> root = treeTableView.getRoot();
        root.getChildren().clear();
        for (Bookmark.Item bookmarkItem : bookmark.getBookmarkItems()) {
            root.getChildren().add(bookmarkItem.toTreeItem());
        }
    }

    public void onAddButtonAction(ActionEvent actionEvent) {
        TreeTableBookmarkItem item = inputItem();
        Optional<TreeItem<TreeTableBookmarkItem>> select = selectItem();
        if (select.isPresent()) {
            TreeItem<TreeTableBookmarkItem> selectItem = select.get();
            ObservableList<TreeItem<TreeTableBookmarkItem>> list = selectItem
                    .getParent()
                    .getChildren();
            list.add(list.indexOf(selectItem) + 1, new TreeItem<>(item));
        } else {
            treeTableView.getRoot().getChildren().add(new TreeItem<>(item));
        }
        statusLabel.setText(_f("Added a item. Title: {0} Page: {1}", item.getTitle(), item.getPage()));
    }

    private TreeTableBookmarkItem inputItem() {
        String title = titleInput.getText();
        String page = pageInput.getText();
        int pageIndex = Strings.parseInt(page, 0);
        return new TreeTableBookmarkItem(title, pageIndex);
    }

    public void onAddChildButtonAction(ActionEvent actionEvent) {
        TreeTableBookmarkItem item = inputItem();
        selectItem().ifPresent(select -> {
            select.getChildren().add(new TreeItem<>(item));
            select.setExpanded(true);
            statusLabel.setText(_f("Added a child item. Title: {0} Page: {1}", item.getTitle(), item.getPage()));
        });
    }

    private Optional<TreeItem<TreeTableBookmarkItem>> selectItem() {
        return Optional.of(treeTableView).map(TreeTableView::getSelectionModel)
                .map(SelectionModel::getSelectedItem);
    }

    public void onUpdateButtonAction(ActionEvent actionEvent) {
        TreeTableBookmarkItem input = inputItem();
        selectItem().ifPresent(item -> {
            item.getValue().setTitle(input.getTitle());
            item.getValue().setPage(input.getPage());
            updateJson();
            statusLabel.setText(_f("Update a item. Title: {0} Page: {1}", input.getTitle(), input.getPage()));
            treeTableView.refresh();
        });
    }

    public void onDeleteButtonAction(ActionEvent actionEvent) {
        selectItem().ifPresent(select -> {
            TreeTableBookmarkItem delete = select.getValue();
            select.getParent().getChildren().remove(select);
            statusLabel.setText(_f("Delete a item. Title: {0} Page: {1}", delete.getTitle(), delete.getPage()));
            if (treeTableView.getRoot().getChildren().isEmpty()) {
                treeTableView.getSelectionModel().clearSelection();
            }
        });
    }

    public void onSaveButtonAction(ActionEvent actionEvent) {
        if (bookmark == null) {
            statusLabel.setText(__("No bookmarks to save."));
            return;
        }
        String srcFilePath = fileName.getText();
        if (!srcFilePath.endsWith(".pdf")) {
            statusLabel.setText(__("You should open a pdf file first."));
            return;
        }
        File srcFile = new File(srcFilePath);
        String srcFileName = srcFile.getName();
        FileChooser chooser = new FileChooser();
        chooser.setTitle(__("Save as"));
        chooser.setInitialDirectory(srcFile.getParentFile());
        chooser.setInitialFileName(srcFileName.substring(0, srcFileName.length() - ".pdf".length())
                + __("_WithBookmark") + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(__("PDF Files"), "*.pdf"));
        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            if (file.getAbsolutePath().equals(srcFilePath)) {
                FxUtil.showAlert(__("Save failed. You should save as a new file."));
                return;
            }
            PdfUtil.addBookmark(srcFilePath, password, bookmark, file.getAbsolutePath());
            statusLabel.setText(_f("Saved success as {0}.", file.getAbsolutePath()));
        } else {
            statusLabel.setText(__("Cancel save."));
        }
    }

    public void onTransButtonAction(ActionEvent actionEvent) {
        String json = jsonTextArea.getText();
        List<Bookmark.Item> bookmarks;
        if (json == null || json.trim().isEmpty()) {
            bookmarks = null;
        } else {
            bookmarks = Jsons.fromJson(json, new TypeReference<List<Bookmark.Item>>() {
            });
        }
        if (bookmarks != null) {
            bookmark = new Bookmark();
            bookmark.getBookmarkItems().addAll(bookmarks);
            buildTree(bookmark);
            statusLabel.setText(__("Success build bookmarks from json of text area."));
        } else {
            statusLabel.setText(__("Trans bookmarks from json failed."));
        }
    }

    public void onResetButtonAction(ActionEvent actionEvent) {
        if (fileName.getText().endsWith(".pdf")) {
            openFile(fileName.getText(), password);
        }
    }

    public void onSelectSrcButtonAction(ActionEvent actionEvent) {

    }

    public void onUpdatePasswordButtonAction(ActionEvent actionEvent) {

    }

    public void onGithubLinkAction(ActionEvent actionEvent) {
        app.getHostServices().showDocument(((Hyperlink) actionEvent.getSource()).getText());
    }

}
