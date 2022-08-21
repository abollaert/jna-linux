package be.assembledbytes.linux.i2c;

import be.assembledbytes.linux.core.LibC;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class I2CDevImpl implements I2CDev {

    /** Logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(I2CDevImpl.class);

    /** ioctl command to set the slave. */
    private static final int I2C_SLAVE = 0x0703;

    /** The file name. */
    private final String fileName;

    /** File descriptor. */
    private int fd = -1;

    /**
     * Create a new instance.
     *
     * @param   fileName    The name of the file.
     */
    public I2CDevImpl(final String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void open() {
        if (!this.isOpen()) {
            logger.info("Opening I2C device at [{}]", this.fileName);

            final int ret = LibC.INSTANCE.open(this.fileName, LibC.O_RDWR);

            if (ret > 0) {
                logger.info("I2C device at [{}] opened, file descriptor [{}]",
                            this.fileName,
                            ret);

                this.fd = ret;
            } else {
                throw new IllegalStateException(String.format("Could not open I2C device at [%s], return code from open was [%s], [%s]",
                                                              this.fileName,
                                                              ret,
                                                              LibC.INSTANCE.strerror(Native.getLastError())));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void close() {
        if (this.isOpen()) {
            logger.info("Closing I2C device at [{}], file descriptor [{}]", this.fileName, this.fd);

            final int ret = LibC.INSTANCE.close(this.fd);

            if (ret != 0) {
                throw new IllegalStateException(String.format("Could not close I2C device at [%s], return code from close was [%s], [%s]",
                                                              this.fileName,
                                                              ret,
                                                              LibC.INSTANCE.strerror(Native.getLastError())));
            } else {
                this.fd = -1;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isOpen() {
        return this.fd != -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setSlave(final long address) {
        if (this.isOpen()) {
            logger.info("Setting slave to [{}]", address);

            final int ret = LibC.INSTANCE.ioctl(this.fd, I2C_SLAVE, new Pointer(address));

            if (ret != 0) {
                throw new IllegalStateException(String.format("Set slave failed : return code [%s], error [%s], [%s]",
                                                              ret,
                                                              Native.getLastError(),
                                                              LibC.INSTANCE.strerror(Native.getLastError())));
            }
        } else {
            throw new IllegalStateException("Cannot set slave on an I2CDev that is not open !");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void write(final byte[] data) {
        if (this.isOpen()) {
            final Memory memory = new Memory(data.length);
            memory.write(0, data, 0, data.length);

            final int ret = LibC.INSTANCE.write(this.fd, memory, data.length);

            if (ret < 0) {
                throw new IllegalStateException(String.format("Set slave failed : return code [%s], error [%s], [%s]",
                                                              ret,
                                                              Native.getLastError(),
                                                              LibC.INSTANCE.strerror(Native.getLastError())));
            }
        } else {
            throw new IllegalStateException("Cannot write to an I2CDev that is not open !");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] read(final int numberOfBytes) {
        if (this.isOpen()) {
            final Memory memory = new Memory(numberOfBytes);

            final int ret = LibC.INSTANCE.read(this.fd, memory, numberOfBytes);

            if (ret < 0) {
                throw new IllegalStateException(String.format("Set slave failed : return code [%s], error [%s], [%s]",
                                                              ret,
                                                              Native.getLastError(),
                                                              LibC.INSTANCE.strerror(Native.getLastError())));
            }

            return memory.getByteArray(0, numberOfBytes);
        } else {
            throw new IllegalStateException("Cannot write to an I2CDev that is not open !");
        }
    }
}
