package puzzle15;

import java.util.List;
import static java.lang.Math.*;

public class MyQueue {

	private int[] IntArray;
	private int head;
	private int tail;
	private int capacity;

	public MyQueue(int capacity) {
		this.IntArray = new int[capacity];
		this.head = 0;
		this.tail = 0;
		this.capacity = capacity;
	}

	public int[] getIntArray() {
		return IntArray;
	}

	public int size() {
		return abs(tail - head);
	}

	public boolean isEmpty() {
		return (tail - head) == 0;
	}

	public int poll() {
		if (head == capacity) {
			head = 0;
		}
		return this.IntArray[head++];
	}

	public void add(int data) {
		if (tail == capacity) {
			tail = 0;
		}
		IntArray[tail++] = data;

	}

	public void addAll(List<Integer> elements) {
		for (int i : elements) {
			this.add(i);
		}
		elements = null;
	}
}
