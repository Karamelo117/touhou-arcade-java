package interfaces;

import clases.Disparo;
import clases.Enemigo;
import clases.Estrella;
import clases.Jugador1;
import basedatos.JugadorDAO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Panel principal y motor de ejecución del videojuego.
 * 
 * Centraliza el bucle principal (game loop) gestionando el renderizado de gráficos,
 * la actualización de estados físicos, la detección de colisiones espaciales,
 * la progresión de niveles por oleadas de dificultad y la transición hacia
 * las cinemáticas finales (victoria o derrota).
 */
public class PanJuego extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    // ========== CONSTANTES DE PANTALLA ==========
    /** Ancho total de la ventana de juego en píxeles. */
    private final int ANCHO = 900;
    
    /** Alto total de la ventana de juego en píxeles. */
    private final int ALTO = 700;
    
    // ========== CONFIGURACIÓN DE NIVELES Y PUNTOS ==========
    /** Umbral de puntos acumulados para ascender al Nivel 2. */
    private final int PUNTOS_NIVEL_2 = 200;
    
    /** Umbral de puntos acumulados para ascender al Nivel 3. */
    private final int PUNTOS_NIVEL_3 = 400;
    
    /** Puntuación requerida para detonar la condición de victoria. */
    private final int PUNTOS_VICTORIA = 600;

    // ========== CONFIGURACIÓN DE ENEMIGOS ==========
    /** Límite de naves enemigas simultáneas en el Nivel 1. */
    private final int MAX_ENEMIGOS_NIVEL_1 = 3;
    
    /** Límite de naves enemigas simultáneas en el Nivel 2. */
    private final int MAX_ENEMIGOS_NIVEL_2 = 5;
    
    /** Límite de naves enemigas simultáneas en el Nivel 3. */
    private final int MAX_ENEMIGOS_NIVEL_3 = 8;

    // ========== VARIABLES DE JUEGO ==========
    /** Temporizador central que dispara las actualizaciones del juego cada 20 ms (~50 FPS). */
    private Timer reloj;
    
    /** Lista de partículas estelares para el efecto visual de fondo infinito. */
    private ArrayList<Estrella> estrellas;
    
    /** Lista dinámica de enemigos activos en el escenario. */
    private ArrayList<Enemigo> enemigos;
    
    /** Lista de proyectiles disparados por la nave del jugador. */
    private ArrayList<Disparo> balas;
    
    /** Entidad controlada por el usuario. */
    private Jugador1 jugador;

    /** Velocidades de desplazamiento vertical asignadas a los enemigos según el nivel. */
    private int vel1 = 4, vel2 = 6, vel3 = 8;
    
    /** Banderas de estado para el movimiento fluido del jugador mediante teclado. */
    private boolean tecla_izquierda = false, tecla_derecha = false;
    
    /** 
     * Puntuación acumulada por el jugador.
     * Al ser estática, debe ser reiniciada obligatoriamente al construir el panel.
     */
    public static int puntos = 0;
    
    /** Cantidad de puntos de vida restantes antes de perder la partida. */
    private int vidas = 5;
    
    /** Nivel de dificultad actual del juego (1, 2 o 3). */
    private int nivel = 0;

    // ========== ANIMACIONES FINALES ==========
    /** Controlador y renderizador de la cinemática de victoria. */
    WinGame wGame;
    
    /** Controlador y renderizador de la cinemática de fin de juego por derrota. */
    GameOver oGame;
    
    /** Enumerador de estados globales de la partida para separar actualización física de cinemáticas. */
    enum Estado { JUGANDO, TERMINANDO }
    
    /** Estado operativo inicial del panel. */
    Estado estado = Estado.JUGANDO;

    /**
     * Constructor del panel de juego.
     * Inicializa las variables críticas, las colecciones de entidades, el fondo estelar,
     * los oyentes de eventos de teclado y arranca el temporizador maestro.
     */
    public PanJuego() {
        setPreferredSize(new java.awt.Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);

        // Reinicio de variables estáticas y vitales para garantizar limpieza entre partidas
        puntos = 0;
        vidas = 5;

        estrellas = new ArrayList<>();
        enemigos = new ArrayList<>();
        balas = new ArrayList<>();
        jugador = new Jugador1();

        crearEstrellas();
        
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        reloj = new Timer(20, this);
        reloj.start();
    }

    /**
     * Renderiza todos los elementos gráficos en el panel estructurados en capas.
     * Capa 1: Campo estelar de fondo.
     * Capa 2: Elementos tácticos (enemigos, proyectiles, HUD, jugador) o cinemáticas.
     * 
     * @param g Objeto Graphics utilizado para dibujar en el componente.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Capa 1: Fondo espacial
        for (Estrella e : estrellas) {
            e.pintar(g);
        }

        // Capa 2: Lógica condicional de renderizado según el estado del juego
        if (estado == Estado.JUGANDO) {
            
            for (Enemigo e : enemigos) {
                if (e.estaActivo()) {
                    e.dibujar(g);
                }
            }
            
            for (Disparo d : balas) {
                d.dibujar(g);
            }

            // Renderizado del HUD (Heads-Up Display)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            
            g.drawString("VIDAS: " + vidas, 30, 40);
            g.drawString("NIVEL: " + nivel, (ANCHO / 2) - 40, 40);
            g.drawString("PUNTOS: " + puntos, ANCHO - 130, 40);

            jugador.dibujar(g);

        } else if (estado == Estado.TERMINANDO) {
            // Delegación de renderizado a las clases cinemáticas correspondientes
            if (wGame != null) {
                wGame.dibujar(g);
            } else if (oGame != null) {
                oGame.dibujar(g);
            }
        }
    }

    /**
     * Ejecuta las rutinas de actualización vectorial, resolución de colisiones 
     * y procesamiento de entradas físicas en cada tick del reloj.
     */
    public void actualizar() {
        moverEstrellas();

        if (estado == Estado.JUGANDO) {
            moverEnemigos();
            plomear();
            colisionesBalas();
            colisionJugador();
            
            if (tecla_izquierda) {
                jugador.moverIzquierda();
            }
            if (tecla_derecha) {
                jugador.moverDerecha();
            }

        } else if (estado == Estado.TERMINANDO) {
            // El motor físico se detiene y solo se calculan los vectores de la cinemática
            if (wGame != null) {
                wGame.mover();
            } else if (oGame != null) {
                oGame.mover();
            }
        }
    }

    /**
     * Evalúa si las vidas del jugador se han agotado.
     * Si la condición se cumple, detiene la captura de movimiento, instancia la secuencia 
     * de Game Over forzando coordenadas dinámicas, y registra el puntaje final en MySQL mediante DAO.
     */
    private void seMurio() {
        if (vidas <= 0 && estado == Estado.JUGANDO) {
            estado = Estado.TERMINANDO;
            
            oGame = new GameOver(ANCHO - 50, ALTO - 50, 30, 30, this);
            
            tecla_izquierda = false;
            tecla_derecha = false;
            
            JugadorDAO dao = new JugadorDAO();
            dao.actualizarScore(FrLogin.usuarioActual, puntos);
        }
    }
    
    /**
     * Evalúa si el puntaje acumulado alcanza o supera la cuota de victoria.
     * De cumplirse, detiene el control físico, transiciona al estado terminal para ejecutar
     * WinGame, e invoca al DAO para actualizar el récord del usuario actual.
     */
    private void gano() {
        if (puntos >= PUNTOS_VICTORIA && estado == Estado.JUGANDO) {
            estado = Estado.TERMINANDO;
            
            wGame = new WinGame(jugador.getX(), jugador.getY(), 30, 30, this);
            
            tecla_izquierda = false;
            tecla_derecha = false;
            
            JugadorDAO dao = new JugadorDAO();
            dao.actualizarScore(FrLogin.usuarioActual, puntos);
        }
    }
    
    /**
     * Regula la curva de dificultad dinámica del juego.
     * 
     * LÓGICA DE PROGRESIÓN (Escalado de dificultad):
     * 1. Verifica en qué rango de puntos se encuentra el jugador.
     * 2. Si el umbral del siguiente nivel se cruza, purga la lista de enemigos en pantalla 
     *    para reiniciar la oleada con las nuevas propiedades físicas.
     * 3. Define la capacidad máxima del búfer de enemigos y su velocidad vectorial (Y).
     * 4. Inyecta nuevas instancias en la matriz si el número de enemigos activos es menor al permitido.
     */
    private void gestionarNivelesYEnemigos() {
        if (puntos >= PUNTOS_NIVEL_3 && nivel != 3) {
            nivel = 3;
            enemigos.clear(); 
        } else if (puntos >= PUNTOS_NIVEL_2 && puntos < PUNTOS_NIVEL_3 && nivel != 2) {
            nivel = 2;
            enemigos.clear();
        } else if (nivel == 0) {
            nivel = 1;
        }

        int maxEnemigos = 0;
        int velocidad = vel1;

        if (nivel == 1) {
            maxEnemigos = MAX_ENEMIGOS_NIVEL_1;
            velocidad = vel1;
        } else if (nivel == 2) {
            maxEnemigos = MAX_ENEMIGOS_NIVEL_2;
            velocidad = vel2;
        } else if (nivel == 3) {
            maxEnemigos = MAX_ENEMIGOS_NIVEL_3;
            velocidad = vel3;
        }

        int enemigosActuales = enemigosActivos();
        
        if (enemigosActuales < maxEnemigos) {
            enemigos.add(new Enemigo(velocidad));
        }
    }

    /**
     * Inicializa el vector de fondo dinámico instanciando la cantidad estipulada de estrellas.
     */
    public void crearEstrellas() {
        for (int i = 0; i < 1000; i++) {
            estrellas.add(new Estrella(ANCHO, ALTO));
        }
    }

    /**
     * Recorre la colección estelar solicitando su actualización espacial.
     */
    public void moverEstrellas() {
        for (Estrella e : estrellas) {
            e.mover(ALTO);
        }
    }

    /**
     * Procesa la actualización física de la matriz enemiga.
     * Elimina lógicamente a los enemigos que han rebasado los límites inferiores del panel.
     */
    private void moverEnemigos() {
        for (Enemigo e : enemigos) {
            e.actualizar();
            e.revisarSalida();
        }
    }

    /**
     * Aplica la traslación vertical a la matriz de proyectiles y purga de memoria (Garbage Collection)
     * aquellos que superan la cota Y=0 para optimizar el rendimiento.
     */
    public void plomear() {
        for (int i = 0; i < balas.size(); i++) {
            balas.get(i).mover();
            if (balas.get(i).getY() < 0) {
                balas.remove(i);
                i--;
            }
        }
    }

    /**
     * Recorre el arreglo de enemigos verificando la bandera lógica de estado interno.
     * 
     * @return Número de entidades enemigas que se encuentran dentro de los límites y vivas.
     */
    public int enemigosActivos() {
        int contador = 0;
        for (Enemigo e : enemigos) {
            if (e.estaActivo()) {
                contador++;
            }
        }
        return contador;
    }
    
    /**
     * Algoritmo de detección de impactos entre proyectiles y enemigos.
     * 
     * EXPLICACIÓN MATEMÁTICA (Colisiones Radiales):
     * 1. Toma el centro geométrico de un proyectil (x1, y1) y el de un enemigo (x2, y2).
     * 2. Aplica el Teorema de Pitágoras para calcular la hipotenusa (distancia lineal euclidiana)
     *    entre ambas coordenadas: d = √((x2 - x1)² + (y2 - y1)²).
     * 3. Si la magnitud resultante es estrictamente menor al umbral de intersección (30 px), 
     *    los objetos colisionan. El enemigo se inactiva, se conceden puntos y el proyectil se elimina.
     */
    public void colisionesBalas() {
        
        for (int i = 0; i < balas.size(); i++) {
            Disparo dis = balas.get(i);
            
            boolean impacto = false;
            
            for (Enemigo e : enemigos) {
                if (e.estaActivo()) {
                    double distancia = Math.sqrt(Math.pow(dis.getX() - e.getX(), 2) + Math.pow(dis.getY() - e.getY(), 2));
                    if (distancia < 30) {
                        e.morir();
                        puntos += 20;
                        impacto = true;
                        
                        break;
                    }
                }
            }
            
            // Eliminación segura en iteración de arraylist
            if (impacto) {
                balas.remove(i);
                i--;
            }
        }
    }

    /**
     * Algoritmo de detección de colisiones fatales.
     * Utiliza la misma lógica euclidiana descrita en colisionesBalas() para verificar
     * intersecciones espaciales entre el área hitbox del jugador y los enemigos activos.
     * Decrementa el contador de vidas ante impactos positivos.
     */
    public void colisionJugador() {
        for (Enemigo en : enemigos) {
            if (en.estaActivo()) {
                double distancia = Math.sqrt(Math.pow(jugador.getX() - en.getX(), 2) + Math.pow(jugador.getY() - en.getY(), 2));
                if (distancia < 30) {
                    vidas -= 1; 
                    en.morir();
                }
            }
        }
    }
    
    /**
     * Bucle maestro invocado de forma asíncrona por la clase Timer.
     * Despacha el pipeline completo: recálculo físico, evaluaciones de finalización, 
     * manejo de memoria dinámica y solicitud asíncrona de renderizado (repaint).
     * 
     * @param e Evento temporal encapsulado por el Swing Timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        actualizar();
        seMurio();
        gano();
        
        if (estado == Estado.JUGANDO) {
            gestionarNivelesYEnemigos();
            // Purga lambda: elimina físicamente del arraylist cualquier enemigo no activo
            enemigos.removeIf(enemigo -> !enemigo.estaActivo());
        }
        
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Oyente de hardware para interceptar presiones de teclado.
     * Enciende las banderas lógicas de movimiento lateral e inyecta
     * instancias de 'Disparo' originadas en las coordenadas del jugador.
     * 
     * @param e Código del hardware interceptado.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (estado == Estado.JUGANDO) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                tecla_izquierda = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                tecla_derecha = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                balas.add(new Disparo(jugador.getX() - 2, jugador.getY()));
            }
        }
    }

    /**
     * Oyente de hardware para interceptar liberaciones de teclado.
     * Apaga las banderas lógicas de movimiento, garantizando inercia cero (frenado inmediato).
     * 
     * @param e Código del hardware interceptado.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            tecla_izquierda = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            tecla_derecha = false;
        }
    }
}