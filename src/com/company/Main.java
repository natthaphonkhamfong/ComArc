package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String b = "   label  Ins   RA   RB  RD   com";
        String[] label = b.split("\\s+");
        if (label[0] == " "){
            System.out.println("NULL");
        }else {
            System.out.println(label[0] + "11");
        }
    }




}
