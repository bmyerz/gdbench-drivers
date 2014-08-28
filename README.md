[GDBench](http://campuscurico.utalca.cl/~rangles/gdbench/) code and additional drivers for the benchmark.

# Build
```sh
cd src
javac *.java
```

# Run
```sh
java -cp src Main -t GrappaDriver -Q
```

# Notes
Note that GDBench is meant to be a transactional workload.
It also prioritizes measurement of response time, so the benchmark harness does not try to do concurrent queries (i.e. throughput).
