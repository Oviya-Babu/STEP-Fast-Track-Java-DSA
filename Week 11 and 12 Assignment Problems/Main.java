import java.util.Scanner;
class QueueArray {
    private final int[] queue;
    private int front, rear, size;
    public QueueArray(int capacity) {
        queue = new int[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }
    void enqueue(int data) {
        if (isFull()) {
            System.out.println("Queue is full!");
            return;
        }
        rear++;
        queue[rear] = data;
        size++;
        System.out.println(data + " enqueued.");
    }
    void dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
            return;
        }
        System.out.println(queue[front] + " dequeued.");
        front++;
        size--;
    }
    void peek() {
        if (isEmpty())
            System.out.println("Queue is empty!");
        else
            System.out.println("Front element: " + queue[front]);
    }
    boolean isFull() {
        return rear == queue.length - 1;
    }
    boolean isEmpty() {
        return size == 0;
    }
    void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
            return;
        }
        System.out.print("Queue elements: ");
        for (int i = front; i <= rear; i++)
            System.out.print(queue[i] + " ");
        System.out.println();
    }
}
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter queue size: ");
        int n = sc.nextInt();
        QueueArray q = new QueueArray(n);
        while (true) {
            System.out.println("\n1. Enqueue  2. Dequeue  3. Peek  4. Display  5. Exit");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            switch (ch) {
                case 1 -> {
                    System.out.print("Enter value to enqueue: ");
                    int val = sc.nextInt();
                    q.enqueue(val);
                }
                case 2 -> q.dequeue();
                case 3 -> q.peek();
                case 4 -> q.display();
                case 5 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}
