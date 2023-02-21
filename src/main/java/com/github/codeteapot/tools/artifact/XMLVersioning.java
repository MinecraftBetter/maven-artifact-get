package com.github.codeteapot.tools.artifact;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@XmlRootElement(name = "versioning")
class XMLVersioning {

    @XmlElement(name = "latest")
    private String latest;

    @XmlElement(name = "release")
    private String release;

    @XmlElement(name = "lastUpdated")
    private long lastUpdated;

    @XmlElementWrapper(name = "versions")
    @XmlElement(name = "version")
    private List<String> versions;


    public ArtifactCoordinates getLatest(XMLMetadata metadata) {return toVersionCoordinates(metadata, latest);}

    public ArtifactCoordinates getRelease(XMLMetadata metadata) {return toVersionCoordinates(metadata, release);}

    public Date getLastUpdated() {return new Date(lastUpdated);}

    public Set<ArtifactCoordinates> getVersions(XMLMetadata metadata) {
        return ofNullable(versions)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(v -> toVersionCoordinates(metadata, v))
                .collect(toSet());
    }

    private ArtifactCoordinates toVersionCoordinates(XMLMetadata metadata, String version) {
        return new ArtifactCoordinates(metadata.getGroupId(), metadata.getArtifactId(), version);
    }
}
