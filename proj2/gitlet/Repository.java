package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.*;

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
    public static final File DELETE_STAGE_FILE = join(GITLET_DIR,"DELETE_STAGE");
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
            Stage stage = new Stage(STAGE_FILE);
            Stage deleteStage = new Stage(DELETE_STAGE_FILE);
            stage.save();
            deleteStage.save();
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
        if (commitMessage.equals("")){
            System.out.println("Please enter a commit message");
            return;
        }
        String currentBranch = CurrentBranch();
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        Commit lastCommit = preCommit();
        Map<String,String> fileMap = new HashMap<>(lastCommit.getTracked());
        List<String> fileList = getStageFiles();
        Stage stage = CurrentStage();
        Map<String,String> stageMap = stage.getStage();
        Stage deleteStage = readObject(DELETE_STAGE_FILE,Stage.class);
        Map<String, String> deleteStageMap = deleteStage.getStage();
        if(stageMap.isEmpty() && deleteStageMap.isEmpty()){
            System.out.println("No changes added to the commit.");
            return;
        }
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
//        处理rm-stage
        for (String file:deleteStageMap.keySet()) {
            if (fileMap.containsKey(file)){
                fileMap.remove(file);
            }
        }
//        清理暂存区
        clearStage();
        List<String> parents = new ArrayList<>();
        parents.add(currentBranchLastCommitID);
        Commit commit = new Commit(fileMap,parents,commitMessage,CurrentBranch());
        Utils.writeObject(HEAD_FILE,currentBranch);
        Utils.writeObject(Utils.join(HEADS_DIR,CurrentBranch()),commit.getID());
        commit.save();
    }

    public static List<String> getStageFiles(){
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
            System.out.println("Cannot remove the current branch.");
        }
        else {
        File branch = Utils.join(HEADS_DIR,branchName);
        if (branch.exists()){
            branch.delete();
        }else {
            System.out.println("A branch with that name does not exist.");
        }
        }
    }
    private static List<String> getAllBranch(){
        return Utils.plainFilenamesIn(HEADS_DIR);
    }
    private static Commit preCommit(){
        String lastCommitID = CurrentBranchLastCommitID();
        return getCommitByID(lastCommitID);
    }
    private static String CurrentBranchLastCommitID(){
        String currentBranch = CurrentBranch();
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        return currentBranchLastCommitID;
    }

    public static void add(String fileName){
        if (fileName.equals("")){
            System.out.println("File does not exist.");
            return;
        }else{
            File file = Utils.join(CWD,fileName);
            if (!file.exists()){
                System.out.println("File does not exist.");
                return;
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
                Stage deleteStage = Utils.readObject(DELETE_STAGE_FILE,Stage.class);
                Map<String, String> deleteStageMap = deleteStage.getStage();
                if (deleteStageMap.containsKey(fileName) && deleteStageMap.get(fileName).equals(blobID)){
                    deleteStageMap.remove(fileName);
                    deleteStage.save();
                }
            }
        }
    }
    public static void global_log(){
        List<String> commitList = Utils.plainFilenamesIn(OBJECT_DIR);
        for (String commit: commitList) {
            Dumpable commitContent = Utils.readObject(Utils.join(OBJECT_DIR,commit),Dumpable.class);
            if (commitContent instanceof Commit){
                logWithCommit((Commit) commitContent);
            }
        }
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
    public static void rm(String fileName){
        Stage stage = readObject(STAGE_FILE,Stage.class);
        Map<String, String> stageMap = stage.getStage();
        if (stageMap.containsKey(fileName)){
//            由于add stage中的尚未被commit所以要删除
            String value = stageMap.get(fileName);
            File blob = Utils.join(OBJECT_DIR,value);
            if (blob.exists()){
                blob.delete();
            }
            stageMap.remove(fileName);
            stage.save();
            return;
        }
        Commit commit = preCommit();
        Stage deleteStage = readObject(DELETE_STAGE_FILE,Stage.class);
        Map<String, String> tracked = commit.getTracked();
        if (tracked.containsKey(fileName)) {
            deleteStage.add(fileName, tracked.get(fileName));
            File deleteFile = Utils.join(CWD,fileName);
            if (deleteFile.exists()){
                deleteFile.delete();
            }
        }else {
            System.out.println("No reason to remove the file.");
        }
    }
    public static void find(String commitMessage){
        List<Commit> commitList= getAllCommit();
        int count = 0;
        for (Commit commit:commitList) {
            if (commit.getMessage().equals(commitMessage)){
                System.out.println(commit.getID());
                count += 1;
            }
        }
        if (count == 0){
            System.out.println("Found no commit with that message.");
        }
    }
    public static void status() {
        System.out.println("=== Branches ===");
        String HEADBranchName = CurrentBranch();
        System.out.println("*" + HEADBranchName);
        List<String> branchList = getAllBranch();
        List<String> CWDFileList = Utils.plainFilenamesIn(CWD);
        Set<String> currentSet = new HashSet<>(CWDFileList);
        List<String> deleteList = new ArrayList<>();
        Commit lastCommit = preCommit();
        Stage stage = CurrentStage();
        Map<String, String> stageMap = stage.getStage();
        Map<String, String> tracked = lastCommit.getTracked();
        List<String> untrackedFileList = new ArrayList<>();
        List<String> modifiedList = new ArrayList<>();
        List<String> fileList = Utils.plainFilenamesIn(CWD);

        for (String branch : branchList) {
            if (!branch.equals(HEADBranchName)) {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        List<String> stageFiles = getStageFiles();
        for (String file : stageFiles) {
            System.out.println(file);
        }
        System.out.println();

        for (String file : fileList) {
            if (baseJudge(file)) {
                if (!tracked.containsKey(file) && !stageMap.containsKey(file)) {
                    untrackedFileList.add(file);
                }
                if (tracked.containsKey(file)) {
                    Blob blob = new Blob(file);
                    if (!blob.getId().equals(tracked.get(file)) && !stageMap.containsKey(file)) {
                        modifiedList.add(file);
                    }
                }
            }
        }

        System.out.println("=== Removed Files ===");
        List<String> deleteStageFileList = getDeleteStageFileList();
        for (String deleteFile : deleteStageFileList) {
            System.out.println(deleteFile);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String file : stageFiles) {
            if (!currentSet.contains(file)) {
                deleteList.add(file);
            }
        }
        for (String file : tracked.keySet()) {
            if (!currentSet.contains(file)) {
                deleteList.add(file);
            }
        }
//        for (String deleteFile : deleteList) {
//            System.out.println(deleteFile + " (deleted)");
//        }
//        for (String file : modifiedList) {
//            System.out.println(file + " (modified)");
//        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
//        for (String file : untrackedFileList) {
//            System.out.println(file);
//        }
        System.out.println();
    }

    private static boolean baseJudge(String file){
        return ((!file.equals("Makefile")) && (!file.equals("pom.xml")) && (!file.equals("gitlet-design.md")) && (!file.equals("clean.sh")));
    }
    private static List<String> getDeleteStageFileList(){
        Stage deleteStage = Utils.readObject(DELETE_STAGE_FILE,Stage.class);
        return new ArrayList<>(deleteStage.getStage().keySet());
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
                System.exit(0);
            }
        }
        File branch = Utils.join(HEADS_DIR,branchName);
        String currentBranch = CurrentBranch();
        String currentBranchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,currentBranch),String.class);
        Utils.writeObject(branch,currentBranchLastCommitID);
    }
//    切换到特定的分支
    public static void switchBranch(String branchName){
/**
 * 将工作区文件替换掉
 */
        String branchLastCommitID = Utils.readObject(Utils.join(HEADS_DIR,branchName),String.class);
        Commit commit = getCommitByID(branchLastCommitID);
        Map<String, String> tracked = commit.getTracked();
        List<String> fileList = plainFilenamesIn(CWD);
        Set<String> untrackedSet = getUntrackedSet();
        for (String fileName: fileList) {
            if (baseJudge(fileName)){
                if (untrackedSet.contains(fileName) && tracked.containsKey(fileName)){
                    Blob blob = new Blob(fileName);
                    if (!blob.getId().equals(tracked.get(fileName))){
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        return;
                    }
                }
            }
        }
        for (String fileName:fileList) {
            if (baseJudge(fileName)){
                File file = new File(fileName);
                file.delete();
            }
        }
        for (String key:tracked.keySet()){
            File file = new File(key);
            Blob blob = Utils.readObject(Utils.join(OBJECT_DIR,tracked.get(key)),Blob.class);
            writeContents(file,blob.getBytes());
        }
        Utils.writeObject(HEAD_FILE,branchName);
        /*
        清空当前暂存区
        **/
        clearStage();
    }
    private static Set<String> getUntrackedSet(){
        Map<String, String> tracked = preCommit().getTracked();
        List<String> fileList = plainFilenamesIn(CWD);
        Map<String,String> stageMap = Utils.readObject(STAGE_FILE,Stage.class).getStage();
        Set<String> untrackedFileSet = new HashSet<>();
        for (String file : fileList){
            if (baseJudge(file)){
                if (!tracked.containsKey(file) && !stageMap.containsKey(file)) {
                    untrackedFileSet.add(file);
                }
            }
        }
        return untrackedFileSet;
    }
//    清空暂存区
    private static void clearStage(){
        Stage stage = Utils.readObject(STAGE_FILE,Stage.class);
        Stage deleteStage = Utils.readObject(DELETE_STAGE_FILE,Stage.class);;
        stage.clear();
        deleteStage.clear();
    }
    public static void checkoutFile(String info){
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
        System.out.println("File does not exist in that commit.");
    }
    public static void checkoutBranch(String branchName){
        String currentBranch = CurrentBranch();
        if (currentBranch.equals(branchName)){
            System.out.println("No need to checkout the current branch.");
            return;
        }
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);
        for (String branch: branchList) {
            if (branch.equals(branchName)){
                switchBranch(branchName);
                return;
            }
        }
        System.out.println("No such branch exists.");
    }

    public static void reset(String commitID) {
        Commit resetCommit = getCommitByID(commitID);
        if (resetCommit != null){
            String CurrentBranch = resetCommit.getCommitBranch();
            Map<String, String> tracked = resetCommit.getTracked();
            List<String> fileList = plainFilenamesIn(CWD);
            for (String file: fileList) {
                if ((baseJudge(file) &&tracked.containsKey(file))){
                    Blob blob = new Blob(file);
                    if (!blob.getId().equals(tracked.get(file))){
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        return;
                    }
                }
            }
            writeObject(HEAD_FILE,CurrentBranch);
            writeObject(Utils.join(HEADS_DIR,CurrentBranch),resetCommit.getID());
            for (String file : fileList){
                if (tracked.containsKey(file)){
                    String value = tracked.get(file);
                    Blob blob = readObject(Utils.join(OBJECT_DIR, value), Blob.class);
                    Utils.writeContents(new File(file), blob.getBytes());
                }else {
                    File delete = new File(file);
                    delete.delete();
                }
            }
            clearStage();
        }else {
            System.out.println("No commit with that id exists.");
        }
    }
    public static void checkout(String commitID,String fileName){
        if (getCommitByID(commitID) != null){
            Commit commit = getCommitByID(commitID);
            Map<String,String> tracked = commit.getTracked();
            if (tracked.containsKey(fileName)){
                File file = new File(fileName);
                String blobID = tracked.get(fileName);
                Blob blob = Utils.readObject(Utils.join(OBJECT_DIR,blobID),Blob.class);
                Utils.writeContents(file,blob.getBytes());
            }else {
                System.out.println("File does not exist in that commit.");
            }
        }
        else {
            System.out.println("No commit with that id exists");
        }
    }

    public static void log() {
        Commit commit = preCommit();
        while (commit.getParents().size() != 0){
            logWithCommit(commit);
            String parentId = commit.getParents().get(0);
            commit = Utils.readObject(Utils.join(OBJECT_DIR,parentId),Commit.class);
        }
        System.out.println("===");
        System.out.println("commit " + commit.getID());
        System.out.println("Date: "+commit.getCurtime());
        System.out.println(commit.getMessage());
        System.out.println();
    }
    private static void logWithCommit(Commit commit){
        System.out.println("===");
        System.out.println("commit " + commit.getID());
        System.out.println("Date: "+commit.getCurtime());
        System.out.println(commit.getMessage());
        System.out.println();
    }
    private static Commit getCommitByID(String commitID){
        if (commitID.length() == 40){
            File commitFile = Utils.join(OBJECT_DIR,commitID);
            if (!commitFile.exists()){
                return null;
            }

            return Utils.readObject(commitFile,Commit.class);
        }
        List<String> commitList = plainFilenamesIn(OBJECT_DIR);
        for (String commit:commitList) {
            if (commit.startsWith(commitID)){
                return Utils.readObject(Utils.join(OBJECT_DIR,commit),Commit.class);
            }
        }
        return null;

    }

    public static void validateInit() {
        if (!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
/**
 *
 * TODO: merge two branch
 */
    /* TODO: fill in the rest of this class. */
    public static void merge(String branch){
//        merge two branch,this two parents is the id
        List<String> branchList = plainFilenamesIn(HEADS_DIR);
        Set <String > branchSet = new HashSet<>(branchList);
        if (branch.equals(CurrentBranch())){
            System.out.println("Cannot merge a branch with itself.");
            return;
        }else if(!branchSet.contains(branch)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String spiltPointID = findSplitPoint(branch);
        Commit spiltPointCommit = getCommitByID(spiltPointID);
        Commit branchLastCommit = getCommitByID(Utils.readObject(Utils.join(HEADS_DIR,branch),String.class));
//        处理spiltPoint 和branch相同的情况
        if (spiltPointCommit.getID().equals(branchLastCommit.getID()
        )){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
//        处理spiltpoint 和Current相同的情况
        if(spiltPointCommit.getID().equals(preCommit().getID())){
            Utils.writeObject(Utils.join(HEADS_DIR,Utils.readObject(HEAD_FILE,String.class)),branchLastCommit.getID());
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        boolean conflict = false;
        Map<String, String> SplitTracked = spiltPointCommit.getTracked();
        Map<String, String> CurrentTracked = preCommit().getTracked();
        Map<String, String> BranchTracked = branchLastCommit.getTracked();
        Map<String,String> MergeTracked = new TreeMap<>();
//        先处理只要一个分支不存在，另外一个分支未修改的情况，则删除
        for (String file:SplitTracked.keySet()) {
            if (!BranchTracked.containsKey(file) && CurrentTracked.containsKey(file)){
                if (CurrentTracked.get(file).equals(SplitTracked.get(file))){
//                    remove it
                    File f = new File(file);
                    if (f.exists()){
                        f.delete();
                    }
                    CurrentTracked.remove(file);
                    SplitTracked.remove(file);
                }
            }else if (!CurrentTracked.containsKey(file) && BranchTracked.containsKey(file)){
                if (BranchTracked.get(file).equals(SplitTracked.get(file))){
//                    remove it
                    File f = new File(file);
                    if (f.exists()){
                        f.delete();
                    }
                    BranchTracked.remove(file);
                    SplitTracked.remove(file);
                }
            }
        }
//        处理一个分支不存在，另一个分支修改了的情况，则发生冲突
        for (String file:SplitTracked.keySet()) {
            if (!BranchTracked.containsKey(file) && CurrentTracked.containsKey(file)){
                if (!CurrentTracked.get(file).equals(SplitTracked.get(file))){
                    Blob blob = handleConflict(file, CurrentBranch(), branch);
                    conflict = true;
                    MergeTracked.put(file,blob.getId());
                    SplitTracked.remove(file);
                    CurrentTracked.remove(file);
                }
            }else if (!CurrentTracked.containsKey(file) && BranchTracked.containsKey(file)){
                if (!BranchTracked.get(file).equals(SplitTracked.get(file))){
                    Blob blob = handleConflict(file, CurrentBranch(), branch);
                    conflict = true;
                    MergeTracked.put(file,blob.getId());
                    SplitTracked.remove(file);
                    BranchTracked.remove(file);
                }
            }
        }
//        处理都存在，都修改的情况
        for (String file:SplitTracked.keySet()) {
            if (BranchTracked.containsKey(file) && CurrentTracked.containsKey(file)){
                if (!BranchTracked.get(file).equals(SplitTracked.get(file)) && !CurrentTracked.get(file).equals(SplitTracked.get(file))){
                    if (!BranchTracked.get(file).equals(CurrentTracked.get(file))){
                        Blob blob = handleConflict(file, CurrentBranch(), branch);
                        conflict = true;
                        MergeTracked.put(file,blob.getId());
                    }else{
                        MergeTracked.put(file,BranchTracked.get(file));
                    }
                    SplitTracked.remove(file);
                    BranchTracked.remove(file);
                    CurrentTracked.remove(file);
                }
            }
        }
//        处理在一个分支中存在，在split和其他分支中都不存在的情况
        for (String file:CurrentTracked.keySet()) {
            if (!SplitTracked.containsKey(file) && !BranchTracked.containsKey(file)){
                MergeTracked.put(file, CurrentTracked.get(file));
            }
        }
        for (String file : BranchTracked.keySet()){
            if (!SplitTracked.containsKey(file) && !CurrentTracked.containsKey(file)){
                MergeTracked.put(file,BranchTracked.get(file));
            }
        }
//        创建mergeCommit
        List<String> parents = new ArrayList<>();
        parents.add(preCommit().getID());
        String branchLastCommitID = readObject(join(HEADS_DIR, branch), String.class);
        parents.add(branchLastCommitID);
        Commit mergeCommit = new Commit(MergeTracked,parents,"Merged "+branch+" into "+CurrentBranch()+".",CurrentBranch());
        mergeCommit.save();
        if (conflict){
            Utils.message("Encountered a merge conflict.");
        }
        reset(mergeCommit.getID());
    }
    private static String findSplitPoint(String otherBranch) {
        Queue<String> q = new LinkedList<>();
        Map<String, Integer> currentBranchMap = new HashMap<>();
        Map<String, Integer> otherBranchMap = new HashMap<>();
        String currentBranchID = preCommit().getID();
        q.add(currentBranchID);
        int depth1 = 0;
        currentBranchMap.put(currentBranchID, depth1);

        while (!q.isEmpty()) {
            String commitID = q.poll();
            Commit commit = getCommitByID(commitID);
            List<String> parents = commit.getParents();
            depth1 += 1;
            for (String parentID : parents) {
                if (!currentBranchMap.containsKey(parentID)) { // 避免重复添加
                    currentBranchMap.put(parentID, depth1);
                    q.add(parentID);
                }
            }
        }

        q.clear(); // 清空队列以复用
        depth1 = 0; // 重置深度计数器
        String otherBranchID = Utils.readObject(Utils.join(HEADS_DIR, otherBranch), String.class);
        q.add(otherBranchID);
        int depth2 = 0;
        while (!q.isEmpty()) {
            String commitID = q.poll();
            Commit commit = getCommitByID(commitID);
            List<String> parents = commit.getParents();
            depth2 += 1;
            for (String parentID : parents) {
                if (!otherBranchMap.containsKey(parentID)) { // 避免重复添加
                    otherBranchMap.put(parentID, depth2);
                    q.add(parentID);
                }
            }
        }

        for (String key : currentBranchMap.keySet()) {
            if (otherBranchMap.containsKey(key)) {
                return key; // 直接返回第一个找到的共同提交ID
            }
        }
        return null; // 如果没有找到共同提交，则返回null
    }

    private static Blob handleConflict(String file,String currentBranch,String givenBranch) {
        File f = new File(file);
        if (f.exists()) {
            String contents = "<<<<<<< HEAD\n" +
                    "contents of file in" + currentBranch +"\n" +
                    "=======\n" +
                    "contents of file in " + givenBranch + "\n" +
                    ">>>>>>> ";
            Utils.writeContents(f, contents);
            Blob blob = new Blob(file);
            blob.save();
            return blob;
        }else{
            return null;
        }
    }

}
