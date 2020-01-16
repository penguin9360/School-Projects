#!/bin/bash
java -cp "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)":target/classes:target/test-classes edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator
