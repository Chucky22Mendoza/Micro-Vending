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
    //boolean final_vending = false;
    private final byte byteFin1 = (byte) 0xF1;
    private final byte byteFin2 = (byte) 0x00;

    private final byte byteInicio1 = (byte) 0xF1;
    private final byte byteInicio2 = (byte) 0x01;
    
    private final byte byteGPS1 = (byte) 0xF1;
    private final byte byteGPS2 = (byte) 0x10;
    
    private final byte byteVerde1 = (byte) 0xF1;
    private final byte byteVerde2 = (byte) 0x11;
    
    private final byte byteAmarillo1 = (byte) 0xF1;
    private final byte byteAmarillo2 = (byte) 0x12;
    
    private final byte byteRojo1 = (byte) 0xF1;
    private final byte byteRojo2 = (byte) 0x13;
    
    private final byte byteApagado1 = (byte) 0xF1;
    private final byte byteApagado2 = (byte) 0x14;
    
    private final byte byteCobro1 = (byte) 0xA1;
    private final byte byteCobro2 = (byte) 0x00;
    private final byte byteCobro3 = (byte) 0x0A;
    
    private String hostVending = "";
    private SerialPort serialPort = new SerialPort(hostVending);

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
        Thread.sleep(15);
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
        Thread.sleep(15);
    }

    /**
     * Metodo para verificar conexion de la vending
     *
     * @return
     */
    public boolean isVendingAlive() {
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
            byte[] buffer = serialPort.readBytes(2);//= serialPort.readBytes(totBytes);//Read 10 bytes from serial port
            respuesta = toHexString(buffer);
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
            Thread.sleep(15);
            serialPort.writeByte(byteInicio1);
            serialPort.writeByte(byteInicio2);
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
            Thread.sleep(15);
            serialPort.writeByte(byteVerde1);
            serialPort.writeByte(byteVerde2);
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
            Thread.sleep(15);
            serialPort.writeByte(byteAmarillo1);
            serialPort.writeByte(byteAmarillo2);
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
            Thread.sleep(15);
            serialPort.writeByte(byteRojo1);
            serialPort.writeByte(byteRojo2);
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
            Thread.sleep(15);
            serialPort.writeByte(byteApagado1);
            serialPort.writeByte(byteApagado2);
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
            Thread.sleep(15);
            serialPort.writeByte(byteGPS1);
            serialPort.writeByte(byteGPS2);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    
    /**
     * Mandar bytes de cobro
     * 
     * @param byteHex1
     * @throws InterruptedException
     */
    public void cobro(byte byteHex1) throws InterruptedException {
        try {
            Thread.sleep(15);
            serialPort.writeByte(byteCobro1);
            serialPort.writeByte(byteCobro2);
            serialPort.writeByte(byteHex1);
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
            serialPort.writeByte(byteFin1);
            serialPort.writeByte(byteFin2);
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
