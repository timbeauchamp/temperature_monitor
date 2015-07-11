import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import java.util.Random;

/**
 * Created by tbeauch on 7/6/2015.
 */


public class Windows implements HardwareInterface
{
    public String platform = "Windows";
    private static Random rand;

    public Windows()
    {
        rand = new Random();
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
        return PinState.getState(rand.nextBoolean());
    }

    @Override
    public double getRange(int channel)
    {
        return rand.nextDouble();
    }

    @Override
    public int readSPI(int channel)
    {
        return 165 + rand.nextInt(85);
    }
}