package deque;

import net.sf.saxon.functions.ConstantFunction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    @Test
    public void TestGet(){
        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
        testDeque.addLast(0);
        testDeque.addFirst(2);
        testDeque.addFirst(1);
        testDeque.addLast(0);
        testDeque.addFirst(2);
        testDeque.addFirst(1);
        testDeque.addLast(0);
        testDeque.addFirst(2);
        testDeque.addFirst(1);
        testDeque.addLast(0);
        testDeque.addFirst(2);
        testDeque.addFirst(1);
        int mid = 2;
        int last = 0;
        int first = 1;
        assertEquals(first,(int) testDeque.get(0));
        assertEquals(mid,(int) testDeque.get(1));
        assertEquals(last,(int) testDeque.get(testDeque.size() - 1));
    }

    @Test
    public void printTest(){
        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
        testDeque.addLast(0);
        testDeque.addFirst(2);
        testDeque.addFirst(1);
//        expect 1 2 0
        testDeque.printDeque();
    }
    @Test
    public void addLastTest(){
        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
        testDeque.addLast(0);
        testDeque.addLast(1);
        testDeque.addLast(2);
        testDeque.addLast(3);
        assertEquals(0,(int)testDeque.get(0));
        assertEquals(1,(int)testDeque.get(1));
        assertEquals(2,(int)testDeque.get(2));
        assertEquals(3,(int)testDeque.get(3));
    }
//    @Test
//    public void resizeTest(){
//        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
//        testDeque.addLast(0);
//        testDeque.addLast(1);
//        testDeque.addFirst(2);
//        testDeque.addFirst(3);
//        testDeque.resize(32);
//    }
    @Test
    public void addFirstTest(){
        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
        testDeque.addFirst(3);
        testDeque.addFirst(2);
        testDeque.addFirst(1);
        testDeque.addFirst(0);
        assertEquals(0,(int)testDeque.get(0));
        assertEquals(1,(int)testDeque.get(1));
        assertEquals(2,(int)testDeque.get(2));
        assertEquals(3,(int)testDeque.get(3));
    }
    @Test
    public void isEmptyTest(){
        ArrayDeque<Integer> testDeque = new ArrayDeque<>();
        assertEquals(true, testDeque.isEmpty());
        testDeque.addFirst(1);
        assertEquals(false, testDeque.isEmpty());
        testDeque.removeLast();
        assertEquals(true, testDeque.isEmpty());
        testDeque.addFirst(1);
        testDeque.addLast(2);
        assertEquals(1,(int)testDeque.get(0));
//        assertEquals(2,(int)testDeque.get(1));
        testDeque.removeFirst();
        testDeque.removeFirst();
        assertEquals(true, testDeque.isEmpty());
    }
    @Test
    public void testResize() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            deque.addFirst(i);
        }
        assertEquals(false,deque.isEmpty());
        assertEquals(16, deque.size());
        assertEquals(15, deque.removeFirst().intValue());
        // 验证扩容后元素顺序不变
        for (int i = 14; i >= 0; i--) {
            assertEquals(i, deque.removeFirst().intValue());
        }
        assertEquals(true,deque.isEmpty());
    }

    @Test
    public void testRemoveLast() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(2, deque.removeLast().intValue());
        assertEquals(1, deque.removeLast().intValue());
        assertEquals(true,deque.isEmpty());
    }

    @Test
    public void testGetAndIterator() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        assertEquals(1, deque.get(0).intValue());
        assertEquals(2, deque.get(1).intValue());
        assertEquals(3, deque.get(2).intValue());

        int index = 0;
        for (Integer item : deque) {
            assertEquals(index + 1, item.intValue());
            index++;
        }
    }
}
