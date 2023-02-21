package com.github.codeteapot.tools.artifact;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
    namespace = "http://maven.apache.org/POM/4.0.0",
    name = "project")
class XMLProject {

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "parent")
  private XMLParent parent;

  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "packaging")
  private String packaging;

  @XmlElementWrapper(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "dependencies")
  @XmlElement(
      namespace = "http://maven.apache.org/POM/4.0.0",
      name = "dependency")
  private List<XMLDependency> dependencies;

  private XMLProject() {
    packaging = null;
    dependencies = null;
  }

  String getExtension(Function<String, String> mapper) {
    return mapper.apply(packaging);
  }

  Set<ArtifactCoordinates> getDependencies() {
    return ofNullable(dependencies)
        .map(List::stream)
        .orElseGet(Stream::empty)
        .map(this::toRequiredDependency)
        .filter(Objects::nonNull)
        .collect(toSet());
  }

  private ArtifactCoordinates toRequiredDependency(XMLDependency dependency) {
    return dependency.toRequiredDependency();
  }
}
