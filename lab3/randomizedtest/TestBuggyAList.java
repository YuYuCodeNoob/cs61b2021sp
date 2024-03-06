package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> testList = new BuggyAList<>();
        testList.addLast(4);
        testList.addLast(4);
        testList.addLast(4);
        Integer first = testList.removeLast();
        Integer second = testList.removeLast();
        Integer third = testList.removeLast();
        assertEquals(first,second);
        assertEquals(third,second);
        assertEquals(first,third);
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L2 = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            int size = L.size();
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L2.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1 && size > 0) {
                // size
                Integer last = L.getLast();
                Integer last2 = L2.getLast();
                System.out.println(last);
            }else if (operationNumber == 2 && size > 0){
                L.removeLast();
                L2.removeLast();
            }
        }
    }
}
