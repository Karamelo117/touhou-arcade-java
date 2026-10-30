package interfaces;

import clases.Estrella; 
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Panel interactivo y animado que muestra la cinemática de introducción del juego.
 * 
 * EXPLICACIÓN LÓGICA DE LA CINEMÁTICA:
 * En lugar de desplazar un objeto (la nave) por un mapa inmenso, se utiliza una técnica 
 * de ilusión óptica: la nave permanece anclada cerca del centro de la pantalla, mientras
 * que el entorno (fondo de estrellas, textos decorativos y líneas) se desplaza verticalmente.
 * Esto genera la percepción de avance a gran velocidad por el espacio profundo.
 * 
 * Adicionalmente, cuenta con un sistema de escritura secuencial (efecto máquina de escribir)
 * para narrar la historia, e implementa textos dinámicos de ayuda para enseñar los controles
 * básicos al jugador durante el proceso.
 */
public class Instrucciones extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    /** Temporizador principal que actúa como el motor físico y de renderizado (loop del juego). */
    private Timer reloj;
    
    /** Constante que define el ancho total de la ventana de la cinemática. */
    private final int ANCHO = 900;
    
    /** Constante que define el alto total de la ventana de la cinemática. */
    private final int ALTO = 700;
    
    /** Colección de objetos Estrella que simulan el campo estelar en movimiento. */
    private ArrayList<Estrella> estrellas;

    /** Posición de la nave en el eje X (centrada horizontalmente). */
    private double naveX = ANCHO / 2.0;
    
    /** Posición actual de la nave en el eje Y. */
    private double naveY;
    
    /** Aceleración/Velocidad actual de la nave en el eje Y para los momentos de despegue. */
    private double velNaveY = 0.0;

    /** 
     * Desplazamiento global (Offset).
     * Modificar esta variable mueve todo el entorno gráfico (textos, líneas) en bloque,
     * simulando el avance o retroceso de la cámara en el espacio.
     */
    private int offsetY; 

    /** 
     * Arreglo con la historia del juego. 
     * Índices 0-2 corresponden a la primera fase; índices 3-5 a la segunda fase.
     */
    private final String[] lineasTexto = {
        "Es el año 3200, el núcleo de la tierra esta desacelerando",
        "y por tanto empezando a enfriarse",
        "Se te asigno la misión de explorar el universo",
        "Encontraste un planeta habitable, mas hay un problema...",
        "No son la única civilización que atraviesa un evento",
        "similar, tendrás que pelear por tu lugar."
    };

    /** Longitud actual de las líneas de decoración azules dibujadas en pantalla. */
    private double progresoLinea = 0;
    
    /** Límite máximo de expansión para las líneas azules decorativas. */
    private final double maxProgresoLinea = 350;

    /** 
     * Variable de control de estado (Máquina de estados).
     * Define qué acción se está ejecutando:
     * 0 = Entrada inicial, 1 = Escribir parte 1, 2 = Salto hiperespacial 1,
     * 3 = Entrada secundaria, 4 = Escribir parte 2, 5 = Salto hiperespacial 2,
     * 6 = Cinemática finalizada (Esperando clic en el botón).
     */
    private int fase = 0; 
    
    /** Temporizador secundario para medir el tiempo de lectura que se le da al jugador. */
    private int contadorTiempo = 0;
    
    /** Cantidad de ciclos (ticks) que la pantalla se pausa para permitir leer el texto. */
    private final int TIEMPO_LECTURA = 60; 

    /** Índice de la línea de texto (del bloque actual) que se está procesando. */
    private int lineaActual = 0; 
    
    /** Cantidad de caracteres revelados hasta el momento en la línea actual. */
    private int charIndex = 0;   

    /** Botón para finalizar la cinemática y regresar al menú. */
    private JButton btnCerrar;

    /**
     * Constructor que inicializa el lienzo, la física inicial y los componentes gráficos.
     */
    public Instrucciones() {
        setPreferredSize(new java.awt.Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);
        setLayout(null); // Desactivamos el layout manager para posicionar el botón libremente
        
        // Inicialización del botón para continuar al menú
        btnCerrar = new JButton("Continuar al Menú");
        // Lo centramos en la pantalla
        btnCerrar.setBounds((ANCHO / 2) - 100, (ALTO / 2) - 25, 200, 50);
        btnCerrar.setBackground(new Color(40, 40, 40));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        btnCerrar.setBorder(javax.swing.BorderFactory.createLineBorder(Color.CYAN, 2));
        btnCerrar.setFocusPainted(false);
        // LO HACEMOS INVISIBLE HASTA QUE TERMINE LA ANIMACIÓN
        btnCerrar.setVisible(false); 
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saltarAlMenu();
            }
        });
        add(btnCerrar);
        
        // Generación del campo estelar (1000 estrellas aleatorias)
        estrellas = new ArrayList<>();
        for(int i = 0; i < 1000; i++){
            estrellas.add(new Estrella(ANCHO, ALTO));
        }
        
        // Posiciones iniciales ocultas (fuera de la pantalla)
        naveY = ALTO + 50; 
        offsetY = -ALTO;

        // Inicia el loop del panel a ~50 FPS (20ms)
        reloj = new Timer(20, this); 
        reloj.start();
    }

    /**
     * Método central de actualización invocado por el Timer.
     * Procesa la lógica de movimiento, el estado de la cinemática y refresca la pantalla.
     * 
     * @param e Evento de acción disparado por el Timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Efecto visual: Acelera el movimiento de las estrellas durante los saltos hiperespaciales
        int velocidadEstrellas = (fase == 2 || fase == 5) ? 5 : 1;
        for(int k = 0; k < velocidadEstrellas; k++) {
            for (Estrella estrella : estrellas) {
                estrella.mover(ALTO);
            }
        }
        
        // Lógica de control delegada según la fase de la cinemática
        switch (fase) {
            case 0:
                if (moverLlegada()) fase = 1;
                break;
            case 1:
                if (leerYDibujar()) fase = 2;
                break;
            case 2:
                if (moverDespegue()) {
                    prepararSiguienteBloque();
                    fase = 3;
                }
                break;
            case 3:
                if (moverLlegada()) fase = 4;
                break;
            case 4:
                if (leerYDibujar()) fase = 5;
                break;
            case 5:
                if (moverDespegue()) {
                    // ¡Animación terminada!
                    fase = 6;
                    // Mostramos el botón para que el usuario pueda avanzar
                    btnCerrar.setVisible(true); 
                }
                break;
            case 6:
                // En la fase 6 no hay animaciones de textos ni nave, 
                // pero el Timer sigue corriendo para que las estrellas se sigan moviendo
                // de fondo mientras el jugador pulsa el botón.
                break;
        }
        
        // Refresca los gráficos en pantalla tras actualizar la lógica
        repaint();
    }

    /**
     * Termina la ejecución de la cinemática, destruye la ventana actual
     * e invoca la aparición del Menú Principal.
     */
    private void saltarAlMenu() {
        if (reloj != null) {
            reloj.stop(); // Detenemos definitivamente el renderizado
        }
        
        // Obtiene el JFrame contenedor para destruirlo y liberar memoria
        Window ventanaActual = SwingUtilities.getWindowAncestor(this);
        if (ventanaActual != null) {
            ventanaActual.dispose();
        }
        
        // Despliega la nueva interfaz del menú
        FrMenu menu = new FrMenu();
        menu.setLocationRelativeTo(null); 
        menu.setVisible(true);
    }

    // --- MÉTODOS DE LÓGICA Y ANIMACIÓN ---

    /**
     * Anima la entrada coordinada de la nave y el entorno gráfico.
     * 
     * @return true si tanto la nave como el escenario han alcanzado su posición final.
     */
    private boolean moverLlegada() {
        boolean naveLista = false;
        boolean textoListo = false;

        // Desplaza la nave hacia el centro vertical
        if (naveY > ALTO / 2.0) {
            naveY -= 10; 
            if (naveY <= ALTO / 2.0) {
                naveY = ALTO / 2.0;
                naveLista = true;
            }
        } else {
            naveLista = true;
        }

        // Desplaza el entorno gráfico (textos y líneas) a su posición original
        if (offsetY < 0) {
            offsetY += 15; 
            if (offsetY >= 0) {
                offsetY = 0;
                textoListo = true;
            }
        } else {
            textoListo = true;
        }

        return naveLista && textoListo;
    }

    /**
     * Gestiona el efecto de escritura caracter por caracter de la historia,
     * así como la expansión de las líneas decorativas.
     * 
     * @return true cuando se han terminado de escribir las 3 líneas correspondientes
     *         y ha concluido el tiempo de lectura.
     */
    private boolean leerYDibujar() {
        if (progresoLinea < maxProgresoLinea) {
            progresoLinea += 12; 
        }

        int offset = (fase < 3) ? 0 : 3;

        // Escritura progresiva del texto
        if (lineaActual < 3) {
            String texto = lineasTexto[offset + lineaActual];
            charIndex++; 
            
            if (charIndex > texto.length()) {
                charIndex = 0;
                lineaActual++; 
            }
            return false;
        }

        // Tiempo de espera para lectura
        contadorTiempo++;
        if (contadorTiempo > TIEMPO_LECTURA) {
            contadorTiempo = 0;
            velNaveY = 0;
            return true;
        }
        
        return false;
    }

    /**
     * Simula la aceleración extrema (despegue) empujando la nave hacia arriba
     * y el entorno gráfico velozmente hacia abajo.
     * 
     * @return true si la nave ha salido completamente del encuadre superior.
     */
    private boolean moverDespegue() {
        velNaveY += 1.5; 
        naveY -= velNaveY; 
        
        offsetY += (int)(velNaveY * 1.5); 
        
        return naveY < -50;
    }

    /**
     * Reinicia las variables físicas y los punteros de texto para preparar
     * la entrada del segundo bloque de texto
     */
    private void prepararSiguienteBloque() {
        naveY = ALTO + 50; 
        offsetY = -ALTO;   
        progresoLinea = 0;
        velNaveY = 0;
        lineaActual = 0;
        charIndex = 0;
    }

    /**
     * Función utilitaria para determinar qué porción de una línea de texto
     * debe renderizarse en un fotograma específico.
     * 
     * @param textoCompleto String completo de la frase.
     * @param lineaDeEsteTexto Índice local (0-2) de la frase dentro del bloque actual.
     * @return Fragmento exacto del texto a pintar en pantalla.
     */
    private String obtenerSubcadena(String textoCompleto, int lineaDeEsteTexto) {
        if (lineaActual > lineaDeEsteTexto) {
            return textoCompleto; 
        } else if (lineaActual == lineaDeEsteTexto) {
            int limite = Math.min(charIndex, textoCompleto.length());
            return textoCompleto.substring(0, limite);
        } else {
            return ""; 
        }
    }



    /**
     * Proceso de renderizado visual del panel.
     * Capas dibujadas en orden: 1. Estrellas, 2. Líneas, 3. Textos, 4. Nave y Textos de Ayuda.
     * 
     * @param g Objeto Graphics que provee las herramientas de dibujo del sistema.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. CAPA DE FONDO: Renderizado de estrellas
        for (Estrella e : estrellas) {
            e.pintar(g);
        }

        // Si la cinemática ya terminó (fase 6), omitimos dibujar líneas, textos y la nave
        if (fase < 6) {
            // Cálculo de posiciones verticales dinámicas mediante el offsetY
            int y1 = 120 + offsetY;
            int y2 = (ALTO / 2) - 60 + offsetY; 
            int y3 = (ALTO - 100) + offsetY;

            // 2. CAPA DECORATIVA: Líneas azules de expansión
            g.setColor(Color.CYAN);
            int trazo = (int) progresoLinea;
            
            if (trazo > 0) { 
                g.drawLine(ANCHO, y1, ANCHO - trazo, y1);
                g.drawLine(0, y2, trazo, y2);
                g.drawLine(ANCHO, y3, ANCHO - trazo, y3);
            }

            // 3. CAPA DE TEXTO: Renderizado de la narrativa
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            
            FontMetrics fm = g.getFontMetrics();
            int offset = (fase < 3) ? 0 : 3;

            String textoCompleto1 = lineasTexto[offset];
            String textoCompleto2 = lineasTexto[offset + 1];
            String textoCompleto3 = lineasTexto[offset + 2];

            int x1 = ANCHO - fm.stringWidth(textoCompleto1) - 20;
            int x2 = 20; 
            int x3 = ANCHO - fm.stringWidth(textoCompleto3) - 20;

            String l1 = obtenerSubcadena(textoCompleto1, 0);
            String l2 = obtenerSubcadena(textoCompleto2, 1);
            String l3 = obtenerSubcadena(textoCompleto3, 2);

            g.drawString(l1, x1, y1 - 10);
            g.drawString(l2, x2, y2 - 10);
            g.drawString(l3, x3, y3 - 10);

            // 4. CAPA SUPERIOR: Nave del jugador y Tooltips (Textos de ayuda)
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(naveX, naveY); 

            // Gráfico de la nave
            int[] puntosX = { 0, -15, 15 };
            int[] puntosY = { -20, 15, 15 };

            g2d.setColor(Color.GREEN);
            g2d.fillPolygon(puntosX, puntosY, 3);
            
            // Lógica de textos instructivos (Tooltips)
            g2d.setColor(Color.CYAN);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            String textoAyuda = "";
            
            // Determinar qué mensaje mostrar dependiendo de la fase
            if (fase == 1 || fase == 2) {
                textoAyuda = "Para moverte usa la flecha izquierda o derecha";
            } else if (fase == 4 || fase == 5) {
                textoAyuda = "Para disparar presiona \"Espacio\"";
            }
            
            // Si hay un mensaje asignado, dibujarlo centrado debajo de la nave
            if (!textoAyuda.isEmpty()) {
                FontMetrics fmAyuda = g2d.getFontMetrics();
                int xAyuda = -fmAyuda.stringWidth(textoAyuda) / 2;
                int yAyuda = 45; // 45 pixeles por debajo del centro de la nave
                g2d.drawString(textoAyuda, xAyuda, yAyuda);
            }
            
            g2d.dispose();
        }
    }
}