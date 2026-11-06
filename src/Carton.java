import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;

public class Carton {

    private int[][] numeros = new int[3][9];
    private int[][] marcados = new int[3][9];

    private boolean[] lineaResuelta = new boolean[3];
    private int lineasValidas = 0;

    private boolean bingoResuelto = false;

    static final String ROJO = "\u001B[31m";
    static final String RESET = "\u001B[0m";

    // Generar un carton aleatorio
    public Carton() {

        Random rnd = new Random();

        boolean[][] ocupados = new boolean[3][9];

        boolean valido;

        // Elegir 5 columnas por fila
        do {

            ocupados = new boolean[3][9];

            for (int f = 0; f < 3; f++) {

                int cantidad = 0;

                while (cantidad < 5) {

                    int c = rnd.nextInt(9);

                    if (!ocupados[f][c]) {

                        ocupados[f][c] = true;
                        cantidad++;
                    }
                }
            }

            valido = true;

            // Comprobar que cada columna tenga 1 o 2 numeros
            for (int c = 0; c < 9; c++) {

                int cantidad = 0;

                for (int f = 0; f < 3; f++) {

                    if (ocupados[f][c]) {
                        cantidad++;
                    }
                }

                if (cantidad == 0 || cantidad == 3) {
                    valido = false;
                }
            }

        } while (!valido);

        // Generar los numeros de cada columna
        for (int c = 0; c < 9; c++) {

            int cantidad = 0;

            // Contar cuantos numeros tiene la columna
            for (int f = 0; f < 3; f++) {

                if (ocupados[f][c]) {
                    cantidad++;
                }
            }

            int[] valores = new int[cantidad];

            // Generar numeros sin repetir
            for (int i = 0; i < cantidad; i++) {

                int num;
                boolean repetido;

                do {

                    if (c == 0) {
                        num = 1 + rnd.nextInt(9);
                    } else if (c == 8) {
                        num = 80 + rnd.nextInt(11);
                    } else {
                        num = c * 10 + rnd.nextInt(10);
                    }

                    repetido = false;

                    for (int j = 0; j < i; j++) {

                        if (valores[j] == num) {
                            repetido = true;
                        }
                    }

                } while (repetido);

                valores[i] = num;
            }

            // Ordenar los numeros de menor a mayor
            Arrays.sort(valores);

            // Colocar los numeros de arriba hacia abajo
            int posicion = 0;

            for (int f = 0; f < 3; f++) {

                if (ocupados[f][c]) {

                    numeros[f][c] = valores[posicion];
                    posicion++;
                }
            }
        }
    }

    // Leer un carton desde un fichero
    public Carton(Scanner lector) {

        for (int f = 0; f < 3; f++) {

            for (int c = 0; c < 9; c++) {

                if (lector.hasNextInt()) {
                    numeros[f][c] = lector.nextInt();
                }
            }
        }

        if (lector.hasNextLine()) {
            lector.nextLine();
        }
    }

    // Comprobar si un numero ya esta en una columna
    public boolean estaEnColumna(int c, int num) {

        for (int f = 0; f < 3; f++) {

            if (numeros[f][c] == num) {
                return true;
            }
        }

        return false;
    }

    // Marcar un numero del carton
    public int marcar(int num) {

        for (int f = 0; f < 3; f++) {

            for (int c = 0; c < 9; c++) {

                if (numeros[f][c] == num) {

                    marcados[f][c] = 1;
                    return 1;
                }
            }
        }

        return 0;
    }

    // Comprobar si hay una fila completa
    public int lineaCompletada() {

        for (int f = 0; f < 3; f++) {

            if (!lineaResuelta[f]) {

                int numerosEnFila = 0;
                int marcadosEnFila = 0;

                for (int c = 0; c < 9; c++) {

                    if (numeros[f][c] != 0) {

                        numerosEnFila++;

                        if (marcados[f][c] == 1) {
                            marcadosEnFila++;
                        }
                    }
                }

                if (marcadosEnFila == numerosEnFila) {
                    return f;
                }
            }
        }

        return -1;
    }

    // Resolver una linea
    public void resolverLinea(int f, boolean acierto) {

        lineaResuelta[f] = true;

        if (acierto) {
            lineasValidas++;
        }
    }

    public int getLineasValidas() {
        return lineasValidas;
    }

    // Comprobar si todos los numeros del carton estan marcados
    public boolean bingoCompletado() {

        if (bingoResuelto) {
            return false;
        }

        for (int f = 0; f < 3; f++) {

            for (int c = 0; c < 9; c++) {

                if (numeros[f][c] != 0 && marcados[f][c] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    // Indicar que el bingo ya se ha resuelto
    public void resolverBingo() {
        bingoResuelto = true;
    }

    // Escribir el carton en un fichero
    public void escribir(PrintWriter pw) {

        for (int f = 0; f < 3; f++) {

            for (int c = 0; c < 9; c++) {

                pw.print(numeros[f][c] + " ");
            }

            pw.println();
        }
    }

    public int[][] getNumeros() {
        return numeros;
    }

    // Mostrar el carton
    public void mostrar() {

        for (int f = 0; f < 3; f++) {

            for (int c = 0; c < 9; c++) {

                if (numeros[f][c] == 0) {

                    System.out.print(" 0 ");

                } else if (marcados[f][c] == 1) {

                    System.out.printf(
                            ROJO + "%2d " + RESET, numeros[f][c]);

                } else {

                    System.out.printf("%2d ", numeros[f][c]);
                }
            }

            System.out.println();
        }
    }
}