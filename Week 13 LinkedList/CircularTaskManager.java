// import java.util.*;
class Task {
    String name;
    int time;
    Task next;
    Task(String name, int time) {
        this.name = name;
        this.time = time;
        this.next = null;
    }
}
public class CircularTaskManager {
    Task head = null;
    void addTask(String name, int time) {
        Task newTask = new Task(name, time);
        if (head == null) {
            head = newTask;
            newTask.next = head; 
        } else {
            Task temp = head;
            while (temp.next != head)
                temp = temp.next;
            temp.next = newTask;
            newTask.next = head;
        }
    }
    void executeTasks() {
        if (head == null) {
            System.out.println("No tasks to execute.");
            return;
        }
        System.out.print("Execution order → ");
        Task curr = head;
        while (head != null) {
            System.out.print(curr.name);
            curr.time--; 
            if (curr.time == 0) {
                removeTask(curr.name);
                if (head == null) break; 
            }
            curr = (curr.next != null) ? curr.next : head;
            if (head != null && curr != head)
                System.out.print(" -> ");
        }
        System.out.println(" (All Completed)");
    }
    void removeTask(String name) {
        if (head == null) return;
        Task curr = head, prev = null;
        if (head.next == head && head.name.equals(name)) {
            head = null;
            return;
        }
        do {
            if (curr.name.equals(name)) break;
            prev = curr;
            curr = curr.next;
        } while (curr != head);

        // Delete found node
        if (curr.name.equals(name)) {
            if (curr == head) {
                // move head to next node
                Task tail = head;
                while (tail.next != head)
                    tail = tail.next;
                head = head.next;
                tail.next = head;
            } else {
                if (prev != null) {
                    prev.next = curr.next;
                }
            }
        }
    }
    void display() {
        if (head == null) {
            System.out.println("No tasks in the list.");
            return;
        }
        Task temp = head;
        do {
            System.out.print(temp.name + "(" + temp.time + "s)");
            temp = temp.next;
            if (temp != head)
                System.out.print(" -> ");
        } while (temp != head);
        System.out.println();
    }

    public static void main(String[] args) {
        CircularTaskManager tm = new CircularTaskManager();
        tm.addTask("T1", 3);
        tm.addTask("T2", 2);
        tm.addTask("T3", 4);
        System.out.print("Initial Tasks: ");
        tm.display();
        tm.executeTasks();
    }
}
