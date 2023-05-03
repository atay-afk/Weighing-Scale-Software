/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Command;
import java.util.List;

/**
 *
 * @author ali
 */
public interface CommandDao {
    void ajouter( Command command );
    List<Command> getCommands();
}
