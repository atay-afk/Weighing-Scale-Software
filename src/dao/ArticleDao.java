/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Beans.Article;
import java.awt.Component;
import java.util.List;

/**
 *
 * @author ali
 */
public interface ArticleDao {
   
    void exportArticle(Component c);
    List<Article> listerArticles();
    void addArticle(Article art);
    void deleteArticle(String id);
    void editArticle(String id,Article art);
}
