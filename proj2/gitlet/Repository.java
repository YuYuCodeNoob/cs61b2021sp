package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    private static String currentBranch;
    private static final String DEFAULT_BRANCH = "master";
//    structure
/*
*  -.gitlet
*       --objects
*           --blob
*           --commit
*       --HEAD(存储当前头的信息)
*       --refs（存储所有分支末端的信息）
*           --heads
*               ---master
*               ---another branch etc.
*       --stage(暂存区)
*       用于存放已经add但是没有commit指向的blob实例，只有执行commit后才有commit的字典指向这个blob
*       commit的字典字典k-values k : filename values:blobID
*
* */
    /*
    * HEAD_DIR中只保留最新的HEAD指针，HEAD内容默认为最新的commitID，在进行版本转换时，只需要把HEAD指向之前的commit即可
    * */

    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
//    refs 保存本地分支
    public static final File REFS_DIR = join(GITLET_DIR,"refs");
//    HEAD 保存头指针索引
    public static final File HEAD_DIR = join(GITLET_DIR,"HEAD");
    private static Commit InitialCommit;
    public static void init(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        }else {
            GITLET_DIR.mkdir();
            OBJECT_DIR.mkdir();
            REFS_DIR.mkdir();
            HEAD_DIR.mkdir();
            initCommit();
            currentBranch = DEFAULT_BRANCH;
        }
    }
    private static void initCommit(){
        Commit commit = new Commit();
        InitialCommit = commit;
        commit.save();
    }
//   由于所有的仓库和所有分支都指向initial commit 所以需要return 一个出去
    public Commit getInitialCommit(){
        return InitialCommit;
    }
    public static void add(String fileName){
        if (fileName.equals("")){
            System.out.println("File does not exist");
            System.exit(1);
        }else{
            File file = new File(fileName);
            if (!file.exists()){
                System.out.println("File does not exist");
                System.exit(1);
            }else{
                Blob blob = new Blob();
            }
        }
    }
    public static void switchBranch(String branchName){
        if (branchName.equals(currentBranch)){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }else {
            currentBranch = branchName;
        }
    }
    /* TODO: fill in the rest of this class. */
}
