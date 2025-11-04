import java.util.*;
class Node {
    int data;
    Node next;
    Node(int data) {
        this.data = data;
        this.next = null;
    }
}
public class SinglyLinkedList {
    Node head;
    void insertAtPosition(int data, int position) {
        Node newNode = new Node(data);

        if (position == 1) {
            newNode.next = head;
            head = newNode;
            return;
        }

        Node temp = head;
        int count = 1;
        while (temp != null && count < position - 1) {
            temp = temp.next;
            count++;
        }
        if (temp == null) {
            System.out.println("Invalid position! Cannot insert " + data);
            return;
        }
        newNode.next = temp.next;
        temp.next = newNode;
    }
    void display() {
        Node temp = head;
        if (temp == null) {
            System.out.println("List is empty.");
            return;
        }
        while (temp != null) {
            System.out.print(temp.data);
            if (temp.next != null)
                System.out.print(" -> ");
            temp = temp.next;
        }
        System.out.println();
    }
    public static void main(String[] args) {
       try( Scanner sc = new Scanner(System.in)){
        SinglyLinkedList list = new SinglyLinkedList();
        list.head = new Node(10);
        list.head.next = new Node(20);
        list.head.next.next = new Node(30);
        list.head.next.next.next = new Node(40);
        System.out.print("Current List: ");
        list.display();
        System.out.print("Enter data to insert: ");
        int data = sc.nextInt();
        System.out.print("Enter position to insert at: ");
        int pos = sc.nextInt();
        list.insertAtPosition(data, pos);
        System.out.print("Updated List: ");
        list.display();
        sc.close();
     }
    }
}
