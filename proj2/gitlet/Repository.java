package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static final File STAGE_FILE = join(GITLET_DIR,"STAGE");
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
*       stage 暂存区中存放的相当于key-value;
*       key-是文件名
*       value 是文件的blobId
*       add 操作发生时，检查commit的hashmap里是否有追踪这个文件，如果没有追踪，则加入到status中 如果已经有了，则查看commit-id是否有差异，
*       有差异则加入到暂存区，
*       没差异则忽略。
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
            Stage stage = new Stage();
            stage.save();
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
    private static Stage CurrentStage(){
        return Utils.readObject(STAGE_FILE,Stage.class);
    }
    public static void commit(String commitMessage) {
        String currentBranch = CurrentBranch();
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        Commit lastCommit = Utils.readObject(Utils.join(OBJECT_DIR,currentBranchLastCommitID),Commit.class);
        Map<String,String> fileMap = new HashMap<>(lastCommit.getTracked());
        List<String> fileList = getStageFiles();
        Stage stage = CurrentStage();
        Map<String,String> stageMap = stage.getStage();
        for (String file: fileList) {
            if (!fileMap.containsKey(file)){
//                            查看filemap 里存不存在这个文件
                String BlobId = stageMap.get(file);
                fileMap.put(file,BlobId);

            }
            else {
                String BlobIdfromStage = stageMap.get(file);
                fileMap.replace(file,BlobIdfromStage);
            }
        }
        /*
        * 清理暂存区
        * */
        stage.clear();
        List<String> parents = new ArrayList<>();
        parents.add(currentBranchLastCommitID);
        Commit commit = new Commit(fileMap,parents,commitMessage,CurrentBranch());
        Utils.writeObject(HEAD_FILE,currentBranch);
        Utils.writeObject(Utils.join(HEADS_DIR,CurrentBranch()),commit.getID());
        commit.save();
    }

    public static List<String> getStageFiles(){
        /*
        * todo 从暂存区中获取文件
        * */
        Stage stage = CurrentStage();
        List<String> fileList = new ArrayList<>(stage.getStage().keySet());
        return fileList;
    }
    private static String CurrentBranch(){
        return readObject(HEAD_FILE,String.class);
    }

    public static void removeBranch(String branchName) {
        String currentBranch = CurrentBranch();
        if (branchName.equals(currentBranch)){
            System.out.println("Cannot remove the current branch");
        }
        else {
        File branch = Utils.join(HEADS_DIR,branchName);
        if (branch.exists()){
            branch.delete();
        }else {
            System.out.println("A branch with that name does not exist");
        }
        }
    }
    private static List<String> getAllBranch(){
        return Utils.plainFilenamesIn(HEADS_DIR);
    }
    private static Commit preCommit(){
        String lastCommitID = CurrentBranchLastCommitID();
        return Utils.readObject(join(OBJECT_DIR,lastCommitID),Commit.class);
    }
    private static String CurrentBranchLastCommitID(){
        String currentBranch = CurrentBranch();
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        return currentBranchLastCommitID;
    }

    public static void add(String fileName){
        if (fileName.equals("")){
            System.out.println("File does not exist");
            System.exit(1);
        }else{
            File file = Utils.join(CWD,fileName);
            if (!file.exists()){
                System.out.println("File does not exist");
                System.exit(1);
            }else{
                Blob blob = new Blob(fileName);
                File blobfile = Utils.join(OBJECT_DIR,blob.getId());
                String blobID = blob.getId();
                if (!blobfile.exists()){
                    blob.save();
                }
                Stage stage = CurrentStage();
                Commit preCommit = preCommit();
                Map<String, String> tracked = preCommit.getTracked();
                if (!tracked.containsKey(fileName)){
                    stage.add(fileName,blobID);
                }else {
                    if (!tracked.get(fileName).equals(blobID)){
                        stage.add(fileName,blobID);
                    }
                }

            }
        }
    }
    public static void global_log(){
        List<String> commitList = Utils.plainFilenamesIn(OBJECT_DIR);
        StringBuffer buffer = new StringBuffer();
        for (String commit: commitList) {
            Dumpable commitContent = Utils.readObject(Utils.join(OBJECT_DIR,commit),Dumpable.class);
            if (commitContent instanceof Commit){
                buffer.append(((Commit) commitContent).getMessage() + "\n");
            }
        }
        System.out.println(buffer.toString());
    }
    public static List<Commit> getAllCommit(){
        List<String> commitCandidate = Utils.plainFilenamesIn(OBJECT_DIR);
        List<Commit> commitList = new ArrayList<>();
        for (String commit: commitCandidate) {
            Dumpable commitContent = Utils.readObject(Utils.join(OBJECT_DIR,commit),Dumpable.class);
            if (commitContent instanceof Commit){
                commitList.add((Commit) commitContent);
            }
        }
        return commitList;
    }
//
    public static void find(String commitMessage){
        List<Commit> commitList= getAllCommit();
        for (Commit commit:commitList) {
            if (commit.getMessage().equals(commitMessage)){
                System.out.println(commit.getID());
            }
        }
    }
    public static void status(){
        System.out.println("=== Branches ===");
        String HEADBranchName = CurrentBranch();
        System.out.println("*" + HEADBranchName);
        List<String> branchList = getAllBranch();
        Commit lastCommit = preCommit();
        Stage stage = CurrentStage();
        Map<String, String> stageMap = stage.getStage();
        Map<String, String> tracked = lastCommit.getTracked();
        List<String> untrackedFileList = new ArrayList<>();
        List<String> modifiedList = new ArrayList<>();
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        for (String branch:branchList) {
            if (!branch.equals(HEADBranchName))
                System.out.println(branch);
        }
        System.out.println("=== Staged Files ===");
        List<String> stageFiles = getStageFiles();
        for (String file: stageFiles) {
            System.out.println(file);
        }
        for (String file:fileList) {
            if (baseJudge(file)){
                if ((!tracked.containsKey(file)) && (!stageMap.containsKey(file))){
                    untrackedFileList.add(file);
                }
                if (tracked.containsKey(file)){
                    Blob blob = new Blob(file);
                    if (!blob.getId().equals(tracked.get(file)) &&(!stageMap.containsKey(file))){
                        modifiedList.add(file);
                    }
                }
            }
        }
        System.out.println("=== Removes Files ===");
//        TODO:remove file
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String file:modifiedList) {
            System.out.println(file + " (modified)");
        }
        System.out.println("=== Untracked Files ===");
//        print the untrackedList
        for (String file:untrackedFileList) {
            System.out.println(file);
        }
    }
    private static boolean baseJudge(String file){
        return ((!file.equals("Makefile")) && (!file.equals("pom.xml")) && (!file.equals("gitlet-design.md")) && (!file.equals("clean.sh")));
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
        String currentBranch = CurrentBranch();
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        Utils.writeObject(branch,currentBranchLastCommitID);
    }
//    切换到特定的分支
    public static void switchBranch(String branchName){
        String currentBranch = CurrentBranch();
        if (currentBranch.equals(branchName)){
            System.out.println("you're already in " + branchName);
            System.exit(1);
        }
        else {
        Utils.writeObject(HEAD_FILE,branchName);
        /*
        清空当前暂存区
        **/
        clearStage();
        System.exit(1);
        }
    }
//    清空暂存区
    private static void clearStage(){
        Stage stage = Utils.readObject(STAGE_FILE,Stage.class);
        stage.clear();
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
//        checkout file
        Commit lastCommit = preCommit();
        Map<String,String> allFileTrack = lastCommit.getTracked();
        for (String key: allFileTrack.keySet()) {
            if (key.equals(info)){
                Blob targetSnap = Utils.readObject(Utils.join(OBJECT_DIR,allFileTrack.get(info)),Blob.class);
                File checkoutFile = new File(info);
                Utils.writeContents(checkoutFile,targetSnap.getBytes());
                System.exit(0);
            }
        }
    }

    public static void reset(String commitID) {
        List<Commit> commitList = getAllCommit();
        Commit resetCommit = null;
        for (Commit commit:commitList) {
            if (commit.getID().equals(commitID)){
                resetCommit = commit;
                }
            }
        if (resetCommit != null){
            String CurrentBranch = resetCommit.getCommitBranch();
            writeObject(HEAD_FILE,CurrentBranch);
            writeObject(Utils.join(HEADS_DIR,CurrentBranch),resetCommit.getID());
            Map<String, String> tracked = resetCommit.getTracked();
            for (String key: tracked.keySet()) {
                String value = tracked.get(key);
                Blob blob = readObject(Utils.join(OBJECT_DIR, value), Blob.class);
                Utils.writeContents(new File(key), blob.getBytes());
            }
            List<String> fileList = plainFilenamesIn(CWD);
            for (String file: fileList) {
                if ((!tracked.containsKey(file)) && (baseJudge(file))){
                    File deleteFile = new File(file);
                    deleteFile.delete();
                }
            }
            clearStage();
        }else {
            System.out.println("No commit with that id exists.");
        }
    }
    /* TODO: fill in the rest of this class. */
}
