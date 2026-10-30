package clases;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Representa un enemigo dentro del juego. Hereda de la clase abstracta Personaje.
 * 
 * Esta clase maneja de forma autónoma el ciclo de vida del enemigo:
 * su movimiento vertical (caída), su renderizado, su desactivación al ser destruido 
 * o salir de la pantalla, y su lógica de reaparición basada en intervalos de tiempo.
 */ 
public class Enemigo extends Personaje {
    
    /** Generador principal de números aleatorios para tiempos y posiciones. */
    private final Random ran = new Random();
    
    /** Tiempo (en milisegundos) que debe transcurrir para que el enemigo vuelva a aparecer tras ser desactivado. */
    private long tiempoReaparicion;
    
    /** Marca de tiempo absoluto (en milisegundos) de la última vez que el enemigo fue desactivado. */
    private long ultimoReinicio;
    
    /** Bandera de estado que indica si el enemigo está actualmente en juego (visible y moviéndose). */
    private boolean activo;
    
    /** Tiempo de espera secundario asignado al morir para generar variabilidad en el juego. */
    private long tiempoEspera;
    
    // NOTA: Estas variables auxiliares asumen que 'anchoPantalla' está definido en la clase padre 'Personaje'.
    Random rand = new Random();
    int aleatorio = rand.nextInt(1, anchoPantalla);
    
    /**
     * Constructor de la clase Enemigo.
     * Configura el estado inicial, la posición aleatoria y los tiempos de reaparición.
     * 
     * @param velocidad Magnitud del desplazamiento en el eje Y por cada actualización.
     */
    public Enemigo (int velocidad) {
        /*
         * Llamada al constructor de la clase padre (Personaje).
         * Parámetros: x, y, ancho, alto, velocidad.
         * 
         * EXPLICACIÓN DE COORDENADAS INICIALES:
         * - X se genera de forma aleatoria (entre 0 y 849) para que no caigan siempre en el mismo sitio.
         * - Y se establece en -30. Al ser un valor negativo, el enemigo aparece "fuera" del
         *   borde superior de la pantalla y entra cayendo de forma natural a la vista del jugador.
         */
        super(new Random().nextInt(850), -30, 30, 30, velocidad);
        
        this.activo = true;
        
        // Se registra el instante exacto en el que se crea el enemigo
        this.ultimoReinicio = System.currentTimeMillis();
        
        // Se definen tiempos aleatorios iniciales (en milisegundos) para su reaparición
        this.tiempoReaparicion = ran.nextInt(1000) + 50;
        this.tiempoEspera = ran.nextInt(1000) + 500;
    }
    
    /**
     * Actualiza el estado físico y lógico del enemigo.
     * Debe ser invocado en cada ciclo principal del juego.
     * 
     * Lógica principal:
     * - Si está activo, actualiza su posición Y (cae hacia abajo).
     * - Si está inactivo, calcula si ya pasó su tiempo de penalización/espera para revivirlo.
     */
    public void actualizar() {
        // Se obtiene el tiempo actual del sistema en milisegundos
        long tiempoActual = System.currentTimeMillis();
        
        if (activo) {
            // El enemigo está vivo: se suma la velocidad a Y, haciendo que descienda por la pantalla.
            this.y = this.y + this.velocidad;
        } 
        else {
            /*
             * EXPLICACIÓN LÓGICA DE TIEMPO (Delta Time):
             * Se resta el tiempo actual menos la marca de tiempo del último reinicio.
             * Si esa diferencia es mayor o igual al tiempo de reaparición estipulado, 
             * significa que el "castigo" terminó y el enemigo debe volver al juego.
             */
            if (tiempoActual - ultimoReinicio >= tiempoReaparicion) {
                this.activo = true;             // Se reactiva
                this.y = -30;                   // Se vuelve a colocar encima de la pantalla
                this.x = ran.nextInt(this.anchoPantalla); // Se escoge una nueva columna aleatoria
            }
        }
    }
    
    /**
     * Cambia el estado del enemigo cuando es destruido (por ejemplo, al recibir un disparo).
     * Lo marca como inactivo y reinicia sus temporizadores para que tarde un momento en reaparecer.
     */
    public void morir() {
        activo = false;
        
        // Se registra el momento exacto de la "muerte"
        ultimoReinicio = System.currentTimeMillis();
        
        // Se asigna un nuevo tiempo de espera (entre 500 y 1499 milisegundos)
        tiempoEspera = ran.nextInt(1000) + 500;
    }
    
    /**
     * Verifica constantemente si el enemigo ha salido de los límites visibles.
     * 
     * Si la coordenada Y del enemigo supera la altura máxima de la pantalla,
     * se le da de baja automáticamente y se prepara su ciclo de reaparición.
     */
    public void revisarSalida() {
        // Verifica que esté activo y que su posición Y haya superado el límite inferior.
        if (this.activo && this.y > this.altoPantalla) {
            
            this.activo = false;
            
            // Se registra el momento exacto en el que abandonó la pantalla
            this.ultimoReinicio = System.currentTimeMillis();
            
            // Se calcula un nuevo tiempo aleatorio antes de que vuelva a caer desde arriba
            this.tiempoReaparicion = ran.nextInt(1000 + 100);
        }
    }
    
    /**
     * Renderiza gráficamente al enemigo en la interfaz de juego.
     * 
     * @param g Objeto Graphics que provee los métodos de dibujo sobre el componente.
     */
    @Override
    public void dibujar(Graphics g) {
        // Solo se consumen recursos de dibujado si el enemigo está participando en el juego
        if (activo) {
            // Se define el color verde representativo de la entidad enemiga
            g.setColor(Color.GREEN);
            
            // Se dibuja un óvalo sólido usando sus coordenadas y dimensiones (alto y ancho)
            g.fillOval(x, y, alto, ancho);
        }
    }
    
    /**
     * Modifica directamente la posición vertical del enemigo.
     * Puede utilizarse para reposicionamientos forzados desde el motor central del juego.
     * 
     * @param newY Nueva coordenada para el eje Y.
     */
    public void setY(int newY) {
        this.y = newY;
    }
    
    /**
     * Permite consultar el estado actual del ciclo de vida del enemigo.
     * Fundamental para saber si el enemigo puede colisionar con disparos o con el jugador.
     * 
     * @return true si el enemigo está activo en la pantalla; false en caso contrario.
     */
    public boolean estaActivo() {
        return this.activo;
    }
}