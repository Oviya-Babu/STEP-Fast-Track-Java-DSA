import java.util.*;
public class NextGreaterElement {
    public static void main(String[] args) {
       try( Scanner sc = new Scanner(System.in)){
        System.out.print("Enter number of elements: ");
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter array elements:");
        for (int i = 0; i < n; i++)
            arr[i] = sc.nextInt();
        int[] result = findNextGreater(arr);
        System.out.println("Next Greater Elements:");
        for (int val : result)
            System.out.print(val + " ");
    }
}
    static int[] findNextGreater(int[] arr) {
        int n = arr.length;
        int[] next = new int[n];
        Stack<Integer> stack = new Stack<>(); 
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] <= arr[i])
                stack.pop();
            next[i] = stack.isEmpty() ? -1 : arr[stack.peek()];
            stack.push(i);
        }
        return next;
    }
}
