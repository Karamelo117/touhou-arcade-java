package basedatos;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 * Clase encargada de gestionar la conexión principal con la base de datos MySQL.
 * Contiene la lógica necesaria para enlazar la aplicación Java con el servidor
 * de base de datos local de forma estática.
 */
public class Conexion {
    
    /**
     * Establece y retorna una conexión activa con la base de datos.
     * Utiliza el driver JDBC para conectarse de forma local (localhost) en el puerto 3306
     * a la base de datos llamada "juego".
     * 
     * Si la conexión falla (por ejemplo, si el servidor MySQL está apagado), 
     * el método captura la excepción, notifica al usuario mediante una alerta visual
     * y termina la ejecución del programa de forma segura para evitar errores en cadena.
     * 
     * @return Un objeto de tipo Connection con la sesión establecida hacia la base de datos.
     */
    public static Connection conectar() {
        try {
            // Cadena de conexión que indica el motor (mysql), servidor (localhost), puerto (3306) y nombre de la BD (juego)
            String url = "jdbc:mysql://localhost:3306/juego";
            
            // Credenciales de acceso al servidor MySQL (por defecto en instalaciones locales)
            String user = "root";
            String pass = "";
            
            // Se utiliza el DriverManager para intentar abrir la conexión con los parámetros dados
            Connection cn = DriverManager.getConnection(url, user, pass);
            
            // Mensaje de confirmación en consola, útil para tareas de depuración del desarrollador
            System.out.println("Conexion exitosa");
            
            return cn;
            
        } catch (Exception e) {
            // Se imprime el error técnico en la consola para facilitar el rastreo del fallo
            System.out.println("Error Conexion: " + e);
            
            // Se muestra una ventana emergente al usuario final indicando el problema
            JOptionPane.showMessageDialog(null, "HA HABIDO UN ERROR AL CONECTAR, POR FAVOR, INTÉNTALO NUEVAMENTE");
            
            // Se fuerza el cierre de la aplicación (código de salida 0) ya que el sistema
            // no puede operar correctamente sin una base de datos activa.
            System.exit(0);
            
            // Este retorno nunca se ejecutará debido al System.exit, pero es requerido por la firma del método
            return null;
        }
    }   
}