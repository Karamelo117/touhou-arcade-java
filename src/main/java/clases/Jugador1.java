package clases;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Clase que representa la entidad física y visual del jugador (la nave) dentro del juego.
 * Hereda de la clase abstracta Personaje para utilizar sus propiedades físicas 
 * (posición, tamaño y velocidad).
 */
public class Jugador1 extends Personaje {

    /** 
     * Arreglo auxiliar con coordenadas predefinidas en el eje X para un triángulo estático.
     * Puede utilizarse para pruebas de renderizado o colisiones específicas.
     */
    private int[] xTrian = {150, 50, 250};
    
    /** 
     * Arreglo auxiliar con coordenadas predefinidas en el eje Y para un triángulo estático.
     */
    private int[] yTrian = {50, 200, 200};

    /**
     * Constructor por defecto de la clase Jugador1.
     * Inicializa la nave enviando parámetros fijos al constructor de la clase padre (Personaje).
     * 
     * Valores de inicialización:
     * - x = 430: Posición inicial centrada horizontalmente.
     * - y = 620: Posición inicial en la parte inferior de la pantalla.
     * - ancho = 30: Ancho de la figura de la nave.
     * - alto = 30: Alto de la figura de la nave.
     * - velocidad = 15: Magnitud de desplazamiento de la nave por cada entrada de movimiento.
     */
    public Jugador1() {
        super(430, 620, 30, 30, 15);    
    }
    
    /**
     * Renderiza la figura geométrica de la nave (un triángulo equilátero/isósceles) 
     * en el componente gráfico.
     * 
     * @param g Objeto Graphics que provee los métodos para dibujar formas en la pantalla.
     */
    @Override
    public void dibujar(Graphics g) {
        /*
         * EXPLICACIÓN GEOMÉTRICA (Cálculo de vértices):
         * Para dibujar el triángulo centrado en sus coordenadas (x, y), primero
         * precalculamos las mitades de su altura y anchura.
         */
        int mitadAncho = ancho / 2;
        int mitadAlto = alto / 2;

        /*
         * Se trazan los 3 puntos (vértices) del triángulo utilizando el punto central (x, y):
         * 1. (x, y - mitadAlto): Vértice superior (la punta de la nave apuntando hacia arriba).
         * 2. (x - mitadAncho, y + mitadAlto): Vértice inferior izquierdo.
         * 3. (x + mitadAncho, y + mitadAlto): Vértice inferior derecho.
         * 
         * Estos puntos se guardan en dos arreglos (puntosX y puntosY) que el método 
         * fillPolygon utiliza para conectar las líneas y rellenar la figura.
         */
        int[] puntosX = { x, x - mitadAncho, x + mitadAncho };
        int[] puntosY = { y - mitadAlto, y + mitadAlto, y + mitadAlto };
        
        // Se define el color verde clásico para la nave del jugador
        g.setColor(Color.GREEN);
        
        // Se dibuja un polígono relleno de 3 lados (triángulo) con los arreglos de coordenadas
        g.fillPolygon(puntosX, puntosY, 3);
    }

    /**
     * Obtiene el arreglo auxiliar de coordenadas X predefinidas.
     * 
     * @return Arreglo de enteros con las coordenadas X.
     */
    public int[] getxTrian() {
        return xTrian;
    }

    /**
     * Obtiene el arreglo auxiliar de coordenadas Y predefinidas.
     * 
     * @return Arreglo de enteros con las coordenadas Y.
     */
    public int[] getyTrian() {
        return yTrian;
    }
}
