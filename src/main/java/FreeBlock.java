class FreeBlock extends FreeBlockAbstract {

  public FreeBlock(Disk dsk) {
    super(dsk);
  }

  @Override
  public void DeallocBlocks(short[] blockPtr, int num_blocks) throws FileSystemException {
    int prev_block_num = block_num;
    for(int i = 0; i < num_blocks; i++) {
      for(int j = 0; j < disk.blockSize; j++) {
        buffer[j] = (byte) 0xff;
      };
      write(blockPtr[i]);
    };
    read(prev_block_num);
  }

  @Override
  public void AllocBlocks(short[] blockPtr, int start, int num_blocks) throws FileSystemException {
    int prev_block_num = block_num;
    if (start + num_blocks > disk.blockSize) {
      throw new FileSystemException("start + num_blocks > maxFileBlock");
    }

    int count = 0;
    short[] free = new short[num_blocks];

    for(short i = 2; i < disk.numBlocks; i++) {
      read(i);
      boolean isFreeBlock = true;

      for(int j = 0; j < disk.blockSize; j++) {
        if (buffer[j] != (byte) 0x00) {
          isFreeBlock = false;
        }
      };
      if(isFreeBlock) {
        free[count] = i;
        count++;
        if (count == num_blocks) {
          break;
        }
      }
    }

    if (count < num_blocks) {
      read(prev_block_num);
      throw new FileSystemException("There are not " + num_blocks + " free blocks to allocate");
    }

    for(int i = 0; i < num_blocks; i++) {
      for(int j = 0; j < disk.blockSize; j++) {
        buffer[j] = (byte) 0x01;
      };
      write(free[i]);
      blockPtr[i + start] = free[i];
    }
    read(prev_block_num);
  }
}
