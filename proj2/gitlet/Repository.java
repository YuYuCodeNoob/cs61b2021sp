package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
*               ---master:存储master分支下最新的commit id
*               ---another branch etc 存储其他分支下最新的commitID.
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
    public static final File HEAD_FILE = join(GITLET_DIR,"HEAD");
//    用于保存所有分支的最新commit信息
    public static final File HEADS_DIR = join(REFS_DIR,"heads");
//    添加暂存区
    public static final File ADD_STAGE_DIR = join(GITLET_DIR,"add_stage");
//    删除暂存区
    public static final File RM_STAGE_DIR = join(GITLET_DIR,"rm_stage");
    private static Commit InitialCommit;
    public static void init(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        }else {
            GITLET_DIR.mkdir();
            OBJECT_DIR.mkdir();
            REFS_DIR.mkdir();
            HEADS_DIR.mkdir();
            initCommit();
            initHEAD();
            initheads();
        }
    }
    private static void initCommit(){
        Commit commit = new Commit();
        InitialCommit = commit;
        commit.save();
    }
    private static void initHEAD(){
        Utils.writeObject(HEAD_FILE,DEFAULT_BRANCH);
    }
    private static void initheads(){
        File heads_file = Utils.join(HEADS_DIR,DEFAULT_BRANCH);
        Utils.writeObject(heads_file,InitialCommit.getID());
    }

    public static void commit(String commitMessage) {
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
                Blob blob = new Blob(fileName);
            }
        }
    }
    public static void global_log(){
        List<String> commitList = Utils.plainFilenamesIn(OBJECT_DIR);
        StringBuffer buffer = new StringBuffer();
        for (String commit: commitList) {
            Commit commitContent = Utils.readObject(Utils.join(OBJECT_DIR,commit),Commit.class);
            buffer.append(commitContent.getMessage()+ "\n");
        }
        System.out.println(buffer.toString());
    }
//
    public static void find(String commitMessage){
        List<String> commitList = Utils.plainFilenamesIn(OBJECT_DIR);
        for (String commit: commitList) {
            Commit commitContent = Utils.readObject(Utils.join(OBJECT_DIR,commit),Commit.class);
            if (commitContent.getMessage().equals(commitMessage)){
                System.out.println(commit);
            }
        }
    }
    public static void status(){
        System.out.println("=== Branches ===");
        String HEADBranchName = readObject(HEAD_FILE, String.class);
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);
        System.out.println("*" + HEADBranchName);
        for (String branch:branchList) {
            if (!branch.equals(HEADBranchName))
                System.out.println(branch);
        }
        System.out.println("=== Staged Files ===");
    }
    public static void branch(String branchName){
        if (branchName.equals("")){
            System.out.println("please enter a new branch name");
            System.exit(1);
        }
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);
        for (String branch:branchList) {
            if (branch.equals(branchName)){
                System.out.println("A branch with that name already exists.");
                System.exit(1);
            }
        }
        File branch = Utils.join(HEADS_DIR,branchName);
        String currentBranch = Utils.readObject(HEAD_FILE,String.class);
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        Utils.writeObject(branch,currentBranchLastCommitID);
    }
//    切换到特定的分支
    public static void switchBranch(String branchName){
        String currentBranch = Utils.readContentsAsString(HEAD_FILE);
        if (currentBranch.equals(branchName)){
            System.out.println("you're already in" + branchName);
            System.exit(1);
        }
        String BranchLastCommitId = readContentsAsString(Utils.join(HEAD_FILE,branchName));
        Utils.writeObject(HEAD_FILE,BranchLastCommitId);
                /*
                Todo ：清空当前暂存区
                *
                **/
        System.exit(1);
    }
    public static void checkout(String info){
//        先检查info 是不是branchName
//        如果是branchName 则切换到特定的分支上
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);
        for (String branch: branchList) {
            if (branch.equals(info)){
                switchBranch(info);
            }
        }
        String lastCommitBranch = Utils.readObject(HEAD_FILE,String.class);
        System.out.println(lastCommitBranch);
        String lastCommitID = Utils.readObject(Utils.join(HEADS_DIR,lastCommitBranch),String.class);
        Commit lastCommit = Utils.readObject(Utils.join(OBJECT_DIR,lastCommitID),Commit.class);
        Map<String,String> allFileTrack = lastCommit.getTracked();
        for (String key: allFileTrack.keySet()) {
            if (key.equals(info)){
                Blob targetSnap = Utils.readObject(Utils.join(OBJECT_DIR,allFileTrack.get(info)),Blob.class);
                File checkoutFile = new File(info);
                Utils.writeObject(checkoutFile,targetSnap.getBytes());
            }
        }
    }
    /* TODO: fill in the rest of this class. */
}
