package clases;

/**
 * 
 * @author josei
 */
public class Jugador {
    
    private int id;
    private String nombre;
    private String apellido;
    private String user;
    private String pass;
    private boolean activo;
    private int score; // Atributo agregado para manejar el puntaje

    // Constructor vacío
    public Jugador() {
        this.nombre = "";
        this.apellido = "";
        this.user = "";
        this.pass = "";
        this.activo = false;
        this.score = 0;
    }

    // Constructor sin ID ni score (por compatibilidad con código existente)
    public Jugador(String nombre, String apellido, String user, String pass, boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.user = user;
        this.pass = pass;
        this.activo = activo;
        this.score = 0;
    }

    // Constructor para altas en base de datos (sin ID, con score)
    public Jugador(String nombre, String apellido, String user, String pass, boolean activo, int score) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.user = user;
        this.pass = pass;
        this.activo = activo;
        this.score = score;
    }

    // Constructor completo para consultas en base de datos
    public Jugador(int id, String nombre, String apellido, String user, String pass, boolean activo, int score) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.user = user;
        this.pass = pass;
        this.activo = activo;
        this.score = score;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
}