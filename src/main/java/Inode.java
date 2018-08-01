// Defines the structure of an index node (Inode):
// used_p: valid inode entry
// file_p: file (true) or directory (false)
// name: name of the file/directory
// blockPtr[]: a list of component blocks (only those blocks that are
// needed are allocated).  For directories, only block 0 is used.
// Invalid inodes do not have valid blockPtr's
// size: the size of the file (unused if this is a directory).

import java.util.*;
import java.lang.*;
import java.io.*;


class Inode {
  ////// These are the data that will be written to the disk
  public boolean used_p;      // true -> inode is in use
  public boolean file_p;      // true -> file, false -> directory
  public byte name[];         // Name of file
  public short blockPtr[];    // Array of references to disk blocks
  public short size;          // Length of file in bytes
  //////

  // Internal variables
  private int mxFileBlock;    // Maximum number of blocks that can be used
  //   for file contents
  private int nameSz;         // The maximum file/directory name

  /**
   * Create a new inode.  nameSize and maxFileBlock are constants provided by the file system.
   * @param nameSize
   * @param maxFileBlock
   */
  public Inode(int nameSize, int maxFileBlock) {
    used_p = false;
    file_p = true;
    name = new byte[nameSize];
    blockPtr = new short[maxFileBlock];
    mxFileBlock = maxFileBlock;
    nameSz = nameSize;
    size = 0;
  };

  /**
   * Print the name of the file/directory.
   *
   * Effects: none
   *
   * Return = nothing
   *
   * @param base
   */
  public void list(String base) {
    String tab = "                                                             ";
    String str = base + "/" + getName();

    if(file_p) {
      str += tab.substring(1, tab.length() - str.length()) + size;
    }else{
      str += "/";
    };
    System.out.println(str);
  };

  /**
   * Allocate the inode.  Set the inode's name to <name>, and
   * file_p according to <file_flag>.
   * Note that no blockPtr's are allocated.
   *
   * Effects:
   * 1. This Inode entry is allocated (the assumption is that it is currently not allocated)
   *
   * throws FileSystemException if:
   * 1. The name is not a valid file/directory name
   *
   * @param name
   * @param file_flag
   * @throws FileSystemException
   */
  public void Alloc(String name, boolean file_flag)
      throws FileSystemException {

    setName(name);
    used_p = true;
    file_p = file_flag;
    size = 0;
  };

  /**
   * Get the name for this inode.  Handles the translation from a fixed length byte array to a String
   *
   * Effects: none
   *
   * Return = the String representation of the Inode's name
   *
   * @return
   */
  public String getName() {
    String out = "";
    try {
      // Create a new string from the ascii array
      out = new String(name, "US-ASCII");
    }catch(UnsupportedEncodingException e){
      System.out.println("Inode::getName(): " + e);
      System.exit(1);
    };

    // Trim off nulls at the end of the string.
    return(out.trim());
  };

  /**
   * Set the name of the inode.  Handles the translation from
   * the String to the inode's internal byte array representation.
   *
   * Effects:
   * 1. Changes the inode's name.
   *
   * Return = nothing
   *
   * throws FileSystemException if:
   * 1. The name is not valid
   *
   * @param str
   * @throws FileSystemException
   */
  public void setName(String str) throws FileSystemException{

    // Check for valid name
    if(!str.matches("^[\\w\\._]+$")) {
      throw new FileSystemException("Ill-formed file name (" + str + ").");
    };

    // Check string length
    if(str.length() > nameSz) {
      throw new FileSystemException("Name too long  (" + str + ").");
    };

    // Do the translation
    try {
      byte[] bytes = str.getBytes("US-ASCII");
      int i;

      // Copy into the internal byte array
      for(i = 0; i < str.length(); ++i) {
        name[i] = bytes[i];
      };
      // Zero out remaining elements
      for(i = str.length(); i < nameSz; ++i) {
        name[i] = 0;
      };

    }catch(UnsupportedEncodingException e){
      System.out.println("Inode::setName(): " + e);
      System.exit(1);
    };
  };

  /**
   * Write the inode to the file.  Assume that the write head
   * has already been positioned in the correct place.
   *
   * Effects:
   * 1. Alters the disk at the current write head
   * 2. The write head is left at the end of this Inode
   *
   * Return = nothing
   *
   * throws FileSystemException if:
   * 1. There is a write error.
   *
   * @param fp
   * @throws FileSystemException
   */
  public void write(RandomAccessFile fp) throws FileSystemException {
    int i;

    try{
      fp.writeBoolean(used_p);  // byte 0->1
      fp.writeBoolean(file_p);  // byte 2->3
      fp.write(name);           // byte 4->4+nameSize-1
      fp.writeShort(size);      // next 2 bytes
      for(i = 0; i < mxFileBlock; ++i) {
        fp.writeShort(blockPtr[i]);   // 2 bytes each
      };
    }catch(IOException e) {
      throw new FileSystemException("Inode::write() error:" + e);
    };
  };

  /**
   * Read the inode from the file.  Assume that the read head
   * has already been positioned in the correct place.
   *
   * Effects:
   * 1. Sets the current Inode state based upon the bytes
   *    retrieved from the file at the current read location
   * 2. Leaves the read head at the end of this Inode.
   *
   * throws FileSystemException if:
   * 1. There is a read error.
   *
   * @param fp
   * @throws FileSystemException
   */
  public void read(RandomAccessFile fp) throws FileSystemException {
    int i;
    try{
      // Used?
      used_p = fp.readBoolean();

      // A file?
      file_p = fp.readBoolean();

      // Name of the Inode
      fp.read(name);

      // Size of the Inode
      size = fp.readShort();

      // Read the set of block pointers
      for(i = 0; i < mxFileBlock; ++i) {
        blockPtr[i] = fp.readShort();
      };
    }catch(IOException e) {
      throw new FileSystemException("Inode::read() error: " + e);
    };
  };
};