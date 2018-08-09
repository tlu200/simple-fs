// Representation of the contents of a directory block.  This
// block consists of a fixed number of inodes
//
// TODO
// - Extend DirectoryBlockAbstract with a class called DirectoryBlock
// -- Define the constructor to call the super() constructor
// -- Define the findName(), numEntries(), and AllocFree() methods
//


import java.util.*;
import java.lang.*;
import java.io.*;


abstract class DirectoryBlockAbstract extends Block {

  // The directory data consists of a set of Inodes
  public Inode inodes[];

  /**
   * Creates the DirectoryBlock structure.
   * @param dsk
   */
  public DirectoryBlockAbstract(Disk dsk) {

    // Reference to the disk
    disk = dsk;
    // The set of inodes contained in this directory block
    inodes = new Inode[maxFiles];
    int i;
    // Create the individual inodes
    for(i = 0; i < maxFiles; ++i) {
      inodes[i] = new Inode(nameSize, maxFileBlock);
    };
  };

  /**
   *  Print the list the component files/directories in this directory
   *  <base> is a string that is printed before the string corresponding
   *  to the name of the file/directory
   *
   *  Effects: none (no changes to the file system).
   *
   *  Return = nothing
   *
   * @param base
   */
  final public void list(String base) {
    int i;
    int count = 0;

    // Loop over all file/directory slots in this directory
    for(i = 0; i < maxFiles; ++i) {
      if(inodes[i].used_p) {
        // Only display those entries that are valid
        inodes[i].list(base);
        ++count;
      };
    };
    if(count == 0) {
      System.out.println("0 files found.");
    };
  };

  /**
   * Set directory entry <index> to be an invalid element.
   *
   * Effects:
   * 1. Removes the specified entry from the directory
   *
   * @param index
   */
  final public void DeallocEntry(int index) {
    inodes[index].used_p = false;
  };

  /**
   * Write this object instance to disk block <block>
   *
   * Effects:
   * 1. Change the state of the specified block on the disk.
   * 2. Leaves the write head at the end of the block
   *
   * Return = nothing
   *
   * Throws FileSystemException if:
   * 1. There is an i/o error
   *
   * @param block
   * @throws FileSystemException
   */
  final public void write(int block) throws FileSystemException {
    int i;
    try{
      // Move the write head to the correct location
      disk.fp.seek(block * disk.blockSize);
    }catch(IOException e) {
      throw new FileSystemException("DirectoryBlock::write(): seek error: " + e);
    };

    // Write the individual inodes out to the disk.
    for(i = 0; i < maxFiles; ++i) {
      inodes[i].write(disk.fp);
    };
  };

  /**
   * Read disk block <block> and stuff the values into this object instance
   *
   * Effects:
   * 1. Changes the state of this DirectoryBlock to that stored
   *    on the disk
   * 2. Leaves the read head at the end of the block
   *
   * Throws FileSystemException if:
   * 1. There is an i/o error
   *
   * @param block
   * @throws FileSystemException
   */
  final public void read(int block) throws FileSystemException {
    int i;
    try {
      // Set the position of the read head
      disk.fp.seek(block * disk.blockSize);
    }catch(IOException e) {
      throw new FileSystemException("DirectoryBlock::write(): seek error: " + e);
    };

    // Read in the individual inodes
    for(i = 0; i < maxFiles; ++i) {
      inodes[i].read(disk.fp);
    };

    // Remember the block number that we read from
    block_num = block;
  };

  /**
   * To be defined
   *
   * Find the entry in the directory that matches <name>
   *
   * Effects: none
   *
   * Return = the index of the Inode that matches.
   *     or = -1 if the name does not match any entries
   *
   * @param name
   * @return
   */
  abstract public int findName(String name);

  /**
   * Effects: none
   *
   * Return = the number of valid entries in this directory
   *
   * @return
   */
  abstract public int numEntries();

  /**
   * Identifies a free entry in the directory structure,
   * initializes it (file_flag = true indicates that this is a file
   * = false indicates that this is a directory), and sets the
   * entry's name.
   *
   * Effects:
   * 1. Sets a free entry in the directory list to be used
   *    and initializes its components
   *
   * Return = the index of the allocated entry
   *     or = -1 if no entries are available
   *
   * throws FileSystemException if:
   * 1. <name> is not a legal file name.
   *
   * @param name
   * @param file_flag
   * @return
   * @throws FileSystemException
   */
  abstract public int AllocFree(String name, boolean file_flag) throws FileSystemException;

};