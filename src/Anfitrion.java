import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Anfitrion {

	static final String RUTA = "C:/bingo/";
	static final String ROJO = "\u001B[31m";
	static final String RESET = "\u001B[0m";
	static final int ESPERA = 60; // segundos de registro

	public static void main(String[] args) throws Exception 
	{
		new File(RUTA).mkdirs();

		// Vaciar los archivos de la partida anterior
		PrintWriter pw = new PrintWriter(RUTA + "jugadores.txt");
		pw.close();
		pw = new PrintWriter(RUTA + "bolas.txt");
		pw.close();
		// NUEVO: borrar el aviso de la partida anterior
		new File(RUTA + "repartidos.txt").delete();

		String[] nombres = new String[50];
		int total = 0;
		int anterior = 0;

		System.out.println("Esperando jugadores durante " + ESPERA + " segundos...");
		for (int segundo = 0; segundo < ESPERA; segundo++) 
		{
			escribirCuenta(ESPERA - segundo);

			Scanner lector = new Scanner(new File(RUTA + "jugadores.txt"));
			total = 0;
			while (lector.hasNextLine() && total < 50) 
			{
				nombres[total] = lector.nextLine();
				total++;
			}
			lector.close();

			if (total > anterior) 
			{
				System.out.println("Nuevo jugador: " + nombres[total - 1]);
				anterior = total;
			}
			Thread.sleep(1000);
		}
		escribirCuenta(0);

		// ---------- NUEVO: generar y repartir los cartones ----------
		// Se generan algunos cartones mas que jugadores
		int numCartones = total + 5;
		Carton[] cartones = new Carton[numCartones];
		String[] duenos = new String[numCartones]; // null = carton sin asignar

		for (int i = 0; i < numCartones; i++) 
		{
			cartones[i] = new Carton();
		}

		// asignaciones.txt: lo leen los jugadores (nombre + las 3 filas de su carton)
		PrintWriter asignaciones = new PrintWriter(RUTA + "asignaciones.txt");
		for (int i = 0; i < total; i++) 
		{
			// Buscar un carton al azar que todavia no tenga dueno
			int pos;
			do
			{
				pos = (int) (Math.random() * numCartones);
			} while (duenos[pos] != null);

			duenos[pos] = nombres[i];

			asignaciones.println(nombres[i]);
			cartones[pos].escribir(asignaciones);

			System.out.println(nombres[i] + " recibe el carton " + (pos + 1));
		}
		asignaciones.close();

		// cartones.txt: para revisar todos los cartones y quien tiene cada uno
		PrintWriter revision = new PrintWriter(RUTA + "cartones.txt");
		for (int i = 0; i < numCartones; i++) 
		{
			if (duenos[i] == null) 
			{
				revision.println("Carton " + (i + 1) + " (sin asignar)");
			} 
			else 
			{
				revision.println("Carton " + (i + 1) + " (jugador: " + duenos[i] + ")");
			}
			cartones[i].escribir(revision);
			revision.println();
		}
		revision.close();

		// Avisar a los jugadores de que ya pueden leer su carton
		PrintWriter aviso = new PrintWriter(RUTA + "repartidos.txt");
		aviso.close();
		// ------------------------------------------------------------

		System.out.println("Empieza la partida con " + total + " jugadores:");
		for (int i = 0; i < total; i++) 
		{
			System.out.println((i + 1) + ". " + nombres[i]);
		}
		System.out.println("----------------------------------------");
		System.out.println("CARTILLA:");
		Scanner sc = new Scanner(System.in);
		boolean[] salio = new boolean[91];

		mostrarTablero(salio);

		System.out.println();
		System.out.print("Pulsa Enter para sacar la bola...");
		System.out.println();
		for (int i = 0; i < 90; i++)
		{
			System.out.println("----------------------------------------");
			
			sc.nextLine();

			int bola;
			do
			{
				bola = (int) (Math.random() * 90) + 1;
			} while (salio[bola]);

			salio[bola] = true;

			// Guardar la bola en el fichero para que la lea el jugador
			pw = new PrintWriter(new FileWriter(RUTA + "bolas.txt", true));
			pw.println(bola);
			pw.close();

			System.out.println(bola);
			System.out.println("CARTILLA:");
			System.out.println();

			mostrarTablero(salio);
		}
		sc.close();
	}

	// NUEVO: escribe los segundos que quedan de registro para que los jugadores los lean
	public static void escribirCuenta(int segundos) throws Exception
	{
		PrintWriter pw = new PrintWriter(RUTA + "cuenta.txt");
		pw.println(segundos);
		pw.close();
	}

	public static void mostrarTablero(boolean[] salio) 
	{
		for (int num = 1; num <= 90; num++) 
		{
			if (salio[num]) 
			{
				System.out.printf(ROJO + "%2d" + RESET + " ", num);
			} 
			else 
			{
				System.out.printf("%2d ", num);
			}

			if (num % 10 == 0) 
			{
				System.out.println();
			}
		}
	}
	
}