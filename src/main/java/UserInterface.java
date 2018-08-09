// User interaction with the file system.

import java.util.*;
import java.lang.*;
import java.io.*;

public class UserInterface {

  public UserInterface(FileSystem fs) {
    boolean flag = true;
    String str;
    int elem;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String[] args;

    try{
      // Loop for each line
      while(flag) {

        System.out.print("\n> ");
        // Read the next line from the user
        do {
          str = br.readLine();
          if(str == null) {
            // Exit cleanly (useful when a file is the input)
            System.exit(0);
          };
          //System.out.println(str);
        }while(str.length() == 0);

        // Split the line into arguments that do not contain spaces
        //  the first substring is our command
        args = str.split("\\s+");

        try {
          // Make sure there is a command
          if(args.length > 0) {

            // Help
            if(args[0].equals("?") || args[0].equals("h")) {
              System.out.println("?                          - Help");
              System.out.println("append <fname1> <fname2>   - appends the contents of <fname1> onto <fname2>");
              System.out.println("b                          - show the free block status");
              System.out.println("c <fname>                  - create a file");
              System.out.println("cat <fname>                - show the contents of the file");
              System.out.println("export <fname1> <fname2>   - copy from external file <fname1> to Disk <fnam2>");
              System.out.println("format                     - format the file system");
              System.out.println("import <fname1> <fname2>   - copy from Disk <fname1> to external file <fnam2>");
              System.out.println("h                          - Help");
              System.out.println("ls <name>                  - list file/directory <name>");
              System.out.println("mkdir <name>               - create a directory");
              System.out.println("mv <fname1> <fname2>       - move a file");
              System.out.println("rm <fname>                 - remove a file");
              System.out.println("rmdir <name>               - remove a directory");
              System.out.println("q                          - quit");

            }else if(args[0].equals("ls")) {

              // List file/directory

              if(args.length == 1) {
                // Nothing specified - list the root directory
                fs.list("");
              }else{
                fs.list(args[1]);
              };
            }else if(args[0].equals("c")) {

              // Create a file
              if(args.length == 2) {
                fs.create(args[1]);
                System.out.println("File created.");
              }else {
                System.out.println("Usage: c <fname>");
              };
            }else if(args[0].equals("rm")) {

              // Remove a file
              if(args.length == 2) {
                fs.delete(args[1]);
                System.out.println("File deleted.");
              }else {
                System.out.println("Usage: rm <fname>");
              };
            }else if(args[0].equals("rmdir")) {

              // Remove a directory
              if(args.length == 2) {
                fs.deleteDir(args[1]);
                System.out.println("Directory deleted.");
              }else {
                System.out.println("Usage: rmdir <fname>");
              };
            }else if(args[0].equals("mkdir")) {

              // Create a directory
              if(args.length == 2) {
                fs.createDir(args[1]);
                System.out.println("Directory created.");
              }else {
                System.out.println("Usage: mkdir <fname>");
              };
            }else if(args[0].equals("format")) {

              // Format the file system
              if(args.length == 1) {
                fs.format();
                System.out.println("File sytem formatted.");
              }else {
                System.out.println("Usage: format");
              };
            }else if(args[0].equals("a")) {

              // Append a few bytes to a file (a test command)
              if(args.length == 2) {
                byte buf[] = new byte[]{'a', 'b', 'c', 'd'};
                fs.append(args[1], buf, buf.length);
              }else {
                System.out.println("Usage: a <fname>");
              };
            }else if(args[0].equals("cat")) {

              // Show the contents of a file
              if(args.length == 2) {
                byte[] bytes = fs.read(args[1]);
                if(bytes != null){
                  // Yes - there are bytes to be shown
                  String outstring = new String(bytes);
                  System.out.println(outstring);
                };
              }else {
                System.out.println("Usage: cat <fname>");
              };
            }else if(args[0].equals("b")) {

              // Display the free blocks
              fs.free_block.display();
            }else if(args[0].equals("q")) {

              // Quit
              flag = false;
            }else if(args[0].equals("import")) {

              // Import a file
              if(args.length == 3) {
                fs.fileImport(args[1], args[2]);
              }else if(args.length == 2) {
                fs.fileImport(args[1], args[1]);
              }else {
                System.out.println("Usage: import <fname1> [<fname2>]");
              }
            }else if(args[0].equals("export")) {

              // Export a file
              if(args.length == 3) {
                fs.fileExport(args[1], args[2]);
              }else if(args.length == 2) {
                fs.fileExport(args[1], args[1]);
              }else {
                System.out.println("Usage: export <fname1> [<fname2>]");
              }
            }else if(args[0].equals("cp")) {

              // Copy a file
              if(args.length == 3) {
                fs.fileCopy(args[1], args[2]);
              }else {
                System.out.println("Usage: cp <fname1> <fname2>");
              }
            }else if(args[0].equals("append")) {

              // Append a file
              if(args.length == 3) {
                fs.fileAppend(args[1], args[2]);
              }else {
                System.out.println("Usage: append <fname1> <fname2>");
              }
            }else if(args[0].equals("mv")) {

              // Move a file
              if(args.length == 3) {
                fs.move(args[1], args[2]);
              }else {
                System.out.println("Usage: mv <fname1> <fname2>");
              }
            }else if(args[0].equals("echo")) {
              System.out.println(str);
            }else if(args[0].startsWith("#")) {
              // Ignore these lines
            }else {

              // Don't understand the command
              System.out.println("?????");
            };
          };
        }catch(FileSystemException e) {
          System.out.println(e);
        };
      };
    }catch(IOException e){System.out.println("ClientInteraction: IO error");};
  };

  public static void main(String args[]) {
    // Create the file system object
    FileSystem fs = new FileSystem();

    // Comment this back in if you want some additional debuggin information
    //fs.disk.debug_flag = true;

    // Start the user interface
    UserInterface ui = new UserInterface(fs);
  };
};