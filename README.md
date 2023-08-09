# SimpleSit
A very simple and lightweight Minecraft plugin to allow players to sit

## For users
Drop the [latest release](https://github.com/Sawors/SimpleSit/releases) of the plugin in your server `/plugins` directory.

To use the plugin, the main command is `/sit` which allows you to sit everywhere (*permission: `sit.sit`*).

**You can create in-game seats by putting signs on the side of a stair block.**

For allowed players, you can force other players to sit with the command `/sit <playername> [<other-players>...]` (*permission: sit.force-sit*)
## For developpers
To integrate the plugin in your projects, add this to your project (replace LATEST_PLUGIN_VERSION with the latest released plugin version):

### Installation

#### Maven

*pom.xml*
```xml
<repositories>
  ...
  <repository>
    <id>reposilite-repository-releases</id>
    <name>Reposilite Repository</name>
    <url>https://maven.sawors.net/releases</url>
  </repository>

</repositories>

<dependencies>
  ...
  <dependency>
    <groupId>io.github.sawors</groupId>
    <artifactId>simple-sit</artifactId>
    <version>LATEST_PLUGIN_VERSION</version>
    <scope>provided</scope>
  </dependency>

</dependencies>
```
#### Gradle 
  
*build.gradle*
```gradle
repositories {
  ...
  maven {
    url = uri("https://maven.sawors.net/releases")
  }
}

dependencies {
  ...
  implementation("io.github.sawors:simple-sit:LATEST_PLUGIN_VERSION")
}
```
#### plugin.yml
and add this to your plugin dependencies in your `plugin.yml` :
```yaml
depends: [...SimpleSit]
```
and don't forget to download the [latest release](https://github.com/Sawors/SimpleSit/releases) and add the plugin to your server !

---
### To interact with the api
Now to interact with the plugin, use the `io.github.sawors.simplesit.SittingManager` class.
A javdoc is provided for all the main methods, which are :
```java
public static boolean isPlayerSitting(Player player) {...}

public static boolean isSeatEntity(Entity entity) {...}

public static void sitPlayer(Player player) {...}

public static void destroySeat(Entity seat) {...}
```
