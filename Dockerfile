# creates the Payara image and have ti speak to MySQL

# took this from studybuddy, not sure if it's needed
FROM payara/server-full:6.2025.8-jdk17

#Download the appropriate connector (see pom.xml) and plug it into the correct mounted volume (mysql container)
ADD --chown=payara:payara --chmod=0644 https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar /opt/payara/appserver/glassfish/domains/domain1/lib/mysql-connector-j.jar

