package com.github.codeteapot.tools.artifact;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

/**
 * Artifact repository.
 */
public class ArtifactRepository {

    private static final Map<String, String> EXTENSION_MAP = Stream.of(
            new SimpleEntry<>("jar", "jar"),
            new SimpleEntry<>("ejb", "jar"),
            new SimpleEntry<>("ejb-client", "jar"),
            new SimpleEntry<>("war", "war"),
            new SimpleEntry<>("ear", "ear"),
            new SimpleEntry<>("pom", "pom")).collect(toMap(Entry::getKey, Entry::getValue));

    private static final String DEFAULT_EXTENSION = "jar";

    private final URL directory;

    /**
     * Repository at given directory URL.
     *
     * @param directory Directory URL.
     */
    public ArtifactRepository(URL directory) {
        this.directory = requireNonNull(directory);
    }

    /**
     * Get an artifact through this repository.
     *
     * @param coordinates Artifact coordinates.
     * @return The artifact.
     * @throws ArtifactRepositoryException When some repository error has been occurred.
     * @throws IOException                 When an I/O error has been occurred.
     */
    public Artifact get(ArtifactCoordinates coordinates) throws ArtifactRepositoryException, IOException {
        coordinates = resolve(coordinates);
        XMLProject project = unmarshalXml(coordinates.getPath("pom"), XMLProject.class);
        try {
            return new Artifact(
                    file(coordinates.getPath(project.getExtension(this::fromPackaging))),
                    project.getDependencies());
        }
        catch (MalformedURLException e) {
            throw new ArtifactRepositoryException(e);
        }
    }

    public ArtifactCoordinates resolve(ArtifactCoordinates coordinates) throws IOException, ArtifactRepositoryException {
        if(coordinates.getVersion() == null || coordinates.getVersion().trim().isEmpty())
        {
            XMLMetadata metadata = unmarshalXml(coordinates.getMetadataPath(), XMLMetadata.class);
            coordinates = metadata.getVersioning().getLatest(metadata);
        }
        return coordinates;
    }

    /**
     * Get all versions
     *
     * @param coordinates Artifact coordinates.
     * @return All the artifact coordinates.
     * @throws ArtifactRepositoryException When some repository error has been occurred.
     * @throws IOException                 When an I/O error has been occurred.
     */
    public Set<ArtifactCoordinates> getAllVersions(ArtifactCoordinates coordinates) throws ArtifactRepositoryException, IOException {
        XMLMetadata metadata = unmarshalXml(coordinates.getMetadataPath(), XMLMetadata.class);
        return metadata.getVersioning().getVersions(metadata);
    }

    private <T> T unmarshalXml(String path, Class<T> tClass) throws ArtifactRepositoryException, IOException {
        try (InputStream input = file(path).openStream()) {
            //Prepare JAXB objects
            JAXBContext jc = JAXBContext.newInstance(tClass);
            Unmarshaller u = jc.createUnmarshaller();

            //Create an XMLReader to use with our filter
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

            //Create the filter (to add namespace) and set the xmlReader as its parent.
            String namespace = tClass.getAnnotation(XmlRootElement.class).namespace();
            if(Objects.equals(namespace, "##default")) namespace = null;
            NamespaceFilter inFilter = new NamespaceFilter(namespace, namespace != null);
            inFilter.setParent(reader);

            //Prepare the input
            InputSource is = new InputSource(input);

            //Create a SAXSource specifying the filter
            SAXSource source = new SAXSource(inFilter, is);

            //Do unmarshalling
            return (T)u.unmarshal(source);
        } catch (JAXBException | MalformedURLException | ParserConfigurationException | SAXException e) {
            throw new ArtifactRepositoryException(e);
        } catch (UncheckedArtifactRepositoryException e) {
            throw e.getCause();
        }
    }

    /**
     * Hash based on {@code directory}.
     */
    @Override
    public int hashCode() {
        return directory.hashCode();
    }

    /**
     * Equality based on {@code directory}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ArtifactRepository) {
            ArtifactRepository repository = (ArtifactRepository) obj;
            return directory.equals(repository.directory);
        }
        return false;
    }

    private String fromPackaging(String packaging) {
        return ofNullable(packaging).map(EXTENSION_MAP::get).orElse(DEFAULT_EXTENSION);
    }

    private URL file(String relativePath) throws MalformedURLException {
        return new URL(directory, relativePath);
    }
}
