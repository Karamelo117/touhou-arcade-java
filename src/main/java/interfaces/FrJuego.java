package interfaces;

import javax.swing.JFrame;

/**
 * Clase que representa la ventana principal del juego.
 * Hereda de JFrame, lo que le permite actuar como el contenedor base 
 * del sistema operativo donde se dibujarán todos los componentes gráficos.
 * 
 * Su única responsabilidad es configurar la ventana exterior (marco, título, 
 * comportamiento de cierre) y alojar el panel de juego (PanJuego) 
 * donde ocurre la lógica y el renderizado real.
 */
public class FrJuego extends JFrame {
    
    /**
     * Constructor de la ventana principal.
     * Configura las propiedades físicas de la ventana gráfica al momento de ser instanciada.
     */
    public FrJuego() {
        
        // Define el texto que aparecerá en la barra superior de la ventana
        setTitle("Touhou");
        
        // Establece las dimensiones fijas de la ventana (ancho: 900, alto: 700).
        // Estas medidas coinciden con los límites físicos definidos en la clase Personaje.
        setSize(900, 700);
        
        // Centra la ventana automáticamente en la pantalla del monitor del usuario
        setLocationRelativeTo(null);
        
        // Configura el comportamiento de cierre: al presionar la "X" de la ventana,
        // se terminará por completo la ejecución de la aplicación (System.exit(0) internamente).
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        /* 
         * Se deshabilita la opción de maximizar o redimensionar la ventana (false).
         * Esto es crítico en juegos de coordenadas estáticas, ya que si el usuario 
         * cambia el tamaño de la ventana, los límites lógicos (bordes) se desfasarían
         * de los límites visuales, rompiendo el sistema de colisiones.
         */
        setResizable(false);
        
        // Se instancia y se añade el lienzo/panel principal donde ocurre todo el juego
        this.add(new PanJuego());
    }
}