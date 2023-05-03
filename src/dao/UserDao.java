/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Users;
import java.awt.Component;
import java.util.List;

/**
 *
 * @author ali
 */
public interface UserDao {
    boolean connexionSucceeded(String login, String pwd, String fonction);
    List<Users> listerUsers();
    Users connectedUser(String login, String pwd, String fonction);
    void addUser(Users user);
    void deleteUser(String login);
    void editUser(String login,Users user);
    void exportUser(Component c);
}
