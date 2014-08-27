
/**
 *
 * @author renzo
 */
import java.io.File;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class QueriesManager {

    private ArrayList<QueryInstance> querymix = new ArrayList<QueryInstance>();
    private ArrayList<TestDataInstance> testdata = new ArrayList<TestDataInstance>();
    private ArrayList<QueryInstance> instancesQ1 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ2 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ3 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ4 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ5 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ6 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ7 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ8 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ9 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ10 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ11 = new ArrayList<QueryInstance>();
    private ArrayList<QueryInstance> instancesQ12 = new ArrayList<QueryInstance>();

    public QueriesManager() {
    }

    public boolean loadTestData(String filename) {
        testdata = new ArrayList<TestDataInstance>();
        Document doc = null;
        try {
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList;

            TestDataInstance tdi;
            String type;
            String value1;
            String value2;
            Element eElement;
            nList = doc.getElementsByTagName("data");
            for (int j = 0; j < nList.getLength(); j++) {
                eElement = (Element) nList.item(j);
                type = eElement.getAttribute("type");
                value1 = eElement.getAttribute("value1");
                value2 = eElement.getAttribute("value2");
                tdi = new TestDataInstance(type);
                if (value1 != null) {
                    tdi.setValue(1, value1);
                }
                if (value2 != null) {
                    tdi.setValue(2, value2);
                }
                testdata.add(tdi);
            }

            this.makeBasicQueryMix();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void makeBasicQueryMix() {
        TestDataInstance tdi;
        QueryInstance instance;
        int testDataCounter = 0;
        Iterator<TestDataInstance> it1;
        it1 = testdata.iterator();

        while (it1.hasNext()) {
            tdi = it1.next();

            if (tdi.getType().compareTo("personID") == 0) {
                testDataCounter++;

                instance = new QueryInstance("Q3");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ3.add(instance);

                instance = new QueryInstance("Q4");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ4.add(instance);

                instance = new QueryInstance("Q5");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ5.add(instance);

                instance = new QueryInstance("Q6");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ6.add(instance);

                instance = new QueryInstance("Q7");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ7.add(instance);

                instance = new QueryInstance("Q12");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ12.add(instance);
            }

            if (tdi.getType().compareTo("personName") == 0) {
                instance = new QueryInstance("Q1");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ1.add(instance);
            }

            if (tdi.getType().compareTo("webpageID") == 0) {
                instance = new QueryInstance("Q2");
                instance.setParameter(1, tdi.getValue(1));
                instancesQ2.add(instance);
            }

            if (tdi.getType().compareTo("friend") == 0) {
                instance = new QueryInstance("Q10");
                instance.setParameter(1, tdi.getValue(1));
                instance.setParameter(2, tdi.getValue(2));
                instancesQ10.add(instance);

                instance = new QueryInstance("Q11");
                instance.setParameter(1, tdi.getValue(1));
                instance.setParameter(2, tdi.getValue(2));
                instancesQ11.add(instance);
            }

            if (tdi.getType().compareTo("path") == 0) {
                instance = new QueryInstance("Q8");
                instance.setParameter(1, tdi.getValue(1));
                instance.setParameter(2, tdi.getValue(2));
                instancesQ8.add(instance);

                instance = new QueryInstance("Q9");
                instance.setParameter(1, tdi.getValue(1));
                instance.setParameter(2, tdi.getValue(2));
                instancesQ9.add(instance);
            }

        }
    }

    public Iterator<QueryInstance> makeQueryMix(int instances_for_query, String type) {
        querymix = new ArrayList<QueryInstance>();
        this.copyInstances(instancesQ1, instances_for_query);
        this.copyInstances(instancesQ2, instances_for_query);
        this.copyInstances(instancesQ3, instances_for_query);
        this.copyInstances(instancesQ4, instances_for_query);
        this.copyInstances(instancesQ5, instances_for_query);
        this.copyInstances(instancesQ6, instances_for_query);
        this.copyInstances(instancesQ7, instances_for_query);
        this.copyInstances(instancesQ8, instances_for_query);
        this.copyInstances(instancesQ9, instances_for_query);
        this.copyInstances(instancesQ10, instances_for_query);
        this.copyInstances(instancesQ11, instances_for_query);
        this.copyInstances(instancesQ12, instances_for_query);
        if(type.compareTo("r")==0){
            Collections.shuffle(querymix);
        }
        return querymix.iterator();
    }
    
    public Iterator<QueryInstance> getQueryMixIterator(){
        return querymix.iterator();
    }
    
    
    private void copyInstances(ArrayList<QueryInstance> instances, int instances_for_query){
        Iterator<QueryInstance> it;
        if (!instances.isEmpty()) {
            it = instances.iterator();
            for (int i = 1; i <= instances_for_query; i++) {
                if (!it.hasNext()) {
                    it = instances.iterator();
                }
                querymix.add(it.next());
            }
        }
    }
    
    
    public Iterator<QueryInstance> makeQueryMixByQuery(int query, int instances_for_query, String type) {
        querymix = new ArrayList<QueryInstance>();
        if(query == 1){
            this.copyInstances(instancesQ1, instances_for_query);
        }
        if(query == 2){
            this.copyInstances(instancesQ2, instances_for_query);
        }
        if(query == 3){
            this.copyInstances(instancesQ3, instances_for_query);
        }
        if(query == 4){
            this.copyInstances(instancesQ4, instances_for_query);
        }
        if(query == 5){
            this.copyInstances(instancesQ5, instances_for_query);
        }
        if(query == 6){
            this.copyInstances(instancesQ6, instances_for_query);
        }
        if(query == 7){
            this.copyInstances(instancesQ7, instances_for_query);
        }
        if(query == 8){
            this.copyInstances(instancesQ8, instances_for_query);
        }
        if(query == 9){
            this.copyInstances(instancesQ9, instances_for_query);
        }
        if(query == 10){
            this.copyInstances(instancesQ10, instances_for_query);
        }
        if(query == 11){
            this.copyInstances(instancesQ11, instances_for_query);
        }
        if(query == 12){
            this.copyInstances(instancesQ12, instances_for_query);
        }
        
        if(type.compareTo("r")==0){
            Collections.shuffle(querymix);
        }
        return querymix.iterator();
    }

    public Query getQueriesByType(String type) {
        Query query = new Query(type);
        QueryInstance instance;
        Iterator<QueryInstance> it = querymix.iterator();
        while (it.hasNext()) {
            instance = it.next();
            if (type.compareTo(instance.getType()) == 0) {
                query.addInstance(instance);
            }
        }
        return query;
    }

    public int getQueryMixSize() {
        return querymix.size();
    }
    
    public long getTotalExecTime(){
        long ttime = 0;
        QueryInstance instance;
        Iterator<QueryInstance> it = querymix.iterator();
        while (it.hasNext()) {
            instance = it.next();
            ttime += instance.getExecutionTime();
        }
        return ttime;
    }
    
}
