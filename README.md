# Overview
Note that GDBench is meant to be a transactional workload.
It also prioritizes measurement of response time, so I think the benchmark harness does not try to do concurrent queries (i.e. throughput).

# webpage
http://campuscurico.utalca.cl/~rangles/gdbench/

# Build
```sh
cd src
javac *.java
```

# Run
```sh
java -cp src Main -t GrappaDriver -Q
```
