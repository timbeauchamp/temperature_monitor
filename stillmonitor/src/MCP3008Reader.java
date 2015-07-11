
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Read an Analog to Digital Converter
 */
public class MCP3008Reader
{
  private final static boolean DISPLAY_DIGIT = "true".equals(System.getProperty("display.digit", "false"));
  // SPI: Serial Peripheral Interface
  private static Pin spiClk  = RaspiPin.GPIO_14; // Pin #14, clock
  private static Pin spiMiso = RaspiPin.GPIO_13; // Pin #13, data in.  MISO: Master In Slave Out
  private static Pin spiMosi = RaspiPin.GPIO_12; // Pin #12, data out. MOSI: Master Out Slave In
  private static Pin spiCs   = RaspiPin.GPIO_10; // Pin #10, Chip Select
 
  public enum MCP3008_input_channels
  {
    CH0(0),
    CH1(1),
    CH2(2),
    CH3(3),
    CH4(4),
    CH5(5),
    CH6(6),
    CH7(7);
    
    private int ch;
    
    MCP3008_input_channels(int chNum)
    {
      this.ch = chNum;
    }
    
    public int ch() { return this.ch; }
  }
  
  private static GpioController gpio;
  
  private static GpioPinDigitalInput  misoInput        = null;
  private static GpioPinDigitalOutput mosiOutput       = null;
  private static GpioPinDigitalOutput clockOutput      = null;
  private static GpioPinDigitalOutput chipSelectOutput = null;
  
  public static void initMCP3008()
  {
    gpio = GpioFactory.getInstance();
    mosiOutput       = gpio.provisionDigitalOutputPin(spiMosi, "MOSI", PinState.LOW);
    clockOutput      = gpio.provisionDigitalOutputPin(spiClk,  "CLK",  PinState.LOW);
    chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs,   "CS",   PinState.LOW);
    
    misoInput        = gpio.provisionDigitalInputPin(spiMiso, "MISO");    
  }
  
  public static void shutdownMCP3008()
  {
    gpio.shutdown();
  }
  
  public static int readMCP3008(int channel)
  {
    chipSelectOutput.high();
    
    clockOutput.low();
    chipSelectOutput.low();
  
    int adccommand = channel;
    if (DISPLAY_DIGIT)
      System.out.println("1 -       ADCCOMMAND: 0x" + lpad(Integer.toString(adccommand, 16).toUpperCase(), "0",  4) + 
                                       ", 0&" + lpad(Integer.toString(adccommand,  2).toUpperCase(), "0", 16));
    adccommand |= 0x18; // 0x18: 00011000
    if (DISPLAY_DIGIT)
      System.out.println("2 -       ADCCOMMAND: 0x" + lpad(Integer.toString(adccommand, 16).toUpperCase(), "0",  4) + 
                                       ", 0&" + lpad(Integer.toString(adccommand,  2).toUpperCase(), "0", 16));
    adccommand <<= 3;
    if (DISPLAY_DIGIT)
      System.out.println("3 -       ADCCOMMAND: 0x" + lpad(Integer.toString(adccommand, 16).toUpperCase(), "0",  4) + 
                                       ", 0&" + lpad(Integer.toString(adccommand,  2).toUpperCase(), "0", 16));
    // Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
    for (int i=0; i<5; i++) //
    {
      if (DISPLAY_DIGIT)
        System.out.println("4 - (i=" + i + ") ADCCOMMAND: 0x" + lpad(Integer.toString(adccommand, 16).toUpperCase(), "0",  4) + 
                                                       ", 0&" + lpad(Integer.toString(adccommand,  2).toUpperCase(), "0", 16));
      if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
        mosiOutput.high();
      else
        mosiOutput.low();
      adccommand <<= 1;      
      // Clock high and low
      tickOnPin(clockOutput);      
    }

    int adcOut = 0;
    for (int i=0; i<12; i++) // Read in one empty bit, one null bit and 10 ADC bits
    {
      tickOnPin(clockOutput);      
      adcOut <<= 1;

      if (misoInput.isHigh())
      {
//      System.out.println("    " + misoInput.getName() + " is high (i:" + i + ")");
        // Shift one bit on the adcOut
        adcOut |= 0x1;
      }
      if (DISPLAY_DIGIT)
        System.out.println("ADCOUT: 0x" + lpad(Integer.toString(adcOut, 16).toUpperCase(), "0",  4) + 
                                 ", 0&" + lpad(Integer.toString(adcOut,  2).toUpperCase(), "0", 16));
    }
    chipSelectOutput.high();

    adcOut >>= 1; // Drop first bit
    return adcOut;
  }
  
  private static void tickOnPin(GpioPinDigitalOutput pin)
  {
    pin.high();
    pin.low();
  }
  
  private static String lpad(String str, String with, int len)
  {
    String s = str;
    while (s.length() < len)
      s = with + s;
    return s;
  }
}
