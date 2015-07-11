import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static String dataFileName = "temps.xml";

    private static HardwareBase hardware = new HardwareBase();

    public static void main(String[] args)
    {
        Pin[] pins = new Pin[8];
        pins[0] = RaspiPin.GPIO_04;
        pins[1] = RaspiPin.GPIO_05;
        pins[2] = RaspiPin.GPIO_06;
        pins[3] = RaspiPin.GPIO_07;

        pins[4] = RaspiPin.GPIO_21;
        pins[5] = RaspiPin.GPIO_22;
        pins[6] = RaspiPin.GPIO_23;
        pins[7] = RaspiPin.GPIO_24;

        hardware.provision(pins);

        double[] temps = new double[4];
        double[] ranges = new double[4];
        boolean[] switches = new boolean[4];

        double range = 0.0;

        for (;;)
        {
            try
            {
                Thread.sleep(10 + (int)(range * 10000));
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            advanceLights();

            temps[0] = getTemp(0);
            temps[1] = getTemp(1);
            temps[2] = 0;
            temps[3] = 0;

            ranges[0] = hardware.getRange(2);
            ranges[1] = hardware.getRange(3);
            ranges[2] = hardware.getRange(4);
            ranges[3] = 0;

            switches[0] = hardware.getPinState(4).isHigh();
            switches[1] = hardware.getPinState(5).isHigh();
            switches[2] = hardware.getPinState(6).isHigh();
            switches[3] = hardware.getPinState(7).isHigh();

            System.out.println("Temp0:" + temps[0] + "(" +(temps[0] * 1.9 + 32) + " F)   Temp1: " + temps[1] + "(" +(temps[1] * 1.9 + 32) + " F)   Range: " + ranges[0] + " switch: " + switches[0]);

            String dataStr = createXML(temps, ranges, switches);
            writeData2File(dataStr);

            range = ranges[0];
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

    public static void writeData2File(String dataStr)
    {

        String wwwRoot = hardware.getWWW_ROOT();
        Path filePath = Paths.get(wwwRoot, dataFileName);
        Charset charset = Charset.forName("US-ASCII");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset))
        {
            writer.write(dataStr, 0, dataStr.length());
        } catch (IOException x)
        {
            System.err.format("IOException: %s%n", x);
        }
    }

    private static String createXML(double[] temps, double[]ranges, boolean[] switches)
    {
        StringBuffer retStr = new StringBuffer();
        retStr.append("<data>\n\t<temps>\n");

        int index = 0;
        for(double temp : temps)
        {
            retStr.append("\t\t<temp id='");
            retStr.append(index++);
            retStr.append("'>");
            retStr.append(temp);
            retStr.append("</temp>\n");
        }
        retStr.append("\t</temps>\n");
        retStr.append("\t<ranges>\n");
        index = 0;
        for(double range : ranges)
        {
            retStr.append("\t\t<range id='");
            retStr.append(index++);
            retStr.append("'>");
            retStr.append(range);
            retStr.append("</range>\n");
        }
        retStr.append("\t</ranges>\n");

        retStr.append("\t<switches>\n");
        index = 0;
        for(boolean aSwitch : switches)
        {
            retStr.append("\t\t<switch id='");
            retStr.append(index++);
            retStr.append("'>");
            retStr.append(aSwitch);
            retStr.append("</switch>\n");
        }
        retStr.append("\t</switches>\n");
        retStr.append("</data>\n");

        return retStr.toString();
    }

}
