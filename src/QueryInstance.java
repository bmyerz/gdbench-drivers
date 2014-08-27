
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author renzo
 */
public class QueryInstance {
    
    private String type = "";
    private String output = "";
    private long exectime = 0;
    private ArrayList<String> parameters = new ArrayList<String>();
    
    
    public QueryInstance(String _type){
        this.type = _type;
    }
    
    String getType(){
        return this.type;
    }
            
    void setOutput(String _value){
        output = _value;
    }
    
    String getOutput(){
        return output;
    }
    
    void setExecutionTime(long _ms){
        exectime = _ms;
    }
    
    long getExecutionTime(){
        return exectime;
    }

    public void setParameter(int param_number, String value) {
        parameters.add(param_number-1, value);
    }

    public String getParameter(int param_number) {
        if (param_number == 0 || param_number > parameters.size()) {
            return null;
        } else {
            return parameters.get(param_number-1);
        }
    }

    public Iterator<String> parametersIterators() {
        return parameters.iterator();
    }
    
    public int parametersNumber(){
        return parameters.size();
    }
    
    
}
