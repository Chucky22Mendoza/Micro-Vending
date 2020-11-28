/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nuevovending;

import static com.sun.org.apache.bcel.internal.classfile.Utility.toHexString;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author yimibus
 */
public class NuevoVending {
    private static boolean coinService = false;
    private static Driver yimiLibrary = new Driver();
    private boolean monedas_full;
    String moneda = "";
    private double coinConvert;
    
    /**
     * Hilo principal de ejecución de lecturas Vending y clasificacion de
     * monedas ingresadas.
     */
    public Thread hilo = new Thread() {

        @Override
        public void run() {

            while (!monedas_full) {
                System.out.println("sda");
                moneda = yimiLibrary.getdataHex();
                System.out.println(moneda);
                switch (moneda) {
                    case "f1 02":
                        coinConvert = 0.50;
                        break;
                    case "f1 03":
                        coinConvert = 1;
                        break;
                    case "f1 04":
                        coinConvert = 2;
                        break;
                    case "f1 05":
                        coinConvert = 5.0;
                        break;
                    case "f1 06":
                        coinConvert = 10.0;
                        break;
                    case "f1 07":
                        coinConvert = 20.0;
                        break;
                    default:
                        break;
                }
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Salida");
        }
    };
    
    /**
     * Metodo para retonar el valor de la ultima moneda ingresada desde el
     * validador o simulador
     *
     * @return valor de la moneda
     */
    public double coincatch() {
        if (coinConvert > 0) {
            double coinBackup = coinConvert;
            coinConvert = 0;
            return coinBackup;
        } else {
            return 0;
        }
    }
    
    /**
     * Lectura de las monedas ingresadas en hilo
     */
    public Thread threadCoinVending = new Thread() {
        @Override
        public void run() {

            while (!coinService) {
                double coin = coincatch();
                
                if (coin > 0) {
                    System.out.println("Moneda: " + coin); 
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            System.out.println("finalizo");
        }
    };
    
    public static SerialPort serialPort = new SerialPort("/dev/ttyUSB0");
    public boolean cmd = true;
    byte byteInicio1 = (byte) 0xF1;
    byte byteInicio2 = (byte) 0x01;
    
    byte byteGPS1 = (byte) 0xF1;
    byte byteGPS2 = (byte) 0x10;
    
    byte byteVerde1 = (byte) 0xF1;
    byte byteVerde2 = (byte) 0x11;
    
    byte byteAmarillo1 = (byte) 0xF1;
    byte byteAmarillo2 = (byte) 0x12;
    
    byte byteRojo1 = (byte) 0xF1;
    byte byteRojo2 = (byte) 0x13;
    
    byte byteApagado1 = (byte) 0xF1;
    byte byteApagado2 = (byte) 0x14;
    
    byte byteCobro1 = (byte) 0xA1;
    byte byteCobro2 = (byte) 0x00;
    byte byteCobro3 = (byte) 0x0A;
    
    public Thread threadCommands = new Thread() {
        @Override
        public void run() {
            while (cmd) {
                String respuesta = "";
                try {
                    serialPort.writeByte(byteInicio1);
                    serialPort.writeByte(byteInicio2);
                    Thread.sleep(1000);
                    
                    respuesta = serialPort.readHexString();
                } catch (SerialPortException e) {
                    System.out.println("Error al recibir datos del puerto: " + e.getMessage());
                } catch (InterruptedException ex) {
                    Logger.getLogger(NuevoVending.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("---> " + respuesta);
            }
        }
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, SerialPortException {
        yimiLibrary.osLinux("ttyUSB0");
        yimiLibrary.open_port();
        yimiLibrary.on_vending();
        
        NuevoVending nv = new NuevoVending();
        //nv.threadCommands.start();
        nv.hilo.start();
        nv.threadCoinVending.start();
        
    }
    
}
