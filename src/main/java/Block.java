// Basic structure + constants for our disk blocks.
// Subclasses define the specifics of how the bytes within a block are used

import java.util.*;
import java.lang.*;
import java.io.*;

abstract class Block {
  // These constants assume a block size of 1024
  static public int nameSize = 20;        // Maximum length of file/dir name
  static public int maxFileBlock = 20;    // Maximum number of blocks for each file
  static public int maxFiles = 16;        // Maximum number of files/dir in a directory

  public int block_num;                   // Set by the read method
  public Disk disk;                       // Reference to the disk object


  // Must be defined by each block type
  abstract public void write(int block) throws FileSystemException;
  abstract public void read(int block) throws FileSystemException;
};