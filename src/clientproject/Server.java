package clientproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

//--------------------------------------------------------Atributos-------------------------------------------------------------------//  
    private final ClientProject cp;
    private BufferedReader entradaDatos;
    private PrintWriter salidaDatos;
    private final Socket socket;

//-------------------------------------------------------Constructor-----------------------------------------------------------------//  
    public Server(Socket socket, ClientProject cp) {
        this.socket = socket;
        this.cp = cp;
    }

//-----------------------------------------------------Bucle del thread---------------------------------------------------------------//
    @Override
    public void run() {
        openIO();
        while (checkSocket()) {
            tiempoEspera();
            try {
                rxMsg(this.entradaDatos.readLine());
            } catch (IOException ex) {
                System.out.println("Hay un problema en la comunicación con el servidor.\n     Intentando reconectar...");
                this.cp.checkServerOnline();
                openIO();
            }
        }
    }

//------------------------------------------------------Métodos públicos-----------------------------------------------------------//
    //Método para recibir mensajes
    public void rxMsg(String msg) {
        this.cp.showMsg(msg);
    }

    //Método de enviar mensajes al servidor
    public void txMsg(String msg) {
        this.salidaDatos.println("msg " + msg);
    }

    //Sobrecargo el método txMsg para enviar al servidor la desconexión del usuario
    public void txMsg() {
        this.salidaDatos.println("bye");
    }

//-----------------------------------------------------Métodos privados -----------------------------------------------------------//
    //Comprobación del socket
    private boolean checkSocket() {
        if (this.socket != null) {
            if (this.socket.isConnected()) {
                return true;//=======================================================================================>
            } else if (this.socket.isClosed()) {
                return false;//======================================================================================>
            }
        }
        return false;
    }

    //Abrir comunicación con el socket
    private void openIO() {
        try {
            if (checkSocket()) {
                this.salidaDatos = new PrintWriter(this.socket.getOutputStream(), true);
                this.entradaDatos = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            }
        } catch (IOException ex) {
            System.out.println("No deberías estar aquí...");
        }
    }

    //Método para hacer esperar al thread
    private void tiempoEspera() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OutServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//------------------------------------------------------------Gets & Sets-------------------------------------------------------------//
    public Socket getSocket() {
        return socket;
    }

}
