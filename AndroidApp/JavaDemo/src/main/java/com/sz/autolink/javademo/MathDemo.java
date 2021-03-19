package com.sz.autolink.javademo;

import java.util.LinkedList;

/**
 * 汇集算法相关题目
 *
 * @author huanxue
 * Created by HSAE_DCY on 2021.3.11.
 */
public class MathDemo {

    private static LinkedList<String> mCharList = new LinkedList<>();

    public static void main(String[] args) {
        boolean check1 = checkBracketRule("()");
        boolean check2 = checkBracketRule("{((()())())[()]}()");
        boolean check3 = checkBracketRule("())");
        boolean check4 = checkBracketRule("([])");
        boolean check5 = checkBracketRule("())(()");
        System.out.println(check1);
        System.out.println(check2);
        System.out.println(check3);
        System.out.println(check4);
        System.out.println(check5);
    }

    /**
     * 给定一个只包含 "()[]{}" 六种字符的字符串。规定它们的优先级由外至内为："{}", "[]", "()"，
     * 同一级的可以嵌套，并列。要求判断给定的字符串是否是合法的括号字串？
     * <p>
     * 例："()", "{((()())())[()]}()" 是合法的。"())", "([])", "())(()" 都是不合法的。
     * <p>
     * 用栈来处理括号的匹配和嵌套，同时记录下括号的优先级状态。
     *
     * @param string
     * @return
     */
    private static boolean checkBracketRule(String string) {
        mCharList.clear();
        String a = String.valueOf(string.charAt(0));
        mCharList.add(a);
        int level = 0;
        switch (a) {
            case "{":
                level = 1;
                break;
            case "[":
                level = 2;
                break;
            case "(":
                level = 3;
                break;
        }
        for (int i = 1; i <= string.length() - 1; i++) {
            String b = String.valueOf(string.charAt(i));
            mCharList.add(b);
//            System.out.println("checkBracketRule  a:" + a + "  b:" + b + "  level:" + level);
            boolean checkResult = checkSingleChar(a, b, level);
//            System.out.println(checkResult + " return-----  i:" + i);
            if (checkResult) {
                level = getNestLevel(a, b);
                a = b;
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean checkSingleChar(String one, String two, int nest) {
        if (two.equals(")")) {
            if (mCharList.contains("(")) {
                mCharList.remove("(");
                return true;
            }
        }
        if (two.equals("]")) {
            if (mCharList.contains("[")) {
                mCharList.remove("[");
                return true;
            }
        }
        if (two.equals("}")) {
            if (mCharList.contains("{")) {
                mCharList.remove("{");
                return true;
            }
        }

        //无循环嵌套
        switch (nest) {
            case 1:
                switch (one) {
                    case "{":
                        switch (two) {
                            case "{":
                            case "[":
                            case "(":
                            case "}":
                                return true;
                        }
                        break;
                    case "[":
                        switch (two) {
                            case "[":
                            case "(":
                            case "]":
                                return true;
                        }
                        break;
                    case "(":
                        switch (two) {
                            case "(":
                            case ")":
                                return true;
                        }
                        break;
                    case ")":
                        switch (two) {
                            case "(":
                            case "]":
                            case "}":
                                return true;
                        }
                        break;
                    case "]":
                        switch (two) {
                            case "[":
                            case "(":
                            case "}":
                                return true;
                        }
                        break;
                    case "}":
                        switch (two) {
                            case "{":
                            case "[":
                            case "(":
                                return true;
                        }
                        break;
                }
            case 2:
                switch (one) {
                    case "{":
                    case "}":
                        switch (two) {
                            case "[":
                            case "(":
                                return true;
                        }
                    case "[":
                    case "]":
                        switch (two) {
                            case "[":
                            case "]":
                            case "(":
                                return true;
                        }
                    case "(":
                        switch (two) {
                            case "[":
                            case "(":
                            case ")":
                                return true;
                        }
                    case ")":
                        switch (two) {
                            case "[":
                            case "(":
                            case ")":
                                return true;
                        }
                }
            case 3:
                switch (one) {
                    case "{":
                    case "}":
                    case "[":
                    case "]":
                    case ")":
                        if (two.equals("(")) {
                            return true;
                        }
                    case "(":
                        switch (two) {
                            case "(":
                            case ")":
                                return true;
                        }
                }
                break;
            case 4:
                if (two.equals("(") || two.equals("[") || two.equals("{")) {
                    return true;
                }
                break;
            case 5:
                if (two.equals("(") || two.equals("[")) {
                    return true;
                }
                break;
            case 6:
                if (two.equals("(")) {
                    return true;
                }
                break;
        }

        return false;
    }

    /**
     * 解决下一位字符选择问题，判断优先级
     *
     * @param one 前一位
     * @param two 后一位
     * @return 0:不合格、1:{{[()]}}  2:[()]  3:()  4:{[(  5:[(  6:(
     */
    private static int getNestLevel(String one, String two) {
        switch (one) {
            case "{":
                switch (two) {
                    case "{":
                        return 1;
                    case "}":
                        return 4;
                    case "[":
                        return 2;
                    case "(":
                        return 3;
                }
                break;
            case "[":
                switch (two) {
                    case "]":
                        return 5;
                    case "[":
                        return 2;
                    case "(":
                        return 3;
                }
                break;
            case "(":
                switch (two) {
                    case "(":
                        return 3;
                    case ")":
                        return 5;
                }
                break;
            case ")":
                switch (two) {
                    case "(":
                        return 3;
                    case ")":
                        return 5;
                    case "]":
                    case "}":
                        return 4;
                }
                if (!mCharList.contains("(")) {
                    return 5;
                }
                break;
            case "]":
                switch (two) {
                    case "(":
                        return 3;
                    case "[":
                        return 2;
                    case "]":
                    case "}":
                        return 4;
                }
                if (!mCharList.contains("[")) {
                    return 4;
                }
                break;
            case "}":
                switch (two) {
                    case "(":
                        return 3;
                    case "[":
                        return 2;
                    case "{":
                        return 1;
                    case "}":
                        return 4;
                }
                break;
        }
        return 0;
    }
}
