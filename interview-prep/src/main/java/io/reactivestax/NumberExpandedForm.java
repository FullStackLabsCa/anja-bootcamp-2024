package io.reactivestax;

public class NumberExpandedForm {
    public static void main(String[] args) {
        expandNumber(3593);
    }

    public static void expandNumber(int n) {
        String num = String.valueOf(n);
        int j=0;
        StringBuilder builder = new StringBuilder(num);
        builder.append(" = ");
        for (int i = num.length() - 1; i >= 0; i--) {
            builder.append(num.charAt(j)).append(" X 1").append("0".repeat(i));
            if(i!=0) builder.append(" + ");
            j++;
        }
        System.out.println(builder);
    }
}
