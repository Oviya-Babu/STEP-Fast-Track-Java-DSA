import java.util.Scanner;
import java.util.Stack;
public class QueueUsingTwoStacks {
    Stack <Integer> stack1 = new Stack<>();
    Stack <Integer> stack2 = new Stack<>();
    public void enqueue(int val){
        stack1.push(val);
    }
    public int dequeue(){
        if(stack2.isEmpty()){
            while(!stack1.isEmpty()){
                stack2.push(stack1.pop());
            }
        }
        if(stack2.isEmpty()){
            System.out.println("Queue is Empty. ");
            return -1;
        }
        return stack2.pop();
    } 
    public static void main(String[] args){
        QueueUsingTwoStacks q = new QueueUsingTwoStacks();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("\n 1. Enqueue \n 2. Dequeue \n 3. Exit\n");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter the value for enqueue: ");
                    int val = sc.nextInt();
                    q.enqueue(val);
                }
                case 2 -> {
                    int removed = q.dequeue();
                    if (removed != -1) {
                        System.out.println("Dequeued: " + removed);
                    }
                }
                case 3 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.print("Invalid choice!");
            }
        }
    }
}
