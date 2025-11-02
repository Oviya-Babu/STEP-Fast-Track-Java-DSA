import java.util.Scanner;
import java.util.Stack;

public class ReverseString {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter the String to be reversed: ");
            String input = sc.nextLine();
            String reversed = reverse(input);
            System.out.println("The reversed String: " + reversed);
        }
    }

    public static String reverse(String str) {
        Stack<Character> stack = new Stack<>();
        for (char ch : str.toCharArray()) {
            stack.push(ch);
        }
        StringBuilder rev = new StringBuilder();
        while (!stack.isEmpty()) {
            rev.append(stack.pop());
        }
        return rev.toString();
    }
}