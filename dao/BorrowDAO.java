package com.library.system.dao;



import com.library.system.model.Book;
import com.library.system.model.BorrowRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    
    /**
     * 借阅图书
     * @param bookId 图书ID
     * @param userId 用户ID
     * @return 是否借阅成功
     */
    public boolean borrowBook(int bookId, int userId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // 1. 插入借阅记录
            String borrowSql = "INSERT INTO borrow_records(book_id, user_id, borrow_date, return_date, returned) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(borrowSql)) {
                pstmt.setInt(1, bookId);
                pstmt.setInt(2, userId);
                pstmt.setDate(3, Date.valueOf(LocalDate.now()));
                pstmt.setDate(4, Date.valueOf(LocalDate.now().plusDays(30)));
                pstmt.setInt(5, 0);
                pstmt.executeUpdate();
            }
            
            // 2. 更新图书状态
            String updateSql = "UPDATE books SET available = false WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 归还图书
     *
     * @param
     * @param
     * @return 是否归还成功
     */
    public boolean returnBook(int bookId, int userId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            //更新借阅记录
            String returnSql = "UPDATE borrow_records SET return_date = ?, returned = ? WHERE book_id = ? and user_id =?";
            try (PreparedStatement pstmt = conn.prepareStatement(returnSql)) {
                pstmt.setDate(1, Date.valueOf(LocalDate.now()));
                pstmt.setInt(2, 1);
                pstmt.setInt(3, bookId);
                pstmt.setInt(4, userId);
                pstmt.executeUpdate();
            }
            
            // 3. 更新图书状态
            if (bookId > 0) {
                String updateSql = "UPDATE books SET available = true WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, bookId);
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 获取用户借阅记录
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getBorrowRecordsByUser(int userId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.* , b.title FROM borrow_records br  left join  books  b on br.book_id = b.id  WHERE user_id = ?  ORDER BY br.borrow_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setId(rs.getInt("id"));
                record.setBookId(rs.getInt("book_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                record.setReturnDate(rs.getDate("return_date").toLocalDate());
                record.setReturned(rs.getBoolean("returned"));
                record.setBookTitle(rs.getString("title"));
                // 设置借阅状态
                LocalDate today = LocalDate.now();
                LocalDate dueDate = record.getReturnDate();
                if (record.isReturned()) {
                    record.setStatus("已归还");
                } else if (today.isAfter(dueDate)) {
                    record.setReturnDate(null);
                    record.setStatus("已超期");
                } else {
                    record.setReturnDate(null);
                    record.setStatus("借阅中");
                }
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
    
    /**
     * 检查图书是否可借
     * @param bookId 图书ID
     * @return 是否可借
     */
    public boolean isBookAvailable(int bookId) {
        String sql = "SELECT returned FROM borrow_records WHERE book_id = ? AND returned = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            // 如果没有未归还的借阅记录，则图书可借
            return !rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> getBorrowedBooksByUser(int userId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM books b " +
                     "JOIN borrow_records br ON b.id = br.book_id " +
                     "WHERE br.user_id = ? AND br.returned = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
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
    
    /**
     * 分页查询借阅记录
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getBorrowRecordsByPage(int page, int pageSize, String keyword) {
        List<BorrowRecord> records = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT br.*, b.title AS book_title, u.username AS user_name ");
        sql.append("FROM borrow_records br ");
        sql.append("LEFT JOIN books b ON br.book_id = b.id ");
        sql.append("LEFT JOIN users u ON br.user_id = u.id ");
        
        int offset = (page - 1) * pageSize;
        int paramIndex = 1;
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("WHERE b.title LIKE ? OR u.username LIKE ? ");
        }
        
        sql.append("ORDER BY br.borrow_date DESC LIMIT ? OFFSET ?");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            if (keyword != null && !keyword.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + keyword + "%");
                pstmt.setString(paramIndex++, "%" + keyword + "%");
            }
            
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex++, offset);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setId(rs.getInt("id"));
                record.setBookId(rs.getInt("book_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                record.setReturned(rs.getBoolean("returned"));
                record.setBookTitle(rs.getString("book_title"));
                record.setUsername(rs.getString("user_name"));
                record.setReturnDate(rs.getDate("return_date").toLocalDate());
                // 设置借阅状态
                LocalDate today = LocalDate.now();
                LocalDate dueDate = record.getReturnDate();
                if (record.isReturned()) {
                    record.setStatus("已归还");
                } else if (today.isAfter(dueDate)) {
                    record.setReturnDate(null);
                    record.setStatus("已超期");
                } else {
                    record.setReturnDate(null);
                    record.setStatus("借阅中");
                }
                
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return records;
    }
    
    /**
     * 获取借阅记录总数
     * @param keyword 搜索关键词
     * @return 记录总数
     */
    public int getTotalBorrowRecordCount(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM borrow_records br ");
        sql.append("LEFT JOIN books b ON br.book_id = b.id ");
        sql.append("LEFT JOIN users u ON br.user_id = u.id ");
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("WHERE b.title LIKE ? OR u.username LIKE ?");
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            if (keyword != null && !keyword.isEmpty()) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }


    public List<BorrowRecord> getBorrowRecordsByPageAndUserId(int id, int currentPage, int pageSize, String searchKeyword) { 
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title, u.username FROM borrow_records br JOIN books b ON br.book_id = b.id JOIN users u ON br.user_id = u.id WHERE br.user_id = ? ";
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            sql += " AND b.title LIKE ? ";
        }
        sql += " ORDER BY br.borrow_date DESC LIMIT ? OFFSET ? ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int paramIndex = 2;

            if (searchKeyword != null && !searchKeyword.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchKeyword + "%");
            }

            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, (currentPage - 1) * pageSize);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setId(rs.getInt("id"));
                record.setBookId(rs.getInt("book_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                record.setReturnDate(rs.getDate("return_date").toLocalDate());
                record.setReturned(rs.getBoolean("returned"));
                record.setBookTitle(rs.getString("title"));
                record.setUsername(rs.getString("username"));
                LocalDate today = LocalDate.now();
                // 设置借阅状态
                if (record.isReturned()) {
                    record.setStatus("已归还");
                } else if (today.isAfter(record.getDueDate())) {
                    record.setReturnDate(null);
                    record.setStatus("已超期");
                } else {
                    record.setReturnDate(null);
                    record.setStatus("借阅中");
                }

                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    } 
 
    public int getTotalBorrowRecordCountByUserId(int id, String searchKeyword) { 
        int count = 0;
        String sql = "SELECT COUNT(*) FROM borrow_records br JOIN books b ON br.book_id = b.id WHERE br.user_id = ? ";
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            sql += " AND b.title LIKE ? ";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            if (searchKeyword != null && !searchKeyword.isEmpty()) {
                pstmt.setString(2, "%" + searchKeyword + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    } 
 }
