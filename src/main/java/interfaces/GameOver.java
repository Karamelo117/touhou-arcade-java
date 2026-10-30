package interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Clase que gestiona y renderiza la pantalla de Game Over.
 * Ejecuta una animación secuencial dividida en tres fases:
 * 1. La nave del jugador vuela hasta el centro del planeta.
 * 2. Un grupo de naves enemigas aparece en formación circular y se cierra sobre la nave.
 * 3. Al ocurrir el impacto, se genera una explosión, aparece el texto "GAME OVER" y un botón para salir.
 */
public class GameOver {

    /** Coordenadas actuales de la nave del jugador en la pantalla. */
    private double x, y;
    
    /** 
     * Componentes de velocidad (dx, dy) que definen el desplazamiento por fotograma.
     * La 'velocidad' determina la rapidez total de la nave.
     */
    private double dx, dy;
    private double velocidad = 5.0;
    
    /** Dimensiones de las naves y el ángulo hacia el cual deben apuntar al dibujarse. */
    private int ancho, alto;
    private double anguloRotacion;

    /** Coordenadas fijas del planeta hacia donde se dirige la nave del jugador. */
    private final double destinoX = 310.0;
    private final double destinoY = 280.0;
    
    /** 
     * Banderas de estado para controlar el flujo de la animación.
     * jugadorLlego: Indica si finalizó la fase 1 (nave en el planeta).
     * alcanzado: Indica si finalizó la fase 2 (enemigos colisionaron con el jugador).
     */
    private boolean jugadorLlego = false; 
    private boolean alcanzado = false;    

    /** 
     * Configuración del cerco enemigo.
     * cantidadEnemigos: Número de naves enemigas que rodearán al jugador.
     * radioCerco: Distancia inicial desde el centro hasta las naves enemigas.
     * velocidadCerco: Rapidez con la que el radio disminuye para acorralar al jugador.
     */
    private final int cantidadEnemigos = 8;
    private double radioCerco = 280.0; 
    private final double velocidadCerco = 4.0;

    /** Variables de tamaño del planeta, el cual crece gradualmente para dar un efecto de acercamiento. */
    private int diametroPlaneta = 300;
    private final int diametroMaximoPlaneta = 900;
    
    /** Variables de la explosión. El radio aumenta progresivamente hasta alcanzar el límite máximo. */
    private int radioExplosion = 0;
    private final int radioMaximo = 80;

    // ========== BOTÓN DE VOLVER A JUGAR ==========
    /** Botón de la interfaz gráfica para reiniciar el juego y volver al menú. */
    private JButton btnVolver;

    /**
     * Constructor de la pantalla de Game Over.
     * Inicializa las posiciones y calcula los vectores de movimiento y rotación.
     * 
     * @param xInicial   Posición inicial en el eje X de la nave del jugador.
     * @param yInicial   Posición inicial en el eje Y de la nave del jugador.
     * @param ancho      Ancho de la figura de la nave.
     * @param alto       Alto de la figura de la nave.
     * @param panelPadre Contenedor principal donde se agregará el botón de interfaz.
     */
    public GameOver(int xInicial, int yInicial, int ancho, int alto, JPanel panelPadre) {
        this.x = xInicial;
        this.y = yInicial;
        this.ancho = ancho;
        this.alto = alto;

        /*
         * Cálculo de trayectoria:
         * 1. Se calcula un vector de dirección restando la posición actual (x, y) del destino (destinoX, destinoY).
         * 2. Se utiliza el Teorema de Pitágoras (a² + b² = c²) para obtener la 'magnitud', que es la distancia total en línea recta.
         * 3. Al dividir dirX y dirY entre esta magnitud, se obtiene un vector "normalizado" (de longitud 1).
         * 4. Este vector normalizado garantiza que, al multiplicarlo por la 'velocidad' más adelante, la nave se mueva a un ritmo constante sin importar qué tan lejos esté.
         */
        double dirX = destinoX - x;
        double dirY = destinoY - y;
        double magnitud = Math.sqrt(dirX * dirX + dirY * dirY);

        if (magnitud > 0) {
            this.dx = dirX / magnitud;
            this.dy = dirY / magnitud;
        } else {
            this.dx = 0;
            this.dy = 0;
            this.jugadorLlego = true;
        }

        /*
         * Cálculo de ángulo:
         * Math.atan2(y, x) devuelve el ángulo en radianes de un vector bidimensional.
         * Como la figura del triángulo (la nave) apunta hacia arriba por defecto (0 grados en dibujo),
         * se le suma Math.PI / 2 (equivalente a 90 grados) para que la "punta" del triángulo 
         * quede alineada correctamente con la trayectoria de vuelo.
         */
        this.anguloRotacion = Math.atan2(dirY, dirX) + (Math.PI / 2);
        
        // --- CONFIGURACIÓN DEL BOTÓN ---
        btnVolver = new JButton("Volver a jugar");
        
        // Posicionamiento absoluto del botón (Centrado en X, en la parte inferior de Y)
        btnVolver.setBounds(350, 600, 200, 50); 
        btnVolver.setFont(new Font("Arial", Font.BOLD, 18));
        btnVolver.setFocusable(false);
        btnVolver.setVisible(false); // Oculto hasta que termine la explosión
        
        // Lógica de acción al presionar el botón
        btnVolver.addActionListener(e -> {
            // Se instancia y muestra el menú principal
            interfaces.FrMenu menu = new interfaces.FrMenu();
            menu.setVisible(true);
            menu.setLocationRelativeTo(null);
            
            // Se busca la ventana contenedora actual y se destruye para liberar recursos
            Window ventanaActual = SwingUtilities.getWindowAncestor(panelPadre);
            if (ventanaActual != null) {
                ventanaActual.dispose();
            }
        });
        
        // Se desactiva el LayoutManager del panel para permitir coordenadas absolutas (setBounds)
        panelPadre.setLayout(null);
        panelPadre.add(btnVolver);
    }

    /**
     * Actualiza la lógica de movimiento y los estados de la animación en cada fotograma.
     * Se divide en tres bloques lógicos correspondientes a las fases de la animación.
     */
    public void mover() {
        // FASE 1: La nave viaja hacia el planeta.
        if (!jugadorLlego) {
            // Se calcula nuevamente la distancia usando el Teorema de Pitágoras para saber cuánto falta.
            double distanciaRestante = Math.sqrt(Math.pow(destinoX - x, 2) + Math.pow(destinoY - y, 2));

            if (distanciaRestante <= velocidad) {
                // Si la distancia es menor que un paso de velocidad, encaja exactamente en el centro.
                x = destinoX;
                y = destinoY;
                jugadorLlego = true; 
                diametroPlaneta = diametroMaximoPlaneta;
            } else {
                // Se actualiza la posición sumando el vector de dirección normalizado multiplicado por la velocidad.
                x += dx * velocidad;
                y += dy * velocidad;

                // El planeta crece para simular que la nave se está acercando visualmente.
                if (diametroPlaneta < diametroMaximoPlaneta) {
                    diametroPlaneta += 4;
                }
            }
        } 
        // FASE 2: Los enemigos convergen hacia la nave del jugador.
        else if (!alcanzado) {
            radioCerco -= velocidadCerco; // Se reduce el radio del círculo enemigo.

            if (radioCerco <= 0) {
                radioCerco = 0;
                alcanzado = true; // Colisión detectada, finaliza la fase 2.
            }
        } 
        // FASE 3: Generación de la explosión y activación del botón.
        else {
            if (radioExplosion < radioMaximo) {
                radioExplosion += 4;
            } else {
                // Al terminar la explosión, se muestra el botón.
                if (!btnVolver.isVisible()) {
                    btnVolver.setVisible(true);
                }
            }
        }
    }
    
    /**
     * Verifica si la animación de la explosión ha alcanzado su tamaño máximo.
     * 
     * @return true si los enemigos colisionaron y la explosión está completa; false en caso contrario.
     */
    public boolean terminoExplosion() {
        return alcanzado && radioExplosion >= radioMaximo;
    }

    /**
     * Renderiza los componentes visuales de la pantalla (planeta, naves, explosión y texto)
     * basándose en el estado actual de la animación.
     * 
     * @param g Objeto Graphics utilizado para dibujar en el componente.
     */
    public void dibujar(Graphics g) {
        // ===== 1. PLANETA DE FONDO =====
        // Se usa un color naranja óxido similar a la superficie de Marte
        g.setColor(new Color(193, 68, 14));
        g.fillOval(-diametroPlaneta / 2, -diametroPlaneta / 2, diametroPlaneta, diametroPlaneta);

        // Nave y emboscada
        if (!alcanzado) {
            // Dibuja la nave del jugador (Verde)
            dibujarTriangulo(g, x, y, anguloRotacion, Color.GREEN);

            // Una vez que el jugador aterriza, se dibuja el anillo de enemigos.
            if (jugadorLlego) {
                for (int i = 0; i < cantidadEnemigos; i++) {
                    
                    /*
                     * Posicionamiento Circular:
                     * 1. Se divide el círculo completo (2 * PI en radianes) entre la cantidad de enemigos.
                     *    Esto nos da la separación exacta (anguloPosicion) que debe tener cada nave respecto a la anterior.
                     * 2. Se convierte este sistema de Coordenadas Polares a Cartesianas usando Seno y Coseno:
                     *    X = centroX + (cos(ángulo) * radio)
                     *    Y = centroY + (sin(ángulo) * radio)
                     * Así, cada nave toma una posición formando un círculo perfecto.
                     */
                    double anguloPosicion = i * (2 * Math.PI / cantidadEnemigos);
                    
                    double enemigoX = destinoX + Math.cos(anguloPosicion) * radioCerco;
                    double enemigoY = destinoY + Math.sin(anguloPosicion) * radioCerco;

                    // A diferencia de la posición, la nave debe mirar hacia el centro. 
                    // Se le resta PI / 2 (90 grados) para que la punta del triángulo apunte correctamente hacia el jugador.
                    double anguloMirada = anguloPosicion - (Math.PI / 2);

                    dibujarTriangulo(g, enemigoX, enemigoY, anguloMirada, Color.RED);
                }
            }
        } else {
            // Explosion del Planeta
            if (radioExplosion < radioMaximo) {
                // Aura naranja exterior de la explosión
                g.setColor(Color.ORANGE);
                g.fillOval((int) destinoX - radioExplosion, (int) destinoY - radioExplosion, 
                          radioExplosion * 2, radioExplosion * 2);

                // Núcleo amarillo interior de la explosión
                int radioNucleo = radioExplosion / 2;
                g.setColor(Color.YELLOW);
                g.fillOval((int) destinoX - radioNucleo, (int) destinoY - radioNucleo, 
                          radioNucleo * 2, radioNucleo * 2);
            }
            
            // Texto de Game Over
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("GAME OVER", 270, 350);
        }
    }

    /**
     * Método auxiliar para renderizar una nave con forma triangular orientada hacia un ángulo específico.
     * 
     * @param g        Objeto Graphics base.
     * @param posX     Posición en el eje X donde se dibujará la nave.
     * @param posY     Posición en el eje Y donde se dibujará la nave.
     * @param rotacion Ángulo en radianes que determina hacia dónde apunta la nave.
     * @param color    Color con el que se rellenará el triángulo.
     */
    private void dibujarTriangulo(Graphics g, double posX, double posY, double rotacion, Color color) {
        // Se crea una copia del contexto gráfico (Graphics2D) para aislar las transformaciones de rotación
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Se traslada el origen al punto exacto donde se dibujará y se rota el contexto
        g2d.translate(posX, posY);
        g2d.rotate(rotacion);

        // Se definen los tres vértices del triángulo centrados en (0,0) del contexto ya rotado
        int medioAncho = ancho / 2;
        int medioAlto = alto / 2;
        int[] puntosX = { 0, -medioAncho, medioAncho };
        int[] puntosY = { -medioAlto, medioAlto, medioAlto };

        g2d.setColor(color);
        g2d.fillPolygon(puntosX, puntosY, 3);
        
        // Se liberan los recursos gráficos del contexto temporal
        g2d.dispose();
    }
}