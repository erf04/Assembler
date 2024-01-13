
import java.io.File;

import java.util.*;
import java.io.FileNotFoundException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        File file=new File("src/input.txt");
        HashMap<Segment,ArrayList<String>> result=Segment.segmentSeparator(file);
        Segment.segmentsHandler();
        Segment datasegment=Segment.findBySegmentType(SegmentType.DATA);
        Segment codesegment=Segment.findBySegmentType(SegmentType.CODE);
        Segment stacksegment=Segment.findBySegmentType(SegmentType.STACK);
        assert stacksegment != null;
//        HashMap<String,String> memeorydata=codesegment.getMemoryArrangement();
        codesegment.memoryVisualize();
        stacksegment.memoryVisualize();
        datasegment.memoryVisualize();


        System.out.println("the end");





    }







}

