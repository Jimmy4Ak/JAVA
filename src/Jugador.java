import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Jugador {

    static final String RUTA = "//192.168.0.6/bingo/";
    static final String ROJO = "\u001B[31m";
    static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws Exception {

        Scanner teclado = new Scanner(System.in);

        // Registrar nombre
        System.out.print("Tu nombre: ");
        String nombre = teclado.nextLine();

        PrintWriter pw = new PrintWriter(
                new FileWriter(RUTA + "jugadores.txt", true));

        pw.println(nombre);
        pw.close();

        // Esperar a que se repartan los cartones
        while (!new File(RUTA + "repartidos.txt").exists()) {
            Thread.sleep(1000);
        }

        // Leer el carton asignado
        Scanner lector = new Scanner(
                new File(RUTA + "asignaciones.txt"));

        Carton carton = null;

        while (carton == null && lector.hasNextLine()) {

            String linea = lector.nextLine();

            if (linea.equals(nombre)) {

                carton = new Carton(lector);

            } else {

                for (int i = 0; i < 3; i++) {
                    if (lector.hasNextLine()) {
                        lector.nextLine();
                    }
                }
            }
        }

        lector.close();

        if (carton == null) {

            System.out.println("No he recibido carton. ¿Te registraste tarde?");
            teclado.close();
            return;

        } else {

            System.out.println();
            System.out.println("TU CARTON:");
            carton.mostrar();
        }

        System.out.println();
        System.out.println("Esperando a que empiece la partida...");
        System.out.println("--------------------------------------");

        Preguntas preguntas = new Preguntas();
        preguntas.cargar(RUTA + "preguntas.txt");

        boolean[] salio = new boolean[91];

        int[] bolas = new int[90];
        int disponibles = 0;
        int jugadas = 0;

        File archivoBingo = new File(RUTA + "bingo_conseguido.txt");

        // true = ha fallado la pregunta del bingo: ya no puede ganar
        boolean eliminado = false;

        // Bucle principal de la partida
        while (jugadas < 90) {

            // Comprobar si alguien ha conseguido el bingo
            if (archivoBingo.exists()) {
                mostrarGanador(archivoBingo);
                break;
            }

            File f = new File(RUTA + "bolas.txt");

            if (f.exists()) {

                Scanner sc = new Scanner(f);

                disponibles = 0;

                while (sc.hasNextInt() && disponibles < 90) {
                    bolas[disponibles] = sc.nextInt();
                    disponibles++;
                }

                sc.close();
            }

            if (jugadas < disponibles) {

                int bola = bolas[jugadas];

                salio[bola] = true;
                jugadas++;

                System.out.println();
                System.out.println("Bola " + jugadas + ": " + bola);
                System.out.println();

                mostrarTablero(salio);

                System.out.println();
                System.out.println("TU CARTON:");

                carton.mostrar();
                System.out.println();

                while (true) {

                    // Comprobar si otro jugador ha ganado
                    if (archivoBingo.exists()) {
                        mostrarGanador(archivoBingo);
                        teclado.close();
                        return;
                    }

                    // Si has fallado el bingo no marcas mas
                    if (eliminado) {
                        break;
                    }

                    System.out.print("Numero a marcar (0 = seguir): ");

                    String entrada = leerLinea(teclado, archivoBingo);

                    // Alguien ha cantado bingo mientras esperabas
                    if (entrada == null) {
                        System.out.println();
                        mostrarGanador(archivoBingo);
                        teclado.close();
                        return;
                    }

                    int num;

                    try {

                        num = Integer.parseInt(entrada.trim());

                    } catch (NumberFormatException e) {

                        System.out.println(
                                "Entrada no valida. Introduce un numero entero.");

                        continue;
                    }

                    if (num < 0 || num > 90) {

                        System.out.println(
                                "Numero no valido. Introduce un numero entre 0 y 90.");

                        continue;
                    }

                    if (num == 0) {
                        break;
                    }

                    if (!salio[num]) {

                        System.out.println("Ese numero todavia no ha salido.");
                        continue;
                    }

                    if (carton.marcar(num) == 1) {

                        System.out.println("Numero marcado correctamente.");

                        carton.mostrar();

                        // COMPROBAR LINEA

                        int fila = carton.lineaCompletada();

                        if (fila != -1) {

                            File archivoLinea =
                                    new File(RUTA + "linea_conseguida.txt");

                            if (archivoLinea.exists()) {

                                System.out.println(
                                        "La unica linea ya ha sido conseguida.");

                                carton.resolverLinea(fila, false);

                            } else {

                                System.out.println("¡HAS COMPLETADO UNA LINEA!");
                                System.out.println(
                                        "Responde correctamente para validarla.");

                                boolean acierto = preguntas.preguntar(teclado);

                                if (acierto) {

                                    try {

                                        if (archivoLinea.createNewFile()) {

                                            FileWriter fw =
                                                    new FileWriter(archivoLinea);

                                            fw.write(nombre);
                                            fw.close();

                                            carton.resolverLinea(fila, true);

                                            System.out.println();
                                            System.out.println("¡ENHORABUENA!");
                                            System.out.println(
                                                    "HAS CONSEGUIDO LA UNICA LINEA.");

                                        } else {

                                            carton.resolverLinea(fila, false);

                                            System.out.println(
                                                    "Otro jugador se ha adelantado.");

                                            System.out.println(
                                                    "La linea ya ha sido conseguida.");
                                        }

                                    } catch (Exception e) {

                                        System.out.println(
                                                "Error al registrar la linea.");
                                    }

                                } else {

                                    carton.resolverLinea(fila, false);

                                    System.out.println(
                                            "Respuesta incorrecta. LINEA RECHAZADA.");
                                }
                            }
                        }

                        // COMPROBAR BINGO (con pregunta)

                        if (carton.bingoCompletado()) {

                            if (archivoBingo.exists()) {

                                // Otro jugador ya ha cantado bingo
                                mostrarGanador(archivoBingo);
                                teclado.close();
                                return;

                            } else {

                                System.out.println();
                                System.out.println("¡HAS COMPLETADO EL CARTON!");
                                System.out.println(
                                        "Responde correctamente para cantar BINGO.");

                                boolean acierto = preguntas.preguntar(teclado);

                                // Solo hay un intento
                                carton.resolverBingo();

                                if (acierto) {

                                    try {

                                        // Solo el primero que acierte crea el archivo
                                        if (archivoBingo.createNewFile()) {

                                            FileWriter fw =
                                                    new FileWriter(archivoBingo);

                                            fw.write(nombre);
                                            fw.close();

                                            System.out.println();
                                            System.out.println("¡ENHORABUENA!");
                                            System.out.println(
                                                    "¡HAS GANADO LA PARTIDA!");

                                        } else {

                                            System.out.println(
                                                    "Otro jugador se ha adelantado.");

                                            mostrarGanador(archivoBingo);
                                        }

                                        teclado.close();
                                        return;

                                    } catch (Exception e) {

                                        System.out.println(
                                                "Error al registrar el BINGO.");
                                    }

                                } else {

                                    eliminado = true;

                                    System.out.println();
                                    System.out.println(
                                            "Respuesta incorrecta. BINGO RECHAZADO.");
                                    System.out.println(
                                            "Ya no puedes ganar, pero seguiras viendo la partida hasta que alguien cante bingo.");
                                }
                            }
                        }

                    } else {

                        System.out.println(
                                "Ese numero no esta en tu carton.");
                    }
                }

                System.out.print("--------------------------------------");

            } else {

                Thread.sleep(500);
            }
        }

        teclado.close();

        System.out.println("Fin de la partida.");
    }

    // Mostrar el ganador del bingo
    public static void mostrarGanador(File archivoBingo) {

        String ganador = "";

        try {

            // El fichero puede estar recien creado y aun vacio
            for (int i = 0; i < 30 && ganador.isEmpty(); i++) {

                Scanner lector = new Scanner(archivoBingo);

                if (lector.hasNextLine()) {
                    ganador = lector.nextLine().trim();
                }

                lector.close();

                if (ganador.isEmpty()) {
                    Thread.sleep(100);
                }
            }

        } catch (Exception e) {

            System.out.println("No se ha podido leer el ganador.");
        }

        System.out.println();
        System.out.println("--------------------------------");
        System.out.println("BINGO CONSEGUIDO POR: " + ganador);
        System.out.println("LA PARTIDA HA TERMINADO.");
        System.out.println("--------------------------------");
    }

    // Lee una linea del teclado sin quedarse bloqueado para siempre
    // Devuelve null si mientras esperas alguien canta bingo
    public static String leerLinea(Scanner teclado, File archivoBingo)
            throws Exception {

        while (System.in.available() == 0) {

            if (archivoBingo.exists()) {
                return null;
            }

            Thread.sleep(200);
        }

        return teclado.nextLine();
    }

    public static void mostrarTablero(boolean[] salio) {

        for (int num = 1; num <= 90; num++) {

            if (salio[num]) {
                System.out.printf(ROJO + "%2d" + RESET + " ", num);
            } else {
                System.out.printf("%2d ", num);
            }

            if (num % 10 == 0) {
                System.out.println();
            }
        }
    }
}