package com.example;

import java.util.List;

public class ReportTool {

    public static void main(String[] args) {
        List<String> rows = List.of("alpha", "beta");
        System.out.println("name,length");
        for (String row : rows) {
            System.out.println(row + "," + row.length());
        }
        System.out.flush();
        System.err.println("done");
    }
}
