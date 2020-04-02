# ProxyCommandForCost

[![Generic badge](https://img.shields.io/badge/Spigot--API-1.14.4-green.svg)](https://www.spigotmc.org/wiki/buildtools/#1-14-4)

Bukkit plugin - Pay and call command (possible with op permissions).

 - */proxycommands* - List of available commands
 - */proxycommands reload* - Reload list from config

### Example of configuration proxy command
    list:
     myFirstCommand:
       alias: /arena
       cost: 15.00
       giveOp: false
       command: /ma join MobArena
     mySecondAway:
       alias: /go-away
       cost: 3
       giveOp: true
       command: /tp  @p 0 100 0

### What I would like to do
- [X] Reload list config
- [ ] Auto complete commands
