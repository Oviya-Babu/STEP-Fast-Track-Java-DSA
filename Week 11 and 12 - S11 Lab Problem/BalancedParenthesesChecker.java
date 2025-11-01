import java.util.Scanner;
import java.util.Stack;
public class BalancedParenthesesChecker {
    public static boolean isBalanced(String expr) {
        Stack<Character> stack = new Stack<>();
        for (char ch : expr.toCharArray()) {
            if (ch == '(' || ch == '{' || ch == '[') {
                stack.push(ch);
            } 
            else if (ch == ')' || ch == '}' || ch == ']') {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((ch == ')' && top != '(') ||
                    (ch == '}' && top != '{') ||
                    (ch == ']' && top != '[')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
    // public static void main(String[] args) {
    //     Scanner sc = new Scanner(System.in);
    //     System.out.print("Enter expression: ");
    //     String input = sc.nextLine();
    //     if (isBalanced(input))
    //         System.out.println("Balanced");
    //     else
    //         System.out.println("Not Balanced");
    //     sc.close();
    // }
    
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter expression: ");
            String input = sc.nextLine();
            if (isBalanced(input))
                System.out.println("Balanced");
            else
                System.out.println("Not Balanced");
        }
    }
}
