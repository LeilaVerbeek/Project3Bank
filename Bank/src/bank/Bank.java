package bank;
/**
 *
 * @author wolly1477
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;
import java.util.Set;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bank implements SerialPortEventListener{

    public static ScreenManager screenManager;
    public static Connection connection= null;
    public static String card = null;
    public static User user = null;
    public static Statement statement = null;
    /**
     * @param args the command line arguments
     */
    SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			//"/dev/tty.usbserial-A9007UX1", // Mac OS X
                        //"/dev/ttyACM0", // Raspberry Pi
			//"/dev/ttyUSB0", // Linux
			"COM4", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
        public static String[] pincode = new String[4];
        String dbName = "Bank";
        
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

     public void initialize() {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println("Failed to open serial port: "+e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    
    public static void ResetAll(){
        Bank.card = null;
        for(int i = 0; i <= Bank.pincode.length-1; i++){
            Bank.pincode[i] = null; 
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine();
                
                if(inputLine.startsWith("card:")){
                    String[] code = inputLine.split(":");
                    
                    screenManager.setSerialData(code[1]);
                }
                 
                if(inputLine.startsWith("pin:")){
                    String[] code = inputLine.split(":");
                    screenManager.setSerialData(code[1]);
                }
            } catch (Exception e) {
                System.err.println("Error, corrupted data received: "+e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public static void main(String[] args) throws Exception {
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
        }catch(SQLException err){
            System.out.println(err.getMessage()); 
        }
        Bank main = new Bank();
        main.initialize();
        
        screenManager = new ScreenManager();
        screenManager.showScreen();
        Thread thread=new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
            }
        };
        thread.start();
        System.out.println("Started");
        
    }


    public static boolean getPin(String card, String pincode){
        String query = "SELECT * FROM pas, rekening, account, klant, login WHERE pas.PasID ='"+card+"' AND pas.Pincode='"+pincode+"'";
        
        try{
            System.out.println(query);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next())
            {
                if(resultSet.getInt("FouteInlog")>2)
                {
                    return false;
                }
                System.out.println(resultSet.getString("Voornaam"));
                user = new User(resultSet.getString("Voornaam"), resultSet.getString("Achternaam"), resultSet.getDouble("Saldo"), resultSet.getBoolean("Geslacht"), resultSet.getString("Pas_PasID"));
                return true;
            }
        }catch(Exception e)
        {
            //error afhandelen
            System.out.println("Errorrrrrr: "+ e);
            
        }
        return false;
    }
}