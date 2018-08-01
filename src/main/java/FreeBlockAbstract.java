// Represents the set of free blocks on our disk (and will live
//  only on block 0 of the disk).
// The representation is as follows:
// - Eight blocks are captured for each byte in the buffer
// - Blocks are ordered from least significant to most significant bits
// - A bit value of 1 indicates that the block is free
// - Numbering of the blocks starts at 0 (ie block 0 is
//   byte 0, bit 0)
// - Since the first two blocks in our file system are always allocated
//   (0 is the free block representation, and 1 is the root directory),
//   the first two bits will always be 0.
//
// TODO
// - Extend FreeBlockAbstract with the FreeBlock class
// -- supply the DeallocBlocks(), AllocBlocks() methods
// -- supply a FreeBlock() constructor (that simply calls super())
//

import java.util.*;
import java.lang.*;
import java.io.*;


abstract class FreeBlockAbstract extends Block {

  // Buffer of bytes that will be written to the first disk block
  public byte buffer[];

  /**
   * Allocate a free block.  Note no meaningfull initialization is performed
   * @param dsk
   */
  public FreeBlockAbstract(Disk dsk) {
    disk = dsk;
    buffer = new byte[disk.blockSize];
  };

  /**
   * Write this FreeBlock to disk block <block>
   *
   * Effects:
   *  1. Changes the block representation on the disk
   *  2. Leaves the write head at the end of the block
   *
   * Return = nothing.
   *
   * throws FileSystemException if:
   * 1. There is a write error
   *
   * @param block
   * @throws FileSystemException
   */
  public void write(int block) throws FileSystemException {
    try{
      // Move the write head to the proper location
      disk.fp.seek(block * disk.blockSize);

      // Write out the bytes
      disk.fp.write(buffer);
    }catch(IOException e){
      // Translate the exception
      throw new FileSystemException("FreeBlock::write(): " + e);
    };
  };

  /**
   * Read the contents of block <block> into this object in preparation for manipulation.
   * Note that block_num maintains a memory of the location from which this block was read
   *
   * Effects:
   * 1. Changes the cached state of this block to match the file
   *
   * Return = nothing.
   *
   * throws FileSystemException if:
   * 1.There is a read error
   *
   * @param block
   * @throws FileSystemException
   */
  public void read(int block) throws FileSystemException {
    try{
      // Move the read head to the proper location of our "disk"
      disk.fp.seek(block * disk.blockSize);

      // Read the data
      disk.fp.read(buffer);

      // Remember the location
      block_num = block;
    }catch(IOException e){
      // Translate the exception
      throw new FileSystemException("FreeBlock::read(): " + e);
    };
  };

  /**
   * Display the contents of the FreeBlock buffer.
   */
  public void display() {
    int i;

    // Only need to look at the number of bytes that are used for
    //  the blocks on the disk.
    for(i = 0; i < disk.numBlocks/8; ++i) {
      System.out.println("Block " + i + ": " + buffer[i]);
    };
  };

  ////////////////////////////////////////////////////////////////////////
  // Must be defined
  //

  /**
   * Deallocate the set of blocks that are indicated by blockPtr elements 0 ... num_blocks-1
   *
   * Assumptions:
   * 1. The object is already a valid FreeBlock (has been read in from
   *    disk or has been otherwise initialized).
   * 2. blockPtr elements 0 .. num_blocks-1 are valid block numbers and
   *    have previously been allocated
   *
   * Effects:
   * 1. The FreeBlock object allocation state is changed
   * 2. The object is written to the disk.
   *
   * Return = nothing
   *
   * FileSystemException is thrown if:
   * 1. There is a write error.
   *
   * @param blockPtr
   * @param num_blocks
   * @throws FileSystemException
   */
  abstract public void DeallocBlocks(short blockPtr[], int num_blocks) throws FileSystemException;

  /**
   * Allocate num_blocks that were previously free.
   * Assumptions:
   * 1. The object is already a valid FreeBlock (has been read in from
   *    disk or has been otherwise initialized).
   * 2. blockPtr is of length maxFileBlock
   *
   * Effects:
   * 1. The FreeBlock object allocation state is changed
   * 2. The object is written to the disk.
   * 3. blockPtr[] elements start ... start+num_blocks-1 are filled in with
   *    values corresponding to the blocks that have been allocated
   * 4. If there are not num_blocks free blocks to allocate or if
   *    start+num_blocks > maxFileBlock, then no allocation is made
   *    (no change in state is made to the object) and a
   *    FileSystemException is thrown.
   *
   * 5. FileSystemException is thrown if there is a write error.
   *
   * Return = nothing
   *
   * throws FileSystemException if:
   * 1. There was a write error to the disk
   * 2. The requested number of blocks does not fit within blockPtr[]
   * 3. The file system is too full to allocate the blocks
   * @param blockPtr
   * @param start
   * @param num_blocks
   * @throws FileSystemException
   */
  abstract public void AllocBlocks(short blockPtr[], int start, int num_blocks)
      throws FileSystemException;
};