import java.util.*;
public class InfixExpressionEvaluator {
    public static int evaluate(String expr, Map<String, Integer> env) {
        try {
            List<String> rpn = toRPN(expr);
            System.out.println("RPN Conversion: " + rpn);
            int result = evalRPN(rpn, env);
            System.out.println("Final Result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return Integer.MIN_VALUE; 
        }
    }
    private static int precedence(String op) {
        switch (op) {
            case "+": case "-": return 1;
            case "*": case "/": case "%": return 2;
            case "^": return 3;
            default: return 0;
        }
    }
    private static boolean isRightAssoc(String op) {
        return op.equals("^");
    }
    private static boolean isOperator(String s) {
        return "+-*/%^".contains(s);
    }
    private static List<String> toRPN(String expr) throws Exception {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        List<String> tokens = tokenize(expr);
        System.out.println("Tokens: " + tokens);
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.matches("-?\\d+")) { 
                output.add(token);
            } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) { 
                if (i + 1 < tokens.size() && tokens.get(i + 1).equals("(")) {
                    stack.push(token); // function name
                } else {
                    output.add(token); // variable
                }
            } else if (isOperator(token)) {
                // Handle unary minus
                if (token.equals("-") && (i == 0 || "(,*/+-%^".contains(tokens.get(i - 1)))) {
                    output.add("0");
                }
                while (!stack.isEmpty() && isOperator(stack.peek())) {
                    String top = stack.peek();
                    if ((isRightAssoc(token) && precedence(token) < precedence(top)) ||
                        (!isRightAssoc(token) && precedence(token) <= precedence(top))) {
                        output.add(stack.pop());
                    } else break;
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new Exception("Mismatched parentheses");
                stack.pop(); // remove '('
                // function case
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    output.add(stack.pop());
                }
            } else if (token.equals(",")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new Exception("Misplaced comma");
            } else {
                throw new Exception("Invalid token: " + token);
            }
        }
        while (!stack.isEmpty()) {
            String t = stack.pop();
            if (t.equals("(") || t.equals(")")) throw new Exception("Mismatched parentheses");
            output.add(t);
        }
        return output;
    }
    private static boolean isFunction(String token) {
        return token.equals("min") || token.equals("max") || token.equals("abs");
    }
    private static int evalRPN(List<String> rpn, Map<String, Integer> env) throws Exception {
        Stack<Integer> stack = new Stack<>();
        for (String token : rpn) {
            if (token.matches("-?\\d+")) {
                stack.push(Integer.parseInt(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) throw new Exception("Missing operands");
                int b = stack.pop(), a = stack.pop();
                int res;
                switch (token) {
                    case "+": res = a + b; break;
                    case "-": res = a - b; break;
                    case "*": res = a * b; break;
                    case "/":
                        if (b == 0) throw new Exception("Divide by zero");
                        res = a / b; break;
                    case "%":
                        if (b == 0) throw new Exception("Divide by zero");
                        res = a % b; break;
                    case "^": res = (int) Math.pow(a, b); break;
                    default: throw new Exception("Unknown operator: " + token);
                }
                stack.push(res);
            } else if (isFunction(token)) {
                if (token.equals("abs")) {
                    if (stack.isEmpty()) throw new Exception("Missing argument for abs");
                    stack.push(Math.abs(stack.pop()));
                } else {
                    if (stack.size() < 2) throw new Exception("Missing arguments for " + token);
                    int b = stack.pop(), a = stack.pop();
                    if (token.equals("min")) stack.push(Math.min(a, b));
                    else if (token.equals("max")) stack.push(Math.max(a, b));
                }
            } else { // variable
                if (!env.containsKey(token)) throw new Exception("Missing variable: " + token);
                stack.push(env.get(token));
            }
        }
        if (stack.size() != 1) throw new Exception("Malformed expression");
        return stack.pop();
    }
    private static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.setLength(0);
                while (i < expr.length() && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) {
                    sb.append(expr.charAt(i++));
                }
                i--;
                tokens.add(sb.toString());
            } else if ("+-*/%^(),".indexOf(c) != -1) {
                tokens.add(String.valueOf(c));
            } else {
                throw new RuntimeException("Invalid character: " + c);
            }
        }
        return tokens;
    }
    public static void main(String[] args) {
        Map<String, Integer> env = new HashMap<>();
        env.put("x", -2);
        env.put("y", -7);
        test("3 + 4 * 2 / (1 - 5) ^ 2 ^ 3", env);
        test("min(10, max(2, 3*4))", env);
        test("-(x) + abs(y)", env);
        test("a + b", env); 
    }
    private static void test(String expr, Map<String, Integer> env) {
        System.out.println("\nExpression: " + expr);
        int res = evaluate(expr, env);
        if (res != Integer.MIN_VALUE)
            System.out.println("=> Output: " + res);
        else
            System.out.println("=> Output: ERROR");
    }
}
