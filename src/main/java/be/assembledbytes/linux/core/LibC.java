package be.assembledbytes.linux.core;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface LibC extends Library  {

    /** Instance. */
    LibC INSTANCE = (LibC) Native.load("c", LibC.class);

    /** Read write. */
    int O_RDWR = 0x02;

    /**
     * Open a file, retugn a file descriptor.
     *
     * @param   pathName        The name of the path.
     * @param   flags           Flags to be used.
     *
     * @return  The file descriptor.
     */
    int open(final String pathName, final int flags);

    /**
     * Close the file with the given descriptor.
     *
     * @param       fileDescriptor      The file descriptor of the file to close.
     *
     * @return      0 on success, -1 on error.
     */
    int close(final int fileDescriptor);

    /**
     * Returns the last error in string format.
     *
     * @param   errnum      Number of the error.
     *
     * @return  The string representation of the last error.
     */
    String strerror(final int errnum);

    /**
     * ioctl.
     *
     * @param   fd              The file descriptor.
     * @param   request         The request.
     * @param   data            The data.
     *
     * @return  return code.
     */
    int ioctl(final int fd, final int request, final Pointer data);

    /**
     * Writes count bytes from the given buffer to the file pointed to by the given fd.
     *
     * @param   fd          File descriptor.
     * @param   buffer      Buffer.
     * @param   count       Count.
     *
     * @return  The number of bytes written or an error.
     */
    int write(final int fd, final Pointer buffer, final int count);

    /**
     * Read count bytes from the given file into the given buffer.
     *
     * @param   fd          File descriptor.
     * @param   buffer      Buffer.
     * @param   count       Number of bytes.
     *
     * @return  Number of bytes read.
     */
    int read(final int fd, final Pointer buffer, final int count);
}
