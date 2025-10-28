import java.util.*;
class ListNode {
    int val;
    ListNode next;
    ListNode(int v) { val = v; }
}
public class PlaylistManager {
    public static String runPlaylist(List<String> commands) {
        ListNode head = null; 
        ListNode tail = null;  
        for (String cmd : commands) {
            String[] parts = cmd.split(" ");
            String op = parts[0];
            switch (op) {
                case "ADD_END" -> {
                    int x = Integer.parseInt(parts[1]);
                    ListNode node = new ListNode(x);
                    if (head == null) {
                        head = tail = node;
                    } else if (tail != null) {
                        tail.next = node;
                        tail = node;
                    }
                }
                case "ADD_AFTER" -> {
                    int a = Integer.parseInt(parts[1]);
                    int b = Integer.parseInt(parts[2]);
                    ListNode cur = head;
                    while (cur != null && cur.val != a) cur = cur.next;
                    if (cur != null) {
                        ListNode node = new ListNode(b);
                        node.next = cur.next;
                        cur.next = node;
                        if (cur == tail) tail = node;
                    }
                }
                case "DELETE" -> {
                    int x = Integer.parseInt(parts[1]);
                    if (head == null) return "";
                    if (head.val == x) {
                        head = head.next;
                        return listToString(head);
                    }
                    ListNode prev = head, cur = head.next;
                    while (cur != null && cur.val != x) {
                        prev = cur;
                        cur = cur.next;
                    }
                    if (cur != null) {
                        prev.next = cur.next;
                        if (cur == tail) tail = prev;
                    }
                }
                case "DEDUP" -> {
                    ListNode outer = head;
                    while (outer != null) {
                        ListNode prev = outer;
                        ListNode inner = outer.next;
                        while (inner != null) {
                            if (inner.val == outer.val) {
                                prev.next = inner.next;
                                if (inner == tail) tail = prev;
                                inner = prev.next;
                            } else {
                                prev = inner;
                                inner = inner.next;
                            }
                        }
                        outer = outer.next;
                    }
                }
                case "REVERSE_K" -> {
                    int k = Integer.parseInt(parts[1]);
                    head = reverseKGroup(head, k);
                    tail = head;
                    if (tail != null) while (tail.next != null) tail = tail.next;
                }
                case "PRINT" -> {
                    return listToString(head);
                }
            }
        }
        return listToString(head);
    }
    private static ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prevGroupEnd = dummy;
        while (true) {
            ListNode kth = prevGroupEnd;
            for (int i = 0; i < k && kth != null; i++) kth = kth.next;
            if (kth == null) break;
            ListNode groupStart = prevGroupEnd.next;
            ListNode nextGroupStart = kth.next;
            ListNode prev = nextGroupStart, cur = groupStart;
            while (cur != nextGroupStart) {
                ListNode tmp = cur.next;
                cur.next = prev;
                prev = cur;
                cur = tmp;
            }
            prevGroupEnd.next = kth;
            prevGroupEnd = groupStart;
        }
        return dummy.next;
    }
    private static String listToString(ListNode head) {
        StringBuilder sb = new StringBuilder();
        ListNode cur = head;
        while (cur != null) {
            sb.append(cur.val);
            if (cur.next != null) sb.append(" ");
            cur = cur.next;
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        List<String> commands = Arrays.asList(
                "ADD_END 10",
                "ADD_END 20",
                "ADD_AFTER 10 15",
                "ADD_END 10",
                "DEDUP",
                "REVERSE_K 2",
                "PRINT"
        );
        String result = runPlaylist(commands);
        System.out.println(result); 
    }
}
