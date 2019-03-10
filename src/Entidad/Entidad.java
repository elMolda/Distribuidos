/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidad;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class Entidad {

    public static void main(String args[]) {
        Socket s = null;
        try {
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);//Direccion del proxy

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            // Hilo para enviar Encuestas al Proxy 
            Thread enviarMensaje = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String[] encuestas = leerArchivo("C:/Users/sistemas/Documents/NetBeansProjects/Distribuidos/" + args[0] + ".txt");

                        try { 
                            out.writeObject(encuestas);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            // Hilo para pedir resultados al Proxy 
            Thread leerMensaje = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            String[] resultados = (String[]) in.readObject();
                            //Funcion ver Resultados
                        } catch (IOException e) {

                            e.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Entidad.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });

            enviarMensaje.start();
            leerMensaje.start();

        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }

    private static String[] leerArchivo(String nombreArchivo) {
        Path ruta = Paths.get(nombreArchivo);
        try {
            List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            String[] lines = lineas.toArray(new String[lineas.size()]);
            return lines;
        } catch (IOException ex) {
            Logger.getLogger(Entidad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
