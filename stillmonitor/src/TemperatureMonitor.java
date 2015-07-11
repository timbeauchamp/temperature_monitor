import java.util.concurrent.Callable;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.wiringpi.Spi;


public class TemperatureMonitor
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
        
        double range = 0.0;

        for (;;)
        {
            Thread.sleep(10 + (int)(range * 10000));
            advanceLights();
            double temp0 = getTemp(0);
            double temp1 = getTemp(1);
            range = hardware.getRange(2);
            System.out.println("Temp0:" + temp0 + "(" + (temp0 * 1.9 + 32) + " F)  Temp1: " + temp1 + "(" + (temp0 * 1.9 + 32) + " F)   Range: " + range);
        }
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

    public static double data2Temp(int data)
    {
        int mv =  (data * 3300)/ 0x400;
        double temperature = (double)(mv - 500) / 10;
//    	System.out.println("Data:" + data + "  mV:" + mv + "  Temp:" + temperature);
        return temperature;
    }

    public static double getTemp(int channel)
    {
        int data = hardware.readSPI(channel);

        double temperature = data2Temp(data);

        return temperature;
    }

}
