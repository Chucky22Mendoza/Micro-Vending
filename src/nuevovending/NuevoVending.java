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
    private static boolean coinService = true;
    private static Driver yimiLibrary = new Driver();
    private boolean monedas_full = true;
    String moneda = "";
    private double coinConvert;
    
    /**
     * Hilo principal de ejecución de lecturas Vending y clasificacion de
     * monedas ingresadas.
     */
    public Thread hilo = new Thread() {

        @Override
        public void run() {

            while (monedas_full) {
                String respuesta = "";
                try {
                    //byte[] buffer = serialPort.readBytes(10);//= serialPort.readBytes(totBytes);//Read 10 bytes from serial port
                    //respuesta = toHexString(buffer);
                    respuesta = serialPort.readHexString();
                } catch (SerialPortException e) {
                    System.out.println("Error al recibir datos del puerto: " + e.getMessage());
                }
                
                String test = yimiLibrary.getdataHex();
                
                moneda = respuesta;
                
                if (moneda == null) {
                    moneda = "0";
                } else {
                    System.err.println("---------> " + moneda);
                }
                
                System.out.println(test + " -- " + moneda);
                
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

            while (coinService) {
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
    public static byte byteInicio1 = (byte) 0xF1;
    public static byte byteInicio2 = (byte) 0x01;
    
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
    
    byte byteFin1 = (byte) 0xF1;
    byte byteFin2 = (byte) 0x00;
    
    public Thread threadCommands = new Thread() {
        @Override
        public void run() {
            while (cmd) {
                String respuesta = "";
                try {
                    Thread.sleep(5);
                    serialPort.writeByte(byteInicio1);
                    Thread.sleep(5);
                    serialPort.writeByte(byteInicio2);
                    Thread.sleep(5);
                    /*serialPort.writeByte(byteVerde1);
                    Thread.sleep(5);
                    serialPort.writeByte(byteVerde2);
                    Thread.sleep(5);
                    serialPort.writeByte(byteAmarillo1);
                    Thread.sleep(5);
                    serialPort.writeByte(byteAmarillo2);
                    Thread.sleep(5);
                    serialPort.writeByte(byteRojo1);
                    Thread.sleep(5);
                    serialPort.writeByte(byteRojo2);
                    Thread.sleep(5);
                    serialPort.writeByte(byteApagado1);
                    Thread.sleep(5);
                    serialPort.writeByte(byteApagado2);
                    Thread.sleep(5);*/
                    respuesta = serialPort.readHexString();
                    System.out.println("--> " + respuesta);
                    
                    if (respuesta != null) {
                        if (respuesta.equals("F0 F0 F0 F0")) {
                            NuevoVending nv = new NuevoVending();
                            nv.hilo.start();
                            nv.threadCoinVending.start();
                            /*Thread.sleep(5);
                            serialPort.writeByte(byteFin1);
                            Thread.sleep(5);
                            serialPort.writeByte(byteFin2);
                            Thread.sleep(5);
                            serialPort.closePort();*/
                            cmd = false;
                        }
                    }
                    
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NuevoVending.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SerialPortException ex) {
                    Logger.getLogger(NuevoVending.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, SerialPortException {
        NuevoVending nv = new NuevoVending();
        
        serialPort = new SerialPort("/dev/ttyUSB0");
        serialPort.openPort();
        serialPort.setParams(
                SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE
        );
        
        /*Thread.sleep(5);
        serialPort.writeByte(byteInicio1);
        Thread.sleep(5);
        serialPort.writeByte(byteInicio2);
        Thread.sleep(5);*/
        
        nv.threadCommands.start();
        //nv.hilo.start();
        //nv.threadCoinVending.start();
        
    }
    
}
