package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage implements Serializable {
    private Map<String,String> stage;
    public Stage(){
        this.stage = new HashMap<>();
    }
    public void save(){
        Utils.writeObject(Repository.STAGE_FILE,this);
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
