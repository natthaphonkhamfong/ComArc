package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String b = "label  Ins   RA   RB  RD   com";
        String[] label = b.split("\\s+");
        if (label[0].isEmpty()){
            //intstruction(label[1], label[2], label[3] , label[4]);
        }else {

            System.out.println(label[0]);
            //intstruction(label[1], label[2], label[3] , label[4]);

        }
    }




}
