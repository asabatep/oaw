FROM quay.io/rockylinux/rockylinux:9 AS build
ENV LANG=en_US.utf8 \
    LC_ALL=en_US.utf8 \
    JAVA_HOME=/usr/lib/jvm/java \
    JVER=1.8.0
COPY corretto-8-headless-compat.spec /root/
WORKDIR /root
RUN dnf -y install rpm-build glibc-langpack-en && \
    rpmbuild -bb --define "_topdir $PWD/rpmbuild" corretto-8-headless-compat.spec && \
    rpm --import https://yum.corretto.aws/corretto.key && \
    curl -s -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo && \
    dnf -y install --setopt=install_weak_deps=0 java-$JVER-amazon-corretto-devel && \
    rpm -ivh rpmbuild/RPMS/noarch/corretto-8-headless-compat-*.rpm && \
    dnf -y install --setopt=install_weak_deps=0 maven javapackages-tools && \
    dnf -y clean all
COPY . /oaw
RUN cd /oaw/oaw && \
    mvn clean install -P docker -DskipTests

FROM quay.io/rockylinux/rockylinux:9
ENV LANG=en_US.utf8 \
    LC_ALL=en_US.utf8 \
    JAVA_HOME=/usr/lib/jvm/java \
    JVER=1.8.0
COPY --from=build /root/rpmbuild/RPMS/noarch/corretto-8-headless-compat-*.rpm /tmp/corretto-8-headless-compat-*.rpm
RUN rpm --import https://yum.corretto.aws/corretto.key && \
    curl -s -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo && \
    dnf -y install --setopt=install_weak_deps=0 glibc-langpack-en java-$JVER-amazon-corretto-devel && \
    rpm -ivh /tmp/corretto-8-headless-compat-*.rpm && \
    rm -f /tmp/corretto-8-headless-compat-*.rpm && \
    dnf -y install --setopt=install_weak_deps=0 epel-release && \
    dnf -y install --setopt=install_weak_deps=0 tomcat tomcat-native fontconfig && \
    curl -Lo /usr/share/tomcat/lib/commons-pool.jar https://repo1.maven.org/maven2/commons-pool/commons-pool/1.6/commons-pool-1.6.jar && \
    curl -Lo /usr/share/tomcat/lib/commons-dbcp.jar https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.jar && \
    curl -Lo /usr/share/maven-poms/tomcat/commons-pool.pom https://repo1.maven.org/maven2/commons-pool/commons-pool/1.6/commons-pool-1.6.pom && \
    curl -Lo /usr/share/maven-poms/tomcat/commons-dbcp.pom https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.pom && \
    dnf -y clean all
COPY --from=build /oaw/portal/target/oaw.war /var/lib/tomcat/webapps
COPY ./tomcat-entrypoint.sh /tomcat-entrypoint.sh
ENTRYPOINT /tomcat-entrypoint.sh
