==== Chunks VS Tasklets ====

This project shows how to implement the sample batch job using two different approaches: Tasklets VS Chunks.

UseCase: process csv file as the following, where the first element is the person name and the second is the birth date

Mae Hodges,10/22/1972
Gary Potter,02/22/1953
Betty Wise,02/17/1968
Wayne Rose,04/06/1977
Adam Caldwell,09/27/1995
Lucille Phillips,05/14/1992

and generate in output another csv where the first element is still the person name but the second is the person age

Mae Hodges,45
Gary Potter,64
Betty Wise,49
Wayne Rose,40
Adam Caldwell,22
Lucille Phillips,25

 - Tasklets: perform a single defined task within each step (for all the data in the input file)
   The Job for the use case will consist of 3 steps:
    - Read ALL lines from the input CSV file.                       (class LinesReader implements Tasklet)
    - Calculate age for every person in the input CSV file.         (class LinesProcessor implements Tasklet)
    - Write name and age of each person to a new output CSV file.   (class LinesWriter implements Tasklet)

 - Chuncks: performs actions over chunks of data, so instead of reading, processing and writing all the lines at once,
            the job will read, process and write a fixed amount of records (chunk) at a time, until there is no more data in the file.

    The Job for the use case will be as follows:
     - While there are lines:
           - Do for X amount of lines:
              - Read one line
              - Process one line
           - Write X amount of lines.