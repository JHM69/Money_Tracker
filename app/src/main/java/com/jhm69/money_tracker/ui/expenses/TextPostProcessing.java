package com.jhm69.money_tracker.ui.expenses;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextPostProcessing {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<ArrayList>getTuple(String textBlock){
        String[] lines = textBlock.split("\n");
        SpellingCorrector Correcter = new SpellingCorrector(256);

        ArrayList<String> items = new ArrayList<>();
        ArrayList<Float> prices = new ArrayList<>();
        Pattern regexTest = Pattern.compile("/(?=.)^/$(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+)?(/.[0-9]{1,2})?$/g");

        for (String line : lines){
            String l = "";
            Matcher matcher = regexTest.matcher(line);
            if (matcher.find()){
                prices.add((float) Integer.parseInt(matcher.group(1)));
                String[] words =line.split("\\s");
                for (String w: words){
                    if (w.matches("[a-zA-Z]+")){
                        String new_w = Correcter.correct(w);
                        l += new_w;
                        l += " ";
                    }
                }
                items.add(l);
            }
        }
        ArrayList<ArrayList> ar = new ArrayList<>();
        ar.add(items);
        ar.add(prices);
        return ar;
    }

}
