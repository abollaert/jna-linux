package be.assembledbytes.linux.i2c;

/**
 * An i2c-dev.
 */
public interface I2CDev {

    /**
     * Opens the device.
     */
    void open();

    /**
     * Closes the device.
     */
    void close();

    /**
     * Indicates whather or not the device is open.
     *
     * @return  true if the device is open, false if not.
     */
    boolean isOpen();

    /**
     * Set the slave address.
     *
     * @param address   Address of the slave.
     */
    void setSlave(final long address);

    /**
     * Writes to the device.
     *
     * @param   data        The data to write.
     */
    void write(final byte[] data);

    /**
     * Reads from the device.
     *
     * @param   numberOfBytes   The number of bytes to read.
     *
     * @return  The data read.
     */
    byte[] read(final int numberOfBytes);
}
