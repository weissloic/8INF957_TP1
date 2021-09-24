package server.src;

import java.lang.reflect.*;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import classe.Parser;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

class Server {

    Server(int port) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        startServer(port);
    }
    static Class classToLoad = null;
    private static String result = "";
    private static Map<String, Object> arrayClass = new HashMap<String, Object>();

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private static void startServer(int port) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        ServerSocket ss = new ServerSocket(port);

        while (true) {
            Socket s = ss.accept();
            ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream din = new ObjectInputStream(s.getInputStream());

            Parser parser = (Parser) din.readObject();
            getCommand(parser);

            dout.writeObject(result);
            dout.flush();
        }
    }

    interface Command {
        void execute(Parser parser) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException;
    }

    static class compilationCommand implements Command {

        private static void runCompilater(String classToCompile) {
            result = "Classe compilée: " + classToCompile;
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, classToCompile);
        }

        @Override
        public void execute(Parser parser) {
            if (parser.getPart(1).contains(",")) {
                String[] parts = parser.getPart(1).split(",");
                for (String part : parts) {
                    runCompilater(part);
                }
            } else {
                runCompilater(parser.getPart(1));
            }
        }
    }

    static class loadingCommand implements Command {

        @Override
        public void execute(Parser parser) throws ClassNotFoundException {
            Class currentClass = Class.forName(parser.getPart(1));
            result = "Classe chargée: " + parser.getPart(1);
        }
    }

    static class functionCommand implements Command {

        public Object traiterAppel(Object pointeurObjet, String nomFonction, String[] types,
                                 Object[] valeurs) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {

            Class[] typeObjects = new Class[types.length];
            for (int i = 0; i < types.length; i++) {
                if (types[i].indexOf(".") > 0) {
                    typeObjects[i] = Class.forName(types[i]);
                } else if (types[i].equals("float")){
                    typeObjects[i] = float.class;
                }
            }

            Object[] finalObjects = new Object[valeurs.length];
            for (int i = 0; i < valeurs.length; i++) {
                if (valeurs[i].toString().indexOf("(") > 0) {
                    String arg = valeurs[i].toString().split(":")[1];
                    String val = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));

                    arrayClass.get(val);
                    finalObjects[i] = arrayClass.get(val);
                }
                if (valeurs[i].toString().toLowerCase().indexOf("float") != -1) {
                    float f = Float.parseFloat(valeurs[i].toString().split(":")[1]);
                    finalObjects[i] = f;
                }
            }
            return invokeFunction(pointeurObjet, nomFonction, typeObjects, finalObjects);
        }

        public Object invokeFunction(Object pointeurObjet, String nomFonction, Class[] typeObjects,
                                     Object[] finalObjects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            Object Class = arrayClass.get(pointeurObjet);
            Object value;

            try {
                if (typeObjects != null && finalObjects != null) {
                    Method method = Class.getClass().getMethod(nomFonction, typeObjects);
                    value = method.invoke(Class, finalObjects);
                }
                else {
                    Method method = Class.getClass().getMethod(nomFonction);
                    value = method.invoke(Class);
                }
                return value;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        public void execute(Parser parser) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {

            Object value;

            if (parser.getPart(3) != null) {
                String[] parametres = parser.getPart(3).split(",");
                String[] types = new String[parametres.length];

                int index = 0;
                for (String string : parametres) {
                    types[index] = string.split(":")[0];
                    index++;
                }
                value = traiterAppel(parser.getPart(1), parser.getPart(2), types, parametres);
            } else {
                value = invokeFunction(parser.getPart(1), parser.getPart(2), null, null);
            }

            if (value == null) {
                result = "invoked method: " + parser.getPart(2);
            } else {
                result = "invoked method: " + parser.getPart(2) + "Resultat: " + value.toString();
                System.out.println(result);
            }
        }
    }

    static class RequestHandler {
        Parser myPars;

        public RequestHandler(Parser parser) {
            myPars = parser;
        }

        private Map<String, Command> commandMap = Map.of(
                "compilation", new compilationCommand(),
                "creation", new creationCommand(),
                "fonction", new functionCommand(),
                "ecriture", new writingCommand(),
                "lecture", new readingCommand(),
                "chargement", new loadingCommand()
        );


        public void handleRequest(String action) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
            Command command = commandMap.get(action);
            if (command == null)
                System.out.println("Unable to find this command");
            else
                command.execute(myPars);
        }
    }

    static class creationCommand implements Command {

        @Override
        public void execute(Parser parser) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            classToLoad = Class.forName(parser.getPart(1));
            Constructor insClassToLoad = classToLoad.getConstructor();
            arrayClass.put(parser.getPart(2), insClassToLoad.newInstance());
            result = "New instance of: " + parser.getPart(2);
        }
    }

    static class writingCommand implements Command {

        @Override
        public void execute(Parser parser) throws IllegalAccessException, InvocationTargetException {

            Method[] methods = classToLoad.getMethods();
            Field[] fields = classToLoad.getDeclaredFields();

            for(Field field: fields) {
                if (parser.getPart(2).equals(field.getName())) {
                    int mod = field.getModifiers();
                    if (Modifier.isPublic(mod)) {
                        field.set(parser.getPart(1), parser.getPart(3));
                        result = parser.getPart(2) + " changed to " + parser.getPart(3);
                    }
                    else {
                        for (Method m : methods) {
                            if (m.getName().equals("set" + capitalize(parser.getPart(2)))) {
                                Object r = m.invoke(arrayClass.get(parser.getPart(1)), parser.getPart(3));
                                result = parser.getPart(2) + " changed to " + parser.getPart(3);
                            }
                        }
                    }
                }
            }
        }
    }

    static class readingCommand implements Command {

        @Override
        public void execute(Parser parser) throws IllegalAccessException, InvocationTargetException {
            Method[] methods = classToLoad.getMethods();
            Field[] fields = classToLoad.getDeclaredFields();

            for(Field field: fields) {
                if (parser.getPart(1).equals(field.getName())) {
                    int mod = field.getModifiers();
                    if (Modifier.isPublic(mod)) {
                        field.get(parser.getPart(2));
                        result = " Value of " + parser.getPart(2) + " is equal to " + field.get(parser.getPart(2));;
                    }
                    else {
                        for (Method m : methods) {
                            if (m.getName().equals("get" + capitalize(parser.getPart(1)))) {
                                Object r = m.invoke(parser.getPart(2));
                                result = " Value of " + parser.getPart(2) + " is equal to " + r.toString();;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void getCommand(Parser parser) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        RequestHandler req = new RequestHandler(parser);
        req.handleRequest(parser.getPart(0));
    }

    public static void main(String args[])
            throws Exception {
        Server serv = new Server(Integer.parseInt(args[0]));
    }
}