package com.library.system.dao;

import com.library.system.model.Book;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setPublishDate(rs.getDate("publish_date").toLocalDate());
                book.setAvailable(rs.getBoolean("available"));
                book.setImagePath(rs.getString("image_path"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, category, publish_date, available, image_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getCategory());
            pstmt.setDate(5, Date.valueOf(book.getPublishDate()));
            pstmt.setBoolean(6, book.isAvailable());
            pstmt.setString(7, book.getImagePath());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> searchBooks(String keyword) {
        return searchBooksByPage(keyword, 1, Integer.MAX_VALUE);
    }
    
    public List<Book> searchBooksByPage(String keyword, int page, int pageSize) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setInt(4, pageSize);
            pstmt.setInt(5, (page - 1) * pageSize);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setPublishDate(rs.getDate("publish_date").toLocalDate());
                book.setAvailable(rs.getBoolean("available"));
                book.setImagePath(rs.getString("image_path"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public int getSearchBooksCount(String keyword) {
        String sql = "SELECT COUNT(*) FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateBook(Book selectedBook) {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, category = ?, " +
                     "publish_date = ?, available = ?, image_path = ? WHERE id = ?";
        System.out.println(selectedBook);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, selectedBook.getTitle());
            pstmt.setString(2, selectedBook.getAuthor());
            pstmt.setString(3, selectedBook.getIsbn());
            pstmt.setString(4, selectedBook.getCategory());
            pstmt.setDate(5, Date.valueOf(selectedBook.getPublishDate()));
            pstmt.setBoolean(6, selectedBook.isAvailable());
            pstmt.setString(7, selectedBook.getImagePath());
            pstmt.setInt(8, selectedBook.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 添加分页查询方法
    public List<Book> getBooksByPage(int page, int pageSize) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT DISTINCT b.*, " +
                "IF(br.returned = 0, u.username, NULL) as borrower_name " +
                "FROM books b " +
                "LEFT JOIN (SELECT * FROM borrow_records WHERE returned = 0) br ON b.id = br.book_id " +
                "LEFT JOIN users u ON br.user_id = u.id " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, (page - 1) * pageSize);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book();
               // 设置属性
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setPublishDate(rs.getDate("publish_date").toLocalDate());
                book.setAvailable(rs.getBoolean("available"));
                book.setImagePath(rs.getString("image_path"));
                book.setBorrowerName(rs.getString("borrower_name"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    // 添加获取总记录数方法
    public int getTotalBooksCount() {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}