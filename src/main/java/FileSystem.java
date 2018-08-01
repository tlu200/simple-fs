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
    String arr[] = name.split("/");
    db.read(1);
    if (arr.length > 1) {
      for (int i = 0; i < arr.length - 1; i++) {
        int index = db.findName(arr[i]);
        if (index == -1 || !db.inodes[index].used_p || db.inodes[index].file_p) {
          throw new FileSystemException("The parent directory does not exist");
        }
        db.read(db.inodes[index].blockPtr[0]);
      }
      name = arr[arr.length - 1];
    }

    if (db.findName(name) != -1) {
      throw new FileSystemException("Entry already exist");
    }

    checkDirectoryName(name);

    int index = db.AllocFree(name, false);
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

  }

  @Override
  public void delete(String name) throws FileSystemException {

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
    return false;
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
        if (index == -1 || !db.inodes[index].used_p || db.inodes[index].file_p) {
          return false;
        }
        db.read(db.inodes[index].blockPtr[0]);
      }
      name = arr[arr.length - 1];
    }
    int index = db.findName(name);
    return !(index == -1 || !db.inodes[index].used_p || db.inodes[index].file_p);
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
}