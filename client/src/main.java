package client.src;

import classe.Parser;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



class File {
    public ArrayList<String> openFile(String namefile) throws IOException {
        BufferedReader in = null;
        ArrayList<String> myList = new ArrayList<String>();
        try {
            in = new BufferedReader(new FileReader(namefile));
            String str;
            while ((str = in.readLine()) != null) {
                myList.add(str);
            }
            return myList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return null;
    }

    public void writeAtTheEndOfFile(String namefile, String text) {
        try {
            final Path path = Paths.get(namefile);
            Files.write(path, Arrays.asList(text), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
            // Add your own exception handling...
        }
    }
}


class MyClient {

    public static ArrayList<String> myList;
    public File file;
    public String filenameClass;
    private static void printArray(ArrayList<String> cmd) {
        int counter = 0;
        for (String num : cmd) {
            System.out.println(counter);
            counter++;
            System.out.println(num);
        }
    }

    public MyClient(String ip, int port, String openFilename, String createFilename) throws Exception {
         //= cmd;
        file = new File();
        filenameClass = createFilename;
        myList = file.openFile(openFilename);
        //ArrayList<String> cmd

        /*if (this.myList != null) {
            printArray(this.myList);
        }*/

        initConnection(ip, port);
    }

    public void initConnection(String ip, int port)
            throws Exception {
        for (String num : myList) {
            Socket s = new Socket(ip, port);
            ObjectInputStream din = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());

            Object str="", str2="";

            Parser test = new Parser(num);
            //System.out.println(counter);
            //counter++;

            dout.writeObject(test);
            dout.flush();

            Parser parser = (Parser) din.readObject();

            file.writeAtTheEndOfFile(filenameClass, parser.getPart(0));
            System.out.println(parser.getPart(0));
            din.close();
            dout.close();

            s.close();
            //str2 = din.readUTF();
            //System.out.println("Server says: "+str2);
            //System.out.println(num);
        }


    }
    public static void main(String args[]) throws Exception {
        MyClient dzq = new MyClient("localhost", 3333, args[0], args[1]);
    }
}
