package deque;

import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();
    }
    @Test
    public void testAddFirst() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);

        assertEquals(3, deque.size());
        assertEquals(3, (int)deque.removeFirst());
        assertEquals(2, (int)deque.removeFirst());
        assertEquals(1, (int)deque.removeFirst());
    }
    @Test
    public void TestGet(){
        Deque<Integer> testDeque = new LinkedListDeque<>();
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
        assertEquals(last,(int) testDeque.get(testDeque.size()-2));
    }
    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }
    @Test
    public void testIsEmpty() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        assertTrue(deque.isEmpty());

        deque.addLast(1);
        assertFalse(deque.isEmpty());

        deque.removeLast();
        assertTrue(deque.isEmpty());
    }
    @Test
    public void testSize() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        assertEquals(0, deque.size());

        deque.addLast(1);
        assertEquals(1, deque.size());

        deque.addLast(2);
        deque.addLast(3);
        assertEquals(3, deque.size());

        deque.removeLast();
        deque.removeLast();
        assertEquals(1, deque.size());

        deque.removeLast();
        assertEquals(0, deque.size());
    }
    @Test
    public void testPrintDeque() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        StringBuilder expectedOutput = new StringBuilder();
        expectedOutput.append("(1,2,3)");
        assertEquals(expectedOutput.toString(), deque.toString());
    }
    @Test
    public void testIterator() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        Iterator<Integer> iterator = deque.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, (int)iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(2, (int)iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(3, (int)iterator.next());
        assertFalse(iterator.hasNext());
    }
    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }
    @Test
    public void getTest(){
        LinkedListDeque<Integer> TestDeque = new LinkedListDeque<>();
        TestDeque.addFirst(1);
        TestDeque.addFirst(2);
        TestDeque.addLast(3);
        int expect0 = 2;
        int expect1 = 1;
        int expect2 = 3;
        assertEquals((Integer) expect0,TestDeque.get(0));
        assertEquals((Integer) expect1,TestDeque.get(1));
        assertEquals((Integer) expect2,TestDeque.get(2));
    }
    @Test
    public void testCurrentTime(){
        LinkedListDeque<Integer> Ns = new LinkedListDeque<>();
        LinkedListDeque<Double> timePerOpps = new LinkedListDeque<>();
        int N = 1000;
        for (int i = 0; i < 10; i++) {
            Ns.addLast(N);
            LinkedListDeque<Integer> testModel = new LinkedListDeque<>();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < N ; j++) {
                testModel.addLast(j);
            }
            double v = sw.elapsedTime();
            timePerOpps.addLast(v);
            N *=2;
        }
        printTimingTable(Ns,timePerOpps,Ns);
    }
    @Test
    public void TestRemoveCurrentTime(){
        LinkedListDeque<Integer> Ns = new LinkedListDeque<>();
        LinkedListDeque<Double> time = new LinkedListDeque<>();
        int N = 1000;
        for (int i = 0; i < 10; i++) {
            Ns.addLast(N);
            LinkedListDeque<Integer> testModel = new LinkedListDeque<>();
            for (int j = 0; j < N ; j++) {
                testModel.addFirst(j);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < N; j++) {
                testModel.removeLast();
            }
            double v = sw.elapsedTime();
            time.addLast(v);
            N *=2;
        }
        printTimingTable(Ns,time,Ns);
    }
    private static void printTimingTable(LinkedListDeque<Integer> Ns, LinkedListDeque<Double> times, LinkedListDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }
    @Test
    public void testGetAndIterator() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
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
    @Test
    public void emptyIteratorTest(){
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        int index = 0;
        for (Integer item: deque
             ) {
            System.out.println(item);
            index++;
        }
        assertEquals(0,index);
    }

}
