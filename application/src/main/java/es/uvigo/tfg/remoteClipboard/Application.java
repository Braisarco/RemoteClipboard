package es.uvigo.tfg.remoteClipboard;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Write your username:");
        ConsoleApplication app = new ConsoleApplication(in.nextLine());
        in.close();
        app.inicializate();
    }
}
