// Parallel Computing

/*

Importance: 

1. MOORE'S LAW. 
   We can no longer use Moore's law to speed up sequential programming. 
   Moore's law: Every 2 years, we can make computers twice as fast. 
   Unique in the computer industry. 
   Now: too much power / too much energy to improve clock speed. 
        instead. we use Moore's law to double the number of processors 
	roughly every two years. 
	therefore, we need to write parallel code to improve performance. 
   This is the case for a single machine.

2. BIG DATA. 
   With so much data, we are now programming across clusters of machines or
   data centers. (Too much memory for one single machine) Therefore, we 
   need to figure out how to parallel process across multiple machines. 

*/

// The dream: automatic parallelization. 

/*

Today: exists for "embarrassingly parallel" operations. 
       includes: map, reduce, filter, sort. 

Big Data: Google's MapReduce, 
          Madoop, 
	  Spark, 
	  ...

Java Streams provides similar capability for a single machine. 

*/

import java.util.Arrays;
import java.util.concurrent.*;

class Sum {
    public static void main(String[] args) {
	int size = Integer.parseInt(args[0]);
        int[] arr = new int[size];

	for(int i = 0; i < size; i++) {
	    arr[i] = i;
	}

	int sum = Arrays.stream(arr)
	    .reduce(0, (i1, i2) -> i1 + i2); // reduce is like fold. 
	System.out.println(sum);
    }
}
   
class Sort {
    public static void main(String[] args) {
	int size = Integer.parseInt(args[0]);
	double[] arr = new double[size];

        for(int i = 0; i < size; i++) {
            arr[i] = Math.random() * size;
	}

        double[] out = Arrays.stream(arr)
	    .parallel() // parallizes the stream. does have benefits. 
	    .sorted()
	    .toArray();

	for(int i = 0; i < 10; i++) 
	    System.out.println(out[i]);
    }
}

/* 

PARALLEL PROGRAMMING. 

sequential programming: one thing happens at a time. 

what can you do with multiple processors? 
1. run multiple totally different programs at the same time. 
   not that great of throughput. 

2. actual parallel programming. 

parallelism: use extra resources to solve a proglem faster. 
concurrency: effectively manage access to shared resources. 

actually, they are kind of the opposite. 
connections: common to use threads for both. 
             if parallel computations need access to shared resources, 
	     then concurrency is needed. 

Sequential: 
a running program has
 - one call stack (with each stack frame holding local variables)
 - one program counter. 
 - objects in the heap. 

Parallel: 
 - a set of threads, each with its own call stack and program counter. 
   no access to another thread's local variables. 
 - shared heap. 
   objects can be shared, but most are not. 

Requirements: 
1. ways to create and run multiple things at once: threads.
2. ways to synchronize the threads. 

*/

/*

FORK-JOIN PARALLELISM

Run each thread in parallel (called a fork)
Wait for threads to finish (called a join). 

Key invariant must be maintained by the programmer: 
  - no race conditions. 

Problems with naive idea (creating large chunks): 
1. Possibly unbalanced. 

2. Want to use (only) processors "available to you now" 
   We are unable to determine the amount of processors statically. 

A Better Approach. 
create small chunks. 

Hand out chunks of data with processor availability.

Even better: 

Build a tree. (divide-and-conquer, recursion)
Recursively split the task into smaller tasks until tasks are small enough
to run sequentially. 
This is logarithmic, instead of linear. 

We will write parallel algorithms in this style. 

*/

// IN JAVA. 
// ForkJoinTask<V> supports a fork() operation. 

/* 
class ForkJoinTask<V> {

    void fork() {
        // create a new thread;
        // invoke this.compute() in the new thread. 
    }

    V join() {
        // wait for the new thread to finish. 
	// return its result. 
    }

}

*/

class SumTask extends RecursiveTask<Long> {
    private final int SEQUENTIAL_CUTOFF = 10000;

    private int[] arr;
    private int low, hi;

    public SumTask(int[] a, int low, int hi) {
	this.arr = a;
	this.low = low;
	this.hi = hi;
    }
    
    public Long compute() {
	if ( hi - low > SEQUENTIAL_CUTOFF ) {
	    int mid = (low + hi) / 2;
	    SumTask left = new SumTask(arr, low, mid);
	    SumTask right = new SumTask(arr, mid, hi);
	    left.fork();
	    //right.fork();
	    //long l1 = left.join();
	    //long l2 = right.join();
	    long l2 = right.compute();
	    long l1 = left.join();
	    return l1 + l2;
	} else {
	    long sum = 0; 
	    for (int i = low; i < hi; i++)
		sum += arr[i];
	    return sum;
	}
    }
}

class SumFJ {
    public static void main(String[] args) {
	int size = Integer.parseInt(args[0]);
	int[] arr = new int[size];
	for(int i = 0; i < size; i++)
	    arr[i] = i;
	
	long sum = new SumTask(arr, 0, size).compute();

	System.out.println(sum);
    }
}
