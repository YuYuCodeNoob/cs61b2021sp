package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Stage implements Serializable {
    private Map<String,String> stage;
    private File saveFile;
    public Stage(File saveFile){
        this.stage = new TreeMap<>();
        this.saveFile = saveFile;
    }
    public void save(){
        Utils.writeObject(saveFile,this);
    }
    public void clear(){
        this.stage = new HashMap<>();
        this.save();
    }
    public Map<String,String> getStage(){
        return stage;
    }

    public void add(String fileName, String blobID) {
        if (stage.containsKey(fileName)){
            stage.replace(fileName,blobID);
        }else {
            stage.put(fileName,blobID);
        }
        save();
    }
}
