package com.library.system.model;

import java.time.LocalDate;

public class BorrowRecord {
    private int id;
    private int bookId;
    private int userId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean returned;
    private String username;
    private String bookTitle;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getStatus() {
        return status;
    }


    public void setStatus(String status){
        this.status = status;
    }


    public LocalDate getDueDate() {
        return borrowDate.plusDays(30); // 借阅期限为30天
    }




}