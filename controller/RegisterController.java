package com.library.system.controller;

import com.library.system.dao.UserDAO;
import com.library.system.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RegisterController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtEmail;

    @FXML
    private Label lblError;

    @FXML
    private ImageView imgAvatar;

    @FXML
    private Button btnUploadAvatar;

    private String avatarPath = null;
    private static final String AVATAR_DIR =  System.getProperty("user.dir")+"/avatars/";

    private UserDAO userDAO = new UserDAO();


    @FXML
    public void initialize() {
        // 初始化时设置默认头像
        File defaultAvatar = new File(AVATAR_DIR + "default_avatar.jpg");
        if (defaultAvatar.exists()) {
            imgAvatar.setImage(new Image(defaultAvatar.toURI().toString()));
        }
    }
    @FXML
    void handleAvatarUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择头像");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(btnUploadAvatar.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // 检查用户名是否已输入
                String username = txtUsername.getText();
                if (username.isEmpty()) {
                    lblError.setText("请先输入用户名");
                    return;
                }

                // 创建头像目录（如果不存在）
                Path avatarDir = Paths.get(AVATAR_DIR);
                if (!Files.exists(avatarDir)) {
                    Files.createDirectories(avatarDir);
                }

                // 生成唯一文件名（用户名+时间戳+原扩展名）
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String uniqueFileName = username + "_" + System.currentTimeMillis() + extension;
                File destFile = new File(AVATAR_DIR + uniqueFileName);

                // 复制文件到头像目录
                try (InputStream in = new FileInputStream(selectedFile);
                     OutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }

                // 设置头像路径
                avatarPath = "avatars/" + uniqueFileName;

                // 显示头像预览
                Image image = new Image(selectedFile.toURI().toString());
                imgAvatar.setImage(image);

            } catch (Exception e) {
                lblError.setText("头像上传失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String fullName = txtFullName.getText();
        String email = txtEmail.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            fullName.isEmpty() || email.isEmpty()) {
            lblError.setText("请填写所有字段");
            return;
        }

        if (!password.equals(confirmPassword)) {
            lblError.setText("两次输入的密码不一致");
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            lblError.setText("用户名已存在");
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setRole("USER");
        newUser.setStatus(1);
        newUser.setAvatarPath(avatarPath);

        try {
            userDAO.addUser(newUser);
            showAlert("注册成功", "用户注册成功！");
            closeWindow();
        } catch (Exception e) {
            lblError.setText("注册失败: " + e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
}