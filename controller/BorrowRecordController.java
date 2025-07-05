package com.library.system.controller;

import com.library.system.dao.BorrowDAO;
import com.library.system.model.BorrowRecord;
import com.library.system.model.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class BorrowRecordController {
    @FXML private TableView<BorrowRecord> tableBorrowRecords;
    @FXML private TableColumn<BorrowRecord, Integer> idColumn;
    @FXML private TableColumn<BorrowRecord, String> bookColumn;
    @FXML private TableColumn<BorrowRecord, String> borrowDateColumn;
    @FXML private TableColumn<BorrowRecord, String> dueDateColumn;
    @FXML private TableColumn<BorrowRecord, String> statusColumn;
    @FXML private TableColumn<BorrowRecord, String> returnDateColumn;
    
    @FXML private TextField txtSearchRecord;
    @FXML private ChoiceBox<String> borrowPageSizeChoice;
    @FXML private Button prevBorrowPageButton;
    @FXML private Button nextBorrowPageButton;
    @FXML private Label borrowPageInfoLabel;

    private User currentUser;
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 0;
    private String searchKeyword = "";

    public void initialize() {
        // 初始化列绑定
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<> ("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory("returnDate"));
        
        // 初始化分页控件
        borrowPageSizeChoice.setValue("10");
        borrowPageSizeChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pageSize = Integer.parseInt(newVal);
                currentPage = 1;
                loadBorrowRecords();
            }
        });
        
//        txtSearchRecord.setOnAction(e -> {
//            searchKeyword = txtSearchRecord.getText().trim();
//            currentPage = 1;
//            loadBorrowRecords();
//        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadBorrowRecords();
    }

    private void loadBorrowRecords() {
        BorrowDAO borrowDAO = new BorrowDAO();
        try {
            List<BorrowRecord> records = borrowDAO.getBorrowRecordsByPageAndUserId(currentUser.getId(), currentPage, pageSize, searchKeyword);
            int totalRecords = borrowDAO.getTotalBorrowRecordCountByUserId(currentUser.getId(), searchKeyword);
            totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            tableBorrowRecords.setItems(FXCollections.observableArrayList(records));
            updatePageInfo();
            updateNavigationButtons();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("加载借阅记录失败", "无法获取借阅数据: " + e.getMessage());
        }
    }
    
    private void updatePageInfo() {
        borrowPageInfoLabel.setText("第 " + currentPage + " 页，共 " + totalPages + " 页");
    }
    
    private void updateNavigationButtons() {
        prevBorrowPageButton.setDisable(currentPage <= 1);
        nextBorrowPageButton.setDisable(currentPage >= totalPages);
    }
    
    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadBorrowRecords();
        }
    }
    
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadBorrowRecords();
        }
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        ((Stage) tableBorrowRecords.getScene().getWindow()).close();
    }

    @FXML
    public void handleSearchRecord(ActionEvent actionEvent) {
        searchKeyword = txtSearchRecord.getText().trim();
        currentPage = 1;
        loadBorrowRecords();
    }

    @FXML
    public void handleResetSearch(ActionEvent actionEvent) {
        txtSearchRecord.setText("");
        currentPage =1;
        loadBorrowRecords();
    }
}