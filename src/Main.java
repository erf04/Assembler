
import java.io.File;

import java.util.Scanner;
import java.io.FileNotFoundException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        getInput();

        if (!Assembler.checkSyntax()){
            System.out.println("something wrong");
            System.exit(-1);
        }

        String address="0x"+"0".repeat(16);
        for (int i = 0; i < Assembler.getSourceCodeLines(); i++) {
            Assembler assembler=new Assembler(Assembler.getLine(i+1));
            if (!assembler.isLabel){
                String machineCode= assembler.getMachineCode();
                if (machineCode!=null){
                    System.out.println(address+" : "+Assembler.convertToHex(assembler.getMachineCode()));
                    address=assembler.generateNewAddress(address);
                }
                else{
                    System.out.println("not correct");
                }

            }

        }

    }
    public static void getInput() throws FileNotFoundException{
        System.out.println("enter your choice:");
        System.out.println("1)cmd    2)file");
        Scanner scanner=new Scanner(System.in);
        int answer=Integer.parseInt(scanner.nextLine());
        if (answer==1){
            System.out.println("type the number of lines you want to assemble: ");
            int len=Integer.parseInt(scanner.nextLine());
            String[] lines=new String[len];
            System.out.println("ok!");
            for (int i = 0; i < len; i++) {
                lines[i]=scanner.nextLine();
            }
            Assembler.saveToDB(lines);


        }
        else if (answer==2){
            System.out.println("type your source file location: ");
            String addr=scanner.nextLine();
            File inputFile=new File(addr);
            Assembler.saveToDB(inputFile);
        }

        else{
            System.out.println("not valid");
        }



    }




}

