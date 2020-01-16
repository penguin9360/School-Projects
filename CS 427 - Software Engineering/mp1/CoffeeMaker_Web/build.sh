#!/bin/sh
mkdir target
cd target
mkdir CoffeeMaker_Web
cd CoffeeMaker_Web
mkdir WEB-INF
cd WEB-INF
mkdir classes
cd classes
# go back to CoffeeMaker_Web
cd /home/pzheng5/cs427fa19/mp1/CoffeeMaker_Web
# go to src/main/java
cd src/main/java
# compile java files
find -name "*.java" | xargs javac
# copy to folder
cp $(find -name "*.class") /home/pzheng5/cs427fa19/mp1/CoffeeMaker_Web/target/CoffeeMaker_Web/WEB-INF/classes/
cd /home/pzheng5/cs427fa19/mp1/CoffeeMaker_Web/target/CoffeeMaker_Web/WEB-INF/classes/
mkdir exceptions
mv $(find -name "*Exception*.class") exceptions
mkdir coffeemaker
mv *.class coffeemaker
mv exceptions coffeemaker
mkdir csc326
mv coffeemaker csc326
mkdir ncsu
mv csc326 ncsu
mkdir edu
mv ncsu edu
cd /home/pzheng5/cs427fa19/mp1/CoffeeMaker_Web
cp src/main/webapp/*.jsp target/CoffeeMaker_Web
cp src/main/webapp/WEB-INF/web.xml target/CoffeeMaker_Web/WEB-INF
cd target/CoffeeMaker_Web
jar -cvf ../CoffeeMaker_Web.war *
