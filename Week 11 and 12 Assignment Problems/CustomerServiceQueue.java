import java.util.*;
public class CustomerServiceQueue {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Queue<String> queue = new LinkedList<>();
        while (true) {
            System.out.println("\n1. Add Customer  2. Serve Customer  3. Display Queue  4. Exit");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            sc.nextLine(); 
            switch (ch) {
                case 1 -> {
                    System.out.print("Enter customer name: ");
                    String name = sc.nextLine();
                    queue.add(name);  // enqueue
                    System.out.println(name + " added to the queue.");
                }
                case 2 -> {
                    if (queue.isEmpty())
                        System.out.println("No customers to serve!");
                    else
                        System.out.println(queue.poll() + " is being served."); // dequeue
                }
                case 3 -> {
                    if (queue.isEmpty())
                        System.out.println("Queue is empty!");
                    else
                        System.out.println("Current Queue: " + queue);
                }
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
