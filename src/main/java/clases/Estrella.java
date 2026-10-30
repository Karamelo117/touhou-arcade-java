package clases;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Representa un elemento visual de fondo (una estrella) en la pantalla de juego.
 * 
 * Esta clase gestiona de forma individual las propiedades físicas de cada estrella,
 * como su posición, su tamaño y su velocidad de caída. Al instanciar
 * múltiples estrellas con velocidades diferentes en el sistema principal, 
 */
public class Estrella {
    
    /** Coordenada actual en el eje X de la estrella. */
    private int x;
    
    /** Coordenada actual en el eje Y de la estrella. */
    private int y;
    
    /** Tamaño (diámetro) de la estrella en píxeles. */
    private int tam;
    
    /** Velocidad de desplazamiento vertical (caída) por fotograma. */
    private int vel;
    
    /** Generador de números aleatorios para la inicialización de propiedades. */
    Random r = new Random();
    
    /**
     * Constructor de la clase Estrella.
     * Posiciona la estrella de manera aleatoria dentro de los límites de la pantalla
     * y le asigna un tamaño y velocidad aleatorios para dotar al fondo de dinamismo.
     * 
     * @param ancho Límite máximo en el eje X (ancho de la pantalla) para su aparición.
     * @param alto  Límite máximo en el eje Y (alto de la pantalla) para su aparición.
     */
    public Estrella(int ancho, int alto) {
        // Posición inicial aleatoria dentro del rango visible de la pantalla
        this.x = r.nextInt(ancho);
        this.y = r.nextInt(alto);
        
        // Se asigna un tamaño aleatorio entre 1 y 3 píxeles
        this.tam = r.nextInt(3) + 1;
        
        // Se asigna una velocidad aleatoria entre 1 y 3. 
        // Las estrellas más veloces darán la sensación visual de estar más cerca de la cámara.
        this.vel = r.nextInt(3) + 1;
    }
    
    /**
     * Actualiza la posición vertical de la estrella en cada ciclo del juego.
     * 
     * EXPLICACIÓN LÓGICA DEL MOVIMIENTO CONTINUO:
     * Al sumar la velocidad ('vel') a la coordenada 'y', la estrella se desplaza hacia 
     * la parte inferior de la ventana. Cuando la posición 'y' iguala o supera el límite 
     * inferior de la pantalla, su valor se reinicia a 0 (el borde superior), 
     * reciclando el objeto y creando un bucle infinito visual de caída estelar.
     * 
     * @param alto La altura de la ventana del juego, utilizada como límite de recolección.
     */
    public void mover(int alto) {
        // Se incrementa la coordenada Y para desplazar la estrella hacia abajo
        this.y += this.vel;
        
        // Verificación de desbordamiento de pantalla
        if (this.y >= alto) {
            // Reposicionamiento en la parte superior para continuar el ciclo
            this.y = 0;
        }
    }
    
    /**
     * Renderiza gráficamente la estrella en el entorno.
     * 
     * @param g Objeto Graphics que provee las herramientas de dibujo sobre el panel.
     */
    public void pintar(Graphics g) {
        // Se establece el color blanco para simular la luz estelar
        g.setColor(Color.WHITE);
        
        // Se dibuja un círculo sólido utilizando el valor 'tam' tanto para el 
        // ancho como para el alto, garantizando una proporción geométrica perfecta.
        g.fillOval(x, y, tam, tam);
    }
}