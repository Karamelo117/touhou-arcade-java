# Touhou Arcade 2D

Juego de disparos 2D tipo arcade desarrollado en Java. El proyecto integra un motor físico personalizado manejado por eventos, renderizado gráfico mediante Java Swing/AWT, y persistencia de datos (CRUD) conectada a una base de datos relacional para la gestión de usuarios y puntuaciones.

## Arquitectura y Tecnologías
* **Lenguaje:** Java 17
* **Gestor de Dependencias:** Maven
* **Base de Datos:** MySQL
* **Interfaz y Renderizado:** Java Swing & AWT (Graphics2D)
* **Patrones de Diseño:** Implementación del patrón DAO (Data Access Object) para separar la lógica de negocio de las consultas SQL.

## Características Principales
* **Motor de Juego (Game Loop):** Implementado a través de `javax.swing.Timer` a ~50 FPS para la actualización vectorial, renderizado de fondos dinámicos (paralaje de estrellas) y procesamiento de colisiones.
* **Detección de Colisiones:** Sistema matemático euclidiano para calcular las intersecciones radiales entre hitboxes dinámicos (proyectiles, nave y enemigos).
* **Curva de Dificultad Dinámica:** Sistema de escalado por oleadas condicionado a la puntuación del jugador, alterando la frecuencia de reaparición y los vectores de velocidad de las entidades enemigas.
* **Sistema de Autenticación:** Interfaz de login y registro de jugadores. El alta de usuarios incluye un algoritmo de generación pseudoaleatoria de contraseñas basándose en los datos ingresados.
* **Persistencia de Récords:** Actualización lógica del puntaje máximo (High Score) tras finalizar la partida verificando la superación del récord previo mediante condicionales SQL.

## Configuración y Ejecución Local

1. Clona este repositorio.
2. Asegúrate de tener un servidor MySQL local ejecutándose en el puerto `3306`.
3. Importa el esquema de la base de datos ejecutando el archivo `database.sql` incluido en la raíz.
4. (Opcional) Si tu servidor MySQL requiere credenciales distintas a `root` sin contraseña, modifícalas en `src/main/java/basedatos/Conexion.java`.
5. Compila y ejecuta la clase `FrMenu.java` o `FrLogin.java` para iniciar la aplicación.

## Deuda Técnica y Mejoras Futuras
Este proyecto representa una iteración funcional. Las siguientes áreas están identificadas para futuras refactorizaciones orientadas a estándares de producción:

* **Seguridad de Credenciales:** Las contraseñas actualmente se almacenan en texto plano. Se requiere integrar algoritmos de hashing en `JugadorDAO` antes de la inserción y durante la validación.
* **Gestión de Configuración:** Las credenciales de la base de datos están *hardcodeadas* en la clase `Conexion`. Deben ser migradas a variables de entorno o a un archivo `.properties` inyectado en tiempo de ejecución.
* **Manejo de Excepciones:** La falla de conexión JDBC finaliza abruptamente la JVM (`System.exit(0)`). El flujo debe refactorizarse para permitir reintentos de conexión y un cierre controlado de los hilos de Swing.
* **Optimización del Motor Gráfico:** Migrar el bucle de juego basado en `Swing Timer` a un hilo dedicado (`Thread` implementando `Runnable`) calculando *Delta Time*, para independizar la velocidad lógica de los FPS gráficos.