import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
 
public class Carton {
 
    // Solo int: los huecos valen 0
    private int[][] numeros = new int[3][9];
    private int[][] marcados = new int[3][9]; // 0 = sin marcar, 1 = marcado

    static final String ROJO = "\u001B[31m";
    static final String RESET = "\u001B[0m";

    public int marcar(int num) {
        int encontrado = 0;
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 9; c++) {
                if (numeros[f][c] == num) {
                    marcados[f][c] = 1;
                    encontrado = 1;
                }
            }
        }
        return encontrado;
    }

    // ---------- LINEAS ----------
    private boolean[] lineaResuelta = new boolean[3]; // true = ya se ha preguntado por esa fila
    private int lineasValidas = 0;

    // Devuelve el numero de fila (0-2) que acaba de completarse y aun no se ha resuelto, o -1 si no hay
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

    // Se llama despues de la pregunta: acierto = true -> linea valida, false -> rechazada
    public void resolverLinea(int f, boolean acierto) {
        lineaResuelta[f] = true;
        if (acierto) {
            lineasValidas++;
        }
    }

    public int getLineasValidas() {
        return lineasValidas;
    }
    // ----------------------------

    // Constructor 1: genera un carton aleatorio (lo usa el anfitrion)
    public Carton() {
        Random rnd = new Random();
 
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 9; c++) {
                numeros[f][c] = 0;
            }
        }
        for (int f = 0; f < 3; f++) {
            int puestos = 0;
            while (puestos < 5) {
                int c = rnd.nextInt(9);
 
                if (numeros[f][c] == 0) {
                    int num;
                    do {
                        num = c * 10 + 1 + rnd.nextInt(10);
                    } while (estaEnColumna(num, c));
                    numeros[f][c] = num;
                    puestos++;
                }
            }
        }
    }
 
    // Constructor 2: lee un carton que ya esta escrito en un fichero (lo usa el jugador)
    // Lee 27 numeros (3 filas de 9)
    public Carton(Scanner lector) {
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 9; c++) {
                numeros[f][c] = lector.nextInt();
            }
        }
        lector.nextLine(); // pasar al final de la ultima linea
    }
 
    private boolean estaEnColumna(int num, int c) {
        for (int f = 0; f < 3; f++) {
            if (numeros[f][c] == num) {
                return true;
            }
        }
        return false;
    }
 
    // Escribe el carton en un fichero: 3 lineas con 9 numeros cada una
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
 
    public void mostrar() {
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 9; c++) {
                if (numeros[f][c] == 0) {
                    System.out.print("0  "); // hueco
                } else if (marcados[f][c] == 1) {
                    System.out.printf(ROJO + "%2d" + RESET + " ", numeros[f][c]);
                } else {
                    System.out.printf("%2d ", numeros[f][c]);
                }
            }
            System.out.println();
        }
    }
 
    public static void main(String[] args) {
        Carton carton = new Carton();
        carton.mostrar();
    }
}