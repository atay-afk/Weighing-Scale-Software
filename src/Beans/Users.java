
package Beans;

/**
 *
 * @author ali
 */
public class Users {
    private String nom;
    private String prenom;
    private String login;
    private String pwd;
    private String fonction;
    private static Users instance=new Users();

    
    public static void setInstance(Users instance) {
        Users.instance = instance;
    }

    public static Users getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Users{" + "nom=" + nom + ", prenom=" + prenom + ", login=" + login + ", pwd=" + pwd + ", fonction=" + fonction + '}';
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getLogin() {
        return login;
    }

    public String getPwd() {
        return pwd;
    }

    public String getFonction() {
        return fonction;
    }
    
    
}
