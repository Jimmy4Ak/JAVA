import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Anfitrion {

    static final String RUTA = "C:/bingo/";
    static final String ROJO = "\u001B[31m";
    static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws Exception {

        new File(RUTA).mkdirs();

        // Vaciar los archivos de la partida anterior
        PrintWriter pw = new PrintWriter(RUTA + "jugadores.txt");
        pw.close();

        pw = new PrintWriter(RUTA + "bolas.txt");
        pw.close();

        // Borrar los avisos de la partida anterior
        new File(RUTA + "linea_conseguida.txt").delete();

        File archivoBingo = new File(RUTA + "bingo_conseguido.txt");
        archivoBingo.delete();

        new File(RUTA + "repartidos.txt").delete();
        new File(RUTA + "asignaciones.txt").delete();

        String[] nombres = new String[50];

        int total = 0;
        int anterior = 0;

        System.out.println("Esperando jugadores durante 10 segundos...");

        for (int segundo = 0; segundo < 10; segundo++) {

            Scanner lector = new Scanner(
                    new File(RUTA + "jugadores.txt"));

            total = 0;

            while (lector.hasNextLine() && total < 50) {
                nombres[total] = lector.nextLine();
                total++;
            }

            lector.close();

            if (total > anterior) {

                System.out.println(
                        "Nuevo jugador: " + nombres[total - 1]);

                anterior = total;
            }

            Thread.sleep(1000);
        }

        // Generar y repartir los cartones
        int numCartones = total + 5;

        Carton[] cartones = new Carton[numCartones];

        String[] duenos = new String[numCartones];

        for (int i = 0; i < numCartones; i++) {
            cartones[i] = new Carton();
        }

        // Crear asignaciones.txt
        PrintWriter asignaciones =
                new PrintWriter(RUTA + "asignaciones.txt");

        for (int i = 0; i < total; i++) {

            int pos;

            do {
                pos = (int) (Math.random() * numCartones);

            } while (duenos[pos] != null);

            duenos[pos] = nombres[i];

            asignaciones.println(nombres[i]);

            cartones[pos].escribir(asignaciones);

            System.out.println(
                    nombres[i] + " recibe el carton " + (pos + 1));
        }

        asignaciones.close();

        // Crear cartones.txt para revisar los cartones
        PrintWriter revision =
                new PrintWriter(RUTA + "cartones.txt");

        for (int i = 0; i < numCartones; i++) {

            if (duenos[i] == null) {

                revision.println(
                        "Carton " + (i + 1) + " (sin asignar)");

            } else {

                revision.println(
                        "Carton " + (i + 1) + " (jugador: "
                        + duenos[i] + ")");
            }

            cartones[i].escribir(revision);

            revision.println();
        }

        revision.close();

        // Avisar a los jugadores de que ya pueden leer su carton
        PrintWriter aviso =
                new PrintWriter(RUTA + "repartidos.txt");

        aviso.close();

        System.out.println();
        System.out.println(
                "Empieza la partida con " + total + " jugadores:");

        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + ". " + nombres[i]);
        }

        System.out.println("----------------------------------------");
        System.out.println("CARTILLA:");

        Scanner sc = new Scanner(System.in);

        boolean[] salio = new boolean[91];

        mostrarTablero(salio);

        System.out.println();
        System.out.println("Pulsa Enter para sacar la bola...");

        // Sacar bolas hasta que un jugador consiga el bingo
        for (int i = 0; i < 90; i++) {

            System.out.println("----------------------------------------");

            // Esperar al Enter, pero parar enseguida si alguien gana
            while (System.in.available() == 0 && !archivoBingo.exists()) {
                Thread.sleep(200);
            }

            if (archivoBingo.exists()) {

                mostrarGanador(archivoBingo);
                break;
            }

            sc.nextLine();

            // Volver a comprobar despues de pulsar Enter
            if (archivoBingo.exists()) {

                mostrarGanador(archivoBingo);
                break;
            }

            int bola;

            do {
                bola = (int) (Math.random() * 90) + 1;

            } while (salio[bola]);

            salio[bola] = true;

            // Guardar la bola en el fichero
            pw = new PrintWriter(
                    new FileWriter(RUTA + "bolas.txt", true));

            pw.println(bola);
            pw.close();

            System.out.println(bola);
            System.out.println();
            System.out.println("CARTILLA:");

            mostrarTablero(salio);
        }

        sc.close();

        System.out.println();
        System.out.println("Fin de la partida.");
    }

    // Mostrar el ganador
    public static void mostrarGanador(File archivoBingo) {

        String ganador = "";

        try {

            // El fichero puede estar recien creado y aun vacio: reintentar un poco
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
        System.out.println("----------------------------------------");
        System.out.println("¡BINGO!");
        System.out.println("GANADOR: " + ganador);
        System.out.println("LA PARTIDA HA TERMINADO.");
        System.out.println("----------------------------------------");
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