package be.assembledbytes.linux.sensor;

import be.assembledbytes.linux.i2c.I2CDev;
import be.assembledbytes.linux.i2c.I2CDevImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * ADC implementation for the ADS1115.
 */
public final class ADS1115 implements ADC {

    /** Logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ADS1115.class);

    /** Registers. */
    private static final int CONFIG_REGISTER = 0x01;
    private static final int CONVERSION_REGISTER = 0x00;

    /** Start a single conversion. */
    private static final int CONFIG_START_SINGLE_CONVERSION = 0x8000;

    private static final int CONFIG_PGA_4_096V = 0x0200;

    /** Single shot conversion mode. */
    private static final int CONFIG_MODE_SINGLE_SHOT = 0x0100;

    /** Data rate, 128 samples / sec. */
    private static final int CONFIG_DATA_RATE_128_SAMPLES = 0x0080;

    /** Disable the comparator config queue. */
    private static final int CONFIG_COMPARATOR_QUEUE_DISABLED = 0x0003;

    /** Channel configurations. */
    private static final int CONFIG_MUX_CHANNEL_0 = 0x4000;
    private static final int CONFIG_MUX_CHANNEL_1 = 0x5000;
    private static final int CONFIG_MUX_CHANNEL_2 = 0x6000;
    private static final int CONFIG_MUX_CHANNEL_3 = 0x7000;

    /** The {@link be.assembledbytes.linux.i2c.I2CDev} to be used. */
    private final I2CDev i2CDev;

    /** The slave address. */
    private final int slaveAddress;

    /**
     * Create a new instance.
     *
     * @param   i2cDev  The I2C device.
     */
    public ADS1115(final I2CDev i2cDev, final int slaveAddress) {
        this.i2CDev = Objects.requireNonNull(i2cDev);
        this.slaveAddress = slaveAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double voltage(final int channel) {
        this.i2CDev.setSlave(this.slaveAddress);
        this.configure(channel);

        return this.readConversionResult();
    }

    /**
     * Configures the device for a read on the given channel.
     *
     * @param   channel     The channel to use.
     */
    private final void configure(final int channel) {
        logger.debug("Configure ADC for single read on channel [{}]", channel);

        int configuration = CONFIG_START_SINGLE_CONVERSION |
                            CONFIG_PGA_4_096V |
                            CONFIG_MODE_SINGLE_SHOT |
                            CONFIG_DATA_RATE_128_SAMPLES |
                            CONFIG_COMPARATOR_QUEUE_DISABLED;

        switch (channel) {
            case 0 -> configuration |= CONFIG_MUX_CHANNEL_0;
            case 1 -> configuration |= CONFIG_MUX_CHANNEL_1;
            case 2 -> configuration |= CONFIG_MUX_CHANNEL_2;
            case 3 -> configuration |= CONFIG_MUX_CHANNEL_3;
            default -> throw new IllegalArgumentException(String.format("Invalid channel specified : [%s]", channel));
        }

        final byte[] data = new byte[3];

        data[0] = CONFIG_REGISTER;
        data[1] = (byte)((configuration >> 8) & 0xFF);
        data[2] = (byte)(configuration & 0xFF);

        this.i2CDev.write(data);

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Read the conversion result from the ADC.
     *
     * @return  The conversion result from the ADC.
     */
    private final double readConversionResult() {
        this.i2CDev.write(new byte[] { CONVERSION_REGISTER });

        final byte[] data = this.i2CDev.read(2);
        final int analogValue = ((data[0] & 0xFF) << 8) + (data[1] & 0xFF);

        return ((double)analogValue * 4.096) / 32767.0;
    }
}
