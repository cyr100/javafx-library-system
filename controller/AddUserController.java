package com.library.system.controller;

import com.library.system.dao.UserDAO;
import com.library.system.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class AddUserController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private ChoiceBox<String> roleChoice;
    @FXML
    private RadioButton activeRadio;
    @FXML
    private RadioButton inactiveRadio;
    @FXML
    private Button cancelButton;

    @FXML
    private ImageView imgAvatar;

    @FXML
    private Button btnUploadAvatar;

    private String avatarPath = null;
    private static final String AVATAR_DIR = System.getProperty("user.dir")+"/avatars/";
    @FXML
    private Text txtUserHeader;

    private ToggleGroup statusGroup;

    private MainController mainController;
    private User editingUser = null;
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        // 初始化时设置默认头像
        File defaultAvatar = new File(AVATAR_DIR + "default_avatar.jpg");
        if (defaultAvatar.exists()) {
            imgAvatar.setImage(new Image(defaultAvatar.toURI().toString()));
        }
        statusGroup = new ToggleGroup();
        activeRadio.setToggleGroup(statusGroup);
        inactiveRadio.setToggleGroup(statusGroup);
        activeRadio.setSelected(true);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUserForEdit(User user) {
        this.editingUser = user;
        txtUserHeader.setText("编辑用户");
        usernameField.setText(user.getUsername());
        usernameField.setDisable(true); // 用户名不可修改
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        roleChoice.setValue(user.getRole());
        
        if (user.getStatus().intValue() ==1) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }
        
        // 加载用户头像
        avatarPath = user.getAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = new File(avatarPath);
            if (avatarFile.exists()) {
                imgAvatar.setImage(new Image(avatarFile.toURI().toString()));
            } else {
                // 头像文件不存在时显示默认头像
                File defaultAvatar = new File(AVATAR_DIR + "default_avatar.png");
                if (defaultAvatar.exists()) {
                    imgAvatar.setImage(new Image(defaultAvatar.toURI().toString()));
                }
            }
        }
        
        // 编辑时密码可为空（不修改密码）
        passwordField.setPromptText("不修改密码请留空");
        this.editingUser = user;
        txtUserHeader.setText("编辑用户");
        usernameField.setText(user.getUsername());
        usernameField.setDisable(true); // 用户名不可修改
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        roleChoice.setValue(user.getRole());
        
        if (user.getStatus().intValue() ==1) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }
        // 编辑时密码可为空（不修改密码）
        passwordField.setPromptText("不修改密码请留空");
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
                String username = usernameField.getText();
                if (username.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "错误", "请先输入用户名");
                    return;
                }

                // 创建头像目录（如果不存在）
                File avatarDir = new File(AVATAR_DIR);
                if (!avatarDir.exists()) {
                    avatarDir.mkdirs();
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
                showAlert(Alert.AlertType.ERROR, "错误", "头像上传失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleChoice.getValue();
        Integer status = activeRadio.isSelected() ? 1 : 0;

        // 基本验证
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "错误", "请填写所有必填字段");
            return;
        }

        // 新建用户时密码必填
        if (editingUser == null && password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "错误", "请输入密码");
            return;
        }

        try {
            // 检查用户名是否已存在
            if (userDAO.usernameExists(username, editingUser != null ? editingUser.getId() : -1)) {
                showAlert(Alert.AlertType.ERROR, "错误", "用户名已存在");
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setRole(role);
            user.setStatus(status);
            // 设置头像路径
            if (avatarPath != null) {
                user.setAvatarPath(avatarPath);
            } else if (editingUser != null) {
                // 编辑用户时保留原头像
                user.setAvatarPath(editingUser.getAvatarPath());
            }

            // 仅在密码不为空时更新密码
            if (!password.isEmpty()) {
                user.setPassword(password); // 注意：实际应用中应加密存储密码
            }

            if (editingUser != null) {
                user.setId(editingUser.getId());
                // 如果密码为空，则使用原密码
                if (password.isEmpty()) {
                    user.setPassword(editingUser.getPassword());
                }
                userDAO.updateUser(user);
                showAlert(Alert.AlertType.INFORMATION, "成功", "用户更新成功");
            } else {
                userDAO.addUser(user);
                showAlert(Alert.AlertType.INFORMATION, "成功", "用户添加成功");
            }

            // 关闭窗口并刷新用户列表
            if (mainController != null) {
                mainController.loadUsers();
            }
            ((Stage) usernameField.getScene().getWindow()).close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "数据库错误", "操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        ((Stage) usernameField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}