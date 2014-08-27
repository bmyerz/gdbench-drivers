
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Iterator;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public abstract class TestDriver {

    private long nodes_counter, edges_counter = 0;
    private long data_time = 0;
    private long query_time = 0;
    ArrayList<QueryInstance> query_mix;
    private String data_filename = "sndata.gd";
    private String testdata_filename = "testdata.xml";
    private String data_loading_report_filename = "DataLoadingReport.txt";
    private String query_exec_report_filename = "QueryExecutionReport.html";
    QueriesManager queries_manager;
    DecimalFormat redondear = new DecimalFormat("#########.##");
    int instances_for_query_default = 100;
    GDReader gdreader;

    TestDriver() {
    }

    abstract boolean createDB(String dbname);

    abstract boolean openDB(String dbname);

    abstract boolean closeDB();

    abstract boolean openTransaction();

    abstract boolean closeTransaction();

    abstract long getNumberOfNodes();

    abstract long getNumberOfEdges();

    abstract long getDBsize();

    abstract boolean insertPerson(long _pid, String _name, String _age, String _location);

    abstract boolean insertWebPage(long _wpid, String _url, String _creation);

    abstract boolean insertFriend(long id_person_1, long id_person_2);

    abstract boolean insertLike(long id_person, long id_webpage);

    //findPersonByName
    abstract long Q1(String person_name);

    //findFansOfWebPageByID
    abstract long Q2(long webpage_id);

    //findLikesOfPersonByID
    abstract long Q3(long person_id);

    //findNameOfPersonByID
    abstract String Q4(long person_id);

    //findPersonMayKnow
    abstract long Q5(long person_id);

    //findRecomendedWebPage
    abstract long Q6(long person_id);

    //findPersonsThatLikeSameWebPagesOfPersonByID
    abstract long Q7(long person_id);

    //existPathBetweeen
    abstract boolean Q8(long person_id1, long person_id2);

    //findShortestPathBetween
    abstract long Q9(long person_id1, long person_id2);

    //findMutualFriends
    abstract long Q10(long person_id1, long person_id2);

    //findMutualLikes
    abstract long Q11(long person_id1, long person_id2);

    //getSocialDegreeByID
    abstract long Q12(long person_id);

    public void run() {
        if (this.runDataLoading()) {
            this.runQueryTest();
        }
    }

    public void run(int instances_for_query, String querymix_type) {
        if (this.runDataLoading()) {
            this.runQueryTest(instances_for_query, querymix_type);
        }
    }

    public boolean runDataLoading() {
        long ctime = 0;
        System.out.println("-----------------------------");
        System.out.println("----- DATA LOADING TEST -----");
        System.out.println("-----------------------------");

        if (!this.createDB("dbtest")) {
            System.out.println("Creating database (FAILED)");
            return false;
        }
        System.out.println("Creating database (OK)");

        if (!this.openDB("dbtest")) {
            System.out.println("Opening database (FAILED)");
            return false;
        }
        System.out.println("Opening database (OK)");

        if (!this.openTransaction()) {
            System.out.println("Opening transaction (FAILED)");
            return false;
        }
        System.out.println("Opening transaction (OK)");


        System.out.println("Loading social network data from " + this.data_filename);

        ctime = System.nanoTime()/1000;

        gdreader = new GDReader(this);
        if (!gdreader.readFile(data_filename)) {
            System.out.println("Data loading (FAILED)");
            return false;
        } else {
            System.out.println("- Person nodes read: " + gdreader.getPeopleCounter());
            System.out.println("- Webpage nodes read: " + gdreader.getWebpagesCounter());
            System.out.println("- Friend edges read: " + gdreader.getFriendsCounter());
            System.out.println("- Like edges read: " + gdreader.getLikesCounter());
            System.out.println("- Total nodes read: " + gdreader.getNodesCounter());
            System.out.println("- Total edges read: " + gdreader.getEdgesCounter());
        }

        data_time = System.nanoTime()/1000 - ctime;
        this.nodes_counter = this.getNumberOfNodes();
        this.edges_counter = this.getNumberOfEdges();

        System.out.println("Data Loading (OK)");
        System.out.println("Number of nodes inserted: " + this.nodes_counter);
        System.out.println("Number of edges inserted: " + this.edges_counter);
        System.out.println("Total time: " + this.data_time + " us");

        if (!this.closeTransaction()) {
            System.out.println("Closing transaction (FAILED)");
            return false;
        }
        System.out.println("Closing transaction (OK)");

        if (!this.closeDB()) {
            System.out.println("Closing database (FAILED)");
            return false;
        }
        System.out.println("Closing database (OK)");

        if (this.makeDataLoadingReport()) {
            System.out.println("Making report (OK)");
        } else {
            System.out.println("Making report (FAILED)");
        }
        System.out.println("The report is available in " + this.data_loading_report_filename);

        return true;
    }

    public boolean runQueryTest() {
        return this.runQueryTest(instances_for_query_default, "s");
    }

    public boolean runQueryTest(int instances_for_query, String querymix_type) {
        return this.runQueryTestByQuery(0, instances_for_query, querymix_type);
    }

    public boolean runQueryTestByQuery(int query_number) {
        return this.runQueryTestByQuery(query_number, instances_for_query_default, "s");
    }

    public boolean runQueryTestByQuery(int query_number, int instances_for_query, String querymix_type) {
        System.out.println("--------------------------------");
        System.out.println("----- QUERY EXECUTION TEST -----");
        System.out.println("--------------------------------");
        queries_manager = new QueriesManager();
        if (!queries_manager.loadTestData(this.testdata_filename)) {
            return false;
        }
        
        if (!this.openDB("dbtest")) {
            System.out.println("Opening database (FAILED)");
            return false;
        }
        System.out.println("Opening database (OK)");

        if (!this.openTransaction()) {
            System.out.println("Opening transaction (FAILED)");
            return false;
        }
        System.out.println("Opening transaction (OK)");

        Iterator<QueryInstance> it;
        if (query_number == 0) {
            it = queries_manager.makeQueryMix(instances_for_query, querymix_type);
        } else {
            it = queries_manager.makeQueryMixByQuery(query_number, instances_for_query, querymix_type);
        }
        
        System.out.println("Executing warmup ... ");
        this.runQueryMix(it);
        
        System.out.println("\n");
        System.out.println("Executing query mix ...");
        it = queries_manager.getQueryMixIterator();
        long ctime = System.currentTimeMillis();
        this.runQueryMix(it);
        query_time = System.currentTimeMillis() - ctime;

        System.out.println("\n");
        if (!this.closeTransaction()) {
            System.out.println("Closing transaction (FAILED)");
            return false;
        }
        System.out.println("Closing transaction (OK)");

        if (!this.closeDB()) {
            System.out.println("Closing database (FAILED)");
            return false;
        }
        System.out.println("Closing database (OK)");

        if (this.makeQueryExecReport()) {
            System.out.println("Making report (OK)");
        } else {
            System.out.println("Making report (FAILED)");
        }
        System.out.println("The report is available in " + this.query_exec_report_filename);

        return true;

    }

    private boolean runGeneralQueryTest(Iterator<QueryInstance> it) {
        System.out.println("Number of query instances to execute:" + queries_manager.getQueryMixSize());
        return true;
    }

    private void runQueryMix(Iterator<QueryInstance> it) {
        QueryInstance instance;

        System.out.print("Executing query instance 0");
        String newline;
        Integer counter = 0;

        while (it.hasNext()) {
            newline = "";
            for (int i = 0; i < counter.toString().length(); i++) {
                newline = newline + "\b";
            }
            counter++;
            newline = newline + counter;
            System.out.print(newline);
            instance = it.next();
            if (instance.getType().compareTo("Q1") == 0) {
                RunQueryTest1(instance);
            }
            if (instance.getType().compareTo("Q2") == 0) {
                RunQueryTest2(instance);
            }
            if (instance.getType().compareTo("Q3") == 0) {
                RunQueryTest3(instance);
            }
            if (instance.getType().compareTo("Q4") == 0) {
                RunQueryTest4(instance);
            }
            if (instance.getType().compareTo("Q5") == 0) {
                RunQueryTest5(instance);
            }
            if (instance.getType().compareTo("Q6") == 0) {
                RunQueryTest6(instance);
            }
            if (instance.getType().compareTo("Q7") == 0) {
                RunQueryTest7(instance);
            }
            if (instance.getType().compareTo("Q8") == 0) {
                RunQueryTest8(instance);
            }
            if (instance.getType().compareTo("Q9") == 0) {
                RunQueryTest9(instance);
            }
            if (instance.getType().compareTo("Q10") == 0) {
                RunQueryTest10(instance);
            }
            if (instance.getType().compareTo("Q11") == 0) {
                RunQueryTest11(instance);
            }
            if (instance.getType().compareTo("Q12") == 0) {
                RunQueryTest12(instance);
            }
        }

    }

    private void RunQueryTest1(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q1(instance.getParameter(1));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest2(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q2(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest3(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q3(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest4(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        String result = this.Q4(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest5(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q5(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest6(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q6(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest7(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q7(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest8(QueryInstance instance) {
        long source_node = Long.parseLong(instance.getParameter(1));
        long target_node = Long.parseLong(instance.getParameter(2));
        long ctime = System.nanoTime()/1000;
        boolean result = this.Q8(source_node, target_node);
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest9(QueryInstance instance) {
        long source_node = Long.parseLong(instance.getParameter(1));
        long target_node = Long.parseLong(instance.getParameter(2));
        long ctime = System.nanoTime()/1000;
        long result = this.Q9(source_node, target_node);
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest10(QueryInstance instance) {
        long source_node = Long.parseLong(instance.getParameter(1));
        long target_node = Long.parseLong(instance.getParameter(2));
        long ctime = System.nanoTime()/1000;
        long result = this.Q10(source_node, target_node);
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest11(QueryInstance instance) {
        long source_node = Long.parseLong(instance.getParameter(1));
        long target_node = Long.parseLong(instance.getParameter(2));
        long ctime = System.nanoTime()/1000;
        long result = this.Q11(source_node, target_node);
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private void RunQueryTest12(QueryInstance instance) {
        long ctime = System.nanoTime()/1000;
        long result = this.Q12(Long.parseLong(instance.getParameter(1)));
        long etime = System.nanoTime()/1000 - ctime;
        instance.setOutput(String.valueOf(result));
        instance.setExecutionTime(etime);
    }

    private boolean makeDataLoadingReport() {
        String text = "GDBenchmark - Data Loading Report\n"
                + "Test data info: \n"
                + "- Person nodes read: " + gdreader.getPeopleCounter() + "\n"
                + "- Webpage node read: " + gdreader.getWebpagesCounter() + "\n"
                + "- Friend edges read: " + gdreader.getFriendsCounter() + "\n"
                + "- Like edges read: " + gdreader.getLikesCounter() + "\n"
                + "- Total nodes read: " + gdreader.getNodesCounter() + "\n"
                + "- Total edges read: " + gdreader.getEdgesCounter() + "\n"
                + "Data loading info: \n"
                + "- Number of nodes inserted: " + this.nodes_counter + "\n"
                + "- Number of edges inserted: " + this.edges_counter + "\n"
                + "- Total time: " + this.data_time + " us";

        try {
            FileWriter file = new FileWriter(this.data_loading_report_filename);
            PrintWriter writer = new PrintWriter(file);
            writer.println(text);
            writer.close();
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ":" + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean makeQueryExecReport() {
        QueryInstance instance;
        Iterator<QueryInstance> qiit;
        String html_text;
        String csv_line;

        try {
            FileWriter html_file = new FileWriter(this.query_exec_report_filename);
            PrintWriter html_writer = new PrintWriter(html_file);

            FileWriter csv_file1 = new FileWriter("report-general.csv");
            PrintWriter csv_writer1 = new PrintWriter(csv_file1);


            FileWriter csv_file2 = new FileWriter("report-detail.csv");
            PrintWriter csv_writer2 = new PrintWriter(csv_file2);


            csv_writer1.println("Query;Instances;TotalTime(us);MaxTime(us);MinTime(us);AvgTime(us)");
            csv_writer2.println("Instance;Type;Input1;Input2;Output;ExecTime(us)");

            html_text =
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                    + "<center>"
                    + "<h1>GDBenchmark - Query Execution Report</h1>"
                    + "</center>"
                    + ""
                    + "<p>List of queries:</p>"
                    + "<ul>"
                    + "<li><B>Q1:</B> Get people having name N </li>"
                    + "<li><B>Q2:</B> Get people that likes a given Web page W </li>"
                    + "<li><B>Q3:</B> Get the Web pages that person P likes </li>"
                    + "<li><B>Q4:</B> Get the name of the person with a given ID </li>"
                    + "<li><B>Q5:</B> Get the friends of the friends of a given person P </li>"
                    + "<li><B>Q6:</B> Get the Web pages liked by the friends of a given person P </li>"
                    + "<li><B>Q7:</B> Get people that likes a Web page which a person P likes </li>"
                    + "<li><B>Q8:</B> Is there a connection (path) between people P1 and P2? </li>"
                    + "<li><B>Q9:</B> Get the shortest path between people P1 and P2 </li>"
                    + "<li><B>Q10:</B> Get the common friends between people P1 and P2</li>"
                    + "<li><B>Q11:</B> Get the common Web pages that people P1 and P2 like</li>"
                    + "<li><B>Q12:</B> Get the number of friends of a person P </li>	"
                    + "</ul>"
                    + "<p>Execution of query mix:</p>"
                    + "<CENTER>"
                    + "<p>Total time (ms): " + this.query_time + "</p>"
                    + "<table border=\"1\" cellpadding=\"2\" cellspacing=\"2\">"
                    + "<tbody>"
                    + "<tr>"
                    + "<td style=\"vertical-align: top;\"><span style=\"font-weight: bold;\">Query</span><br></td>"
                    + "<td style=\"vertical-align: top; text-align: right;\"><span style=\"font-weight: bold;\">Instances</span><br></td>"
                    + "<td style=\"vertical-align: top; text-align: right; font-weight: bold;\">Total Time (us)<br></td>"
                    + "<td style=\"vertical-align: top; text-align: right; font-weight: bold;\">Max Time (us)<br></td>"
                    + "<td style=\"vertical-align: top; text-align: right; font-weight: bold;\">Min Time (us)<br></td>"
                    + "<td style=\"vertical-align: top; text-align: right; font-weight: bold;\">Avg Time (us)<br></td>"
                    + "</tr>";

            html_writer.println(html_text);

            Query query;
            String qtype;
            for (int i = 1; i <= 12; i++) {
                qtype = "Q" + i;
                query = queries_manager.getQueriesByType(qtype);
                if (query.CountInstances() != 0) {

                    //write to hmtl file
                    html_text = "<tr>"
                            + "<td style=\"font-weight: bold;\">" + query.getType() + "<br></td>"
                            + "<td style=\"text-align: right;\">" + query.CountInstances() + "<br></td>"
                            + "<td style=\"text-align: right;\">" + query.getTotalExecTime() + "<br></td>"
                            + "<td style=\"text-align: right;\">" + query.getMaxExecTime() + "<br></td>"
                            + "<td style=\"text-align: right;\">" + query.getMinExecTime() + "<br></td>"
                            + "<td style=\"text-align: right;\">" + query.getAvgExecTime() + "<br></td>"
                            + "</tr>";
                    html_writer.println(html_text);

                    //write to csv file
                    csv_line = query.getType()
                            + ";" + query.CountInstances()
                            + ";" + query.getTotalExecTime()
                            + ";" + query.getMaxExecTime()
                            + ";" + query.getMinExecTime()
                            + ";" + query.getAvgExecTime();
                    csv_writer1.println(csv_line);
                }
            }
            html_writer.println("</tbody> </table> </CENTER> <br><br>");

            //WRITE DETAILS OF QUERY EXECUTION TEST

            html_text = "<p>Query mix:</p><br>"
                    + "</ul>"
                    + "<CENTER><table border='1'>"
                    + "<br><br>"
                    + "<tbody>"
                    + "<tr>"
                    + "<td style=\"vertical-align: top;\"><span style=\"font-weight: bold;\">Instance</span><br></td>"
                    + "<td style=\"vertical-align: top;\"><span style=\"font-weight: bold;\">Type</span><br></td>"
                    + "<td style=\"vertical-align: top;\"><span style=\"font-weight: bold;\">Input1</span><br></td>"
                    + "<td style=\"vertical-align: top;\"><span style=\"font-weight: bold;\">Input2</span><br></td>"
                    + "<td style=\"vertical-align: top; font-weight: bold;\">Output<br></td>"
                    + "<td style=\"vertical-align: top; font-weight: bold;\">ExecTime(us)<br></td>"
                    + "</tr>";
            html_writer.println(html_text);

            int j = 1;
            qiit = queries_manager.getQueryMixIterator();
            while (qiit.hasNext()) {
                instance = qiit.next();

                //write to html file
                html_text = "<tr>";
                html_text += "<td>" + j + "</td>";
                html_text += "<td>" + instance.getType() + "</td>";
                html_text += "<td>" + instance.getParameter(1) + "</td>";
                if (instance.getParameter(2) != null) {
                    html_text += "<td>" + instance.getParameter(2) + "</td>";
                } else {
                    html_text += "<td>-</td>";
                }
                html_text += "<td>" + instance.getOutput() + "</td>";
                html_text += "<td>" + instance.getExecutionTime() + "</td>";
                html_text += "</tr>";
                html_writer.println(html_text);

                //write to CSV file
                if (instance.getParameter(2) == null) {
                    csv_line = j + ";" + instance.getType() + ";" + instance.getParameter(1) + ";-;" + instance.getOutput() + ";" + instance.getExecutionTime();
                } else {
                    csv_line = j + ";" + instance.getType() + ";" + instance.getParameter(1) + ";" + instance.getParameter(2) + ";" + instance.getOutput() + ";" + instance.getExecutionTime();
                }
                csv_writer2.println(csv_line);

                j++;
            }

            html_writer.println("</tbody></table>");
            html_writer.close();
            csv_writer1.close();
            csv_writer2.close();
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ":" + e.getMessage());
            return false;
        }
        return true;
    }
}
