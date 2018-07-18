/**
 * TODO:
 * Implement:
 * list()
 * createDir()
 * deleteDir()
 * create()
 * delete()
 * read(String name)
 * read(String name, int offset, int length)
 * existsFile()
 * existsDirectory()
 * append()
 * move()
 * fileCopy()
 * fileAppend()
 */

import java.util.*;
import java.lang.*;
import java.io.*;


abstract class FileSystemAbstract {

  protected int f_index;          // Set by getFileRef()
  protected DirectoryBlock db;    // Set by getFileRef()
  protected String fname;         // Set by getFileRef()

  public Disk disk;             // Disk reference
  public FreeBlock free_block;  // Cached copy of the free block

  /**
   * Create the FileSystem structures
   *
   * Effects:
   * 1. Opens the simulated disk
   * 2. Caches the free block
   */
  public FileSystemAbstract() {
    // Create reference to a disk of a specific geometry
    disk = new Disk("Disk0", 1024, 256);
    // Cache the free block list
    free_block = new FreeBlock(disk);
    try {
      free_block.read(0);
    }catch(FileSystemException e) {
      System.out.println("FileSystem(): Init error");
      System.exit(1);
    };
  };

  /**
   * Format the file system
   *
   * Effects:
   * 1.  Block 0 is configured as the free_block representation
   * 2.  Block 1 is configured as an empty root directory
   * 3.  All but the first two blocks are marked as being free
   *
   * throws FileSystemException if:
   * 1. An I/O error occurs with the external file system.
   *
   * @throws FileSystemException
   */
  final public void format () throws FileSystemException{
    disk.format();
    free_block.read(0);
    disk.debug("f:" + free_block.buffer[0]);
  };

  /**
   * Check the name of the directory.  If valid, returns without doing anything
   *
   * Effects:
   * 1. Nothing
   *
   * throws FileSystemException if:
   * 1. The directory name is not valid
   *
   * @param name
   * @throws FileSystemException
   */
  final protected void checkDirectoryName(String name) throws FileSystemException {
    // Check for valid characters
    if(!name.matches("^[\\w\\._/]*$")) {
      throw new FileSystemException("Ill-formed file name (" + name + ").");
    };

    // Check for repeated "/"
    if(name.matches(".*//.*")) {
      throw new FileSystemException("Ill-formed file name (" + name + ").");
    };
  };

  /*
   * Technically not FileSystem functions, as they are built upon file system functionality
   */

  /**
   * Import a file from the base file system into our file system
   *
   * Effects:
   * 1. Creates a new file in our file system
   * 2. If <fname2> already exists, it is first removed
   *
   * throws FileSystemException if:
   * 1. <fname2> is a directory
   * 2. The file system is too full to insert the file
   * 3. The parent directory of <fname2> is full
   * 4. <fname1> does not exist or is not a file
   * 5. there is a read error on <fname1>
   *
   * @param fname1
   * @param fname2
   * @throws FileSystemException
   */
  final public void fileImport(String fname1, String fname2)
      throws FileSystemException {
    int i;
    int len;
    byte[] buf = new byte[1000];
    try {
      // Open the original file in the base file system
      FileInputStream f = new FileInputStream(fname1);

      // Create the new file in the local file system
      create(fname2);

      // While there are bytes to read from the base file system
      while((len=f.read(buf)) > 0) {
        // Append these bytes to the local file system
        append(fname2, buf, len);
      };
      f.close();
    }catch(IOException e) {
      // Catch any errors from the import file access
      throw new FileSystemException("Import file error: " + e);
    };
  };

  /**
   * Exports a file from our file system to the base file system
   *
   * Effects:
   * 1. Creates a new file in the base file system.
   *
   * throws FileSystemException if:
   * 1. <fname1> is a directory
   * 2. There is an error in creating the file in the base file system
   * 3. <fname1> does not exist
   *
   * @param fname1
   * @param fname2
   * @throws FileSystemException
   */
  final public void fileExport(String fname1, String fname2)
      throws FileSystemException {
    int i;
    byte[] buf;

    try {
      if(existsFile(fname1)) {
        // The original file exists

        // Create the output file in the base system
        FileOutputStream f = new FileOutputStream(fname2);
        i = 0;
        // Read the first 1000 bytes
        buf = read(fname1, i, 1000);
        // While there are bytes in the buffer
        while(buf != null) {
          // Write the bytes to
          f.write(buf);
          // Increment the byte count
          i += 1000;
          // Read the next 1000 bytes from the local file system
          buf = read(fname1, i, 1000);
        };
        f.close();
      }else if(existsDirectory(fname1)) {
        // original was a directory
        throw new FileSystemException(fname1 + " is a directory.");
      }else{
        // original does not exist
        throw new FileSystemException(fname1 + " does not exist.");
      };
    }catch(IOException e) {
      // Catch any errors from the export file access
      throw new FileSystemException("Export file error: " + e);
    };
  };

  // To be provided in class FileSystem
  /**
   * List a named file or the contents of a named directory.
   * <name> is an absolute name.
   *
   * Effects:
   * 1. No changes are made to the file system
   * 2. If <name> does not exist, then a 'file not found' error is printed
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid name
   * 2. there is a read error
   *
   * @param name
   * @throws FileSystemException
   */
  abstract public void list(String name) throws FileSystemException;

  /**
   * Create a named directory.
   * <name> is an absolute directory name
   *
   * Effects:
   * 1. A new entry is added to the parent directory
   * 2. A new directory block is allocated
   *
   * throws FileSystemException if :
   * 1. <name> is not a valid name
   * 2. the parent directory does not exist
   * 3. The file system is too full to allocate the necessary space for the new directory
   * 4. The parent directory does not have enough space.
   * @param name
   * @throws FileSystemException
   */
  abstract public void createDir(String name) throws FileSystemException;

  /**
   * Delete a named directory.
   * <name> is an absolute directory name
   *
   * Effects:
   * 1. The specified directory block is deallocated
   * 2. The directory's entry is removed from its parent
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid name
   * 2. The directory does not exist
   * 3. The named directory is not empty
   * 4. The name corresponds to a file
   *
   * @param name
   * @throws FileSystemException
   */
  abstract public void deleteDir(String name) throws FileSystemException;

  /**
   * Create a named file
   * <name> is an absolute file name
   *
   * Effects
   * Inserts the file into the parent directory's file list.
   * The file is of size 0.
   * If the file already exists, then its contents are removed
   * (ie it is set back to a file of size 0).
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid name
   * 2. The directory does not exist
   * 3. The name corresponds to a directory
   * 4. The parent directory is full
   *
   * @param name
   * @throws FileSystemException
   */
  abstract public void create(String name) throws FileSystemException;

  /**
   * Delete the named file
   *
   * Effects:
   * Removes the specified file from its parent directory's list
   * Releases the data blocks used by the file
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid name
   * 2. The file does not exist
   * 3. The name corresponds to a directory
   *
   * @param name
   * @throws FileSystemException
   */
  abstract public void delete(String name) throws FileSystemException;

  /**
   * Read all of the bytes from a named file.
   *
   * Effects:
   * None (no changes are made to the file system)
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid name
   * 2. The file does not exist
   * 3. The name corresponds to a directory
   *
   * @param name
   * @return
   * @throws FileSystemException
   */
  abstract public byte[] read(String name) throws FileSystemException;

  /**
   * Read <length> bytes from a file starting at position <offset>
   *   (where 0 is the first byte in the file).
   *
   * Return = the array of <length> bytes (normally)
   *        = the array of size-offset bytes (if smaller than length)
   *        = null if offset is passed the end of the file.
   *
   * Effects:
   * None (no changes are made to the file system)
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid name
   * 2. The file does not exist
   * 3. The name corresponds to a directory
   *
   * @param name
   * @param offset
   * @param length
   * @return
   * @throws FileSystemException
   */
  abstract public byte[] read(String name, int offset, int length) throws FileSystemException;

  /**
   * Return = true if <name> exists and is a file
   *
   * Effects: none
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid file/directory name
   *
   * @param name
   * @return
   * @throws FileSystemException
   */
  abstract public boolean existsFile(String name) throws FileSystemException;

  /**
   * Return = true if <name> exists and is a directory
   *
   * Effects: none
   *
   * throws FileSystemException if:
   * 1. <name> is not a valid file/directory name
   *
   * @param name
   * @return
   * @throws FileSystemException
   */
  abstract public boolean existsDirectory(String name) throws FileSystemException;

  /**
   * Append the first <bufLen> bytes in <buf> to file <name>
   *
   * Return = nothing
   *
   * Effects:
   * 1. buf[0 ... bufLen-1] are appended to the end of the file
   * 2. If these bytes would make the file longer than can be
   *    represented by the inode, then only those bytes that would
   *    fit are appended to the file
   * 3. If not enough data blocks can be allocated to fit these
   *    bytes, then NO change is made to the file
   *
   * throws FileSystemException if:
   * 1. <name> does not exist
   * 2. <name> is a directory
   * 3. The file system is full (and not enough blocks can be allocated
   * 4. The file is too long to be represented by the inode
   *
   * @param name
   * @param buf
   * @param bufLen
   * @throws FileSystemException
   */
  abstract public void append(String name, byte[] buf, int bufLen)
      throws FileSystemException;

  /**
   * Move absolute <name1> to absolute <name2>
   * Notation: assume that <name1> = <path1>/<nm1>
   * Where <path> is the parent directory reference (and may be ""),
   * and <nm> is the local name of the file or directory
   *
   * Return = nothing
   *
   * Effects:
   * If <name1> is a file:
   * If <name2> is a file, then the old <name2> is removed and
   * replaced with the contents of <name1>
   *
   * If <name2> does not exist: <name2> inherits the contents
   * of <name1>
   *
   * If <name2> is a directory, then <name2>/<nm1> inherits the
   * contents of <name1>
   *
   * If <name1> is a directory:
   * If <name2> does not exist, then <name2> inherits the
   * contents of <name1>
   *
   * If <name2> is a directory, then <name2>/<nm1> inherits the
   * contents of <name1>
   *
   * throws FileSystemException if:
   * <name1> does not exist
   * <name1> or <name2> are not valid names
   * <name1> and <name2> are the same object
   * <name1> is a directory, and <name2> is a file
   * <name1> is an ancestor of <name2> (ie, doing the move would
   * set up a cycle in the directory structure that would not
   * be reachable from the root directory).
   *
   * HINT: 1. This should behave just like the unix "mv" command
   * 2. Because there are so many different conditions,
   * implement this method last.
   *
   * @param name1
   * @param name2
   * @throws FileSystemException
   */
  abstract public void move(String name1, String name2) throws FileSystemException;


  ////////////////////////////////////////////////////////////////////////
  // Technically not FileSystem functions, as they are built upon
  //   file system functionality
  //

  // public void fileCopy(String fname1, String fname2)
  //                                   throws FileSystemException
  //
  //  Copy file <fname1> to <fname2>
  //
  /*
   Effects:
    1.  Creates file <fname2> and copies the contents of
         <fname1> into the new file
    2.  If <fname2> is a directory, then a file is created within
         <fname2> with the same name as in <fname1>

   throws FileSystemException if:
    1.  Parent directory of <fname2> is full
    2.  File system is full
    3.  <fname1> or <fname2> are not valid file names
    4.  <fname1> does not exist
    5.  <fname1> is a directory
  //

  /**
   * Copy file <fname1> to <fname2>
   * Effects:
   * 1. Creates file <fname2> and copies the contents of
   *    <fname1> into the new file
   * 2. If <fname2> is a directory, then a file is created within
   *    <fname2> with the same name as in <fname1>
   *
   * throws FileSystemException if:
   * 1. Parent directory of <fname2> is full
   * 2. File system is full
   * 3. <fname1> or <fname2> are not valid file names
   * 4. <fname1> does not exist
   * 5. <fname1> is a directory
   * @param fname1
   * @param fname2
   * @throws FileSystemException
   */
  abstract public void fileCopy(String fname1, String fname2)
      throws FileSystemException;

  /**
   * Append file <fname1> to <fname2>
   *
   * Effects:
   * 1.  Appends the bytes of <fname1> onto <fname2>
   *
   * throws FileSystemException if:
   * 1. <fname1> or <fname2> are not valid file names
   * 2. <fname1> or <fname2> do not exist
   * 3. <fname1> or <fnam2> are directories
   * 4. File system is full
   *
   * @param fname1
   * @param fname2
   * @throws FileSystemException
   */
  abstract public void fileAppend(String fname1, String fname2)
      throws FileSystemException;
};