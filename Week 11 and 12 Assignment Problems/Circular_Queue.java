import java.util.Scanner;

class CircularQueue {
    private final int[] queue;
    private int front, rear, count;
    private final int size;

    // Constructor
    public CircularQueue(int size) {
        this.size = size;
        queue = new int[size];
        front = 0;
        rear = -1;
        count = 0;
    }
    void enqueue(int data) {
        if (isFull()) {
            System.out.println("Queue is full!");
            return;
        }
        rear = (rear + 1) % size;
        queue[rear] = data;
        count++;
        System.out.println(data + " inserted.");
    }
    void dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
            return;
        }
        System.out.println(queue[front] + " deleted.");
        front = (front + 1) % size;
        count--;
    }
    void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
            return;
        }
        System.out.print("Queue elements: ");
        for (int i = 0; i < count; i++) {
            int index = (front + i) % size;
            System.out.print(queue[index] + " ");
        }
        System.out.println();
    }
    boolean isFull() {
        return count == size;
    }
    boolean isEmpty() {
        return count == 0;
    }
}
public class Circular_Queue {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter queue size: ");
        int n = sc.nextInt();
        CircularQueue cq = new CircularQueue(n);
        while (true) {
            System.out.println("\n1. Enqueue  2. Dequeue  3. Display  4. Exit");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            switch (ch) {
                case 1 -> {
                    System.out.print("Enter value to insert: ");
                    int val = sc.nextInt();
                    cq.enqueue(val);
                }
                case 2 -> cq.dequeue();
                case 3 -> cq.display();
                case 4 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}
