package classe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser implements Serializable {

    private String myResultat;
    public ArrayList<String> myCommands = new ArrayList<String>();


    public Parser(String cmd) {
        for (String str: cmd.split("#")) {
            myCommands.add(str);
        }
        myResultat = "";
    }

    public String getMyResultat() {
        return myResultat;
    }

    public void setMyResultat(String myResultat) {
        this.myResultat = myResultat;
    }

    public ArrayList<String> getmyCommands() {
        return myCommands;
    }

    public void setMyComands(ArrayList<String> myCommands) {
        this.myCommands = myCommands;
    }

    public String getPart(int index) {
        if (myCommands.size() > index) {
            return myCommands.get(index);
        }
        return (null);
    }
}
