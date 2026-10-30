package basedatos;

import clases.Jugador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Clase Data Access Object (DAO) para la entidad Jugador.
 * Se encarga de gestionar todas las operaciones de persistencia en la base de datos
 * correspondientes a los jugadores (Crear, Leer, Actualizar y Eliminar - CRUD).
 */
public class JugadorDAO {
     
    /**
     * Registra un nuevo jugador en la base de datos.
     * 
     * @param daoJugador Objeto de tipo Jugador que contiene los datos a guardar.
     */
    public void altaJugador(Jugador daoJugador) {
        try {
            // Se establece la conexión con la base de datos
            Connection cn = Conexion.conectar();
            
            // Sentencia SQL para insertar un nuevo registro. 
            // Los signos de interrogación (?) actúan como parámetros de seguridad para evitar inyecciones SQL.
            String sql = "INSERT INTO jugador(nombre, apellido, user, pass, activo, score) VALUES(?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql);
      
            // Se asignan los valores del objeto Jugador a cada parámetro de la consulta SQL
            ps.setString(1, daoJugador.getNombre());
            ps.setString(2, daoJugador.getApellido());
            ps.setString(3, daoJugador.getUser());
            ps.setString(4, daoJugador.getPass());
            ps.setBoolean(5, daoJugador.isActivo()); 
            ps.setInt(6, daoJugador.getScore()); 

            // Se ejecuta la instrucción de inserción en la base de datos
            ps.executeUpdate();
            System.out.println("Jugador guardado");
            
        } catch(Exception e) {
            // En caso de error (como un fallo de conexión o un nombre de usuario duplicado), se captura y se muestra
            System.out.println("ERROR DE ALTA: " + e);
        }
    }
    
    /**
     * Elimina el registro de un jugador específico de la base de datos.
     * 
     * @param id El identificador único (Primary Key) del jugador que se desea eliminar.
     */
    public void borrarRegistro(int id) {
        try {
            Connection cn = Conexion.conectar();
            
            // Sentencia SQL para borrar un registro basado en su ID
            String sql = "DELETE FROM jugador WHERE id = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            
            // Se ejecuta la eliminación
            ps.executeUpdate();
            
            // Se notifica al usuario del éxito de la operación mediante una ventana emergente
            JOptionPane.showMessageDialog(null, "Registro eliminado exitosamente");
            System.out.println("Registro eliminado");
            
        } catch(Exception e) {
            System.out.println("ERROR AL ELIMINAR: " + e);
        }
    }

    /**
     * Consulta y extrae todos los jugadores registrados en la base de datos.
     * 
     * @return Una lista (ArrayList) que contiene objetos de tipo Jugador con la información de cada registro.
     */
    public ArrayList<Jugador> consultarTodo() {
        // Se inicializa la lista vacía que almacenará los resultados
        ArrayList<Jugador> listaJugadores = new ArrayList<>();
        
        try {
            Connection cn = Conexion.conectar();
            
            // Sentencia SQL para seleccionar todos los registros de la tabla 'jugador'
            String sql = "SELECT * FROM jugador";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            // Se ejecuta la consulta y se guarda el resultado en un ResultSet (conjunto de datos)
            ResultSet rs = ps.executeQuery();
            
            // Se recorre cada fila devuelta por la base de datos
            while (rs.next()) {
               // Se extraen los datos de las columnas de la fila actual
               int id = rs.getInt("id");
               String nombre = rs.getString("nombre");
               String apellido = rs.getString("apellido");
               String user = rs.getString("user");
               String pass = rs.getString("pass");
               boolean activo = rs.getBoolean("activo");
               int score = rs.getInt("score"); 
         
               // Se instancia un nuevo objeto Jugador con los datos extraídos
               Jugador jug = new Jugador(id, nombre, apellido, user, pass, activo, score); 
               
               // Se añade el objeto a la lista
               listaJugadores.add(jug);
            }
        } catch(Exception e) {
            System.out.println("ERROR EN CONSULTAR TODO: " + e);
        }
        
        // Se retorna la lista llena (o vacía si no hubo resultados o si ocurrió un error)
        return listaJugadores;
    }

    /**
     * Busca a un jugador en específico utilizando su identificador único.
     * 
     * @param idBusqueda El identificador (ID) del jugador a buscar.
     * @return Un objeto de tipo Jugador si se encuentra el registro, o 'null' si no existe.
     */
    public Jugador consultarPorId(int idBusqueda) {
        Jugador jug = null;
        
        try {
          Connection cn = Conexion.conectar();
          
            // Sentencia SQL para seleccionar un único registro filtrado por su ID
            String sql = "SELECT * FROM jugador WHERE id = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idBusqueda);
            
            ResultSet rs = ps.executeQuery();
            
            // Si el ResultSet tiene al menos una fila de resultados, se procesa la información
            if (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String user = rs.getString("user");
                String pass = rs.getString("pass");
                boolean activo = rs.getBoolean("activo");
                int score = rs.getInt("score");
                
                // Se construye el objeto con la información recuperada de la base de datos
                jug = new Jugador(id, nombre, apellido, user, pass, activo, score);
            }
        } catch(Exception e) {
            System.out.println("ERROR EN CONSULTAR POR ID: " + e);
        }
        
        return jug;
    }
    
    /**
     * Actualiza la puntuación (score) de un jugador, asegurando que solo se guarde 
     * si el nuevo puntaje es mayor al récord histórico que ya posee.
     * 
     * @param usuario El nombre de usuario (user) del jugador.
     * @param score   La nueva puntuación obtenida en la partida.
     */
    public void actualizarScore(String usuario, int score) {
        try {
            Connection cn = Conexion.conectar();
            
            /* 
             * EXPLICACIÓN LÓGICA SQL:
             * Se actualiza la columna 'score' SOLO SI coinciden dos condiciones:
             * 1. El 'user' debe ser igual al usuario proporcionado.
             * 2. El 'score' actual almacenado en la base de datos debe ser estrictamente menor (<) al nuevo score.
             * Esto previene que un jugador reemplace su récord máximo con una puntuación más baja.
             */
            String sql = "UPDATE jugador SET score = ? WHERE user = ? AND score < ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setInt(1, score);   // El nuevo valor a establecer
            ps.setString(2, usuario); // El usuario a buscar
            ps.setInt(3, score);   // La condición matemática contra el valor actual
            
            // Se ejecuta la actualización. 'executeUpdate' retorna el número de filas que fueron modificadas.
            int filasAfectadas = ps.executeUpdate();
            
            // Si filasAfectadas es mayor a 0, significa que se cumplió la condición y el récord se superó.
            if (filasAfectadas > 0) {
                System.out.println("Nuevo récord para: " + usuario);
            } else {
                System.out.println("El score no superó el récord actual o el usuario no existe.");
            }
            
        } catch(Exception e) {
            System.out.println("ERROR AL ACTUALIZAR SCORE: " + e);
        }
    }
}