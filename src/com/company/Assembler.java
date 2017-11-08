package com.company;

public class Assembler {

    int l = 0; // number line start at 0

    public void line(String line){
        String[] label = line.split("\\s+");
        if (label[0].isEmpty()){
            //intstruction(label[1], label[2], label[3] , label[4]);
        }else {

            System.out.println(label[0]);
            //intstruction(label[1], label[2], label[3] , label[4]);

        }

    }

}
