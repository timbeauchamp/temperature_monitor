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
    public String getWWW_ROOT()
    {
        return "c:/www";
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
    public double getRange(int channel)
    {
        return .7;
    }

    @Override
    public int readSPI(int channel)
    {
        return 512;
    }
}