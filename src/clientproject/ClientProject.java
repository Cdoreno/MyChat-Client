package clientproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public final class ClientProject extends JFrame implements ActionListener {

//----------------------------------------------------Atributos no Graficos----------------------------------------------------------//
    private int clientesOnline;
    private String nombre;
    private OutServer os;
    private String pathIconStatus;
    private int puerto;
    private Server servidor;
    private String url;

//----------------------------------------------------Atributos Graficos-------------------------------------------------------------//
    private JButton btnSalir;
    private JButton btnSend;
    private JLabel connectionIcon;
    private JTextField input;
    private JTextPane msgArea;
    private BufferedImage status;
    private JLabel usersOnline;

// ----------------------------------------------------Constructor------------------------------------------------------------------- //
    public ClientProject() {
        crearVentanaLogin();
        addOutServer();
    }

//----------------------------------------------------Main de la clase----------------------------------------------------------------//
    public static void main(String[] args) {
        ClientProject clientProject = new ClientProject();
    }

//---------------------------------------------------Métodos públicos-------------------------------------------------------------- //         
    //Añadir el conector con el servidor
    public void addOutServer() {
        this.os = new OutServer(this.puerto, this.url, this.nombre, this);
        this.os.start();
    }

    //Añadir server
    public void addServer(Socket socket) {
        this.servidor = new Server(socket, this);
        new Thread(servidor).start();
    }

    //Comprobar si tengo conexión con el servidor
    public boolean checkServerOnline() {
        if (this.servidor != null && this.servidor.getSocket() != null) {
            this.pathIconStatus = "src\\res\\connected.png";
            updateIconStatus();
            habilitarInput(true);
            return true; // ========================================================================================>
        }
        this.pathIconStatus = "src\\res\\disconnected.png";
        updateIconStatus();
        habilitarInput(false);
        return false;
    }

    //Metodo para recibir los mensajes del servidor
    public void showMsg(String msg) {
        configMsgToTextPane(msg, 1);
    }

    //Método para enviar los mensajes al servidor
    public void txMsg(String msg) {
        this.servidor.txMsg(msg);
        configMsgToTextPane("Yo digo: " + msg, 2);
    }

// ----------------------------------------------------Métodos privados ----------------------------------------------------------- //
    //Botón de cerrar la aplicación
    private JButton addBtnSalir() {
        this.btnSalir = new JButton();
        try {
            BufferedImage icon = ImageIO.read(new File("src\\res\\exit.png"));
            this.btnSalir.setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            Logger.getLogger(ClientProject.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.btnSalir.addActionListener(this);
        return btnSalir;
    }

    //Boton de enviar
    private JButton addBtnSend() {
        this.btnSend = new JButton();
        try {
            BufferedImage icon = ImageIO.read(new File("src\\res\\sendImg.png"));
            btnSend.setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            Logger.getLogger(ClientProject.class.getName()).log(Level.SEVERE, null, ex);
        }
        btnSend.addActionListener(this);
        return btnSend;
    }

    //Icono de status de conexión
    private JLabel addConnectionIcon() {
        this.connectionIcon = new JLabel();
        checkServerOnline();
        return connectionIcon;
    }

    //Input donde el usuario escribe los mensajes
    private JTextField addInputMsg() {
        this.input = new JTextField();
        this.input.setPreferredSize(new Dimension(250, 50));
        this.input.addActionListener(this);
        return this.input;
    }

    //Area que contiene los mensajes recibidos/enviados
    private JScrollPane addMsgArea() {

        this.msgArea = new JTextPane();
        this.msgArea.setPreferredSize(new Dimension(248, 450));
        this.msgArea.add(new JScrollPane());
        this.msgArea.setEnabled(false);

        JScrollPane jsp = new JScrollPane(this.msgArea);
        return jsp;
    }

    //Panel que contiene la zona de los mensajes (tema de color más que anda)
    private JPanel addMsgZone() {
        JPanel msgZone = new JPanel();
        msgZone.setBackground(new Color(12, 183, 242));
        msgZone.add(addMsgArea());
        return msgZone;
    }

    //Panel superior de notificaciones, conectado y cuandos users online
    private JPanel addNotifyBar() {
        JPanel notifyBar = new JPanel();
        notifyBar.setBackground(new Color(12, 183, 242));
        notifyBar.add(addConnectionIcon());
        notifyBar.add(addUsersOnline());
        notifyBar.add(addBtnSalir());
        return notifyBar;
    }

    //Panel que contiene el input y el botón para enviar mensajes
    private JPanel addSenderBar() {
        JPanel senderBar = new JPanel();
        senderBar.setBackground(new Color(12, 183, 242));
        senderBar.add(addInputMsg());
        senderBar.add(addBtnSend());
        return senderBar;
    }

    //Texto donde salen los usuarios online
    private JLabel addUsersOnline() {
        this.usersOnline = new JLabel();
        this.usersOnline.setPreferredSize(new Dimension(200, 50));
        //this.usersOnline.setText("Usuarios online: " + this.clientesOnline);
        this.usersOnline.setText("Usuarios online: " + "SIN IMPLEMENTAR");
        return this.usersOnline;
    }

    //Método que crea la ventana de inicio de sesión
    private void crearVentanaLogin() {
        JTextField urlDestino = new JTextField();
        JTextField puertoServidor = new JTextField();
        JTextField nombre = new JTextField();

        urlDestino.setText("localhost");
        puertoServidor.setText("8888");
        nombre.setText("Anónimo");

        Object[] message = {
            "URL del servidor:", urlDestino,
            "Puerto del servidor:", puertoServidor,
            "Introduce tu nombre:", nombre
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Iniciar sesión en chat", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            this.url = urlDestino.getText();
            this.puerto = Integer.parseInt(puertoServidor.getText());
            this.nombre = nombre.getText();
            crearVentana();
        } else {
            System.out.println("Inicio de sesión cancelado.");
            System.exit(0);
        }
    }

    //Método que crea toda la ventana del chat principal
    private void crearVentana() {
        Container cp = this.getContentPane();
        cp.add(panelPrincipal());
        configVentana();
    }

    //Método para coger el texto del input
    private void cogerTextoDeInput() {
        if (checkServerOnline()) {
            if (!"".equals(this.input.getText())) {
                txMsg(this.input.getText());
                this.input.setText("");
            }
        }
    }

    //Configurar alineación del TextPane 
    private void configMsgToTextPane(String msg, int sender) {

        StyledDocument doc = this.msgArea.getStyledDocument();
        SimpleAttributeSet attrib = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrib, 15);
        StyleConstants.setBold(attrib, true);

        switch (sender) {
            case 1: // En caso de mensajes entrantes (1) 
                //Alinea la linea a la izquierda y de color gris
                StyleConstants.setAlignment(attrib, StyleConstants.ALIGN_LEFT);
                break;
            case 2: // En caso de mensajes salientes (2)
                //Alinea la linea a la izquierda y de color verde
                StyleConstants.setAlignment(attrib, StyleConstants.ALIGN_RIGHT);
                break;
        }

        try {
            String auxMsg = "\n" + msg;
            doc.insertString(doc.getLength(), auxMsg, attrib);
            doc.setParagraphAttributes(doc.getLength(), 1, attrib, false);
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Parámetros de configuración de la ventana
    private void configVentana() {
        this.setTitle(this.nombre + " conectado");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    //Método para habilitar/deshabilitar el input y el botón en caso de perdida de conexión con el servidor
    private void habilitarInput(Boolean onOff) {
        this.input.setEnabled(onOff);
        this.btnSend.setEnabled(onOff);
    }

    //Panel que contiene el resto de los componentes y ordena todo de forma BorderLayout
    private JPanel panelPrincipal() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(12, 183, 242));

        panel.add(addSenderBar(), BorderLayout.SOUTH);
        panel.add(addNotifyBar(), BorderLayout.NORTH);
        panel.add(addMsgZone(), BorderLayout.CENTER);

        return panel;
    }

    //Eventos al cerrar la ventana
    private void salir() {
        if (this.servidor != null) {
            this.servidor.txMsg();
        }
        System.exit(0);
    }

    //Cambiar icono de status
    private void updateIconStatus() {
        try {
            this.status = ImageIO.read(new File(this.pathIconStatus));
            connectionIcon.setIcon(new ImageIcon(status));
        } catch (IOException ex) {
            Logger.getLogger(ClientProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

// ----------------------------------------------------Gets & Sets------------------------------------------------------------------- //
    //Set de la variable ClientesOnline para mostrar por GUI el número de clientes conectados
    public void setClientesOnline(int clientesOnline) {
        this.clientesOnline = clientesOnline;
    }

// -------------------------------------------------- Listerners & Actions ---------------------------------------------------------- //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.btnSend || e.getSource() == this.input) {
            cogerTextoDeInput();
        }

        if (e.getSource() == this.btnSalir) {
            salir();
        }
    }

    public void mouseDragged(MouseEvent e) {
        setLocation(MouseInfo.getPointerInfo().getLocation());
    }

}
