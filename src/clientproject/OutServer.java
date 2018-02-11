package clientproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutServer extends Thread {

//--------------------------------------------------------Atributos-------------------------------------------------------------------//    
    private final ClientProject cp;
    private final String id;
    private final String nombre;
    private final int puerto;
    private PrintWriter salidaDatos;
    private Socket socket;
    private final String url;

//-------------------------------------------------------Constructor-----------------------------------------------------------------//
    public OutServer(int puerto, String url, String nombre, ClientProject cp) {
        this.url = url;
        this.puerto = puerto;
        this.cp = cp;
        this.nombre = nombre;
        this.id = "&";
    }

//-----------------------------------------------------Bucle del thread---------------------------------------------------------------//
    @Override
    public void run() {
        while (true) {
            tiempoEspera();
            if (!this.cp.checkServerOnline()) {
                addServer(crearSocket());
            }
        }
    }

//----------------------------------------------------Métodos privados-------------------------------------------------------------//
    //Añadimos el servidor con el Socket creado
    private void addServer(Socket socket) {
        this.cp.addServer(socket);
    }

    //Método para crear el socket
    private Socket crearSocket() {
        try {
            this.socket = new Socket(this.url, this.puerto);
            this.salidaDatos = new PrintWriter(this.socket.getOutputStream(), true);
            this.salidaDatos.println(id + this.nombre);
            System.out.println("Te has conectado al servidor");
            return this.socket; // ==================================================================================>
        } catch (IOException ex) {
            System.out.println("No se ha podido establecer la conexión con el servidor.\n     Reintentando la conexión...");
        }
        return null;
    }

//Método para hacer esperar al thread
    private void tiempoEspera() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OutServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
