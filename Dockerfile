FROM quay.io/rockylinux/rockylinux:9 AS build
RUN dnf -y install maven-openjdk8 && dnf -y clean all
COPY . /oaw
RUN cd /oaw/oaw && \
    mvn clean install -P desarrollo -DskipTests

FROM quay.io/rockylinux/rockylinux:9
RUN dnf -y install --setopt=install_weak_deps=0 tomcat java-1.8.0-openjdk-headless fontconfig && \
    alternatives --set java java-1.8.0-openjdk.x86_64 && \
    curl -JLO https://distrib-coffee.ipsl.jussieu.fr/pub/linux/altlinux/p10/branch/noarch/RPMS.classic/apache-commons-pool-1.6-alt2_22jpp11.noarch.rpm && \
    curl -JLO https://distrib-coffee.ipsl.jussieu.fr/pub/linux/altlinux/p10/branch/noarch/RPMS.classic/apache-commons-dbcp-1.4-alt2_29jpp11.noarch.rpm && \
    rpm -ivh apache-commons-pool*rpm && \
    rpm -ivh apache-commons-dbcp*rpm && \
    ln -s /usr/share/java/commons-dbcp.jar /usr/share/tomcat/lib/ && \
    ln -s /usr/share/java/commons-pool.jar /usr/share/tomcat/lib/ && \
    rm -vf apache*rpm && \
    dnf -y clean all
COPY --from=build /oaw/portal/target/oaw.war /var/lib/tomcat/webapps
COPY ./tomcat-entrypoint.sh /tomcat-entrypoint.sh
ENTRYPOINT /tomcat-entrypoint.sh
