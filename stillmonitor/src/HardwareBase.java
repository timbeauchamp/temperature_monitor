// Base class for implementation


import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * Created by tbeauch on 7/6/2015.
 */
public class HardwareBase implements HardwareInterface
{
    HardwareInterface actualHardware = null;
    public String platform = "";

    public HardwareBase()
    {
        if(isRaspberry())
        {
            actualHardware = new Raspberry();

        }
        else
        {
            actualHardware = new Windows();
        }
    }

    private static boolean isRaspberry()
    {
        String osName = System.getProperty("os.name");
        System.out.println(osName);
        ;

        if(osName.toLowerCase().contains("windows"))
        {
            System.out.println("Not a Raspberry");
            return false;
        }
        else if(osName.toLowerCase().contains("mac"))
        {
            System.out.println("Not a Raspberry");
            return false;
        }
        else
        {
            System.out.println("Assuming a Raspberry");
            return true;
        }
    }

    @Override
    public void provision(Pin[] pins)
    {
    	actualHardware.provision(pins);

    }

    @Override
    public void setPinState(int pin, PinState state)
    {
        actualHardware.setPinState(pin, state);
    }

    @Override
    public PinState getPinState(int pin)
    {

        return actualHardware.getPinState(pin);
    }

    @Override
    public double getTemp(int channel)
    {
        return actualHardware.getTemp(channel);
    }
}
