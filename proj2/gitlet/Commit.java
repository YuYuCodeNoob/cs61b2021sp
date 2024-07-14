package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private final List<String> Parents;
    /** The message of this Commit. */
    private String message;
    private final Map<String,String> Tracked;
    private Date curtIme;
    private final String ID;
    private File commitFile;
    public Commit(){
        this.message = "initial commit";
        this.Parents = new ArrayList<>();
        this.Tracked = new HashMap<>();
        this.curtIme = new Date(0);
        this.ID = generateID();
        this.commitFile = generateFIleName();
    }
//    将当前的Date curTime时间对象转化为String格式 便于产生UID;
    public String Date2String(Date date){
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
        return df.format(date);
    }
    private String generateID(){
        return Utils.sha1(Date2String(curtIme),message,Parents.toString(),Tracked.toString());
    }
    /* TODO: fill in the rest of this class.
    *   */
    private File generateFIleName(){
        return Utils.join(Repository.OBJECT_DIR,ID);
    }

    public void save() {
        Utils.writeObject(commitFile, this);
    }
//    测试能否正常读取数据
    public void test(){
        Commit commit = Utils.readObject(commitFile, Commit.class);
        System.out.println(commit.ID+""+commit.message+commit.Parents+commit.Tracked);
    }
}
