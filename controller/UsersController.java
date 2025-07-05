package com.library.system.controller;

import com.library.system.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UsersController {

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TableColumn<User, Integer> colUserId;

    @FXML
    private TableColumn<User, String> colUsername;

    @FXML
    private TableColumn<User, String> colFullName;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colRole;



    private ObservableList<User> userList = FXCollections.observableArrayList();

    public void initialize(List<User> users) {
        initializeTableView();
        loadUsers(users);
    }

    private void initializeTableView() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableViewUsers.setItems(userList);
    }

    private void loadUsers(List<User> users) {
        userList.clear();
        userList.addAll(users);
    }
}    