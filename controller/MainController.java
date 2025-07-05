package com.library.system.controller;

import com.library.system.dao.BookDAO;
import com.library.system.dao.BorrowDAO;
import com.library.system.dao.UserDAO;
import com.library.system.model.Book;
import com.library.system.model.BorrowRecord;
import com.library.system.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private BorderPane mainPane;


//    @FXML
//    private Menu menuManage;
    @FXML
    private Menu menuBorrow;
    @FXML
    private MenuItem menuItemManageBooks;
    @FXML
    private MenuItem menuItemManageUsers;
    @FXML
    private MenuItem menuItemBorrowBook;
    @FXML
    private MenuItem menuItemReturnBook;
    @FXML
    private MenuItem menuItemViewRecords;

    @FXML
    private TableView<Book> tableViewBooks;

    @FXML
    private TableColumn<Book, Integer> colId;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, String> colAuthor;

    @FXML
    private TableColumn<Book, String> colIsbn;

    @FXML
    private TableColumn<Book, String> colCategory;

    @FXML
    private TableColumn<Book, LocalDate> colPublishDate;

    @FXML
    private TableColumn<Book, Boolean> colAvailable;


    @FXML
    private TableColumn<Book, String> colImage;

    @FXML
    private Label lblWelcome;

    @FXML
    private ImageView imgUserAvatar;

    @FXML
    private Label lblUsername;

    private User loggedInUser;
    private static final String AVATAR_DIR =  System.getProperty("user.dir") +"/avatars/";

    @FXML
    private TextField txtSearchBook;

    @FXML
    private Button btnAddBook;
    @FXML
    private Button btnDeleteBook;

    @FXML
    private Button btnEditBook;

    @FXML
    private TableColumn<Book, String> colBorrower;

    @FXML
    private TableColumn<Book, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> fullNameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Integer> statusColumn;

    @FXML
    private TableView<User> userTable;

    @FXML
    private ChoiceBox bookPageSizeChoice;
    


    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabUsers;




    @FXML
    private Tab tabBorrowRecords;

    // 添加分页相关属性和方法
    // 图书分页属性
    private int bookCurrentPage = 1;
    private int bookPageSize = 10;
    private int totalBooks = 0;

    // 用户分页属性
    private int userCurrentPage = 1;
    private int userPageSize = 10;
    private int totalUsers = 0;

    // 借阅记录分页属性
    private int borrowCurrentPage = 1;
    private int borrowPageSize = 10;
    private int totalBorrowRecords = 0;

    @FXML
    private Button btnPrevious;

    @FXML
    private Button btnNext;

    @FXML
    private Label lblPageInfo;

    @FXML
    private Button prevUserPageButton;

    @FXML
    private Button btnBorrow;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnView;


    // 借阅记录相关控件
    @FXML
    private TextField txtSearchRecord;

    @FXML
    private ChoiceBox borrowPageSizeChoice;

    @FXML
    private Button prevBorrowPageButton;

    @FXML
    private Button nextBorrowPageButton;

    @FXML
    private Label borrowPageInfoLabel;

    @FXML
    private TableView<BorrowRecord> tableViewBorrowRecords;

    @FXML
    private TableColumn<BorrowRecord, Integer> borrowRecordIdColumn;

    @FXML
    private TableColumn<BorrowRecord, String> borrowRecordBookTitleColumn;

    @FXML
    private TableColumn<BorrowRecord, String> borrowRecordUserNameColumn;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> borrowRecordBorrowDateColumn;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> borrowRecordDueDateColumn;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> borrowRecordReturnDateColumn;

    @FXML
    private TableColumn<BorrowRecord, String> borrowRecordStatusColumn;


    @FXML
    private Button nextUserPageButton;

    @FXML
    private Label userPageInfoLabel;

    @FXML
    private TextField txtSearchUser;

    @FXML
    private Button addUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button editUserButton;


    // 定义角色常量（建议放在公共常量类中）
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_STAFF = "STAFF";
    private static final String ROLE_USER = "USER";





    private User currentUser;
    private BookDAO bookDAO = new BookDAO();
    private UserDAO userDAO = new UserDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<BorrowRecord> borrowRecordList = FXCollections.observableArrayList();

    public void initialize(User user) {
        this.currentUser = user;
        this.loggedInUser = user;
        initializeTableView();
        updateUserInfoDisplay();
        loadBooks();
        initializeUserTable();
        loadUsers();
        initializeBorrowRecordTable();
        loadBorrowRecords();
        setupMenuBasedOnRole();
    }

    private void initializeTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublishDate.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colBorrower.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        colAvailable.setCellFactory(column -> new TableCell<Book, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "可借阅" : "不可借阅");
                }
            }
        });
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colImage.setCellFactory(column -> new TableCell<Book, String>() {
    private final ImageView imageView = new ImageView();
    
    @Override
    protected void updateItem(String imagePath, boolean empty) {
        super.updateItem(imagePath, empty);
        if (empty || imagePath == null || imagePath.isEmpty()) {
            setGraphic(null);
        } else {
            try {
                Image image = new Image("file:" + System.getProperty("user.dir") + "/" + imagePath);
                imageView.setImage(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                // 设置图像视图的对齐方式为居中
                imageView.setSmooth(true); // 可选：平滑缩放
                imageView.setStyle("-fx-alignment: center;"); // 居中对齐
                setGraphic(imageView);
            } catch (Exception e) {
                setGraphic(null);
                e.printStackTrace();
            }
        }
    }
});
        tableViewBooks.setItems(bookList);
    }

    private void initializeUserTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<User, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case 0:
                            setText("禁用");
                            break;
                        case 1:
                            setText("启用");
                            break;
                        default:
                            setText(item.toString());
                    }
                }
            }
        });

        // 设置角色列的单元格工厂，显示中文角色名称
        roleColumn.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case "ADMIN":
                            setText("管理员");
                            break;
                        case "STAFF":
                            setText("员工");
                            break;
                        case "USER":
                            setText("借阅人");
                            break;
                        default:
                            setText(item);
                    }
                }
            }
        });

        userTable.setItems(userList);
    }

    private void initializeBorrowRecordTable() {
        borrowRecordIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        borrowRecordBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowRecordUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        borrowRecordBorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        borrowRecordReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        borrowRecordDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        borrowRecordStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableViewBorrowRecords.setItems(borrowRecordList);
    }


    
    // 修改loadBooks方法
    private void loadBooks(String keyword) {
        bookList.clear();
        if (keyword == null || keyword.trim().isEmpty()) {
            totalBooks = bookDAO.getTotalBooksCount();
            List<Book> books = bookDAO.getBooksByPage(bookCurrentPage, bookPageSize);
            bookList.addAll(books);
        } else {
            totalBooks = bookDAO.getSearchBooksCount(keyword);
            List<Book> books = bookDAO.searchBooksByPage(keyword, bookCurrentPage, bookPageSize);
            bookList.addAll(books);
        }
        updatePaginationControls();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        updateUserInfoDisplay();
    }

    private void updateUserInfoDisplay() {
        if (loggedInUser != null) {
            lblWelcome.setText("欢迎，" + loggedInUser.getFullName());
            lblUsername.setText(loggedInUser.getFullName());

            // 加载用户头像
            String avatarPath = loggedInUser.getAvatarPath();
            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    imgUserAvatar.setImage(new Image(avatarFile.toURI().toString()));
                    return;
                }
            }

            // 如果没有头像或头像文件不存在，使用默认头像
            File defaultAvatar = new File(AVATAR_DIR + "default_avatar.jpg");
            if (defaultAvatar.exists()) {
                imgUserAvatar.setImage(new Image(defaultAvatar.toURI().toString()));
            }
        }
    }

    private void loadBooks() {
        loadBooks(null);
    }
    public void loadUsers() {
        if (loggedInUser == null) return;
        // 根据用户角色控制权限
        if ("USER".equals(loggedInUser.getRole())) {
            addUserButton.setVisible(false);
            editUserButton.setVisible(false);
            deleteUserButton.setVisible(false);
        }

        userList.clear();
        totalUsers = userDAO.getTotalUserCount(null);
        List<User> users = userDAO.getUsersByPage(userCurrentPage, userPageSize, null);
        userList.addAll(users);
        updateUserPaginationControls();
    }


    
    // 添加分页控制更新方法
    private void updatePaginationControls() {
        btnPrevious.setDisable(bookCurrentPage <= 1);
        btnNext.setDisable(bookCurrentPage >= Math.ceil(totalBooks / (double)bookPageSize));
        lblPageInfo.setText(String.format("第%d页/共%d页", bookCurrentPage, (int)Math.ceil(totalBooks / (double)bookPageSize)));
    }

    private void updateUserPaginationControls() {
        prevUserPageButton.setDisable(userCurrentPage <= 1);
        nextUserPageButton.setDisable(userCurrentPage >= Math.ceil(totalUsers / (double)userPageSize));
        userPageInfoLabel.setText(String.format("第%d页/共%d页", userCurrentPage, (int)Math.ceil(totalUsers / (double)userPageSize)));
    }
    
    // 添加分页按钮事件处理
    @FXML
    void handlePreviousPage(ActionEvent event) {
        if (bookCurrentPage > 1) {
            bookCurrentPage--;
            loadBooks();
        }
    }
    
    @FXML
    void handleNextPage(ActionEvent event) {
        if (bookCurrentPage < Math.ceil(totalBooks / (double)bookPageSize)) {
            bookCurrentPage++;
            loadBooks();
        }
    }
    
    @FXML
    void handleBookPageSizeChange(ActionEvent event) {
        try {
            String selectedSize = (String) bookPageSizeChoice.getValue();
            int newSize = Integer.parseInt(selectedSize);
            if (newSize > 0) {
                bookPageSize = newSize;
                bookCurrentPage = 1; // 重置到第一页
                loadBooks(txtSearchBook.getText().trim());
            } else {
                showAlert("错误", "无效的页码大小");
            }
        } catch (NumberFormatException e) {
            showAlert("错误", "无效的页码大小格式");
        }
    }

//    @FXML
//    void handleSearchBooks(ActionEvent event) {
//        String keyword = txtSearchBook.getText().trim();
//        bookCurrentPage = 1;
//        loadBooks(keyword);
//    }

    @FXML
    void handleResetSearch(ActionEvent event) {
        txtSearchBook.setText("");
        bookCurrentPage = 1;
        loadBooks();
    }

    @FXML
    public void handleUserResetSearch(ActionEvent actionEvent) {
        txtSearchUser.setText("");
        userCurrentPage =1;
        loadUsers();
    }

    private void setupMenuBasedOnRole() {
//        // 根据用户角色显示不同菜单
//        String role = currentUser.getRole();
//        menuItemManageBooks.setVisible(role.equals("ADMIN") || role.equals("STAFF"));
//        menuItemManageUsers.setVisible(role.equals("ADMIN"));
//        menuManage.setVisible(role.equals("ADMIN") || role.equals("STAFF"));
//        menuBorrow.setVisible(role.equals("USER"));
//        menuItemBorrowBook.setVisible(role.equals("USER"));
//        menuItemReturnBook.setVisible(role.equals("USER"));
//        menuItemViewRecords.setVisible(role.equals("USER"));
//        btnAddBook.setVisible(role.equals("ADMIN") || role.equals("STAFF"));
//        btnEditBook.setVisible(role.equals("ADMIN") || role.equals("STAFF"));
//        btnDeleteBook.setVisible(role.equals("ADMIN"));
//        if(role.equals("USER")){
//            tabPane.getTabs().remove(tabUsers);
//            tabPane.getTabs().remove(tabBorrowRecords);
//        }
//        if(role.equals("STAFF")){
//            addUserButton.setVisible(false);
//            editUserButton.setVisible(false);
//            deleteUserButton.setVisible(false);
//
//        }
        String role = currentUser != null ? currentUser.getRole() : "";

// 缓存角色判断结果
        boolean isAdmin = ROLE_ADMIN.equals(role);
        boolean isStaff = ROLE_STAFF.equals(role);
        boolean isUser = ROLE_USER.equals(role);

// 设置菜单项可见性
//        menuItemManageBooks.setVisible(isAdmin || isStaff);
//        menuItemManageUsers.setVisible(isAdmin);
//        menuManage.setVisible(isAdmin || isStaff);
//        menuBorrow.setVisible(isUser);
//        menuItemBorrowBook.setVisible(isUser);
//        menuItemReturnBook.setVisible(isUser);
//        menuItemViewRecords.setVisible(isUser);
        btnAddBook.setVisible(isAdmin || isStaff);
        btnEditBook.setVisible(isAdmin || isStaff);
        btnDeleteBook.setVisible(isAdmin);
        btnBorrow.setVisible(isUser);
        btnReturn.setVisible(isUser);
        btnView.setVisible(isUser);

// 移除用户相关 Tab（需确保 tabPane 不为 null）
        if (tabPane != null) {
            if (isUser) {
                tabPane.getTabs().remove(tabUsers);
                tabPane.getTabs().remove(tabBorrowRecords);
                if (btnAddBook.getParent() != null) {
                    ((Pane) btnAddBook.getParent()).getChildren().remove(btnAddBook);
                }
                if (btnEditBook.getParent() != null){
                    ((Pane) btnEditBook.getParent()).getChildren().remove(btnEditBook);
                }
                if (btnDeleteBook.getParent() != null) {
                    ((Pane) btnDeleteBook.getParent()).getChildren().remove(btnDeleteBook);
                }
            }
        }




// 设置用户按钮可见性（需确保按钮不为 null）
        if (isStaff) {
            if (addUserButton != null) addUserButton.setVisible(false);
            if (editUserButton != null) editUserButton.setVisible(false);
            if (deleteUserButton != null) deleteUserButton.setVisible(false);
        }
    }

    @FXML
    void handleManageBooks(ActionEvent event) {
        loadBooks(); // 刷新图书列表
    }

    @FXML
    void handleManageUsers(ActionEvent event) {
        loadUsers();
    }

    @FXML
    void handleBorrowBooks(ActionEvent event) {
        Book selectedBook = tableViewBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("错误", "请选择要借阅的图书");
            return;
        }
        
        if (!selectedBook.isAvailable()) {
            showAlert("错误", "该图书已被借出");
            return;
        }
        
        try {
            // 调用BorrowDAO借阅图书
            BorrowDAO borrowDAO = new BorrowDAO();
            if(borrowDAO.borrowBook(selectedBook.getId(), currentUser.getId())) {
                // 刷新列表
                refreshBookList();
            } else {
                showAlert("错误", "更新图书状态失败");
            }
            showAlert("成功", "图书借阅成功");
        } catch (Exception e) {
            showAlert("错误", "借阅失败: " + e.getMessage());
        }
    }

    @FXML
    void handleReturnBooks(ActionEvent event) {
        Book selectedBook = tableViewBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("错误", "请选择要归还的图书");
            return;
        }
        
        if (selectedBook.isAvailable()) {
            showAlert("错误", "该图书未被借出");
            return;
        }
        else if (!selectedBook.getBorrowerName().equals(currentUser.getFullName())) {
            showAlert("错误", "您不能归还不属于您的图书");
            return;
        }
        
        try {
            // 调用BorrowDAO归还图书
            BorrowDAO borrowDAO = new BorrowDAO();
            if(borrowDAO.returnBook(selectedBook.getId(), currentUser.getId())) {
                // 刷新列表
                refreshBookList();
            } else {
                showAlert("错误", "更新图书状态失败");
            }
            showAlert("成功", "图书归还成功");
        } catch (Exception e) {
            showAlert("错误", "归还失败: " + e.getMessage());
        }
    }

    @FXML
    void handleViewBorrowed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/BorrowRecordView.fxml"));
            Parent root = loader.load();
            // 设置控制器并传递数据
            BorrowRecordController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("我的借阅记录");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "无法打开借阅记录窗口: " + e);
        }
    }

    @FXML
    void handleAddBook(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/AddBookView.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("添加图书");
            stage.setScene(new Scene(root, 600, 400));
            
            AddBookController controller = loader.getController();
            controller.setMainController(this);
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshBookList() {
        bookCurrentPage = 1; // 重置到第一页
        loadBooks();
    }

    public BookDAO getBookDAO() {

        return new BookDAO();
    }

    @FXML
    public void handleExit(ActionEvent actionEvent) {
        // 确认退出
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认退出");
        alert.setHeaderText(null);
        alert.setContentText("确定要退出系统吗？");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 加载登录页
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/LoginView.fxml"));
                Parent root = loader.load();
                // 创建新场景
                Scene scene = new Scene(root, 800, 600);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setTitle("图书管理系统");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleAbout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("图书管理系统");
        alert.setContentText("版本: 1.0\n作者: cyr开发团队\n版权所有 © 2025");
        alert.showAndWait();
    }

    // 借阅记录分页相关方法
    @FXML
    private void loadBorrowRecords() {
        String keyword = txtSearchRecord.getText() != null ? txtSearchRecord.getText().trim() : null;
        borrowRecordList.clear();
        
        try {
            totalBorrowRecords = borrowDAO.getTotalBorrowRecordCount(keyword);
            List<BorrowRecord> records = borrowDAO.getBorrowRecordsByPage(borrowCurrentPage, borrowPageSize, keyword);
            borrowRecordList.addAll(records);
            updateBorrowPageInfo();
        } catch (Exception e) {
            showAlert("错误", "加载借阅记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBorrowPageInfo() {
        int totalPages = (int) Math.ceil(totalBorrowRecords / (double) borrowPageSize);
        prevBorrowPageButton.setDisable(borrowCurrentPage <= 1);
        nextBorrowPageButton.setDisable(borrowCurrentPage >= totalPages || totalPages == 0);
        borrowPageInfoLabel.setText(String.format("第 %d 页 / 共 %d 页", borrowCurrentPage, totalPages));
    }

    @FXML
    void handlePreviousBorrowPage(ActionEvent event) {
        if (borrowCurrentPage > 1) {
            borrowCurrentPage--;
            loadBorrowRecords();
        }
    }

    @FXML
    void handleNextBorrowPage(ActionEvent event) {
        int totalPages = (int) Math.ceil(totalBorrowRecords / (double) borrowPageSize);
        if (borrowCurrentPage < totalPages) {
            borrowCurrentPage++;
            loadBorrowRecords();
        }
    }

    @FXML
    void handleBorrowPageSizeChange(ActionEvent event) {
        try {
            String selectedSize = (String) borrowPageSizeChoice.getValue();
            int newSize = Integer.parseInt(selectedSize);
            if (newSize > 0) {
                borrowPageSize = newSize;
                borrowCurrentPage = 1; // 重置到第一页
                loadBorrowRecords();
            } else {
                showAlert("错误", "无效的页码大小");
            }
        } catch (NumberFormatException e) {
            showAlert("错误", "无效的页码大小格式");
        }
    }

    @FXML
    void handleSearchBorrowRecords(ActionEvent event) {
        borrowCurrentPage = 1;
        loadBorrowRecords();
    }

    @FXML
    void handleResetBorrowSearch(ActionEvent event) {
        txtSearchRecord.setText("");
        borrowCurrentPage = 1;
        loadBorrowRecords();
    }

    @FXML
    public void handleSearchBooks(ActionEvent actionEvent) {
        String keyword = txtSearchBook.getText();
        bookList.clear();
        if(keyword.isEmpty()) {
            loadBooks();
        } else {
            totalBooks = bookDAO.getSearchBooksCount(keyword);
        List<Book> books = bookDAO.searchBooksByPage(keyword, bookCurrentPage, bookPageSize);
            bookList.addAll(books);
            updatePaginationControls();
        }
    }

    @FXML
    void handleEditBook(ActionEvent event) {
        Book selectedBook = tableViewBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("错误", "请选择要编辑的图书");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/AddBookView.fxml"));
            Parent root = loader.load();
            
            AddBookController controller = loader.getController();
            controller.setMainController(this);
            controller.setBookForEdit(selectedBook);
            
            Stage stage = new Stage();
            stage.setTitle("编辑图书");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // 编辑完成后刷新列表
            refreshBookList();
        } catch (IOException e) {
            showAlert("错误", "加载编辑界面失败: " + e.getMessage());
        }
    }

    @FXML
    void handleDeleteBook(ActionEvent event) {
        Book selectedBook = tableViewBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("错误", "请选择要删除的图书");
            return;
        }
        
        // 确认删除对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("确定要删除这本图书吗？");
        alert.setContentText("图书: " + selectedBook.getTitle());
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 调用DAO删除图书
                bookDAO.deleteBook(selectedBook.getId());
                showAlert("成功", "图书删除成功");
                refreshBookList();
            } catch (Exception e) {
                showAlert("错误", "删除失败: " + e.getMessage());
            }
        }
    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        System.out.println(content);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void handleSearchUsers(ActionEvent actionEvent) {
        String keyword = txtSearchUser.getText();
        userList.clear();
        if(keyword.isEmpty()) {
            loadUsers();
        } else {
            totalUsers = userDAO.getTotalUserCount(keyword);
            List<User> users = userDAO.getUsersByPage(userCurrentPage, userPageSize, keyword);
            userList.addAll(users);
            updateUserPaginationControls();
        }
    }

    public void handleAddUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/AddUserView.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("添加用户");
            stage.setScene(new Scene(root));
            AddUserController controller = loader.getController();
            controller.setMainController(this);
            
            stage.showAndWait();
            loadUsers();
        } catch (IOException e) {
            showAlert("错误", "加载添加用户界面失败: " + e.getMessage());
        }
    }

    public void handleEditUser(ActionEvent actionEvent) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("错误", "请选择要编辑的用户");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/system/views/AddUserView.fxml"));
            Parent root = loader.load();
            
            AddUserController controller = loader.getController();
            controller.setMainController(this);
            controller.setUserForEdit(selectedUser);
            
            Stage stage = new Stage();
            stage.setTitle("编辑用户");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadUsers();
        } catch (IOException e) {
            showAlert("错误", "加载编辑用户界面失败: " + e.getMessage());
        }
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("错误", "请选择要删除的用户");
            return;
        }
        
        // 确认删除对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("确定要删除该用户吗？");
        alert.setContentText("用户: " + selectedUser.getUsername());
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 调用DAO删除用户
                userDAO.deleteUser(selectedUser.getId());
                showAlert("成功", "用户删除成功");
                loadUsers();
            } catch (Exception e) {
                showAlert("错误", "删除失败: " + e.getMessage());
            }
        }
    }

    public void handlePrevUserPage(ActionEvent actionEvent) {
        if (userCurrentPage > 1) {
            userCurrentPage--;
            loadUsers();
        }
    }

    public void handleNextUserPage(ActionEvent actionEvent) {
        if (userCurrentPage < Math.ceil(totalUsers / (double)userPageSize)) {
            userCurrentPage++;
            loadUsers();
        }
    }

    public void handleUserPageSizeChange(ActionEvent actionEvent) {
        ChoiceBox<String> comboBox = (ChoiceBox<String>) actionEvent.getSource();
        String selectedSize = comboBox.getValue();
        if (selectedSize != null && !selectedSize.isEmpty()) {
            try {
                int newPageSize = Integer.parseInt(selectedSize);
                if (newPageSize > 0) {
                    userPageSize = newPageSize;
                    userCurrentPage = 1; // 重置到第一页
                    loadUsers();
                }
            } catch (NumberFormatException e) {
                showAlert("错误", "无效的页码大小");
            }
        }
    }
    
//    @FXML
//    void handleBookPageSizeChange(ActionEvent event) {
//        ChoiceBox<String> comboBox = (ChoiceBox<String>) event.getSource();
//        String selectedSize = comboBox.getValue();
//        if (selectedSize != null && !selectedSize.isEmpty()) {
//            try {
//                int newPageSize = Integer.parseInt(selectedSize);
//                if (newPageSize > 0) {
//                    bookPageSize = newPageSize;
//                    bookCurrentPage = 1; // 重置到第一页
//                    loadBooks();
//                }
//            } catch (NumberFormatException e) {
//                showAlert("错误", "无效的页码大小");
//            }
//        }
//    }
//
//
}
