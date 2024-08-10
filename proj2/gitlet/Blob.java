package gitlet;

import java.io.File;
import java.io.Serializable;
/**
    @author yyx
    快照类，当file第一次加入暂存区的时候会生成一个Blob实例，如果file已经存在于暂存区中，则比较file是否改变，
    若未改变则不加入到暂存区中，若文件改变则更新暂存区中文件，在commit后，所有暂存区的文件清空，commitID指向对应的版本Blob
*/

public class Blob implements Serializable,Dumpable{
    private String id;
    private String fileName;
    private byte[] bytes;
    private File storePlace;
    public Blob(String fileName) {
        this.fileName = fileName;
        this.bytes = Utils.readContents(new File(fileName));
        this.id = Utils.sha1(this.fileName, this.bytes);
        this.storePlace = generateStoredPlace();
    }
    public String getId(){
        return this.id;
    }
    private File generateStoredPlace(){
        return Utils.join(Repository.OBJECT_DIR, this.id);
    }
    public void save(){
        Utils.writeObject(generateStoredPlace(), this);
    }
    public static void store(Blob blob){
    }
    public byte[] getBytes(){
        return bytes;
    }

    @Override
    public void dump() {

    }
    public byte[] getContents(){
        return bytes;
    }

    public String getFileName() {
        return fileName;
    }
}