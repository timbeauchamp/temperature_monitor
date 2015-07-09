import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * Created by tbeauch on 7/6/2015.
 */
public interface HardwareInterface
{
    public String platform = "";
    public void provision(Pin[] pins);
    public void setPinState(int pin, PinState state);
    public PinState getPinState(int pin);

//    public double getTemp(int channel);
    public double getRange(int channel);
    public int readSPI(int channel);
}
