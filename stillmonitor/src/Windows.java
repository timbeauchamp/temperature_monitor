import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * Created by tbeauch on 7/6/2015.
 */


public class Windows implements HardwareInterface
{
    public String platform = "Windows";

    public Windows()
    {

    }


    @Override
    public void provision(Pin[] pins)
    {

    }

    @Override
    public void setPinState(int pin, PinState state)
    {

    }

    @Override
    public PinState getPinState(int pin)
    {

        return null;
    }

    @Override
    public double getTemp(int channel)
    {
        return 0;
    }
    
    @Override
    public double getRange(int channel)
    {
        return 0;
    }
}