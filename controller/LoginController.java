package com.library.system.controller;

import com.library.system.dao.UserDAO;
import com.library.system.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private UserDAO userDAO = new UserDAO();

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("错误", "请输入用户名和密码");
            return;
        }

        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            showAlert("错误", "用户名不存在");
            return;
        }
        if (!user.getStatus().equals(1)) {
            showAlert("错误", "用户被禁用");
            return;
        }
        if (user == null || !user.getPassword().equals(password)) {
            showAlert("登录失败", "用户名或密码错误");
            return;
        }

        // 登录成功，根据角色显示不同界面
        try {
            Stage currentStage = (Stage) txtUsername.getScene().getWindow();
            currentStage.close();

            showMainWindow(user);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "无法加载主界面");
        }
    }

    private void showMainWindow(User user) throws IOException {
        Stage mainStage = new Stage();
        mainStage.setTitle("图书管理系统 - " + user.getFullName() + " [" + user.getRole() + "]");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);
        
        MainController controller = loader.getController();
        controller.initialize(user);

        mainStage.setScene(scene);
        mainStage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void handleRegister(ActionEvent event) {
        try {
            Stage currentStage = (Stage) txtUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/RegisterView.fxml"));
            Parent root = loader.load();
            
            Stage registerStage = new Stage();
            registerStage.setTitle("用户注册");
            registerStage.setScene(new Scene(root));
            registerStage.initOwner(currentStage);
            
            currentStage.hide();
            registerStage.showAndWait();
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}