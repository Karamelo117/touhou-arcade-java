package clases;

import java.awt.Graphics;

/**
 * Clase abstracta que sirve como plantilla base para todas las entidades físicas
 * interactivas dentro del juego (como el Jugador y los Enemigos).
 * 
 * Centraliza las propiedades fundamentales (posición, dimensiones y velocidad) 
 * y provee la lógica estándar para el movimiento lateral, incluyendo la 
 * detección y restricción de los bordes de la pantalla.
 */
public abstract class Personaje {
    
    /** Coordenada actual en el eje X. Es protegida para que las clases hijas puedan acceder a ella. */
    protected int x;
    
    /** Coordenada actual en el eje Y. */
    protected int y;
    
    /** Ancho físico de la entidad. */
    protected int ancho;
    
    /** Alto físico de la entidad. */
    protected int alto;
    
    /** Magnitud de desplazamiento por cada actualización de movimiento. */
    protected int velocidad;
    
    /** Constante que define el ancho total de la ventana de juego. */
    protected static final int anchoPantalla = 900;
    
    /** Constante que define el alto total de la ventana de juego. */
    protected static final int altoPantalla = 700;
    
    /**
     * Constructor base para inicializar cualquier personaje.
     * 
     * @param x         Posición inicial en el eje X.
     * @param y         Posición inicial en el eje Y.
     * @param ancho     Ancho de la figura del personaje.
     * @param alto      Alto de la figura del personaje.
     * @param velocidad Velocidad de desplazamiento.
     */
    public Personaje(int x, int y, int ancho, int alto, int velocidad){
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
    }
    
    /**
     * Desplaza al personaje hacia la izquierda.
     * 
     * EXPLICACIÓN LÓGICA DE BORDES:
     * Resta la velocidad a la coordenada X. Inmediatamente después, verifica si 
     * el personaje ha cruzado el límite izquierdo de la pantalla (x < 0). 
     * De ser así, fuerza su posición a 0 para crear una barrera invisible (colisión con el borde).
     */
    public void moverIzquierda(){
        this.x = this.x - velocidad;
        
        if (this.x < 0) {
            x = 0;
        }
    }
    
    /**
     * Desplaza al personaje hacia la derecha.
     * 
     * EXPLICACIÓN LÓGICA DE BORDES:
     * Suma la velocidad a la coordenada X. Luego verifica si la posición superaría 
     * el borde derecho. La validación (anchoPantalla - ancho - 20) toma en cuenta 
     * el ancho del propio personaje para que no se oculte la mitad de su cuerpo, 
     * además de un margen adicional (~20 píxeles) que compensa los bordes nativos 
     * de la ventana del sistema operativo (decoraciones del JFrame).
     */
    public void moverDerecha(){
        this.x = this.x + velocidad;
        
        if (this.x > anchoPantalla - ancho - 20) {
            // Reposiciona al personaje justo en el límite permitido
            x = anchoPantalla - ancho - 16;
        }
    }
    
    /**
     * Método abstracto que delega la responsabilidad de renderizado a las clases hijas.
     * Cada personaje específico (Jugador, Enemigo) debe definir cómo se dibujará a sí mismo,
     * ya que pueden tener formas, colores o sprites completamente distintos.
     * 
     * @param g Objeto Graphics que provee las herramientas de dibujo.
     */
    public abstract void dibujar(Graphics g);

    /**
     * Obtiene la posición actual en el eje X del personaje.
     * Utilizado principalmente para cálculos de colisiones externas.
     * 
     * @return El valor entero de la coordenada X.
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la posición actual en el eje Y del personaje.
     * Utilizado para validar colisiones o límites de pantalla verticales.
     * 
     * @return El valor entero de la coordenada Y.
     */
    public int getY() {
        return y;
    }
}