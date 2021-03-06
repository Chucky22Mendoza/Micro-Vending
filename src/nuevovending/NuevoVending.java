/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nuevovending;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 *
 * @author yimibus
 */
public class NuevoVending {
    private static final Driver yimiLibrary = new Driver();
    private static volatile boolean isCoinService = false;
    private static volatile boolean isFalseCoin = true;
    private static volatile boolean isMonedasFull = false;
    private static volatile boolean isFalseThread = true;
    
    private static volatile String moneda = "";
    private static volatile double coinConvert;
    
    /**
     * Método para ejecutar el hilo de transmición principal
     */
    public void threadStart() {
        isFalseThread = true;
    }
    
    /**
     * Método para parar el hilo de transmición principal
     */
    public void threadStop() {
        isFalseThread = false;
    }
    
    /**
     * 
     * Elegir sistema operativo para vending
     * 
     * @param osflag
     * @param portName
     * @throws InterruptedException 
     */
    public static void os(boolean isOS,String portName) throws InterruptedException {
        if (isOS) {
            yimiLibrary.osWindows(portName);
        }else {
            yimiLibrary.osLinux(portName);
        }
    }
    
    /**
     * Iniciar hilo de vending
     * 
     * @throws InterruptedException 
     */
    public static void initVending() throws InterruptedException {
        isFalseThread = true;
        Thread.sleep(100);
    }
    
    /**
     * Detener hilo de vending
     * 
     * @throws InterruptedException 
     */
    public static void endVending() throws InterruptedException {
        isFalseThread = false;
        Thread.sleep(100);
    }
    
    /**
     * Iniciar vending
     * 
     * @throws InterruptedException
     * @throws SerialPortException 
     */
    public static void startVending() throws InterruptedException, SerialPortException {
        yimiLibrary.onVending();
        Thread.sleep(100);
    }
    
    /**
     * Detener vending
     * 
     * @throws InterruptedException
     * @throws SerialPortException 
     */
    public static void stopVending() throws InterruptedException, SerialPortException {
        yimiLibrary.offVending();
        Thread.sleep(100);
    }
    
    /**
     * Cerrar puerto de vending
     * 
     * @throws SerialPortException
     * @throws InterruptedException 
     */
    public static void closePort() throws SerialPortException, InterruptedException {
        yimiLibrary.closePort();
        Thread.sleep(100);
    }
    
    /**
     * Abrir puerto de vending
     * 
     * @throws SerialPortException
     * @throws InterruptedException 
     */
    public static void openPort() throws SerialPortException, InterruptedException {
        yimiLibrary.openPort();
        Thread.sleep(100);
    }
    
    /**
     * Iniciar hilo que detecta las monedas que ingresan
     */
    public static void startListenerVending() {
        isFalseThread = true;
    }
    
    /**
     * Detener hilo que detecta las monedas que ingresan
     */
    public static void stopListenerVending() {
        isFalseThread = false;
    }
    
    /**
     * Encender led verde
     * 
     * @throws InterruptedException 
     */
    public static void green() throws InterruptedException {
        yimiLibrary.greenLed();
        Thread.sleep(100);
    }
    
    /**
     * Encender led rojo
     * 
     * @throws InterruptedException 
     */
    public static void red() throws InterruptedException {
        yimiLibrary.redLed();
        Thread.sleep(100);
    }
    
    /**
     * Encender led amarillo
     * 
     * @throws InterruptedException 
     */
    public static void yellow() throws InterruptedException {
        yimiLibrary.yellowLed();
        Thread.sleep(100);
    }
    
    /**
     * Apagar leds
     * 
     * @throws InterruptedException 
     */
    public static void off() throws InterruptedException {
        yimiLibrary.ledsOff();
        Thread.sleep(100);
    }
    
    /**
     * Obtener coordenadas del gps
     * 
     * @return
     * @throws InterruptedException 
     */
    private static String[] gps() throws InterruptedException {
        String respuesta = yimiLibrary.coordGPS();
        String[] coordStr = respuesta.split(",");
        coordStr[0] = coordStr[0].trim();
        coordStr[1] = coordStr[1].trim();
        coordStr[2] = coordStr[2].trim();
        
        for (int i = 0; i < coordStr.length; i++) {
            System.out.println("String -> " + coordStr[i]);
        }
        Thread.sleep(100);
        
        return coordStr;
    }
    
    /**
     * Mostrar en display el dinero a cobrar
     * 
     * @param numero1
     * @throws InterruptedException 
     */
    public static void cobrar(int numero1) throws InterruptedException {
        byte byte1 = (byte) numero1;
        yimiLibrary.cobro(byte1);
        Thread.sleep(100);
    }
    
    /**
     * Hilo principal de ejecución de lecturas Vending y clasificacion de
     * monedas ingresadas.
     */
    public static Thread hilo = new Thread() {
        @Override
        public synchronized void run() {
            while (!isMonedasFull) {
                while (isFalseThread) {
                    moneda = yimiLibrary.getdataHex();
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
            }
        }
    };
    
    /**
     * Metodo para retonar el valor de la ultima moneda ingresada desde el
     * validador o simulador
     *
     * @return valor de la moneda
     */
    public static double coincatch() throws InterruptedException, SerialPortException {
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
    public static Thread threadCoinVending = new Thread() {
        @Override
        public synchronized void run() {
            while (!isCoinService) {
                while (isFalseCoin) {
                    try {
                        double coin = coincatch();
                        if (coin > 0) {
                            System.out.println(coin);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NuevoVending.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SerialPortException ex) {
                        Logger.getLogger(NuevoVending.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    };
    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws jssc.SerialPortException
     */
    public static void main(String[] args) throws InterruptedException, SerialPortException {
        boolean isExit = false;
        Scanner in = new Scanner(System.in);
        
        try {
            String port = "ttyUSB0";
            os(false, port);
            openPort();
            hilo.start();
            threadCoinVending.start();
            startVending();
            System.out.println("Pasó el vending");
        } catch (InterruptedException | SerialPortException ex) {
            System.out.println(ex.getMessage());
        }
        
//        Init init = new Init();
//        init.setVisible(true);
        
        
        while (!isExit) {
            System.out.println("");
            System.out.println("Opciones de tarjeta de cobro");
            System.out.println("1) Led verde");
            System.out.println("2) Led amarillo");
            System.out.println("3) Led rojo");
            System.out.println("4) Apagar leds");
            System.out.println("5) Coordenadas de GPS");
            System.out.println("6) Cobrar");
            System.out.println("0) Salir");
            System.out.print("Elige una opción: ");
            int opt = in.nextInt();
            System.out.println("");
            switch (opt) {
                case 1:
                    green();
                    break;
                case 2:
                    yellow();
                    break;
                case 3:
                    red();
                    break;
                case 4:
                    off();
                    break;
                case 5:
                    gps();
                    break;
                case 6:
                    System.out.print("Dinero a cobrar: ");
                    int dineros = in.nextInt();
                    System.out.println("");
                    cobrar(dineros);
                    break;
                case 0:
                    isExit = true;
                    break;
                default:
                    isExit = true;
                    break;
            }
        }
        
        /*
        stopListenerVending();
        stopVending();
        endVending();
        closePort();
        isCoinService = true;
        System.exit(0);
        */
    }
}
