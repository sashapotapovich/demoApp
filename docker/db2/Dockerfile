FROM ibmcom/db2
ENV LICENSE=accept
ENV DB2INST1_PASSWORD=Password11!
ENV DBNAME=testdb
VOLUME docker/db2/dbdata:/database
EXPOSE 50000


docker run -itd --name mydb2 --privileged=true -p 50000:50000 -e LICENSE=accept -e DB2INST1_PASSWORD=<choose> -e DBNAME=testdb -v <db dir>:/database ibmcom/db2