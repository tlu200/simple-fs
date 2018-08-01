// Represents the contents of a data block.  The
// buffer[] component occupies the entire block.

import java.util.*;
import java.lang.*;
import java.io.*;

class DataBlock extends Block {

  // Buffer of bytes that will be written to the first disk block
  public byte buffer[];

  // Allocate a new DataBlock
  public DataBlock(Disk dsk) {
    disk = dsk;
    buffer = new byte[disk.blockSize];
  };

  /**
   * Write this FreeBlock to disk block <block>
   * Effects:
   * 1. Change the state of the specified block on the disk.
   * 2. Leaves the write head at the end of the block
   *
   * Throws FileSystemException if:
   * 1. There is an i/o error
   *
   * @param block
   * @throws FileSystemException
   */
  public void write(int block) throws FileSystemException {
    try{
      // Move the write head to the correct location on the disk
      disk.fp.seek(block * disk.blockSize);

      // Write out the data
      disk.fp.write(buffer);
    }catch(IOException e){
      // Translate the exception
      throw new FileSystemException("DataBlock::write() error: " + e);
    };
  };

  /**
   * Read disk block <block> into this DataBlock
   * Effects:
   * 1. Changes the state of this DirectoryBlock to that stored on the disk
   * 2. Leaves the read head at the end of the block
   * 3. Remembers the originating block (stored in block_num)
   *
   * Throws FileSystemException if:
   * 1. There is an i/o error
   *
   * @param block
   * @throws FileSystemException
   */
  public void read(int block) throws FileSystemException {
    try{
      // Move the read head to the correct location on the disk
      disk.fp.seek(block * disk.blockSize);

      // Read in the buffer of bytes
      disk.fp.read(buffer);

      // Remember the original block number
      block_num = block;
    }catch(IOException e){
      // Translate exceptions
      throw new FileSystemException("DataBlock::read() error: " + e);
    };
  };
};