package be.assembledbytes.linux.sensor;

/**
 * Represents an ADC.
 */
public interface ADC {

    /**
     * Read the voltage on a particular channel.
     *
     * @param       channel     The channel to read the voltage on.
     *
     * @return      The voltage.
     */
    double voltage(final int channel);
}
