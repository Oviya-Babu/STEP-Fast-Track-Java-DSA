import java.util.*;
public class InfixToPostfix {
    // Function to define operator precedence
    static int precedence(char ch) {
        return switch (ch) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> -1;
        };
    }
    static String infixToPostfix(String expr) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (char c : expr.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {     
                result.append(c);
            } 
            else if (c == '(') {
                stack.push(c);
            } 
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    result.append(stack.pop());
                stack.pop(); // remove '('
            } 
            else { 
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek()))
                    result.append(stack.pop());
                stack.push(c);
            }
        }
        while (!stack.isEmpty())
            result.append(stack.pop());
        return result.toString();
    }
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter infix expression: ");
            String expr = sc.nextLine();
            System.out.println("Postfix: " + infixToPostfix(expr));
        }
    }
}
