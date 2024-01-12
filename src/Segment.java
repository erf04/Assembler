import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Segment {
    SegmentType type;
    String addressPointer;

    public SegmentType setSegmentType(String name){
        if (name.equals("stack")){
            this.type=SegmentType.STACK;
            return type;
        }
        else if (name.equals("data")){
            this.type=SegmentType.DATA;
            return type;
        }
        else if (name.equals("code")){
            this.type=SegmentType.CODE;
            return type;
        }

       return null;

    }

    public static Segment findBySegmentType(SegmentType type, HashMap<Segment,ArrayList<String>> hashmap){
        for (Map.Entry<Segment, ArrayList<String>> entry:hashmap.entrySet() ){
            if (entry.getKey().type==type){
                return entry.getKey();
            }
        }
        return null;
    }
}
