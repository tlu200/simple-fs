class DirectoryBlock extends DirectoryBlockAbstract {

  public DirectoryBlock(Disk dsk) {
    super(dsk);
  }

  @Override
  public int findName(String name) {
    return 0;
  }

  @Override
  public int numEntries() {
    return 0;
  }

  @Override
  public int AllocFree(String name, boolean file_flag) throws FileSystemException {
    return 0;
  }
}
