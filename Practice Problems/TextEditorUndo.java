import java.util.*;
public class TextEditorUndo {
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
            System.out.print("Enter command (TYPE <word>/UNDO/PRINT/EXIT): ");
            String cmd = sc.next();
            if (cmd.equalsIgnoreCase("TYPE")) {
                String word = sc.next();
                stack.push(word);
            }
            else if (cmd.equalsIgnoreCase("UNDO")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                } else {
                    System.out.println("Nothing to undo!");
                }
            }
            else if (cmd.equalsIgnoreCase("PRINT")) {
                if (stack.isEmpty()) {
                    System.out.println("(empty)");
                } else {
                    for (int i = 0; i < stack.size(); i++) {
                        System.out.print(stack.get(i));
                        if (i < stack.size() - 1)
                            System.out.print(" ");
                    }
                    System.out.println(); 
                }
            }
            else if (cmd.equalsIgnoreCase("EXIT")) {
                System.out.println("Exiting editor...");
                break;
            }
            else {
                System.out.println("Invalid command!");
            }
                }
            }
        }
    }
