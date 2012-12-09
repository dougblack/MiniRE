#!/bin/bash
set -v
javac tokenizer/*.java interpreter/*.java
java interpreter/Main

