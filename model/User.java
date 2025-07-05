package com.library.system.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role; // "管理员", "员工", "借阅人"
    private Integer  status; // "启用", "禁用"
    private String avatarPath; // 用户头像路径

    // 构造函数、getter和setter方法
    public User() {}

    public User(int id, String username, String password, String fullName, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
}