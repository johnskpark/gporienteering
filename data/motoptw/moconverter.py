import os
import re
import sys
import subprocess
import random

if len(sys.argv) < 2:
    print("Insufficient number of parameters. Need the data file and the desired output file")
    sys.exit()

rule_file = sys.argv[1]
output_file = sys.argv[2]


print("Rule directory: " + rule_file + ", output file: " + output_file)

try:
    os.remove(output_file)
except OSError:
    pass

input_f = open(rule_file, "r")

# Dump the entire file into a string
string_dump = []
for line in input_f:
    string_dump.append(re.sub("\n", "", line))

input_f.close()

split_results = []
scores = []
for i in range(2, len(string_dump)):
    line = string_dump[i].rstrip()
    # if line is white space only, then ignore
    if len(line)==0:
        continue
    split = line.split(" ")
    split_results.append(split)
    scores.append(split[5])
    
shuf_scores = scores[1:]

random.seed(10)
random.shuffle(shuf_scores)

shuf_scores.insert(0, scores[0])

for i in range(0, len(shuf_scores)):
    split_results[i].insert(6, shuf_scores[i])

output_f = open(output_file, "w")

output_f.write(string_dump[0] + " 2\n")
output_f.write(string_dump[1] + "\n")
for i in range(0, len(split_results)):
    output_f.write(" ".join(split_results[i]) + "\n")

output_f.close()



