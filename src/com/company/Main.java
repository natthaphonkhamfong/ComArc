package com.company;
import java.util.*;

public class Main {


    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        System.out.println("Please insert the filename =");
        System.out.println("Example = [textfilename].txt where inside is the assembly program.");
        maindua(s.next());
    }
    public static void main3(){
        String program=ReadFile.loadFile("assembly.txt");
        int index=0;
        ArrayList<String> result = new ArrayList<String>();
        for (String temp:program.split("\n")){
            if(temp.length() >1){
                if(index<10){
                    result.add("[0"+index+"] "+temp);
                }
                else {
                    result.add("["+index+"] "+temp);
                }
                index+=4;
            }
        }
        for(String i : result){
            System.out.println(i+";");
        }
    }


}
