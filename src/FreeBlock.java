class FreeBlock extends FreeBlockAbstract {

  public FreeBlock(Disk dsk) {
    super(dsk);
  }

  @Override
  public void DeallocBlocks(short[] blockPtr, int num_blocks) throws FileSystemException {

  }

  @Override
  public void AllocBlocks(short[] blockPtr, int start, int num_blocks) throws FileSystemException {

  }
}
