import java.util.*;
public class MarkdownBracketCleaner {
    public static String cleanMarkdownBrackets(String s) {
        Deque<Integer> open = new ArrayDeque<>(); 
        Deque<Integer> star = new ArrayDeque<>(); 
        boolean[] keep = new boolean[s.length()]; 
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ("([{<".indexOf(c) != -1) {
                open.push(i); 
            } else if (c == '*') {
                star.push(i); 
            } else if (")]}>" .indexOf(c) != -1) {
                if (!open.isEmpty() && isMatching(s.charAt(open.peek()), c)) {
                    keep[i] = true;
                    keep[open.pop()] = true;
                } 
                else if (!star.isEmpty()) {
                    keep[i] = true;
                    star.pop();
                }
            } else {
                keep[i] = true;
            }
        }
        List<Integer> unmatchedOpen = new ArrayList<>();
        while (!open.isEmpty()) unmatchedOpen.add(open.pop());
        Collections.reverse(unmatchedOpen); // ensure increasing order
        List<Integer> remainingStars = new ArrayList<>(star);
        Collections.sort(remainingStars);
        int j = 0;
        for (int idx : unmatchedOpen) {
            while (j < remainingStars.size() && remainingStars.get(j) < idx) j++;
            if (j < remainingStars.size()) {
                keep[idx] = true;
                keep[remainingStars.get(j)] = false; 
                j++;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (keep[i] && c != '*') {
                sb.append(c);
            }
        }
        String res = sb.toString();
        return isBalanced(res) ? res : "";
    }
    private static boolean isMatching(char o, char c) {
        return (o == '(' && c == ')') ||
               (o == '[' && c == ']') ||
               (o == '{' && c == '}') ||
               (o == '<' && c == '>');
    }
    private static boolean isBalanced(String s) {
        Deque<Character> st = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if ("([{<".indexOf(c) != -1) st.push(c);
            else if (")]}>" .indexOf(c) != -1) {
                if (st.isEmpty() || !isMatching(st.pop(), c)) return false;
            }
        }
        return st.isEmpty();
    }
    public static void main(String[] args) {
        String s1 = "The sum is (a[b*c] + d)";
        String s2 = "<[*(])>";
        String s3 = "hello*)(";
        System.out.println(cleanMarkdownBrackets(s1)); 
        System.out.println(cleanMarkdownBrackets(s2)); 
        System.out.println(cleanMarkdownBrackets(s3)); 
    }
}
