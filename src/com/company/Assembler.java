package com.company;
import java.lang.*;
import java.io.*;
import java.util.*;

public class Assembler {

    int l = 0; // number of line start at 0

    private String inst, rs, rt, rd;
    private ArrayList<String> label = new ArrayList<String>();
    private int  regA;
    private int  regB;
    private int  destReg;
    private static boolean isRun;


    public static void main(String[] args) {
        isRun = true;
        Assembler();
    }

    public void read(String line){
        String[] CurrentLine = line.split("\\s+");
        int checklabel = 0;
        if (!CurrentLine[0].isEmpty()){
            checklabel = label.indexOf(CurrentLine[0]);
            if (checklabel > 0){
                isRun = false;
                System.out.println("error label already have " + CurrentLine[0]);
            }else{
                label.add(CurrentLine[0]);
            }

        }
        setInst(CurrentLine[1], CurrentLine[2], CurrentLine[3], CurrentLine[4]);


    }

    public static void Assembler(){
        while (isRun){

        }
    }

    public void setInst(String inst, String field0 ,String field1 ,String field2) {
        String binary = "";
        if(inst.equals("add")){
            binary = "000";
        }

        else if (inst.equals("nand")){
            binary = "001";
        }

        else if (inst == "lw"){
            binary = "010";
        }

        else if (inst.equals("sw")){
            binary = "011";
        }
        else if (inst.equals("beq")){
            binary = "100";
        }

        else if (inst.equals("jalr")){
            binary = "101";
        }

        else if (inst.equals("halt")){
            binary = "110";
        }

        else if (inst.equals("noop")){
            binary = "000";
        }

    }

    public void Rtype(String op, int rs, int rt, int rd ){
        int instruction =

    }




}