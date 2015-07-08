import java.util.concurrent.Callable;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;


public class TriggerGpioExample
{
    private static PinState[][] sLights = new PinState[][]{
            {PinState.HIGH,PinState.LOW,PinState.LOW,PinState.LOW},
            {PinState.LOW,PinState.HIGH,PinState.LOW,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.HIGH,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.LOW,PinState.HIGH},
            {PinState.HIGH,PinState.LOW,PinState.LOW,PinState.LOW},
            {PinState.HIGH,PinState.HIGH,PinState.LOW,PinState.LOW},
            {PinState.LOW,PinState.HIGH,PinState.HIGH,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.HIGH,PinState.HIGH},
            {PinState.HIGH,PinState.LOW,PinState.LOW,PinState.HIGH},
            {PinState.HIGH,PinState.HIGH,PinState.LOW,PinState.LOW},
            {PinState.LOW,PinState.HIGH,PinState.HIGH,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.HIGH,PinState.HIGH},
            {PinState.HIGH,PinState.LOW,PinState.LOW,PinState.HIGH},
            {PinState.HIGH,PinState.HIGH,PinState.LOW,PinState.LOW},
            {PinState.HIGH,PinState.HIGH,PinState.HIGH,PinState.LOW},
            {PinState.LOW,PinState.HIGH,PinState.HIGH,PinState.HIGH},
            {PinState.HIGH,PinState.HIGH,PinState.LOW,PinState.LOW},
            {PinState.LOW,PinState.HIGH,PinState.HIGH,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.HIGH,PinState.HIGH},
            {PinState.HIGH,PinState.LOW,PinState.LOW,PinState.HIGH},
            {PinState.LOW,PinState.HIGH,PinState.LOW,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.HIGH,PinState.LOW},
            {PinState.LOW,PinState.LOW,PinState.LOW,PinState.HIGH},
            {PinState.LOW,PinState.LOW,PinState.LOW,PinState.LOW}
    };
    private static int sState = 0;

    static HardwareBase hardware = new HardwareBase();

    public static void main(String[] args) throws InterruptedException
    {

        Pin[] pins = new Pin[4];
        pins[0] = RaspiPin.GPIO_04;
        pins[1] = RaspiPin.GPIO_05;
        pins[2] = RaspiPin.GPIO_06;
        pins[3] = RaspiPin.GPIO_07;

        hardware.provision(pins);

// keep program running until user aborts (CTRL-C)
        for (;;)
        {
            Thread.sleep(1000);
 //           advanceLights();
            double temp1 = hardware.getTemp(0);
            double temp2 = hardware.getTemp(1);
            System.out.println("Temp 1: " + temp1 + "    " + "Temp 2: " + temp2);
            
        }

// stop all GPIO activity/threads by shutting down the GPIO controller
// (this method will forcefully shutdown all GPIO monitoring threads and
// scheduled tasks)
// gpio.shutdown();// <--- implement this method call if you wish to
// terminate the Pi4J GPIO controller
    }

    static void advanceLights()
    {
        if(++sState >= sLights.length)
        {
            sState = 0;
        }
//        System.out.println("advancing state: " + sState);
        PinState[] lights;

        for(int i = 0; i < 4; i++)
        {
            lights = sLights[sState];
            hardware.setPinState(i, lights[i]);
        }

    }

}
