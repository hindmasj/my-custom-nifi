# My Custom NiFi

Looking at some techniques for processing JSON within NiFi. See [my-nifi-cluster](../my-nifi-cluster) for the docker based cluster to run these on.

# Creation
Used the Maven archetypes feature to generate the basis layout and sample code. I am still not sure about all the names I used and ended up doing a lot of editing afterwards so that the artifact IDs ended up looking like I wanted.

## Generation

The "generate" command.

```
mvn archetype:generate
```

Select the NiFi processor archetype and the latest version of NiFi.

```
Choose archetype:
1: remote -> org.apache.nifi:nifi-processor-bundle-archetype (-)
2: remote -> org.apache.nifi:nifi-service-bundle-archetype (-)
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): : 1
Choose org.apache.nifi:nifi-processor-bundle-archetype version:
1: 0.0.2-incubating
...
49: 1.15.3
Choose a number: 49: 49
```

Make some initial definitions. I think I should have chosen "my-custom-nifi" for the base name and the artefact ID. After this I also had to edit the package name and the lower reaches of the directory tree to eliminate the '-' character from the name (illegal character) and reverse the names "nifi" and "processors" in the order.

```
Define value for property 'artifactBaseName': my-nifi
Define value for property 'nifiVersion' 1.15.3: :
Define value for property 'groupId': io.github.hindmasj
Define value for property 'artifactId': my-nifi-processors
Define value for property 'version' 1.0-SNAPSHOT: : 0.0.1-SNAPSHOT
Define value for property 'package' io.github.hindmasj.processors.my-nifi: :
```

## Disable Enforcer Plugin

The enforcer plugin needs to be disabled as the NiFi archetype does not allow snapshot versions. Add this to the archiver POM, *nar/pom.xml*.

``` xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-enforcer-plugin</artifactId>
      <configuration>
        <skip>true</skip>
      </configuration>
    </plugin>
  </plugins>
</build>
```
