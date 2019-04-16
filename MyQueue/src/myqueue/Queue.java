package myqueue;

import java.util.Stack;
import java.util.NoSuchElementException;


public class Queue<E> extends Stack<E> {
    private static final long serialVersionUID = 1L;
    
    public final int dump = 10;

    private Stack<E> stk; // input stack

    public Queue() {
        super();
        this.stk = new Stack<E>();
    }

    public boolean add(E e) throws IllegalStateException, ClassCastException, NullPointerException,
            IllegalArgumentException {
        if (e == null) {
            throw new NullPointerException();
        }
        if (this.stk.size() >= this.dump) {
        	if (!super.empty()) {
        		throw new IllegalStateException();
        	}
        	while (!this.stk.empty()) {
        		super.push(this.stk.pop());
        	}
        }
        this.stk.push(e);
        return true;
    }

    /**
     * This is a non-restricted queue, offer() does the same as add().
     */
    public boolean offer(E e) throws ClassCastException, NullPointerException, IllegalArgumentException {
        try {
            return this.add(e);
        } catch (IllegalStateException except) {
            return false;
        }
    }

    public E remove() throws NoSuchElementException {
        E e = this.poll();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }

    public E poll() {
        if (super.empty()) {
        	while (!this.stk.empty()) {
        		super.push(this.stk.pop());
        	}
        }
        if (super.empty()) {
        	return null;
        }
        return super.pop();
    }

    public E peek() {
    	if (super.empty()) {
    		while (!this.stk.empty()) {
    			super.push(this.stk.pop());
    		}
    	}
    	if (super.empty()) {
    		return null;
    	}
        return super.peek();
    }

    public E element() throws NoSuchElementException {
        E e = this.peek();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e;
    }

}
