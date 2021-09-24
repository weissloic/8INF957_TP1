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

class File {
    public ArrayList<String> openFile(String namefile) throws IOException {
        BufferedReader in = null;
        ArrayList<String> myList = new ArrayList<>();
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
        }
    }
}

class MyClient {

    public static ArrayList<String> myList;
    public File file;
    public String filenameClass;

    public MyClient(String ip, int port, String openFilename, String createFilename) throws Exception {
        file = new File();
        filenameClass = createFilename;
        myList = file.openFile(openFilename);

        initConnection(ip, port);
    }

    public void initConnection(String ip, int port)
            throws Exception {
        for (String num : myList) {
            Socket s = new Socket(ip, port);
            ObjectInputStream din = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());

            Parser test = new Parser(num);

            dout.writeObject(test);
            dout.flush();

            String parser = (String) din.readObject();

            file.writeAtTheEndOfFile(filenameClass, parser);
            System.out.println(parser);
            din.close();
            dout.close();

            s.close();
        }
    }
    public static void main(String args[]) throws Exception {
        MyClient dzq = new MyClient("localhost", 3333, args[0], args[1]);
    }
}
