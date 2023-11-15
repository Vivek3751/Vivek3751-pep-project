package DAO;
import Util.ConnectionUtil;
import java.sql.*;

import Model.Account;
public class AccountDAO {
    //private AccountDAO accountDAO;

    //to add new User Account Registrations.
    public Account newUserRegister(Account account){
        Connection conn=ConnectionUtil.getConnection();
        try{
            String sql="insert into account(username,password) values(?,?)";
            PreparedStatement ps=conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            ps.executeUpdate();
            ResultSet generatedKeys=ps.getGeneratedKeys();
            if(generatedKeys.next()){
                int accountId = generatedKeys.getInt(1);
                return new Account(accountId, account.getUsername(), account.getPassword());
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    //get username of existing account
    public String getUserNameString(Account account) {
        Connection conn=ConnectionUtil.getConnection();
        String string="";
        try {
            String sql = "SELECT username FROM account WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            // Retrieve the username from the result set
            string+= resultSet.getString("username");
        }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return string;
    }
    //get account details by using username
    public Account getAccountByUsername(String username) {
        Connection conn = ConnectionUtil.getConnection();
        Account account = null;

        try {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String storedUsername = resultSet.getString("username");
                String storedPassword = resultSet.getString("password");

                account = new Account(accountId, storedUsername, storedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
        }

        return account;
    }

    public Account getAccountById(int accountId) {
        Connection conn = ConnectionUtil.getConnection();
        Account account = null;

        try {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                account = new Account(accountId, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
        }

        return account;
    }
}
