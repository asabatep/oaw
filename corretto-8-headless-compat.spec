Name:      corretto-8-headless-compat
Version:   0.1.0
Release:   1
Epoch:     1
Summary:   Virtual provides shim mapping java-1.8.0-headless/-devel and the Maven JDK binding onto Corretto 8
License:   MIT
BuildArch: noarch

Requires:  java-1.8.0-amazon-corretto-devel
Provides:  java-1.8.0-headless
Provides:  java-headless
Provides:  jre-1.8.0-headless
Provides:  java-1.8.0-devel
Provides:  java-devel
Provides:  maven-jdk-binding

%description
Paquet de pega que només serveix per fer creure a dnf que el Java d'Amazon 
és equivalent a OpenJDK.

%files

%changelog
