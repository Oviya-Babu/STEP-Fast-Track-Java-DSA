class Node {
    int data;
    Node next;
    Node(int data) {
        this.data = data;
    }
}
class CircularLinkedList {
    Node tail = null; // tail.next = head
    void insertAtBeginning(int data) {
        Node newNode = new Node(data);
        if (tail == null) { // empty list
            tail = newNode;
            tail.next = tail;
        } else {
            newNode.next = tail.next; // newNode → old head
            tail.next = newNode;      // tail → new head
        }
    }
    void insertAtEnd(int data) {
        Node newNode = new Node(data);
        if (tail == null) {
            tail = newNode;
            tail.next = tail;
        } else {
            newNode.next = tail.next; // newNode → head
            tail.next = newNode;      // old tail → new node
            tail = newNode;           // new node becomes tail
        }
    }
    void deleteAtBeginning() {
        if (tail == null) return; // empty list
        Node head = tail.next;
        if (head == tail) { // only one node
            tail = null;
        } else {
            tail.next = head.next; 
        }
    }
    void deleteAtEnd() {
        if (tail == null) return;
        Node head = tail.next;
        if (head == tail) {
            tail = null;
        } else {
            Node temp = head;
            while (temp.next != tail) temp = temp.next;
            temp.next = head;
            tail = temp;
        }
    }
    void display() {
        if (tail == null) {
            System.out.println("Circular List = []");
            return;
        }
        Node head = tail.next;
        System.out.print("Circular List = [");
        Node temp = head;
        do {
            System.out.print(temp.data);
            temp = temp.next;
            if (temp != head) System.out.print(" -> ");
        } while (temp != head);
        System.out.println(" -> back to " + head.data + "]");
    }
}
public class Circular_linkedlist {
    public static void main(String[] args) {
        CircularLinkedList list = new CircularLinkedList();
        list.insertAtBeginning(5);    // Using insertAtBeginning
        list.insertAtEnd(10);
        list.insertAtEnd(20);
        list.insertAtEnd(30);
        list.deleteAtBeginning();
        list.insertAtEnd(40);
        list.deleteAtEnd();
        list.display();
    }
}
