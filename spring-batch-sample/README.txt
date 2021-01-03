------ SPRING BATCH SAMPLE -----

Spring batch follows the traditional batch architecture where a job repository does the work
of scheduling and interacting with the job.

A job can have more than one steps – and every step typically follows the sequence of reading data,
processing it and writing it.

And of course the framework will do most of the heavy lifting for us here – especially when it
comes to the low level persistence work of dealing with the jobs
– using sqlite for the job repository.

Simple use-case: migrate some financial transaction data from CSV to XML.
The input file has a very simple structure – it contains a transaction per line, made up of:
a username, the user id, the date of the transaction and the amount:

username, userid, transaction_date, transaction_amount
devendra, 1234, 31/10/2015, 10000
john, 2134, 3/12/2015, 12321
robin, 2134, 2/02/2015, 23411

 - SampleApp:          Single-threaded, single process job execution.
 - SampleParallelApp:  Parallel Processing using a multi-threaded implementation: a single Job and Step partitioning.

For testing the Skip Logic of Spring Batch Job, we created a file input/recordWithInvalidData containing some wrong lines
in the input/record.csv file (username is missing, or the transaction_amount is negative)

  - SampleSkippingApp:   starts two jobs implementing the skip logic
        - the first one defines explicitly the skip rules in the step building
        - the second one uses a custom step policy in the step building