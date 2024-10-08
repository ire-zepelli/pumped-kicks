package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import env.env;

public class dbActions {
    protected static Connection conn = null;
    protected static String USERNAME = env.USERNAME;
    protected static String PASSWORD = env.PASSWORD;
    protected static String DRIVER = env.DRIVER;
    protected static String URL = env.URL;

    public static Connection getDataBaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }


    public static boolean checkAuth(String enteredUsername, String enteredPassword) throws SQLException, ClassNotFoundException {
        Connection testConnection = getDataBaseConnection();
        boolean authenticated = false;
        String username = "", password = "";

        if(testConnection != null){
            Statement statement = testConnection.createStatement();
            if(statement != null){
                ResultSet resultSet = statement.executeQuery("SELECT * from users WHERE username = '"+ enteredUsername +"';");
                if(resultSet != null){
                    while (resultSet.next()) {
                        username = resultSet.getString("username");
                        password = resultSet.getString("password");
                    }

                    //check authentication
                    if(username.equals(enteredUsername) && password.equals(enteredPassword)){
                        authenticated = true;
                    }

                }
                resultSet.close();
            }
            statement.close();
        }
        testConnection.close();
        return authenticated;
    }

    public static boolean addUser(String username, String password) throws SQLException, ClassNotFoundException{
        Connection testConnection = getDataBaseConnection();
        
        if(testConnection!= null){
            Statement statement = testConnection.createStatement();
            if(statement != null){
                //check if user exists
                if(doesUserExist(username, statement)){
                    return false;
                }

                //adds user
                int condition = statement.executeUpdate("INSERT INTO users (username, password) values ('" + username + "','" + password + "');");
                if(condition == 0){
                    System.out.println("Failed to add user");
                    return false;
                }
                else if(condition > 0){
                    System.out.println("User Added Succesfully");
                    return true;
                }
                statement.close();
            }
            testConnection.close();
        }
        return false;
    }

    public static boolean doesUserExist(String username, Statement statement) throws SQLException, ClassNotFoundException{
        ResultSet resultSet = statement.executeQuery("SELECT * from users WHERE username = '"+ username +"';");

        if(resultSet != null){
            while (resultSet.next()) {
                        if(resultSet.getString("username").isBlank()){
                            return false;
                        }
                        return true;
                }
        }
        return false;
    }


}
