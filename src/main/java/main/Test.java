package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] a){
        String substring="123412341234";
        String advcash="";
        String p = "(U(\\s\\d{4}){3})|(U(\\d){12})|((\\d){12})|(\\d{4}(\\s\\d{4}){2})";//"(U[\\s[\\d]{4})]{3})";
        Pattern pattern = Pattern.compile(p,Pattern.UNICODE_CHARACTER_CLASS);
        if (advcash!=null) {
            try {
                Matcher matcher = pattern.matcher(substring);
                if (matcher.matches()) {
                    advcash = substring;
                }
            } catch (Exception e) {
                System.out.println("Не прошло проверку имя "+substring);
            }
        }
        System.out.println(advcash);
    }
}
