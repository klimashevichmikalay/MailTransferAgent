@echo off
start mvn compile exec:java -Dexec.mainClass="smtp.mail.server.main" -Dexec.args="465"
start mvn compile exec:java -Dexec.mainClass="smtp.mail.server.main" -Dexec.args="587 127.0.0.1 465"
start mvn compile exec:java -Dexec.mainClass="smtp.mail.server.main" -Dexec.args="4200 127.0.0.1 587"
