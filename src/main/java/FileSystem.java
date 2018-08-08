class FileSystem extends FileSystemAbstract {
  public FileSystem() {
    db = new DirectoryBlock(disk);
  }

  @Override
  public void list(String name) throws FileSystemException {
    db.read(1);
    if (name == null || name.length() == 0) {
      db.list("");
      return;
    }

    String arr[] = name.split("/");

    for(String dir: arr) {
      int index = db.findName(dir);
      if (index == -1) {
        throw new FileSystemException("The parent directory does not exist");
      }
      db.read(db.inodes[index].blockPtr[0]);
    }

    db.list(name);
  }

  @Override
  public void createDir(String name) throws FileSystemException {
    String entryName = goToParentDirectory(name);

    if (db.findName(entryName) != -1) {
      throw new FileSystemException("Entry already exist");
    }

    checkDirectoryName(entryName);

    int index = db.AllocFree(entryName, false);
    free_block.AllocBlocks(db.inodes[index].blockPtr, 0, 1);

    DirectoryBlock dir = new DirectoryBlock(disk);
    dir.write(db.inodes[index].blockPtr[0]);

    db.write(db.block_num);
  }

  @Override
  public void deleteDir(String name) throws FileSystemException {

  }

  @Override
  public void create(String name) throws FileSystemException {
    checkDirectoryName(name);
    if(existsDirectory(name)) {
      // original was a directory
      throw new FileSystemException(name + " is a directory.");
    }

    String entryName = goToParentDirectory(name);

    int index = db.findName(entryName);
    if (index != -1) {
      db.inodes[index].size = 0;
    } else {
      db.AllocFree(entryName, true);
    }
    db.write(db.block_num);
  }

  @Override
  public void delete(String name) throws FileSystemException {
    Boolean exist = this.existsFile(name);
    if (!exist) {
      throw new FileSystemException("File not exists.");
    }
    String entryName = goToParentDirectory(name);
    int index = db.findName(entryName);
    db.DeallocEntry(index);
    db.write(db.block_num);
  }

  @Override
  public byte[] read(String name) throws FileSystemException {
    return new byte[0];
  }

  @Override
  public byte[] read(String name, int offset, int length) throws FileSystemException {
    return new byte[0];
  }

  @Override
  public boolean existsFile(String name) throws FileSystemException {
    String entryName = goToParentDirectory(name);
    int index = db.findName(entryName);
    return index != -1 && db.inodes[index].file_p;
  }

  @Override
  public boolean existsDirectory(String name) throws FileSystemException {
    if (name == null || name.length() == 0) {
      throw new FileSystemException("Ill-formed file name: could not be empty");
    }
    String arr[] = name.split("/");
    db.read(1);
    if (arr.length > 1) {
      for (int i = 0; i < arr.length - 1; i++) {
        int index = db.findName(arr[i]);
        if (index == -1 || db.inodes[index].file_p) {
          return false;
        }
        db.read(db.inodes[index].blockPtr[0]);
      }
      name = arr[arr.length - 1];
    }
    int index = db.findName(name);
    return !(index == -1 || db.inodes[index].file_p);
  }

  @Override
  public void append(String name, byte[] buf, int bufLen) throws FileSystemException {

  }

  @Override
  public void move(String name1, String name2) throws FileSystemException {

  }

  @Override
  public void fileCopy(String fname1, String fname2) throws FileSystemException {

  }

  @Override
  public void fileAppend(String fname1, String fname2) throws FileSystemException {

  }

  private String goToParentDirectory(String name) throws FileSystemException {
    String arr[] = name.split("/");
    db.read(1);
    if (arr.length > 1) {
      for (int i = 0; i < arr.length - 1; i++) {
        int index = db.findName(arr[i]);
        if (index == -1 || db.inodes[index].file_p) {
          throw new FileSystemException("The parent directory does not exist");
        }
        db.read(db.inodes[index].blockPtr[0]);
      }
      name = arr[arr.length - 1];
    }
    return name;
  }
}
