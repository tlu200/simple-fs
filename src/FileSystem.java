class FileSystem extends FileSystemAbstract {

  @Override
  public void list(String name) throws FileSystemException {

  }

  @Override
  public void createDir(String name) throws FileSystemException {

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
    return false;
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
