package gitlet;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0){
            System.out.println("Please Enter a Command");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateArgs(args, 2);
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                validateArgs(args,2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateArgs(args,2);
                break;
            case "merge":
                /*
                * TODO:merge two branch
                * */
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                Repository.global_log();
                break;
            case "status":
                Repository.status();
                break;
            case "find":
                validateArgs(args,2);
                Repository.find(args[1]);
                break;
            case "checkout":
                if(args.length == 2){
                Repository.checkoutBranch(args[1]);
                }
                else if (args.length == 3){
                    if (!args[1].equals("--")){
                        System.out.println("Incorrect operands");
                        System.exit(0);
                    }
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                }
                else if (args.length == 4){
                    if (!args[2].equals("--")){
                        System.out.println("Incorrect operands");
                        System.exit(0);
                    }
                    String commitID = args[1];
                    String fileName = args[3];
                    Repository.checkout(commitID,fileName);
                }
                break;
            case "branch":
                validateArgs(args,2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validateArgs(args,2);
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                validateArgs(args,2);
                String commitID = args[1];
                Repository.reset(commitID);
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
    public static void validateArgs(String [] args, int n){
        if (args.length != n){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
