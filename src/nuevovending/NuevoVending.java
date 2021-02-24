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
     * Metodo para ejecutar el hilo de transmición principal
     */
    public void threadStart() {
        isFalseThread = true;
    }
    
    /**
     * Metodo para parar el hilo de transmición principal
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
        yimiLibrary.on_vending();
        Thread.sleep(100);
    }
    
    /**
     * Detener vending
     * 
     * @throws InterruptedException
     * @throws SerialPortException 
     */
    public static void stopVending() throws InterruptedException, SerialPortException {
        yimiLibrary.off_vending();
        Thread.sleep(100);
    }
    
    /**
     * Cerrar puerto de vending
     * 
     * @throws SerialPortException
     * @throws InterruptedException 
     */
    public static void closePort() throws SerialPortException, InterruptedException {
        yimiLibrary.close_port();
        Thread.sleep(100);
    }
    
    /**
     * Abrir puerto de vending
     * 
     * @throws SerialPortException
     * @throws InterruptedException 
     */
    public static void openPort() throws SerialPortException, InterruptedException {
        yimiLibrary.open_port();
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
    
    public static void green() throws InterruptedException {
        yimiLibrary.green_led();
        Thread.sleep(100);
    }
    
    public static void red() throws InterruptedException {
        yimiLibrary.red_led();
        Thread.sleep(100);
    }
    
    public static void yellow() throws InterruptedException {
        yimiLibrary.yellow_led();
        Thread.sleep(100);
    }
    
    public static void off() throws InterruptedException {
        yimiLibrary.leds_off();
        Thread.sleep(100);
    }
    
    public static String gps() throws InterruptedException {
        String respuesta = yimiLibrary.coord_GPS();
        Thread.sleep(100);
        return respuesta;
    }
    
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
    public static double coincatch() {
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
                    double coin = coincatch();
                    if (coin > 0) { 
                        System.out.println(coin);
                    }
                }
            }
        }
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, SerialPortException {
        boolean isExit = false;
        Scanner in = new Scanner(System.in);
        
        try {
            String port = "ttyUSB0";
            os(false, port);
            openPort();
            hilo.setPriority(Thread.MAX_PRIORITY);
            hilo.start();
            threadCoinVending.start();
        } catch (InterruptedException | SerialPortException ex) {
            System.out.println(ex.getMessage());
        }
        
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
                case 5:
                    System.out.println(gps());
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
        
        stopListenerVending();
        stopVending();
        endVending();
        closePort();
        isCoinService = true;
        System.exit(0);
    }
}
