package clases;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Clase que representa un proyectil o disparo dentro del juego.
 * Se encarga de gestionar la posición espacial, el desplazamiento y 
 * el renderizado visual del disparo en la pantalla.
 */
public class Disparo {
    
    /** Coordenada X actual del proyectil. */
    private int x;
    
    /** Coordenada Y actual del proyectil. */
    private int y;
    
    /** Velocidad de desplazamiento vertical por fotograma. */
    private int vel;
    
    /** Ancho visual del proyectil (en píxeles). */
    private int ancho;
    
    /** Alto visual del proyectil (en píxeles). */
    private int alto;
    
    /**
     * Constructor de la clase Disparo.
     * Establece la posición inicial del proyectil, generalmente originada 
     * en las coordenadas actuales de la entidad que lo dispara (como la nave del jugador).
     * También define los valores fijos de velocidad y tamaño.
     * 
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     */
    public Disparo(int x, int y){
        this.x = x;
        this.y = y;
        this.vel = 10;
        this.ancho = 5;
        this.alto = 15;
    }
    
    /**
     * Actualiza la posición vertical del proyectil para simular su avance.
     * 
     * EXPLICACIÓN DEL MOVIMIENTO:
     * En el sistema de coordenadas gráficas de Java 2D, el punto (0,0) se encuentra 
     * en la esquina superior izquierda de la ventana. Para que un objeto se desplace 
     * hacia "arriba" (hacia el borde superior de la pantalla), es necesario RESTAR 
     * su velocidad a la coordenada Y.
     */
    public void mover(){
        this.y = this.y - this.vel;
    }
    
    /**
     * Renderiza el proyectil en la interfaz gráfica.
     * 
     * @param g Objeto Graphics utilizado para aplicar colores y dibujar formas geométricas.
     */
    public void dibujar(Graphics g){
        // Se establece el color rojo para el rayo/proyectil
        g.setColor(Color.red);
        
        // Se dibuja un rectángulo sólido basándose en las coordenadas y dimensiones actuales
        g.fillRect(x, y, ancho, alto);
    }

    /**
     * Obtiene la posición actual en el eje X del proyectil.
     * Esencial para implementar el sistema de detección de colisiones con enemigos.
     * 
     * @return El valor entero de la coordenada X.
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la posición actual en el eje Y del proyectil.
     * Necesario para el cálculo de colisiones y para verificar si el disparo 
     * ya salió del área visible (borde superior) y debe ser destruido.
     * 
     * @return El valor entero de la coordenada Y.
     */
    public int getY() {
        return y;
    }
}