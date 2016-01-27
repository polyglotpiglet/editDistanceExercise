
This is just a fun exercise using graphs to compute edit distance between words.

Notes

- Graph is generated every time method called - might want to have dictionary/filename as constructor arg and
prepopulate graph if doing lots of queries.

- I have got two implementations of the constructEdges function. The first was my naive implementation and the time it takes
is proportional to the square of the number of words in the dictionary. This will get pretty slow for large inputs so I
rewrote it (the new algorithm is explained in the method comment) and the time for the second implementation to execute is proportional
to the number of words in the dictionary (ie this impl should be significantly faster.)