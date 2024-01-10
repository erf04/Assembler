import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Assembler extends Data {
    String instruction=null;
    int numberOfOperands=0;
    Operand operand1=new Operand();
    Operand operand2=new Operand();

    boolean isLabel=false;
    String labelName;
    String codeLine;
    private String machineCode=null;
    private static int length;
    static ArrayList<Assembler> labels=new ArrayList<Assembler>();
    Assembler(String code)
    {
        this.codeLine=code.toLowerCase().trim();
//        lines.add(code);

       if (code.contains(":")){
           this.isLabel=true;
           this.labelName=code.toLowerCase().trim();
           Assembler.labels.add(this);
           return;
       }
       String[] splited= code.split(",");
       if (splited.length==1){
           this.numberOfOperands=1;
           this.instruction=splited[0].split(" ")[0].trim().toLowerCase();
           this.operand1.name=splited[0].split(" ")[1].trim().toLowerCase();
           if (this.operand1.name.contains("]")){
               this.operand1.type=OperandType.MEMORY;
               this.operand1.name=this.operand1.name.split("]")[0].substring(1).trim();
           }
           else if (this.in(this.operand1.name,REG_32) || this.in(this.operand1.name,REG_16) || this.in(this.operand1.name,REG_8)){
               this.operand1.type=OperandType.REGISTER;
           }
           else if (this.instruction.equals("jmp")){
               this.operand1.type=OperandType.LABEL;
           }
           else if (this.instruction.equals("push")){
               this.operand1.type=OperandType.IMMEDIATE;
           }
           else{
               System.out.println("not valid");
               System.exit(-1);
           }
           this.operand2=null;
//           System.out.printf("%s %s %s %d",this.instruction,this.operand1,this.operand2,this.numberOfOperands);
       }
       else if (splited.length==2){
           this.numberOfOperands=2;
           this.instruction=splited[0].split(" ")[0].trim().toLowerCase();
           this.operand1.name=splited[0].split(" ")[1].trim().toLowerCase();
           if (this.operand1.name.contains("]")){
               this.operand1.type=OperandType.MEMORY;
               this.operand1.name=this.operand1.name.split("]")[0].substring(1).trim();
           }
           else{
               this.operand1.type=OperandType.REGISTER;
           }

           this.operand2.name=splited[1].trim().toLowerCase();
           if (this.operand2.name.contains("]")){
               this.operand2.type=OperandType.MEMORY;
               this.operand2.name=this.operand2.name.split("]")[0].substring(1).trim();
           }
           else{
               this.operand2.type=OperandType.REGISTER;
           }

       }
       else{
           System.out.println("there is something wrong");
       }



    }

    public static void saveToDB(File sourceCode) throws FileNotFoundException {
        if (!sourceCode.canRead()){
            System.out.println("not able to read file!");

        }
        else{
            Scanner reader=new Scanner(sourceCode);
            int lines=0;
            while(reader.hasNextLine()){
                String line=reader.nextLine();
                Data.lines.add(line.toLowerCase().trim());
                lines++;
            }
            Assembler.length=lines;
            findLabels();
        }

    }

    public static void saveToDB(String[] input){
        lines.addAll(Arrays.asList(input));
        Assembler.length= input.length;
        findLabels();

    }

    private static void findLabels(){
        for (int i=0;i<Data.lines.size();i++){
            if (Data.lines.get(i).contains(":")){
                String label=Data.lines.get(i).substring(0,Data.lines.get(i).length()-1);
                Data.labels.put(label.toLowerCase().trim(),i+"");
            }
        }
    }
    public static boolean checkSyntax(){
        for (int i = 0; i < lines.size(); i++) {
            Assembler assembler=new Assembler(lines.get(i));
            if (assembler.isLabel)
                continue;
            if ((assembler.instruction.equals("inc") || assembler.instruction.equals("dec")
                    || assembler.instruction.equals("push") || assembler.instruction.equals("pop")
                    || assembler.instruction.equals("jmp")) && assembler.numberOfOperands==2 ){

                System.out.println("number of operands does not match to instruction");
                return false;
            }

            else if ((assembler.instruction.equals("xor") || assembler.instruction.equals("and")
                    || assembler.instruction.equals("or") || assembler.instruction.equals("add")
                    || assembler.instruction.equals("sub")) && assembler.numberOfOperands==1){


                System.out.println("number of operands does not match to instruction");
                return false;
            }

            else if (assembler.numberOfOperands==2 && assembler.operand1.type==OperandType.MEMORY && assembler.operand2.type==OperandType.MEMORY){
                System.out.println("operands can not be memories");
                return false;
            }

            else if (assembler.numberOfOperands==2 && !assembler.checkSize(assembler.operand1.name,assembler.operand2.name)){
                return false;
            }
            else if ((assembler.instruction.equals("inc") || assembler.instruction.equals("dec")) &&  assembler.operand1.type==OperandType.MEMORY){
                return false;
            }
        }

        return true;


    }

    private boolean checkSize(String a,String b){
        if ((this.in(a,REG_8) && this.in(b,REG_8)) || (this.in(a,REG_16) && this.in(b,REG_16)) || (this.in(a,REG_32) && this.in(b,REG_32)))
            return true;
        return false;
    }

    private boolean in(String x,String[] array){
        for (int i=0;i<array.length;i++){
            if (array[i].equals(x))
                return true;
        }
        return false;
    }

    public String getOpCode(){
        return OPCODE.get(this.instruction);
    }

    public int getD(){
        if (this.numberOfOperands==2 && this.operand2.type==OperandType.REGISTER){
            return 0;
        }
        return 1;
    }

    public int getS(){
        if (this.numberOfOperands==2 && this.in(this.operand1.name,REG_8)){
            return 0;
        }
        return 1;
    }

    public String getByteOne(){
        if (this.numberOfOperands==2)
            return getOpCode()+getD()+getS();

        return null;
    }

    public String getMod(){
        if (this.numberOfOperands==2 && this.operand1.type==OperandType.REGISTER && this.operand2.type==OperandType.REGISTER){
            return "11";
        }
        else if (this.numberOfOperands==2 && (this.operand1.type==OperandType.MEMORY || this.operand2.type==OperandType.MEMORY)){
            return "00";
        }

        return null;
    }

    public String getRegRMValue(){
        if (this.numberOfOperands==2 && this.operand2.type==OperandType.REGISTER){
            return REG_VALUE.get(this.operand2.name)+REG_VALUE.get(this.operand1.name);
        }

        else if (this.numberOfOperands==2 && this.operand1.type==OperandType.REGISTER){
            return REG_VALUE.get(this.operand1.name)+REG_VALUE.get(this.operand2.name);

        }
        return null;
    }

    public String getByteTwo(){
        if (this.numberOfOperands==2)
            return getMod()+getRegRMValue();
        return null;
    }


    public String getMachineCode(){
        if (this.numberOfOperands==2){
            String prefix="";
            if (this.in(this.operand1.name,REG_16) && this.in(this.operand2.name,REG_16) && this.operand2.type==OperandType.MEMORY ){
                prefix="01100111 01100110 ";
            }
            else if (this.in(this.operand1.name,REG_16) && this.in(this.operand2.name,REG_16)){
                prefix="01100110 ";
            }


            return prefix+getByteOne()+ " "+getByteTwo();

        }


        else if (!this.isLabel && this.instruction.equals("jmp")){
            int jmpIndex=this.findJmp();
            int distanceInBytes=findLabelDistance(jmpIndex,this.operand1.name);
            String in32=Integer.toBinaryString(distanceInBytes);
            String in8;
            if (distanceInBytes<0){
                in8=in32.substring(in32.length()-8);
            }
            else{
                in8="0".repeat(8-in32.length())+in32;
            }

            return OPCODE.get("jmp")+" "+in8;
        }

        else if (this.numberOfOperands==1){
            String prefix;
            if (this.instruction.equals("push") && this.operand1.type==OperandType.IMMEDIATE){
                String bin=Integer.toBinaryString(Integer.parseInt(this.operand1.name));
                String temp="0".repeat(8-bin.length())+bin;

                return "01101010 "+temp;
            }
            if (this.in(this.operand1.name,REG_8)){
                if (this.instruction.equals("inc") || this.instruction.equals("dec")){
                    prefix="11111110 "+"1"+OPCODE.get(this.instruction);
                    return prefix+REG_VALUE.get(this.operand1.name);
                }
                else if (this.instruction.equals("push") || this.instruction.equals("pop")){
                    return null;
                }
                else{
                    return null;
                }

            }
            else if (this.in(this.operand1.name,REG_16)){
                if (this.instruction.equals("inc") || this.instruction.equals("dec")){
                    prefix="01100110 "+"0";
                }
                else if (this.instruction.equals("push")){
                    if (this.operand1.type==OperandType.MEMORY){
                        prefix="01100111 ";
                        return prefix+"11111111 "+"00110"+REG_VALUE.get(this.operand1.name);
                    }
                    else{
                        prefix="01100110 0";
                    }
                }


                else{
                    prefix="01100110 0";
                }

            }

            else if (this.instruction.equals("push") && this.in(this.operand1.name,REG_32) && this.operand1.type==OperandType.MEMORY){
                return "11111111 "+"00110"+REG_VALUE.get(this.operand1.name);
            }


            else{
                prefix="0";
            }

            return prefix+OPCODE.get(this.instruction)+REG_VALUE.get(this.operand1.name);

        }

        return null;
    }



    public static String convertToHex(String binary){
        String[] bytes=binary.split(" ");
        String answer="";
        for (int i = 0; i < bytes.length; i++) {
            String Byte1=bytes[i].substring(0,4);
            answer= answer + HEX.get(Byte1);

            String Byte2=bytes[i].substring(4);
            if (!Byte2.isBlank()){
                answer= answer+HEX.get(Byte2);
                if (i!=bytes.length-1)
                    answer+=" ";
            }


        }
        return answer;
    }

    public int getSize(){
        if (this.isLabel)
            return 0;
        if (this.instruction.equals("jmp"))
            return 2;
        String[] splited=Assembler.convertToHex(this.getMachineCode()).split(" ");
        return splited.length;
    }

    private static int sumBytes(int beginIndex,int endIndex){
        int sum=0;
        for (int i = beginIndex+1; i < endIndex; i++) {
            Assembler assembler=new Assembler(Data.lines.get(i));
            sum+=assembler.getSize();
        }
        return sum;
    }

    public static int findLabelDistance(int index,String labelName){
      int labelIndex=Integer.parseInt(Data.labels.get(labelName));
      if (labelIndex>index){
          return sumBytes(index,labelIndex);
      }
      else{
          return -1*(sumBytes(labelIndex,index)+2);
      }

    }

    private int findJmp(){
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(this.codeLine)){
                return i;
            }
        }
        return -1;
    }

    public String generateNewAddress(String oldAddress){
        int decimal=Integer.parseInt(oldAddress.substring(2),16);

        decimal+=this.getSize();
        String bin=Integer.toBinaryString(decimal);
        String temp="0".repeat(8-bin.length())+bin;
        String hex=Assembler.convertToHex(temp);
        String newAddress="0".repeat(16-hex.length())+hex;
        return "0x"+newAddress;
    }

    public static String getLine(int line){
        return Data.lines.get(line-1);
    }

    public static int getSourceCodeLines(){
        return Assembler.length;
    }








}
