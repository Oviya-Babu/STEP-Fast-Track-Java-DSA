import java.util.*;
public class TextEditorUndo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Stack<String> textStack = new Stack<>();
        String currentText = "";
        while (true) {
            System.out.println("\n1. Type Text  2. Undo  3. Show Text  4. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter text to add: ");
                    String newText = sc.nextLine();
                    textStack.push(currentText); 
                    currentText += newText;
                }
                case 2 -> {
                    if (!textStack.isEmpty())
                        currentText = textStack.pop(); 
                    else
                        System.out.println("Nothing to undo!");
                }
                case 3 -> System.out.println("Current Text: " + currentText);
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
