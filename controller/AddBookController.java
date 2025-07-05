package com.library.system.controller;

import com.library.system.model.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;

public class AddBookController {

    @FXML
    private Text txtHeader;
    
    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtAuthor;

    @FXML
    private TextField txtIsbn;

    @FXML
    private TextField txtCategory;

    @FXML
    private DatePicker dpPublishDate;

    @FXML
    private CheckBox chkAvailable;

//    @FXML
//    private TextField txtImagePath;
private String imgPath = null;

    @FXML
    private ImageView imgBook;

    private MainController mainController;
    private Book currentBook;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void setBookForEdit(Book book) {
        this.currentBook = book;
        txtHeader.setText("修改图书");
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtIsbn.setText(book.getIsbn());
        txtCategory.setText(book.getCategory());
        dpPublishDate.setValue(book.getPublishDate());
        chkAvailable.setSelected(book.isAvailable());
        // 加载用户头像
        imgPath = book.getImagePath();
        if (imgPath != null && !imgPath.isEmpty()) {
            File avatarFile = new File(imgPath);
            if (avatarFile.exists()) {
                imgBook.setImage(new Image(avatarFile.toURI().toString()));
            }
        }
        //txtImagePath.setText(book.getImagePath());
    }

    @FXML
    void handleSaveBook() {
        boolean isEditMode = txtHeader.getText().equals("修改图书");
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String isbn = txtIsbn.getText();
        String category = txtCategory.getText();
        LocalDate publishDate = dpPublishDate.getValue();
        boolean available = chkAvailable.isSelected();
        String imagePath = null;
        // 设置路径
        if (imgPath != null) {
            imagePath = imgPath;
        } else if (currentBook != null) {
            // 编辑用户时保留原头像
            imagePath = currentBook.getImagePath();
        }

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty() || publishDate == null) {
            showAlert("错误", "请填写所有必填字段");
            return;
        }

        Book book = isEditMode ? currentBook : new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setPublishDate(publishDate);
        book.setAvailable(available);
        book.setImagePath(imagePath);

        // 根据模式执行添加或更新
        if (isEditMode) {
            mainController.getBookDAO().updateBook(book);
        } else {
            mainController.getBookDAO().addBook(book);
        }
        
        // 刷新主界面图书列表
        mainController.refreshBookList();
        
        // 关闭对话框
        ((Stage) txtTitle.getScene().getWindow()).close();
    }
    
    @FXML
    void handleSelectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图书封面");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(txtTitle.getScene().getWindow());
        if (selectedFile != null) {
            // 将图片复制到项目目录下的images文件夹
            try {
                Path imagesDir = Paths.get("bkimages");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }
                
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destPath = imagesDir.resolve(fileName);
                Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
//                txtImagePath.setText(destPath.toString());
                // 显示头像预览
                Image image = new Image(selectedFile.toURI().toString());
                imgBook.setImage(image);
                imgPath = destPath.toString();
            } catch (IOException e) {
                showAlert("错误", "图片保存失败: " + e.getMessage());
            }
        }
}
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleCancel(ActionEvent actionEvent) {
        // 确认取消
        boolean isEditMode = txtHeader.getText().equals("修改图书");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认取消");
        alert.setHeaderText(null);
        if(isEditMode){
            alert.setContentText("确定要取消修改图书吗？所有输入将丢失。");
        }else {
            alert.setContentText("确定要取消添加图书吗？所有输入将丢失。");
        }

        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 关闭窗口
            ((Stage)txtTitle.getScene().getWindow()).close();
        }
    }

}