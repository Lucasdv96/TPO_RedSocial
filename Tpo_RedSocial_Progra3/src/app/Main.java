package app;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        new Menu(entrada).iniciar();
        entrada.close();
    }
}
