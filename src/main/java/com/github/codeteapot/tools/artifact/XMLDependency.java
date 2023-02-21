package com.github.codeteapot.tools.artifact;

import javax.xml.bind.annotation.XmlElement;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

class XMLDependency {

    private static final Set<String> REQUIRED_SCOPES = Stream.of("compile", "runtime")
            .collect(toSet());

    @XmlElement(
            namespace = "http://maven.apache.org/POM/4.0.0",
            name = "groupId")
    private String groupId;

    @XmlElement(
            namespace = "http://maven.apache.org/POM/4.0.0",
            name = "artifactId")
    private String artifactId;

    @XmlElement(
            namespace = "http://maven.apache.org/POM/4.0.0",
            name = "version")
    private String version;

    @XmlElement(
            namespace = "http://maven.apache.org/POM/4.0.0",
            name = "scope")
    private String scope;

    private XMLDependency() {
        groupId = null;
        artifactId = null;
        version = null;
    }

    ArtifactCoordinates toRequiredDependency() {
        if (scope == null || REQUIRED_SCOPES.contains(scope)) {
            return version == null ? new ArtifactCoordinates(groupId, artifactId) : new ArtifactCoordinates(groupId, artifactId, version);
        }
        return null;
    }
}
