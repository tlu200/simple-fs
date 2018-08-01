class DirectoryBlock extends DirectoryBlockAbstract {

  public DirectoryBlock(Disk dsk) {
    super(dsk);
  }

  @Override
  public int findName(String name) {
    for(int i = 0; i < maxFiles; ++i) {
      if(inodes[i].used_p && inodes[i].getName().equals(name)) {
        return i;
      }
    };
    return -1;
  }

  @Override
  public int numEntries() {
    int count = 0;
    for(int i = 0; i < maxFiles; ++i) {
      if(inodes[i].used_p) {
        count++;
      }
    };

    return count;
  }

  @Override
  public int AllocFree(String name, boolean file_flag) throws FileSystemException {
    for(int i = 0; i < maxFiles; ++i) {
      if(!inodes[i].used_p) {
        inodes[i].Alloc(name, file_flag);
        return i;
      }
    }

    return -1;
  }
}
