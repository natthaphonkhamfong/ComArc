package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        //FileInputStream fstream = new FileInputStream("E:\\CPE304\\Project\\ComArc\\assembly.txt");
        try (BufferedReader br = new BufferedReader(new FileReader("E:\\CPE304\\Project\\ComArc\\src\\com\\company\\assembly.txt"))) {

            String Currentline;

            while ((Currentline = br.readLine()) != null) {
                line(Currentline);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void line(String line){
        String[] label = line.split("\\s+");
        if (label[0].isEmpty()){
            System.out.println("NULL");
            //intstruction(label[1], label[2], label[3] , label[4]);
        }else {

            System.out.println(label[0]);
            //intstruction(label[1], label[2], label[3] , label[4]);

        }

    }

}