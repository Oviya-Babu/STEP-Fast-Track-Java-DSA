import java.util.*;
class Node {
    int data;
    Node prev, next;
    Node(int data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
}
public class DoublyLinkedList {
    Node head;
    void append(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        Node temp = head;
        while (temp.next != null)
            temp = temp.next;
        temp.next = newNode;
        newNode.prev = temp;
    }
    void deleteAll(int value) {
        Node temp = head;
        while (temp != null) {
            if (temp.data == value) {
                if (temp.prev == null)
                    head = temp.next;
                else
                    temp.prev.next = temp.next;
                if (temp.next != null)
                    temp.next.prev = temp.prev;
            }
            temp = temp.next;
        }
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
                System.out.print(" <-> ");
            temp = temp.next;
        }
        System.out.println();
    }
    public static void main(String[] args) {
      try(  Scanner sc = new Scanner(System.in)){
        DoublyLinkedList list = new DoublyLinkedList();
        list.append(10);
        list.append(20);
        list.append(30);
        list.append(20);
        list.append(40);
        System.out.print("Original List: ");
        list.display();
        System.out.print("Enter value to delete: ");
        int val = sc.nextInt();
        list.deleteAll(val);
        System.out.print("Updated List: ");
        list.display();
        sc.close();
    }
}
}
