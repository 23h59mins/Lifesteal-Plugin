![Banner](https://cdn.modrinth.com/data/cached_images/a085a30e30b7cabfe823ba7574443623770568af_0.webp)

---

# LifestealSMP 🔪❤️

LifestealSMP is a highly configurable and fully featured Lifesteal plugin designed for modern Minecraft SMP servers.  
It offers a complete hearts-based survival system, revive mechanics, advanced crafting, multi-language support, and full administrative control.

---

[![Discord invitation](https://cdn.modrinth.com/data/cached_images/987780219d2e4f8a4466f8e3d8493ec27a6c0c84_0.webp)](https://discord.gg/BbqeDSN9)
[![Github Invitation](https://cdn.modrinth.com/data/cached_images/d56dbb421a54bcc2f7913d7237b4eb197e476a27_0.webp)](https://github.com/23h59mins/Lifesteal-Plugin)
![Operation in Minecraft 1.21+](https://cdn.modrinth.com/data/cached_images/59fec7211986e2f899526a091bca2dd304bbbfaa_0.webp)

---

## 🔧 Core Features

- ✅ **Heart System:** Fully configurable heart loss and gain based on deaths, PvP, or environment.
- ✅ **Heart Items:** Withdraw hearts into physical items that can be traded or consumed.
- ✅ **Heart Tiers:** 5 tiers of heart items allow restoring from +1 up to +5 hearts.
- ✅ **Custom Crafting Recipes:** Create your own crafting recipes for heart items and revive beacon.
- ✅ **Revive System:** Revive banned players using special Revive Beacons via a chat-based confirmation system.
- ✅ **Rare Revive Beacon Recipe:** Uses expensive materials like Netherite Blocks, Nether Stars, Wither Skulls, Enchanted Golden Apples & Totems.
- ✅ **Ban System:** Players get automatically banned upon reaching 0 hearts.
- ✅ **Admin Control:** Powerful `/lifestealadmin` system with full server control.
- ✅ **Command Hiding:** Commands are hidden dynamically for players without permission.
- ✅ **Database Support:** MySQL and SQLite database options for scalable player data.
- ✅ **Multi-Language Support:** Fully translatable through external language files.
- ✅ **Configuration Reloading:** Reload configuration and language files live using `/reloadlifesteal`.
- ✅ **Safe Bukkit API Design:** Compatible with Bukkit, Spigot, Paper & Purpur.
- ✅ **Recipe viewer**
---

## 🎯 Commands

### Main Commands:

| Command                            | Usage                     | Permission             |
| ---------------------------------- | ------------------------- | ---------------------- |
| `/withdrawheart`                   | Withdraw hearts into item | `lifesteal.withdraw`   |
| `/reloadlifesteal`                 | Reload configuration      | `lifesteal.reload`     |
| `/recipeview <heart\|beacon\|all>` | Display recipes           | `lifesteal.recipeview` |
| `/lifestealadmin`                  | Admin main command        | `lifesteal.admin`      |

### Admin Subcommands (`/lifestealadmin`)

| Subcommand | Description |
|------------|-------------|
| `set <player> <amount>` | Set hearts |
| `add <player> <amount>` | Add hearts |
| `remove <player> <amount>` | Remove hearts |
| `giveheart <player> [tier] [amount]` | Give heart items (Tier 1-5) |
| `revivebeacon <player>` | Give Revive Beacon |
| `unban <player>` | Unban player |
| `purgebans` | Unban everyone |
| `setmax <amount>` | Set maximum hearts |
| `resetall` | Reset hearts for all players |
| `banlist` | List all banned players |
| `reload` | Reload config & language |

---

## 🎯 Permissions

| Permission | Description | Default |
|-------------|-------------|---------|
| `lifesteal.admin` | Full admin access | `OP` |
| `lifesteal.reload` | Reload configurations | `OP` |
| `lifesteal.withdraw` | Withdraw hearts into items | `true` |
| `lifesteal.revive` | Use revive beacons | `true` |
| `lifesteal.bypasslimit` | Bypass max hearts limit | `OP` |

---

## 🔁 PlaceholderAPI Support

LifestealSMP includes built-in PlaceholderAPI expansion for dynamic integration with scoreboards, tab lists, nametags, holograms, and other placeholder-compatible systems. You can display player-specific data across your server in real-time.

| Placeholder                      | Description                                                               |
| -------------------------------- | ------------------------------------------------------------------------- |
| `%lifesteal_hearts%`             | Shows the player’s **current number of hearts** (if online)               |
| `%lifesteal_maxhearts%`          | Returns the **configured max hearts** a player can reach                  |
| `%lifesteal_is_banned%`          | Displays **"true" or "false"** based on whether the player is banned      |
| `%lifesteal_database_type%`      | Shows the **active database type**, either `"mysql"` or `"sqlite"`        |
| `%lifesteal_lang%`               | Displays the **active language config file name**                         |
| `%lifesteal_startinghearts%`     | Returns the **configured default hearts** for new players                 |
| `%lifesteal_prefix%`             | Inserts the plugin’s **configured prefix string**                         |
| `%lifesteal_online%`             | Displays **"true" or "false"** depending on player online status          |
| `%lifesteal_ban_reason%`         | Displays a hardcoded reason (**"Out of hearts"**) if the player is banned |
| `%lifesteal_playername%`         | Displays the player’s **current username**                                |
| `%lifesteal_uuid%`               | Returns the player’s **UUID**                                             |
| `%lifesteal_health%`             | Shows the player’s **current health points (HP)**                         |
| `%lifesteal_maxhealth%`          | Shows the player’s **current max health (HP)**                            |
| `%lifesteal_hearts_left%`        | Shows **how many hearts are left** before hitting max hearts              |
| `%lifesteal_online_count%`       | Displays the **number of online players**                                 |
| `%lifesteal_database_connected%` | Returns **"true" or "false"** depending on DB connection status           |

These placeholders allow server owners and developers to deeply customize their user experience, creating rich, dynamic interfaces that reflect each player's lifesteal status in real-time.

---

## 🚀 Supported Platforms

---

![Available for multi platforms](https://cdn.modrinth.com/data/cached_images/7b056939c687312c139b8a9efbd8a51a98b11d1e_0.webp)

---

- ✅ Bukkit 26.1+
- ✅ Spigot 26.1+
- ✅ Paper 26.1+
- ✅ Purpur 26.2+
- ✅ MySQL & SQLite Databases

---

## 🔒 No NMS - No Version Lock

- 100% version-independent.
- No use of NMS, reflections, or unsafe internal APIs.
- Fully safe for future Minecraft versions.

---

## ⚙ Planned Expansions

- ✅ Economy integration
- ✅ Custom revive options
- ✅ More heart tier recipes
- ✅ Crafting recipe GUI configuration
- ✅ Expanded admin control panels

---

## 🔥 Perfect For:

- ✅ Survival servers
- ✅ Hardcore SMP
- ✅ Lifesteal communities
- ✅ Private or public SMP servers
- ✅ Large network environments

---

LifestealSMP delivers a full Lifesteal SMP experience while remaining fully scalable, safe, and professional for any Minecraft server.

---

Made with ❤️ by **MidnightZone**

---

![Have fun](https://cdn.modrinth.com/data/cached_images/9afac1f9e643f803b665f9bb60a3676467bc2ef6_0.webp)
