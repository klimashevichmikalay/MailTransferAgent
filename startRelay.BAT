@echo off
cd  C:\Users\Garant\Documents\NetBeansProjects\MailTransferAgentSMTP
start mvn cobertura:cobertura
start mvn compile exec:java -Dexec.mainClass="smtp.mail.server.main" -Dexec.args="587 127.0.0.1 465"
start mvn compile exec:java -Dexec.mainClass="smtp.mail.server.main" -Dexec.args="25 127.0.0.1 587"


