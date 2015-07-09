/**
 * Created by tbeauch on 7/6/2015.
 */

import com.pi4j.io.gpio.*;

import java.util.concurrent.Callable;

import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.wiringpi.Spi;


public class Raspberry implements HardwareInterface
{
    // create gpio controller
    private static GpioController gpio;
    private static GpioPinDigitalOutput myLed[];

    public String platform = "Raspberry";

    public Raspberry()
    {
        gpio = GpioFactory.getInstance();
        myLed = new GpioPinDigitalOutput[4];
    }

    @Override
    public void provision(Pin[] pins)
    {
        // provision gpio pin #2 as an input pin with its internal pull down
        // resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(
                RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        System.out.println(" ... complete the GPIO #2 circuit and see the triggers take effect.");

// setup gpio pins #04, #05, #06, #07 as an output pins and make sure
// they are all LOW at startup

        int numPins = pins.length;
        int index = 0;
        for(Pin pin : pins)
        {
            myLed[index] = gpio.provisionDigitalOutputPin(pin, pin.getName(), PinState.LOW);
            index++;
        }

// create a gpio control trigger on the input pin ; when the input goes
// HIGH, also set gpio pin #04 to HIGH
        myButton.addTrigger(new GpioSetStateTrigger(PinState.HIGH, myLed[0],
                PinState.HIGH));

// create a gpio control trigger on the input pin ; when the input goes
// LOW, also set gpio pin #04 to LOW
        myButton.addTrigger(new GpioSetStateTrigger(PinState.LOW, myLed[0],
                PinState.LOW));

// create a gpio synchronization trigger on the input pin; when the
// input changes, also set gpio pin #05 to same state
        myButton.addTrigger(new GpioSyncStateTrigger(myLed[1]));

// create a gccpio pulse trigger on the input pin; when the input goes
// HIGH, also pulse gpio pin #06 to the HIGH state for 1 second
        myButton.addTrigger(new GpioPulseStateTrigger(PinState.HIGH, myLed[2],
                1000));

// create a gpio pulse trigger on the input pin; when the input goes
// HIGH, also pulse gpio pin #06 to the HIGH state for 1 second
        myButton.addTrigger(new GpioPulseStateTrigger(PinState.HIGH, myLed[3],
                3000));

// create a gpio callback trigger on gpio pin#4; when #4 changes state,
// perform a callback
// invocation on the user defined 'Callable' class instance
        myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>()
        {
            public Void call() throws Exception
            {
                System.out.println(" --> GPIO TRIGGER CALLBACK RECEIVED ");
                return null;
            }
        }));

    }

    @Override
    public void setPinState(int pin, PinState state)
    {
        myLed[pin].setState(state);
    }

    @Override
    public PinState getPinState(int pin)
    {
        return myLed[pin].getState();
    }

//    // SPI operations
//    public static byte WRITE_CMD = 0x40;
//    public static byte READ_CMD  = 0x41;
//    
//    // configuration
//    private static final byte IODIRA = 0x00; // I/O direction A
//    private static final byte IODIRB = 0x01; // I/O direction B
//    private static final byte IOCON  = 0x0A; // I/O config
//    private static final byte GPIOA  = 0x12; // port A
//    private static final byte GPIOB  = 0x13; // port B
//    private static final byte GPPUA  = 0x0C; // port A pullups
//    private static final byte GPPUB  = 0x0D; // port B pullups
//    private static final byte OUTPUT_PORT = GPIOA;
//    private static final byte INPUT_PORT  = GPIOB;
//    private static final byte INPUT_PULLUPS = GPPUB;
    

    public double getRange(int channel)
    {
        // setup SPI
      
    	int fd = Spi.wiringPiSPISetup(0, 10000000);
        if (fd <= -1)
        {
            System.out.println(" ==>> SPI SETUP FAILED");
            return -50.0;
        }
        int data = readSPI(channel);
        double range = (double)data/1024d;

    	return range;
    }
    

    public static int toUnsigned(byte in)
    {
        return in & 0xFF;
    }


    public int readSPI(int channel)
    {
        byte packet[] = new byte[3];
        packet[0] = 1;    // address byte
        packet[1] = (byte) ((8 + channel) << 4);    // channel and diff
        packet[2] = 0b00000000;  // data byte
        
//        System.out.println("[TX] " + bytesToHex(packet));
        Spi.wiringPiSPIDataRW(0, packet, 3);
//        System.out.println("[RX] " + bytesToHex(packet));
        int msb = packet[1] & 3;
        int lsb = toUnsigned(packet[2]);

        int result = (msb << 8) + lsb;
        return result;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}