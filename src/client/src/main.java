package client.src;

import ca.uqac.registraire.Parser;
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
    /**
     * @param namefile: path of the file
     * @return
     * @throws IOException
     */
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

    /**
     * @param namefile path of the file
     * @param text which will write in the file
     */
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

    /**
     * @param ip ip adress of the server
     * @param port port of the server
     * @param openFilename path of the commands file
     * @param createFilename path of the output file
     * @throws Exception
     */
    public MyClient(String ip, int port, String openFilename, String createFilename) throws Exception {
        file = new File();
        filenameClass = createFilename;
        myList = file.openFile(openFilename);

        initConnection(ip, port);
    }

    /**
     * @param ip ip adress of the server
     * @param port port of the server
     * @throws Exception
     */
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
        if (args.length != 4) {
            System.out.println("Please enter 4 args to start the program");
            System.exit(84);
        }
        MyClient client = new MyClient(args[0], Integer.parseInt(args[1]), args[2], args[3]);
        System.exit(0);
    }
}
