/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import Entidad.Entidad;
import java.net.*;
import java.io.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class Cliente {

    private static String[] votaciones;

    public static void main(String args[]) {
        Socket s = null;
        try {
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);//Direccion del proxy

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            String id = args[1];

            Thread enviarMensaje;
            enviarMensaje = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            out.writeObject(votaciones);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Thread leerMensaje;
            leerMensaje = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {

                            String[] encuestasRecibidas = (String[]) in.readObject();//Pedir Encuestas al proxy
                            votar(encuestasRecibidas, id);
                        } catch (IOException e) {

                            e.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Entidad.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            });

            leerMensaje.start();
            if(votaciones!=null)
                enviarMensaje.start();

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

    private static void votar(String[] encuestas, String id) {
        votaciones = new String[encuestas.length];
        String voto = null;
        String votacion;
        Scanner teclado = new Scanner(System.in);
        for (int i = 0; i < encuestas.length; i++) {
            String[] EntidadProyecto = encuestas[i].split(",");
            String Entidad = EntidadProyecto[0];
            String Proyecto = EntidadProyecto[1];
            System.out.println("Entidad: " + Entidad + "\n" + "Proyecto: " + Proyecto);
            System.out.println("Cual es su votacion? (1.Alto 2.Medio 3.Bajo)");
            votacion = teclado.nextLine();

            voto = (id + "," + Proyecto + "," + Entidad + "," + votacion);
            votaciones[i] = voto;

        }
        for (String votos : votaciones) {
            System.out.println(votos);
        }
    }
}
