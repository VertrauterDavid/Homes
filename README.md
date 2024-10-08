# Homes

[![download](https://img.shields.io/github/downloads/VertrauterDavid/Homes/total?style=for-the-badge)](https://github.com/VertrauterDavid/Homes/releases/latest)
![license](https://img.shields.io/github/license/VertrauterDavid/Homes?style=for-the-badge)
![stars](https://img.shields.io/github/stars/VertrauterDavid/Homes?style=for-the-badge)
![forks](https://img.shields.io/github/forks/VertrauterDavid/Homes?style=for-the-badge)

<hr>

### Installation
1. Download jar from [here](https://github.com/VertrauterDavid/Homes/releases/latest)
2. Put the jar in your plugins folder
3. Restart your server
4. Setup SQL in `plugins/Homes/config.yml`
5. Restart your server (not reload)

### Update
1. Download the new jar from [here](https://github.com/VertrauterDavid/Homes/releases/latest)
2. Replace the old jar with the new one
3. Delete the old `config.yml` and restart your server

<hr>

### Future updates
- SQLite Support
- Placeholder Support
- More GUI config options

<hr>

<details>
    <summary><h3 style="display: inline;">Screenshots</h3></summary>
    <img alt="1" width="60%" src="https://vertrauterdavid.net/assets/images/projects/homes/1.png">
    <img alt="2" width="60%" src="https://vertrauterdavid.net/assets/images/projects/homes/2.png">
    <img alt="3" width="60%" src="https://vertrauterdavid.net/assets/images/projects/homes/3.png">
    <img alt="4" width="60%" src="https://vertrauterdavid.net/assets/images/projects/homes/4.png">
</details>

<hr>

<details>
    <summary><h3 style="display: inline;">Commands</h3></summary>

| Command                       | Action                      | Permission |
|-------------------------------|:----------------------------|------------|
| `/homes` `/home`              | Open your Home Gui          |            |
| `/home <1-7>`                 | Teleport to a home directly |            |
| `/home set <1-7>`             | Set a home directly         |            |
| `/home delete <1-7>`          | Delete a home directly      |            |
| `/home remove <1-7>`          | Delete a home directly      |            |
| `/home <player> <1-7>`        |                             | home.admin |
| `/home <player> set <1-7>`    |                             | home.admin |
| `/home <player> delete <1-7>` |                             | home.admin |
| `/home <player> remove <1-7>` |                             | home.admin |

</details>

<hr>

<details>
    <summary><h3 style="display: inline;">Permissions</h3></summary>

| Permissions    |                                                   |
|----------------|:--------------------------------------------------|
| `homes.1`      | 1 Home                                            |
| `homes.2`      | 2 Home                                            |
| `homes.3`      | 3 Home                                            |
| `homes.4`      | 4 Home                                            |
| `homes.5`      | 5 Home                                            |
| `homes.6`      | 6 Home                                            |
| `homes.7`      | 7 Home                                            |
| `homes.bypass` | Bypass cooldown                                   |
| `homes.admin`  | Permission for `/homes <player>` `/home <player>` |

</details>
