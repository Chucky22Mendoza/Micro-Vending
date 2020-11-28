/**
 * @author Jesús Mendoza Verduzco 
 * @email loginlock22@gmail.com
 */
package nuevovending;

import static com.sun.org.apache.bcel.internal.classfile.Utility.toHexString;
import java.util.Arrays;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Clase para inicializar vending
 * 
 * @author Jesús Mendoza Verduzco
 */
public class Driver {
    boolean final_vending = false;
    private static byte Byte = (byte) 0xF1;
    private static byte Byte1 = (byte) 0x00;

    private static byte byteInicio1 = (byte) 0xF1;
    private static byte byteInicio2 = (byte) 0x01;
    
    private static byte byteGPS1 = (byte) 0xF1;
    private static byte byteGPS2 = (byte) 0x10;
    
    private static byte byteVerde1 = (byte) 0xF1;
    private static byte byteVerde2 = (byte) 0x11;
    
    private static byte byteAmarillo1 = (byte) 0xF1;
    private static byte byteAmarillo2 = (byte) 0x12;
    
    private static byte byteRojo1 = (byte) 0xF1;
    private static byte byteRojo2 = (byte) 0x13;
    
    private static byte byteApagado1 = (byte) 0xF1;
    private static byte byteApagado2 = (byte) 0x14;
    
    private static byte byteCobro1 = (byte) 0xA1;
    private static byte byteCobro2 = (byte) 0x00;
    private static byte byteCobro3 = (byte) 0x0A;
    
    public String hostVending = "";
    public SerialPort serialPort = new SerialPort(hostVending);

    /**
     * Metodo para seleccionar modo de operacion compatible con sistema
     * operativo Windows. Puerto en Windows, ejemplo: COM8
     * 
     * @param port 
     * @throws java.lang.InterruptedException
     */
    public void osWindows(String port) throws InterruptedException {
        hostVending = port;
        serialPort = new SerialPort(hostVending);
        Thread.sleep(1000);
    }

    /**
     * Metodo para seleccionar modo de operacion compatible con sistema
     * operativo Linux. Puerto en Linux, ejemplo: ttyUSB0
     *
     * @param port 
     * @throws java.lang.InterruptedException 
     */
    public void osLinux(String port) throws InterruptedException {
        hostVending = "/dev/" + port;
        serialPort = new SerialPort(hostVending);
        Thread.sleep(1000);
    }

    /**
     * Metodo para verificar conexion de la vending
     *
     * @return
     */
    public boolean vendingAlive() {
        try {
            System.out.println("Status Vending: " + Arrays.toString(serialPort.getLinesStatus()));
            return true;
        } catch (SerialPortException ex) {
            return false;
        }
    }

    /**
     * Metodo de conversion de lecturas USB-SERIAL
     *
     * @return Valor HexadecimalString para validar tipo de moneda ingresado
     */
    public String getdataHex() {
        String respuesta = "";
        try {
            //byte[] buffer = serialPort.readBytes(10);//= serialPort.readBytes(totBytes);//Read 10 bytes from serial port
            //respuesta = toHexString(buffer);
            respuesta = serialPort.readHexString();
        } catch (SerialPortException e) {
            System.out.println("Error al recibir datos del puerto: " + e.getMessage());
        }
        return respuesta;
    }

    /**
     * Metodo para activar vending
     *
     * @throws InterruptedException
     */
    public void on_vending() throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteInicio1);
            Thread.sleep(5);
            serialPort.writeByte(byteInicio2);
            Thread.sleep(5);
            //System.out.println("-->" + getdataHex());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Metodo para LED verde
     *
     * @throws InterruptedException
     */
    public void green_led() throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteVerde1);
            Thread.sleep(5);
            serialPort.writeByte(byteVerde2);
            Thread.sleep(5);
            //System.out.println("-->" + getdataHex());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Metodo para LED amarillo
     *
     * @throws InterruptedException
     */
    public void yellow_led() throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteAmarillo1);
            Thread.sleep(5);
            serialPort.writeByte(byteAmarillo2);
            Thread.sleep(5);
            //System.out.println("-->" + getdataHex());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Metodo para LED rojo
     *
     * @throws InterruptedException
     */
    public void red_led() throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteRojo1);
            Thread.sleep(5);
            serialPort.writeByte(byteRojo2);
            Thread.sleep(5);
            //System.out.println("-->" + getdataHex());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Metodo para apagar LEDs
     *
     * @throws InterruptedException
     */
    public void leds_off() throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteApagado1);
            Thread.sleep(5);
            serialPort.writeByte(byteApagado2);
            Thread.sleep(5);
            //System.out.println("-->" + getdataHex());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Metodo para obtener coordenadas de GPS
     *
     * @throws InterruptedException
     */
    public void coord_GPS() throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteGPS1);
            Thread.sleep(5);
            serialPort.writeByte(byteGPS2);
            Thread.sleep(5);
            System.out.println("-->" + getdataHex());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Mandar bytes de cobro
     * 
     * @throws InterruptedException
     */
    public void cobro(byte byteHex1, byte byteHex2) throws InterruptedException {
        try {
            Thread.sleep(5);
            serialPort.writeByte(byteCobro1);
            Thread.sleep(5);
            //serialPort.writeByte(byteHex2);
            serialPort.writeByte(byteCobro2);
            Thread.sleep(5);
            //serialPort.writeByte(byteHex1);
            serialPort.writeByte(byteCobro3);
            Thread.sleep(5);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Metodo para desactivar vending
     *
     * @throws InterruptedException
     */
    public void off_vending() throws InterruptedException {
        try {
            Thread.sleep(15);
            serialPort.writeByte(Byte);
            serialPort.writeByte(Byte1);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Metodo para abrir puerto seleccionado
     *
     * @throws SerialPortException
     * @throws InterruptedException
     */
    public void open_port() throws SerialPortException, InterruptedException {
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

    }

    /**
     * Metodo para cerrar puerto
     *
     * @throws SerialPortException
     */
    public void close_port() throws SerialPortException {
        serialPort.closePort();
    }
}
