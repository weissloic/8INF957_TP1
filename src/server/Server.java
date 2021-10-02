package server;

import java.lang.reflect.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import ca.uqac.registraire.Parser;
import ca.uqac.registraire.Cours;
import ca.uqac.registraire.Etudiant;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


/**
 *
 */
class Server {
    static Class classToLoad = null;
    private static String result = "";
    private static Map<String, Object> arrayClass = new HashMap<String, Object>();
    private static File file;
    private static PrintStream ps;

    /**
     * Le constructeur prend en argument le port du serveur au moment du lancement du programme
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     **/
    Server(int port, String fileTrace) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        startServer(port, fileTrace);
        try {
            ps = new PrintStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace(ps);
        }
    }

    /**
     * La fonction permet de mettre en majuscule le premier char d'un string
     **/
    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    /**
     * startServer lance la boucle infinie de gestion des sockets / commandes
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     **/
    private static void startServer(int port, String fileTrace) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        ServerSocket ss = new ServerSocket(port);
        file = new File(fileTrace);

        while (true) {
            Socket s = ss.accept();
            ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream din = new ObjectInputStream(s.getInputStream());

            Parser parser = (Parser) din.readObject();
            getCommand(parser);

            dout.writeObject(result);
            dout.flush();

            din.close();
            dout.close();
            s.close();
        }
    }

    /**
     * L'interface Command va donner la méthode execute a toutes les classes devant réaliser une commande
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     **/
    interface Command {
        void execute(Parser parser) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IOException;
    }

    /**
     * La classe compilation lance la compilation d'une classe Java
     **/
    static class compilationCommand implements Command {
        /**
         * Compile une classe java selon son nom
         * @param classToCompile nom de la classe
         **/
        private static void runCompilater(String classToCompile) throws IOException {

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int resCompil = compiler.run(null, null, null, classToCompile);

            if (resCompil == 0) {
                int index = classToCompile.lastIndexOf(".");
                String filename = classToCompile.substring(classToCompile.lastIndexOf("/"), index).replace("/", "");
                try {
                    Files.move(Paths.get(classToCompile.substring(0, index) + ".class"), Paths.get("./src/server/" + filename + ".class"), StandardCopyOption.REPLACE_EXISTING);
                    result = "Classe compilée: " + classToCompile;
                } catch (IOException e) {
                    e.printStackTrace(ps);
                }
            }
        }

        /**
         * Compile une classe java selon son nom
         * @param parser Objet reçu par le client
         **/
        @Override
        public void execute(Parser parser) throws IOException {
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

    /**
     * La classe loading charge l'instance d'une classe Java
     **/
    static class loadingCommand implements Command {

        /**
         * Chargement d'une classe java selon son nom
         * @param parser Objet reçu par le client
         **/
        @Override
        public void execute(Parser parser) throws ClassNotFoundException {
            Class currentClass = Class.forName(parser.getPart(1));
            result = "Classe chargée: " + parser.getPart(1);
        }
    }

    static class functionCommand implements Command {

        /**
         * La méthode callFunction lance une méthode d'une classe java selon son nom
         * @param pointeurObjet Nom de la classe
         * @param nomFonction Nom de la fonction à charger
         * @param types Objet reçu par le client
         * @param valeurs Objet reçu par le client
         * @throws ClassNotFoundException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchMethodException
         **/
        public Object callFunction(Object pointeurObjet, String nomFonction, String[] types,
                                 Object[] valeurs) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {


            // On instancie les types de classes selon le fichier d'entrée
            Class[] typeObjects = new Class[types.length];
            for (int i = 0; i < types.length; i++) {
                if (types[i].indexOf(".") > 0) {
                    typeObjects[i] = Class.forName(types[i]);
                } else if (types[i].equals("float")){
                    typeObjects[i] = float.class;
                }
            }

            // On instancie les types de classes
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


        /**
         * La méthode invokeFunction lance une méthode d'une classe java selon son nom
         * @param objectPointer Nom de la classe
         * @param functionName Nom de la fonction à charger
         * @param typeObjects Les types des objets à charger
         * @param finalObjects Les valeurs des objets
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchMethodException
         **/
        public Object invokeFunction(Object objectPointer, String functionName, Class[] typeObjects,
                                     Object[] finalObjects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            Object Class = arrayClass.get(objectPointer);
            Object value;

            try {
                // Fonction avec param
                if (typeObjects != null && finalObjects != null) {
                    Method method = Class.getClass().getMethod(functionName, typeObjects);
                    value = method.invoke(Class, finalObjects);
                }
                // Fonction sans param
                else {
                    Method method = Class.getClass().getMethod(functionName);
                    value = method.invoke(Class);
                }
                return value;
            } catch (NoSuchMethodException e) {
                e.printStackTrace(ps);
            } catch (IllegalAccessException e) {
                e.printStackTrace(ps);
            } catch (InvocationTargetException e) {
                e.printStackTrace(ps);
            }
            return null;
        }


        @Override
        public void execute(Parser parser) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
            Object value;

            if (parser.getPart(3) != null) {
                String[] parametres = parser.getPart(3).split(",");
                String[] types = new String[parametres.length];

                System.out.println(parametres.toString());
                System.out.println(types.toString());

                int index = 0;
                for (String string : parametres) {
                    types[index] = string.split(":")[0];
                    index++;
                }
                value = callFunction(parser.getPart(1), parser.getPart(2), types, parametres);
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

    /**
     * La classe request va gérer les différentes fonctions à effectuer selon la string que le client envoie
     **/
    static class RequestHandler {
        Parser myPars;
        /**
         * Constructeur de la classe requête, permet d'assigner la variable parser en var local
         * @param parser Objet envoyé depuis le serveur
         **/
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

        /**
         * La méthode handleRequest lance une méthode d'une classe java selon son nom
         * @param action String de la commande à effectuer
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchMethodException
         **/
        public void handleRequest(String action) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IOException {
            Command command = commandMap.get(action);
            if (command == null)
                System.out.println("Unable to find this command");
            else
                command.execute(myPars);
        }
    }

    /**
     * La classe creation permet de charger le constructeur d'une classe et de l'instancier
     **/
    static class creationCommand implements Command {

        @Override
        public void execute(Parser parser) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            classToLoad = Class.forName(parser.getPart(1));
            arrayClass.put(parser.getPart(2), classToLoad.getConstructor().newInstance());
            result = "New instance of: " + parser.getPart(2);
        }
    }

    /**
     * La classe writing permet de changer la valeur d'une variable dans une class
     **/
    static class writingCommand implements Command {

        @Override
        public void execute(Parser parser) throws IllegalAccessException, InvocationTargetException {

            Field[] fields = classToLoad.getDeclaredFields();

            for(Field field: fields) {
                if (parser.getPart(2).equals(field.getName())) {
                    int mod = field.getModifiers();
                    if (Modifier.isPublic(mod)) {
                        field.set(parser.getPart(1), parser.getPart(3));
                        result = parser.getPart(2) + " changed to " + parser.getPart(3);
                    }
                    else {
                        Method[] methods = classToLoad.getMethods();
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

    /**
     * La classe reading permet de lire la valeur d'une variable dans une class
     **/
    static class readingCommand implements Command {

        @Override
        public void execute(Parser parser) throws IllegalAccessException, InvocationTargetException {

            Field[] fields = classToLoad.getDeclaredFields();

            for(Field field: fields) {
                if (parser.getPart(1).equals(field.getName())) {
                    int mod = field.getModifiers();
                    if (Modifier.isPublic(mod)) {
                        field.get(parser.getPart(2));
                        result = " Value of " + parser.getPart(2) + " is equal to " + field.get(parser.getPart(2));;
                    }
                    else {
                        Method[] methods = classToLoad.getMethods();
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


    private static void getCommand(Parser parser) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, IOException {
        RequestHandler req = new RequestHandler(parser);
        req.handleRequest(parser.getPart(0));
    }

    private static void help() {
        System.out.println("This program work as a Server and handle command from a client");
        System.out.println("Please provide 2 args:");
        System.out.println("- The first one is the port which will be opened by the Server");
        System.out.println("- The second one is a filename which will contain all logs of the program");
    }

    public static void main(String args[])
            throws Exception {

        if (args.length != 2) {
            help();
        }

        Server serv = new Server(Integer.parseInt(args[0]), args[1]);
    }
}