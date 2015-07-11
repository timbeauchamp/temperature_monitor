/**
 * Created by tbeauch on 7/6/2015.
 */

import com.pi4j.io.gpio.*;

import java.util.concurrent.Callable;

import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;


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
    public String getWWW_ROOT()
    {
        return "/var/www";
    }

    @Override
    public void provision(Pin[] pins)
    {
    	MCP3008Reader.initMCP3008();

        int numPins = pins.length;
        myLed = new GpioPinDigitalOutput[numPins];

        // provision gpio pin #2 as an input pin with its internal pull down
        // resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(
                RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        System.out.println(" ... complete the GPIO #2 circuit and see the triggers take effect.");

// setup gpio pins #04, #05, #06, #07 as an output pins and make sure
// they are all LOW at startup


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
    
    public double getRange(int channel)
    {
        int data = readSPI(channel);
        double range = (double)data/1024;

    	return range;
    }

    public int readSPI(int channel)
    {
    	int retVal = 0;
    	retVal = MCP3008Reader.readMCP3008(channel);

        return retVal;
    }

}