FROM tomcat:9.0-jre8

COPY start.sh /usr/local/start.sh 
RUN chmod 755 /usr/local/start.sh 

ENTRYPOINT ["/usr/local/start.sh"]