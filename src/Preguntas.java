
import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class Preguntas {

    private String[] enunciados = new String[20];
    private String[][] opciones = new String[20][3];
    private int[] correctas = new int[20];
    private int total = 0;

    public void cargar(String ruta) {

        try {

            Scanner lector = new Scanner(new File(ruta), "UTF-8");

            while (lector.hasNextLine() && total < 20) {

                enunciados[total] = lector.nextLine();

                for (int j = 0; j < 3; j++) {
                    opciones[total][j] = lector.nextLine();
                }

                correctas[total] = Integer.parseInt(lector.nextLine().trim());

                total++;
            }

            lector.close();

        } catch (Exception e) {

            System.out.println("No se ha podido leer el fichero de preguntas");
        }
    }

    public int preguntaAleatoria() {

        Random rnd = new Random();

        return rnd.nextInt(total);
    }

    public String getEnunciado(int p) {
        return enunciados[p];
    }

    public String getOpcion(int p, int j) {
        return opciones[p][j];
    }

    public boolean esCorrecta(int p, int respuesta) {
        return correctas[p] == respuesta;
    }

    public boolean preguntar(Scanner teclado) {

        int p = preguntaAleatoria();

        System.out.println(enunciados[p]);

        for (int j = 0; j < 3; j++) {
            System.out.println((j + 1) + ") " + opciones[p][j]);
        }

        int respuesta;

        // Repetir hasta introducir una opcion valida
        while (true) {

            System.out.print("Selecciona una opcion (1, 2 o 3): ");

            String entrada = teclado.nextLine();

            try {

                respuesta = Integer.parseInt(entrada.trim());

            } catch (NumberFormatException e) {

                System.out.println("ERROR: Debes introducir un numero (1, 2 o 3).");
                continue;
            }

            if (respuesta < 1 || respuesta > 3) {

                System.out.println("ERROR: Opcion no valida. Elige 1, 2 o 3.");
                continue;
            }

            break;
        }

        return esCorrecta(p, respuesta);
    }

    public static void main(String[] args) {

        Preguntas preguntas = new Preguntas();

        preguntas.cargar("C:/bingo/preguntas.txt");

        Scanner teclado = new Scanner(System.in);

        if (preguntas.preguntar(teclado)) {

            System.out.println("Correcta, línea aprobada");

        } else {

            System.out.println("Incorrecta, línea rechazada");
        }
    }
}
