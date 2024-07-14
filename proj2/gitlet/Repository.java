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
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static void init(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        }else {
            GITLET_DIR.mkdir();
            OBJECT_DIR.mkdir();
            initCommit();
            currentBranch = DEFAULT_BRANCH;
        }
    }
    private static void initCommit(){
        Commit commit = new Commit();
        commit.save();
    }

    /* TODO: fill in the rest of this class. */
}
