package server.src;

import java.net.*;
import java.io.*;
import classe.Parser;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

class Server {



    private static void runCompilater(String classToCompile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, classToCompile);
    }

    private static void compilationClass(String classToCompile) {
        if (classToCompile.contains(",")) {
            String[] parts = classToCompile.split(",");
            for (String part : parts) {
                runCompilater(part);
            }
        } else {
            runCompilater(classToCompile);
        }
    }

    //private static void


    private static void getCommand(Parser parser) throws ClassNotFoundException {
        switch (parser.getPart(0)) {
            case "fonction":
                System.out.println("Monday");

                break;
            case "compilation":
                System.out.println("Tuesday");
                compilationClass(parser.getPart(1));
                break;
            case "chargement":
                System.out.println("Wednesday");
                Class currentClass = Class.forName(parser.getPart(1));
                break;
            case "creation":
                System.out.println("Thursday");
                break;
            case "ecriture":
                System.out.println("Friday");
                break;
            case "lecture":
                System.out.println("Saturday");
                break;
        }
    }

    public static void main(String args[])
            throws Exception {
                ServerSocket ss = new ServerSocket(3333);

                while (true) {
                    Socket s = ss.accept();
                    ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream din = new ObjectInputStream(s.getInputStream());

                    Parser parser = (Parser) din.readObject();
                    getCommand(parser);
                    System.out.println(parser.getPart(0));



                    dout.writeObject(parser);
                    dout.flush();
                    //din.close();
                    //dout.close();
                    //s.close();
                    //ss.close();
                }

                /*while(!parser.getPart(0).equals("dqzdzq")) {
                    parser.getmyCommands();
                    str2 = "teststtstststs"; //br.readLine();
                    dout.writeUTF(str2);
                    dout.flush();
                    str = din.readObject();
                }*/

    }
}