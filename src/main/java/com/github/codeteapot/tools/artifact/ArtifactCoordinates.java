package com.github.codeteapot.tools.artifact;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Coordinates used to identify an artifact.
 */
public class ArtifactCoordinates implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String groupId;
  private final String artifactId;
  private final String version;

  /**
   * Coordinates with given group identifier and artifact identifier.
   *
   * @param groupId Group identifier.
   * @param artifactId Artifact identifier.
   */
  @ConstructorProperties({
          "groupId",
          "artifactId"
  })
  public ArtifactCoordinates(String groupId, String artifactId) {
    this.groupId = requireNonNull(groupId);
    this.artifactId = requireNonNull(artifactId);
    this.version = null;
  }

  /**
   * Coordinates with given group identifier, artifact identifier and version.
   *
   * @param groupId Group identifier.
   * @param artifactId Artifact identifier.
   * @param version Version.
   */
  @ConstructorProperties({
      "groupId",
      "artifactId",
      "version"
  })
  public ArtifactCoordinates(String groupId, String artifactId, String version) {
    this.groupId = requireNonNull(groupId);
    this.artifactId = requireNonNull(artifactId);
    this.version = requireNonNull(version);
  }

  /**
   * Group identifier.
   *
   * @return The group identifier.
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Artifact identifier.
   *
   * @return The artifact identifier.
   */
  public String getArtifactId() {
    return artifactId;
  }

  /**
   * Version.
   *
   * @return The version.
   */
  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return format("%s:%s:%s", groupId, artifactId, version);
  }

  /**
   * Based on artifact identifier hash.
   */
  @Override
  public int hashCode() {
    return artifactId.hashCode();
  }

  /**
   * Based on compounded equality of group, artifact identifier and version.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ArtifactCoordinates) {
      ArtifactCoordinates coordinates = (ArtifactCoordinates) obj;
      return groupId.equals(coordinates.groupId)
          && artifactId.equals(coordinates.artifactId)
          && Objects.equals(version, coordinates.version);
    }
    return false;
  }

  String getMetadataPath() {
    return concat(
            Stream.of(groupId.split("\\.")),
            Stream.of(artifactId, "maven-metadata.xml"))
            .collect(joining("/"));
  }

  String getPath(String extension) {
    return concat(
        Stream.of(groupId.split("\\.")),
        Stream.of(artifactId, version, format("%s-%s.%s", artifactId, version, extension)))
            .collect(joining("/"));
  }
}
