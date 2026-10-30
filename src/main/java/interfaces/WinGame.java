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
 * Clase que gestiona y renderiza la pantalla de victoria (WinGame).
 * Ejecuta una animación donde la nave viaja desde la esquina inferior derecha
 * hacia un planeta naranja. Al aterrizar, se despliega una bandera ondeando,
 * aparece el texto "GANASTE" y se muestra un botón para volver a jugar.
 */
public class WinGame {

    /** Coordenadas actuales de la nave en la pantalla. */
    private double x, y;
    
    /** Componentes del vector de dirección (dx, dy) hacia donde se desplaza la nave. */
    private double dx, dy;
    
    /** Magnitud de la velocidad a la que se desplaza la nave por fotograma. */
    private double velocidad = 5.0;
    
    /** Dimensiones visuales de la nave. */
    private int ancho, alto;
    
    /** Ángulo en radianes para rotar visualmente la nave hacia su destino. */
    private double anguloRotacion;

    /** Variables que controlan el tamaño del planeta para simular un efecto de acercamiento (zoom). */
    private int diametroPlaneta = 300; 
    private final int diametroMaximoPlaneta = 900; 
    
    /** Coordenadas fijas donde se dibujará la bandera de victoria. */
    private int banderaX = 100;
    private int banderaY = 130;

    /** Coordenadas centrales del planeta, que representan el punto de aterrizaje de la nave. */
    private final double destinoX = 310.0;
    private final double destinoY = 280.0;
    
    /** Bandera de estado que indica si la nave ya llegó al punto de destino. */
    private boolean alcanzado = false;

    /** Variable de tiempo acumulado utilizada en la función seno para animar el ondeado de la bandera. */
    private double tiempoBandera = 0.0;
    
    // ========== BOTÓN DE VOLVER A JUGAR ==========
    /** Botón de la interfaz gráfica para reiniciar el juego y volver al menú. */
    private JButton btnVolver;

    /**
     * Constructor de la pantalla de victoria.
     * 
     * @param xInicial   (Ignorado) Posición X original. Se omite para forzar la salida desde la esquina inferior derecha.
     * @param yInicial   (Ignorado) Posición Y original. Se omite para forzar la salida desde la esquina inferior derecha.
     * @param ancho      Ancho de la figura de la nave.
     * @param alto       Alto de la figura de la nave.
     * @param panelPadre Contenedor principal donde se agregará el botón de interfaz.
     */
    public WinGame(int xInicial, int yInicial, int ancho, int alto, JPanel panelPadre) {
        
        // MODIFICACIÓN: Se ignoran los parámetros xInicial e yInicial.
        // Se fuerza a la nave a aparecer siempre desde la esquina inferior derecha.
        this.x = 900.0; 
        this.y = 700.0; 
        
        this.ancho = ancho;
        this.alto = alto;

        /*
         * EXPLICACIÓN MATEMÁTICA (Cálculo de trayectoria):
         * 1. Se calcula la diferencia entre el punto de destino y el punto de origen (dirX, dirY).
         * 2. Se obtiene la 'magnitud' (distancia total) usando el Teorema de Pitágoras: √(x² + y²).
         * 3. Se normaliza el vector dividiendo dirX y dirY entre la magnitud. Esto da un vector unitario
         *    que indica solo la dirección. Luego, al multiplicarlo por la 'velocidad', la nave 
         *    se moverá a un ritmo constante sin importar la distancia inicial.
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
            this.alcanzado = true;
        }

        /*
         * EXPLICACIÓN MATEMÁTICA (Cálculo de ángulo):
         * Math.atan2(y, x) calcula el ángulo en radianes entre el eje X positivo y el punto dado.
         * Se suma Math.PI / 2 (90 grados) porque, por defecto, el polígono de la nave se dibuja
         * apuntando hacia arriba ("Norte"). Esto corrige la orientación para que mire hacia su destino.
         */
        this.anguloRotacion = Math.atan2(dirY, dirX) + (Math.PI / 2);
        
        // --- CONFIGURACIÓN DEL BOTÓN ---
        btnVolver = new JButton("Volver a jugar");
        
        // Posicionamiento absoluto del botón (Centrado en X, en la parte inferior de Y)
        btnVolver.setBounds(350, 600, 200, 50); 
        btnVolver.setFont(new Font("Arial", Font.BOLD, 18));
        btnVolver.setFocusable(false);
        btnVolver.setVisible(false); // Permanece oculto hasta que finaliza la animación de vuelo
        
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
     * Actualiza la lógica de movimiento en cada ciclo del juego.
     * Controla el acercamiento de la nave, el crecimiento del planeta y la animación de la bandera.
     */
    public void mover() {
        if (!alcanzado) {
            // Se calcula la distancia restante entre la nave y el destino
            double distanciaRestante = Math.sqrt(Math.pow(destinoX - x, 2) + Math.pow(destinoY - y, 2));

            if (distanciaRestante <= velocidad) {
                // Si la distancia es menor a un paso de velocidad, la nave encaja en el destino exacto
                x = destinoX;
                y = destinoY;
                alcanzado = true;  
                diametroPlaneta = diametroMaximoPlaneta;
                
                // Se hace visible el botón de volver a jugar
                if (!btnVolver.isVisible()) {
                    btnVolver.setVisible(true);
                }
                
            } else {
                // Se actualiza la posición sumando el vector de dirección por la velocidad
                x += dx * velocidad;
                y += dy * velocidad;

                // El planeta aumenta de tamaño para crear un efecto óptico de cercanía
                if (diametroPlaneta < diametroMaximoPlaneta) {
                    diametroPlaneta += 4;
                }
            }
        } else {
            // Si la nave ya aterrizó, se incrementa la variable de tiempo para animar la bandera
            tiempoBandera += 0.15;
        }
    }

    /**
     * Renderiza los componentes visuales de la pantalla de victoria.
     * 
     * @param g Objeto Graphics utilizado para dibujar en el panel.
     */
    public void dibujar(Graphics g) {
        // 1. PLANETA DE FONDO
        // Se calcula la esquina superior izquierda del óvalo para que el centro coincida con (0,0) relativo
        int planetaX = -diametroPlaneta / 2;
        int planetaY = -diametroPlaneta / 2;

        // Se usa un color naranja óxido similar a la superficie de Marte
        g.setColor(new Color(193, 68, 14));
        g.fillOval(planetaX, planetaY, diametroPlaneta, diametroPlaneta);

        // 2. BANDERA (Solo se dibuja si la nave ya alcanzó el destino)
        if (alcanzado) {
            dibujarBandera(g, banderaX, banderaY);
        }

        // 3. NAVE
        // Se aísla la rotación y traslación copiando el objeto Graphics
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(x, y);
        g2d.rotate(anguloRotacion);

        int medioAncho = ancho / 2;
        int medioAlto = alto / 2;

        // Se define un polígono triangular centrado en sus propias coordenadas (0,0)
        int[] puntosX = { 0, -medioAncho, medioAncho };
        int[] puntosY = { -medioAlto, medioAlto, medioAlto };

        g2d.setColor(Color.GREEN);
        g2d.fillPolygon(puntosX, puntosY, 3);
        
        // 4. TEXTO DE VICTORIA
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("GANASTE", 270, 350);

        // Se liberan los recursos de la copia de Graphics
        g2d.dispose();
    }

    /**
     * Dibuja una bandera con un efecto de animación de onda transversal (ondeado al viento).
     * 
     * @param g    Objeto Graphics para renderizar.
     * @param posX Posición en el eje X donde inicia el asta de la bandera.
     * @param posY Posición en el eje Y de la base del asta.
     */
    private void dibujarBandera(Graphics g, int posX, int posY) {
        int anchoBandera = 70;
        int altoBandera = 40;
        int largoAsta = 80;

        // Se dibuja el asta (poste) de color blanco
        g.setColor(Color.WHITE);
        g.fillRect(posX, posY - largoAsta, 4, largoAsta);

        int origenX = posX + 4;
        int origenY = posY - largoAsta;
        
        // Variables para controlar la física visual de la onda
        double amplitud = 6.0;    // Altura máxima de la curva de la tela
        double frecuencia = 0.07; // Cantidad de curvas a lo largo del ancho de la tela

        /*
         * EXPLICACIÓN MATEMÁTICA (Onda Senoidal):
         * Se dibuja la tela línea vertical por línea vertical.
         * Math.sin() genera una curva que sube y baja.
         * - 'i * frecuencia': Asigna un punto de la curva diferente a cada línea horizontal, formando la onda.
         * - '- tiempoBandera': Al restar el tiempo, la onda se desplaza hacia la derecha en cada fotograma.
         * - 'factorAnclaje': Es una fracción (de 0 a 1). Hace que la parte de la tela pegada al poste 
         *    (donde i=0) casi no se mueva, y la punta de la tela (donde i=anchoBandera) se mueva con toda la amplitud.
         */
        for (int i = 0; i < anchoBandera; i++) {
            double factorAnclaje = (double) i / anchoBandera;
            
            // Se calcula el desplazamiento vertical de esta línea específica de la tela
            int desplazamientoY = (int) (amplitud * factorAnclaje * 
                                         Math.sin((i * frecuencia) - tiempoBandera));

            int yArriba = origenY + desplazamientoY;
            int yAbajo = yArriba + altoBandera;

            // Se dibuja un hilo o línea vertical que compone la bandera
            g.setColor(Color.YELLOW);
            g.drawLine(origenX + i, yArriba, origenX + i, yAbajo);
        }
    }

    /**
     * Verifica el estado del aterrizaje de la nave.
     * 
     * @return true si la nave ha llegado al centro del planeta, false si sigue en trayecto.
     */
    public boolean llegoAlDestino() {
        return alcanzado;
    }
}