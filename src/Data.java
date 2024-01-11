import java.util.ArrayList;
import java.util.HashMap;

public abstract class Data {
    static HashMap<String,String> OPCODE=new HashMap<String,String>(){{
        put("add","000000");
        put("and","001000");
        put("or","000010");
        put("sub","001010");
        put("inc","100 0");
        put("dec","100 1");
        put("xor","001100");
        put("push","101 0");
        put("pop","101 1");
        put("jmp","11101011");

    }};

    static String[] REG_8={
                "al","cl",
                "dl","bl",
                "ah","ch",
                "dh","bh"

    };
    static String[] REG_16={
            "ax","cx",
            "dx","bx",
            "sp","bp",
            "si","di"
    };

    static String[] REG_32={
            "eax",
            "ecx",
            "edx",
            "ebx",
            "esp",
            "ebp",
            "esi",
            "edi"
    };

    static HashMap<String,String> REG_VALUE=new HashMap<String,String>(){{
       put("al","000");
       put("ax","000");
       put("eax","000");
       put("cl","001");
       put("cx","001");
       put("ecx","001");
       put("dl","010");
       put("dx","010");
       put("edx","010");
       put("bl","011");
       put("bx","011");
       put("ebx","011");
       put("ah","100");
       put("sp","100");
       put("esp","100");
       put("ch","101");
       put("bp","101");
       put("ebp","101");
       put("dh","110");
       put("si","110");
       put("esi","110");
       put("bh","111");
       put("di","111");
       put("edi","111");
    }};

    static HashMap<String,String> HEX=new HashMap<String,String>(){{
       put("0000","0");
       put("0001","1");
       put("0010","2");
       put("0011","3");
       put("0100","4");
       put("0101","5");
       put("0110","6");
       put("0111","7");
       put("1000","8");
       put("1001","9");
       put("1010","A");
       put("1011","B");
       put("1100","C");
       put("1101","D");
       put("1110","E");
       put("1111","F");
    }};

    static HashMap<String,Integer> types=new HashMap<>(){{
        put("word",2);
        put("byte",1);
        put("dword",4);
    }};
    static HashMap<String,Assembler> Assembled=new HashMap<String, Assembler>();
    static ArrayList<String> lines=new ArrayList<String>();
    static HashMap<String,String> labels=new HashMap<String,String>();




}
