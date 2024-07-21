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
public class Commit implements Serializable,Dumpable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
/**
 *  用来记录当前commit的所有父提交，可以用于分支合并
 *  例子为如果开发者在当前main分支下创建了feature-A分支，并且进行了两次提交
 *  feature-A :
 *      HEAD->commit_D->commit_C
 *  main:
 *      commit_B->commit_A
 *   现在如果要对main和feature-A进行分支合并
 *   则会创建一个新的commit_E
 *      它要使用main 和 feature-A的最新commit进行分支合并以及解决冲突
 *      这就是为何使用List<String>的数据结构
 *      因为可能需要多个分支进行合并
 *      String存储的是CommitID
 */

    private final List<String> Parents;
    /** The message of this Commit. */
//    message用于存放提交时的所添加的信息
    private String message;
//    Tracked是一个字典 key是fileName values 是Blob-Id即fileName的版本号
    private final Map<String,String> Tracked;
    private Date curtime;
    private final String ID;
    private File commitFile;
    private String commitBranch;
    public Commit(){
        this.message = "initial commit";
        this.Parents = new ArrayList<>();
        this.Tracked = new HashMap<>();
        this.curtime = new Date(0);
        this.commitBranch ="master";
        this.ID = generateID();
        this.commitFile = generateFileName();
    }

    public Commit(Map<String, String> fileMap, List<String> parents,String commitMessage,String commitBranch) {
        this.Tracked = fileMap;
        this.Parents = parents;
        this.message = commitMessage;
        this.curtime = new Date();
        this.ID = generateID();
        this.commitBranch = commitBranch;
        this.commitFile = generateFileName();
    }

    //    将当前的Date curTime时间对象转化为String格式 便于产生UID;
    public String Date2String(Date date){
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
        return df.format(date);
    }

    public String getID() {
        return ID;
    }

    private String generateID(){
        return Utils.sha1(Date2String(curtime),message,Parents.toString(),Tracked.toString());
    }
    /* TODO: fill in the rest of this class.
    *   */
    private File generateFileName(){
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
    public String getMessage(){
        return message;
    }
    public String getCurtime(){
        return Date2String(curtime);
    }
    public Map<String,String> getTracked(){
        return Tracked;
    }
    public String getCommitBranch(){
        return commitBranch;
    }
    @Override
    public void dump() {
    }
}
