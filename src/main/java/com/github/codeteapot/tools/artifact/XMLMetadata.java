package com.github.codeteapot.tools.artifact;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metadata")
class XMLMetadata {

  @XmlElement(name = "groupId")
  private String groupId;

  @XmlElement(name = "artifactId")
  private String artifactId;

  @XmlElement(name = "versioning")
  private XMLVersioning versioning;

  public String getGroupId() { return groupId; }
  public String getArtifactId() { return artifactId; }
  public XMLVersioning getVersioning() { return versioning; }
}
