// Class that simulates the disk.  The disk is a randomaccess file
// that contains the bytes necessary to represent all blocks.

import java.util.*;
import java.lang.*;
import java.io.*;


class Disk {
  public int numBlocks;               // Number of blocks
  public int blockSize;               // Number of bytes within each block
  public RandomAccessFile fp;         // Reference to the file
  public boolean debug_flag;          // Set if in debuggin mode

  /**
   * Disk interface constructor
   * Exits if there is a file error
   *
   * @param fname the name of the disk file on the base file system
   * @param bs size of the blocks
   * @param nb number of blocks
   */
  public Disk(String fname, int bs, int nb) {
    // Remember the disk parameters
    blockSize = bs;
    numBlocks = nb;
    debug_flag = false;
    try {
      // Open the disk file in r/w mode
      fp = new RandomAccessFile(fname, "rw");
    }catch(FileNotFoundException e) {
      System.out.println("Disk(): FileNotFoundException " + e);
      System.exit(1);
    };
  };

  /**
   * Generic debugging logger.
   * The string is only written if <debug_flag> has been set.
   *
   * @param strg String to be written to System.out
   */
  public void debug(String strg) {
    if(debug_flag) {
      System.out.println(strg);
    };
  };

  /**
   * Format the disk.
   * Effects:
   * 1. Initializes the first block (block 0) as the free block(the first two blocks are marked as not free)
   * 2. Initializes block 1 as the root directory; it is empty
   * 3. Clears the remaining blocks
   *
   * throws FileSystemException if:
   * 1. There is a disk write error
   *
   * @throws FileSystemException
   */
  public void format() throws FileSystemException {
    int i;

    // Master free space record
    FreeBlock fb = new FreeBlock(this);
    // Initialize free space
    fb.buffer[0] = (byte) 0xfc;
    for(i = 1; i < blockSize; ++i) {
      fb.buffer[i] = (byte) 0xff;
    };
    fb.write(0);

    // Root directory.
    DirectoryBlock dir = new DirectoryBlock(this);
    dir.write(1);

    // Empty data block for remaining blocks.
    DataBlock db = new DataBlock(this);
    for(i = 2; i < numBlocks; ++i) {
      db.write(i);
    };
  };
};