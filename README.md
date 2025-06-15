# Administration Plugin

A comprehensive Minecraft Paper plugin for server administration and stealth policing, designed for Minecraft 1.21.4.

## Features

### 🕵️ Stealth Policing Commands
- **Vanish** - Become invisible to players for stealth monitoring
- **Command Spy** - Monitor all commands executed by players
- **Social Spy** - Monitor private messages between players
- **Inventory See** - View any player's inventory in real-time
- **Ender Chest** - View any player's ender chest contents
- **Disguise** - Transform appearance as other players, mobs, or blocks
- **Maximum Stealth Mode** - Hide ALL command logs and traces from console

### 🛡️ Player Management
- **Freeze** - Completely freeze players (movement, commands, interactions)
- **God Mode** - Make players invincible to all damage
- **Flight** - Toggle flight mode for any player
- **Teleport** - Advanced teleportation system

### ⚙️ Administrative Tools
- **Gamemode** - Change gamemode with shortcuts
- **Permission-based** - Granular permission system
- **Real-time monitoring** - Live updates and notifications

## Installation

1. **Install ProtocolLib** - Download and install ProtocolLib from [SpigotMC](https://www.spigotmc.org/resources/protocollib.1997/)
2. **Download** the `administration-1.0.0.jar` file from the `target/` directory
3. **Place** the JAR file in your server's `plugins/` folder
4. **Restart** your server
5. **Configure** permissions using your permission plugin

## Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/vanish [player]` | `/v` | Toggle vanish mode | `administration.vanish` |
| `/invsee <player>` | `/inv` | View player's inventory | `administration.invsee` |
| `/enderchest <player>` | `/ec`, `/echest` | View player's ender chest | `administration.enderchest` |
| `/teleport <player> [target]` | `/tp` | Teleport to/between players | `administration.teleport` |
| `/gamemode <mode> [player]` | `/gm` | Change gamemode | `administration.gamemode` |
| `/fly [player]` | - | Toggle flight mode | `administration.fly` |
| `/god [player]` | - | Toggle god mode | `administration.god` |
| `/freeze <player>` | - | Freeze/unfreeze player | `administration.freeze` |
| `/spy` | - | Toggle command spy | `administration.spy` |
| `/socialspy` | `/ss` | Toggle social spy | `administration.socialspy` |
| `/disguise <type> [name/data] [player]` | `/d` | Disguise as player/mob/block | `administration.disguise` |
| `/undisguise [player]` | `/ud` | Remove disguise | `administration.disguise` |
| `/stealth [on/off/toggle/status] [player]` | `/ghost`, `/invisible` | Maximum stealth mode | `administration.stealth` |

## Permissions

### Main Permission
- `administration.*` - Grants access to all commands (default: op)

### Individual Permissions
- `administration.vanish` - Use vanish command
- `administration.vanish.see` - See vanished players
- `administration.invsee` - View other players' inventories
- `administration.enderchest` - View other players' ender chests
- `administration.teleport` - Teleport to players
- `administration.gamemode` - Change gamemode
- `administration.fly` - Toggle flight
- `administration.god` - Toggle god mode
- `administration.freeze` - Freeze players
- `administration.freeze.bypass` - Bypass freeze restrictions
- `administration.spy` - Use command spy
- `administration.socialspy` - Use social spy
- `administration.disguise` - Use disguise commands
- `administration.stealth` - Use maximum stealth mode

## Gamemode Shortcuts

The gamemode command supports various shortcuts:
- `0`, `s`, `survival` → Survival
- `1`, `c`, `creative` → Creative  
- `2`, `a`, `adventure` → Adventure
- `3`, `sp`, `spectator` → Spectator

## Maximum Stealth Mode 👻

### 🔥 **ULTIMATE STEALTH SYSTEM**
The most advanced stealth system ever created for Minecraft administration:

```bash
# Enable maximum stealth mode
/stealth on

# Toggle stealth mode
/stealth

# Check stealth status
/stealth status

# Enable for another player
/stealth on PlayerName
```

### **What Gets Hidden:**
- ✅ **ALL command executions** from console logs
- ✅ **Permission denied messages** (completely silent)
- ✅ **Plugin loading/enabling messages**
- ✅ **Command history traces**
- ✅ **Server log entries** mentioning stealth players
- ✅ **Error messages** and feedback
- ✅ **Any traces of admin activity**

### **Stealth Features:**
- **Silent Commands** - All admin commands show `[Silent]` prefix instead of normal messages
- **Log Filtering** - Advanced log filter removes ALL traces from console
- **Zero Footprint** - No evidence of admin activity anywhere
- **Multi-Player Support** - Each admin can have independent stealth mode
- **Auto-Cleanup** - Stealth mode automatically cleans up on logout

### **Perfect For:**
- 🕵️ **Undercover investigations**
- 🔍 **Silent player monitoring**
- 🚫 **Zero-trace moderation**
- 🎭 **Covert operations**
- 📊 **Behavior analysis**

## Disguise System

### 🎭 Player Disguises
Transform your appearance to look like any player:
```
/disguise player Notch
/disguise player Steve PlayerName
```

### 🐺 Mob Disguises
Disguise as various mobs for ultimate stealth:
```
/disguise mob zombie
/disguise mob villager
/disguise mob cow
```

**Available Mobs**: zombie, skeleton, creeper, spider, enderman, witch, villager, cow, pig, sheep, chicken, wolf, cat, horse, iron_golem, snow_golem, blaze, ghast, slime, magma_cube, wither_skeleton, stray, husk, phantom, drowned, pillager, ravager, vex

### 🧱 Block Disguises
Appear as any block type:
```
/disguise block stone
/disguise block grass_block
/disguise block diamond_ore
```

### 🔄 Remove Disguises
```
/undisguise          # Remove your own disguise
/undisguise player   # Remove another player's disguise
```

## Features in Detail

### 🕵️ Vanish System
- **Complete invisibility** - Hidden from player list, join/quit messages
- **Permission-based visibility** - Staff can see vanished players with `administration.vanish.see`
- **Potion effects** - Adds invisibility effect for extra stealth
- **Auto-hide** - New players automatically can't see vanished players

### 👻 Maximum Stealth System
- **Log Filtering** - Advanced server log filtering removes ALL admin traces
- **Silent Messaging** - Special message system for stealth operations
- **Command Hiding** - Completely hides admin commands from console logs
- **Zero Evidence** - No traces left in any server logs or files
- **Multi-Admin Support** - Each admin has independent stealth mode
- **Auto-Cleanup** - Automatic cleanup on plugin disable/player logout

### 🎭 Disguise System
- **ProtocolLib powered** - Uses packet manipulation for client-side appearance changes
- **Server-side unchanged** - Your actual player data remains intact
- **Real-time updates** - Disguises apply instantly to all viewers
- **Auto-cleanup** - Disguises removed on logout/server restart
- **Multiple types** - Support for players, mobs, and blocks

### 🔒 Freeze System
- **Complete lockdown** - Prevents movement, commands, interactions
- **Item protection** - Can't drop or pick up items
- **Bypass permission** - Staff can bypass with `administration.freeze.bypass`
- **Visual feedback** - Clear messages to frozen players

### 👁️ Spy Systems
- **Command Spy** - See all commands executed by other players
- **Social Spy** - Monitor private messages (detects /msg, /tell, /whisper, /w)
- **Real-time notifications** - Instant alerts with player names
- **Toggle on/off** - Easy enable/disable for each spy type

### 🎒 Inventory Management
- **Live inventory viewing** - Real-time access to player inventories
- **Ender chest access** - View and modify ender chest contents
- **No notifications** - Target players aren't notified of access

## Building from Source

1. **Clone** or download the source code
2. **Install Maven** (if not already installed)
3. **Run build command**:
   ```bash
   mvn clean package
   ```
4. **Find JAR** in `target/administration-1.0.0.jar`

## Requirements

- **Minecraft Version**: 1.21.4
- **Server Software**: Paper (recommended) or Spigot
- **Java Version**: 21 or higher
- **Dependencies**: ProtocolLib (required for disguise functionality)

## Configuration

The plugin works out of the box with no configuration required. All settings are permission-based.

## Support

For issues, suggestions, or contributions:
1. Check existing issues
2. Create detailed bug reports
3. Include server version and plugin version
4. Provide relevant console logs

## License

This plugin is provided as-is for educational and server administration purposes.

---

**⚠️ Important**: This plugin provides powerful administrative tools. Only grant permissions to trusted staff members. Always test commands in a safe environment before using on a live server.

**📦 Dependencies**: Make sure to install ProtocolLib before using this plugin. The disguise functionality requires ProtocolLib to manipulate client packets.

**👻 Maximum Stealth**: The stealth mode completely hides ALL admin activity from console logs. Use responsibly and ensure proper oversight of staff activities. 