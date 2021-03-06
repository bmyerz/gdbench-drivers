import java.lang.*;
import java.lang.Exception;
import java.lang.InstantiationError;
import java.lang.InstantiationException;
import java.lang.RuntimeException;

/**
 *
 */
public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Benchmarking options:");
            System.out.println("-D      Run loading data test");
            System.out.println("-Q      Run execution test for all queries");
            System.out.println("-q #    Run query execution test for query type # (1<=#<=12).");
            System.out.println("-i #    Defines the number # of instances for query (default 100)");
            System.out.println("-m s|r  Defines the type of query mix (sequential | random");
            System.out.println("-t <name> TestDriver class");
            return;
        }

        boolean D = false;
        boolean Q = false;
        int q = 0;
        int i = 100;
        String m = "s";
        String clazz = "MyTestDriver";
        try {
            int k = 0;
            while (k < args.length) {
                if (args[k].equals("-D")) {
                    D = true;
                } else if (args[k].equals("-Q")) {
                    Q = true;
                } else if (args[k].equals("-q")) {
                    k++;
                    q = Integer.parseInt(args[k]);
                    if (q < 1 || q > 12) {
                        System.out.println("Error in parameter -q");
                        return;
                    }
                } else if (args[k].equals("-i")) {
                    k++;
                    i = Integer.parseInt(args[k]);
                    if (i < 0) {
                        System.out.println("Error in parameter -i");
                        return;
                    }
                } else if (args[k].equals("-m")) {
                    k++;
                    m = args[k];
                    if (m.compareTo("s") != 0 && m.compareTo("r") != 0) {
                        System.out.println("Error in parameter -m");
                        return;
                    }
                } else if (args[k].equals("-t")) {
                    k++;
                    clazz = args[k];
                } else {
                    System.out.println("Error: Invalid parameters");
                    return;
                }
                k++;
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid parameters");
            return;
        }

        // instantiate
      TestDriver testdriver;
      try {
        testdriver = (TestDriver) Class.forName(clazz).newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      if (D) {
            testdriver.runDataLoading();
        }
        if (Q) {
            testdriver.runQueryTest(i, m);
        }
        if (q != 0) {
            testdriver.runQueryTestByQuery(q, i, m);
        }
    }
}
