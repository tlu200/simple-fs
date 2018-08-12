#SimpleFS

Simulate a disk using a randomAccess file class in Java.This approach allows us to open the simulated disk in a combined read/write mode and allows us to arbitrarily position the read/write head anywhere in the file between read and write operations.

The simulated disk is partitioned into fixed-size blocks of 1024 bytes each (there are 256 of these blocks). Our file system that is built on top of these blocks will use blocks in one of three ways: to represent the free space, to represent directories, and to represent file data.

To generate the class files:
```
mvn package
```

Then run it:
```
java -jar target/simple-fs-1.0-SNAPSHOT.jar
```

Format the Disk when you run it the first time
```
> format
```