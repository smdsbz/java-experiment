import java.lang.System;
import java.util.NoSuchElementException;

import myqueue.Queue;


public class TestMyQueue {
    public static void main(String[] args) {
        System.out.println("Creating instance of Queue<Integer>...");
        Queue<Integer> q = new Queue<>();
        System.out.println("   q.peek() -> " + q.peek());
        System.out.println();

        System.out.println("Inserting [1, 2, 3] into our queue...");
        System.out.println("   q.add(1) -> " + q.add(1));
        System.out.println("   q.peek() -> " + q.peek());
        System.out.println("   q.add(2) -> " + q.add(2));
        System.out.println("   q.add(3) -> " + q.add(3));
        System.out.println();

        System.out.println("Polling from queue...");
        Integer i;
        while (true) {
            System.out.println("   q.poll() -> " + (i = q.poll()));
            if (i == null) {
                System.out.println("   Last `q.poll()` got `null`!");
                break;
            }
        }

        System.out.print("   q.remove() -> ");
        try {
            System.out.println(q.remove());
        } catch (NoSuchElementException exception) {
            System.out.println("caught NoSuchElementException!");
        }
        System.out.println();
        
        System.out.println("Inserting [0..22] into queue...");
        for (int j = 0; j < 23; ++j) {
        	System.out.print("    q.add(" + j + ") -> ");
        	try {
            	System.out.println(q.add(j));
        	} catch (IllegalStateException e) {
        		System.out.println("failed: got IllegalStateException!");
        		break;
        	}
        }
        System.out.println("Retrieving all from queue...");
        while (true) {
        	System.out.println("   q.pool() -> " + (i = q.poll()));
        	if (i == null) {
        		break;
        	}
        }
        System.out.println();
        
        System.out.println("All done!");
    }
}
