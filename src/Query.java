/**
 *
 * @author renzo
 */
import java.util.ArrayList;
import java.util.Iterator;

public class Query {

    private ArrayList<QueryInstance> instances;
    private String type = "";


    public Query(String query_type) {
        type = query_type;
        instances = new ArrayList<QueryInstance>();
    }

    public long getTotalExecTime() {
        QueryInstance instance;
        long totaltime = 0;
        Iterator<QueryInstance> it = instances.iterator();
        while(it.hasNext()){
            instance = it.next();
            totaltime = totaltime + instance.getExecutionTime();
        }
        return totaltime;
    }

    public String getType(){
        return type;
    }
    
    public float getAvgExecTime() {
        long totaltime = this.getTotalExecTime();
        if(instances.size()==0){
            return 0;
        }
        return (float) totaltime / instances.size();
    }

    public long getMinExecTime() {
        QueryInstance instance;
        long mintime = Long.MAX_VALUE;
        long itime;
        Iterator<QueryInstance> it = instances.iterator();
        while(it.hasNext()){
            instance = it.next();
            itime = instance.getExecutionTime();
            if(itime < mintime){
                mintime = itime; 
            }
        }
        return mintime;
    }

    public long getMaxExecTime() {
        QueryInstance instance;
        long maxtime = 0;
        long itime;
        Iterator<QueryInstance> it = instances.iterator();
        while(it.hasNext()){
            instance = it.next();
            itime = instance.getExecutionTime();
            if(itime > maxtime){
                maxtime = itime; 
            }
        }
        return maxtime;
    }
    
    public int CountInstances(){
        return instances.size();
    }
    
    ArrayList<QueryInstance> getInstances(){
        return instances;
    }
    
    Iterator<QueryInstance> getInstancesIterator(){
        return instances.iterator();
    }

    public void addInstance(QueryInstance instance) {
        instances.add(instance);
    }
    
}
