# Lifesteal-Plugin
A simple Lifesteal plugin constantly being updated

## 💡 Features

- 🩸 Fully configurable Lifesteal heart system
- ❤️ Withdraw hearts into physical items (with tier support)
- 🔥 Heart tiers (Tier 1 ➔ Tier 5, configurable healing amounts)
- 🏷️ Admin commands with full permission control
- 🚫 Automatic banning when hearts reach zero
- 🛡️ Revive system with beacon-based revival process
- 🎯 Custom crafting for Heart Items and Revive Beacon
- 🌐 Multi-language support (easy to translate)
- 💾 MySQL & SQLite support for player data persistence
- 🔄 Live config and language reload (`/reloadlifesteal`)
- ✅ Bukkit-safe, Paper-safe, Purpur-safe, and Bukkit-safe

---

## ⚙ Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/withdrawheart` | `lifesteal.withdraw` | Withdraw 1 heart into item |
| `/reloadlifesteal` | `lifesteal.reload` | Reload configuration & language |
| `/lifestealadmin` | `lifesteal.admin` | Full admin control panel |

**Admin subcommands:**

- `set`, `add`, `remove`, `giveheart`, `revivebeacon`, `unban`, `purgebans`, `setmax`, `resetall`, `banlist`, `reload`

---

## 📦 Permissions

| Node | Default |
|------|---------|
| `lifesteal.admin` | `OP` |
| `lifesteal.reload` | `OP` |
| `lifesteal.withdraw` | `true` |
| `lifesteal.revive` | `true` |
| `lifesteal.bypasslimit` | `OP` |

---

## 🔧 Configuration

- Full configuration for hearts, crafting, bans, revive system.
- Multi-language support (`lang.yml` system).
- MySQL and SQLite support.

---

## ✅ Supported Versions

- Minecraft 1.21+  
- Compatible with Bukkit, Spigot, Paper, Purpur

---

## 🚀 Future Improvements (Planned)

- PlaceholderAPI support (optional expansion)
- Economy support integration
- Additional revive customization
- More crafting options for heart tiers

---

## 🔒 No NMS. No Reflection. No version lock.

Safe, stable, and easy to maintain across Minecraft versions.

---

Made with ❤️ by **MidnightZone**
