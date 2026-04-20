# CAPI - 30 commands, API, customizable massages.

## What is this?
CAPI is **A+** code minecraft plugin, made by one person, 
i created it, because wanted to create my own "CMI", 
there's a lots of "basic" commands, like /feed (custom realization), 
/heal (same as /feed),
/gm - the same as cmi, just more customizable

### building project:
```bash

# Build the plugin (creates shadowed jar with all NMS versions)
./gradlew build

# Build without running tests
./gradlew build -x test

# Clean build
./gradlew clean build

# Run a test server (Paper 1.21.11)
./gradlew runServer
```
### Testing:
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:test

# Run PackMerger debug tool
./gradlew :core:runPackMergerDebug --args="path/to/pack"
```
### how to use API:
### if you're using gradle + kotlin: (gradle.kts)
add this to the settings.gradle.kts:
```kotlin
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
# add other repos like paper here
		}
	}
```
and add the dependency: 
```kotlin
dependencies {
           implementation("com.github.CatsT0day:CAPI:Tag")
}
```
### if you're using basic gradle:
Add it in your root settings.gradle at the end of repositories:
```groovy
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
# same
		}
	}
```
then add The dependency
```groovy
	dependencies {
	        implementation 'com.github.CatsT0day:CAPI:Tag'
	}
```
### if you're using maven:
Add to pom.xml
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Add the dependency

```xml
	<dependency>
	    <groupId>com.github.CatsT0day</groupId>
	    <artifactId>CAPI</artifactId>
	    <version>Tag</version>
	</dependency>
```
Note: Replace Tag in the dependency with the actual version tag (e.g., 1.0.0, main-SNAPSHOT, etc.).
## some api examples: 
 ### wanna get the player with this api and control them?
 no problem, import: 
  ```java
   import me.catst0day.capi.User.CAPIUser;
```
here's some exaples to get the user, and use it: 
#### with UUID: 
```java
package put.yourpackage.here;

import me.catst0day.capi.User.CAPIUser;
import org.bukkit.command.CommandSender;
import java.util.UUID;

public class Example {
    public void runExample(CommandSender sender) {
        UUID playerUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        CAPIUser apiUser = CAPI.getInstance().getUser(playerUUID);
        if (apiUser != null) {
            sender.sendMessage("Omg, I found you, and whoever is reading this, I hope they understand how to use this API: " + apiUser.getName());
        } else {
            sender.sendMessage("Bro where are you, i cant find you :(((( - java, sorry for my english.");
        }
    }
}
```
#### Setting home with nickname of the plr: 
``` java
package me.catst0day.capi;

import me.catst0day.capi.User.CAPIUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Example {
    public boolean setPlayerHome(Player player, String homeName) {
        CAPIUser user = CAPI.getInstance().getUser(player);
        Location playerLocation = player.getLocation();

        boolean success = user.setHome(homeName, playerLocation);
        if (success) {
            player.sendMessage(
"Seeeeeettttthhhhhooooommeeee '" + homeName + "' sssseeetttt!");
            return true;
        } else {
            player.sendMessage(
"idk what happened, but the code broke, I hope you understand how to use this API.");
            return false;
        }
    }
}
```
#### you also can create the menus like in AbstractMenus (without animations :( )
import:
```java
import me.catst0day.capi.GUI.CAPIGui;
import me.catst0day.capi.GUI.CAPIGuiButton;
import me.catst0day.capi.GUI.CAPIGuiManager;
import me.catst0day.capi.GUI.CAPIGuiListener;
```
for example, you can use it to create homes with GUI, as CMI did: 
```java
    private void show(Player player) {
    List<String> homes = plugin.getInstance().getHomeManager().getPlayerHomes(player.getUniqueId());

    CAPIGui gui = new CAPIGui(player, "§7Homes", 6);

    fillBorders(gui);

    int slot = 10;
    for (String home : homes) {
        CAPIGuiButton homeButton = createHomeButton(player, home);
        gui.addButton(slot, homeButton);
        slot++;
        if (slot % 9 == 8) slot += 2;
    }

    gui.open();
}

private void fillBorders(CAPIGui gui) {
    CAPIGuiButton glassButton = new CAPIGuiButton(Material.WHITE_STAINED_GLASS_PANE)
            .setName(" ");

    for (int i = 0; i < 9; i++) {
        gui.addButton(i, glassButton);
        gui.addButton(45 + i, glassButton);
    }

    for (int row = 1; row < 5; row++) {
        gui.addButton(row * 9, glassButton);
        gui.addButton(row * 9 + 8, glassButton);
    }
}

private CAPIGuiButton createButton(Player player, String homeName) {
    CAPIGuiButton button = new CAPIGuiButton(Material.PLAYER_HEAD)
            .setName("§a" + homeName)
            .addLore("§fTeleport to home")
            .addLore("")
            .addLore("§eClick to teleport")
            .onLeftClick(p -> {
                Location homeLocation = plugin.getInstance().getHomeManager().getHome(p.getUniqueId(), homeName);
                if (homeLocation != null) {
                    p.teleport(homeLocation);
                    p.closeInventory();
                    p.sendMessage(plugin.getMessage("homeTeleported")
                            .replace("{homename}", homeName));
                }
            });
    return button;
}
```
Thats it! Thanks for visiting/downloading my plugin 
