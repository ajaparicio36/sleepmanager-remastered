<div align="center">
  
# 🌙 Sleep Manager

**A seamless sleep voting system for your SMP server**

[![PaperMC 1.21](https://img.shields.io/badge/PaperMC-1.21+-8A2BE2)](https://papermc.io/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Latest Release](https://img.shields.io/github/v/release/tatayless/sleepmanager-remastered?include_prereleases&label=latest%20release)](https://github.com/tatayless/sleepmanager-remastered/releases)

</div>

## ✨ Features

Sleep Manager brings a democratic approach to skipping the night in Minecraft multiplayer servers with an elegant voting system, solving the classic "one player sleeps, everyone must wait" problem.

- **🗳️ Democratic Sleep System**: When a player tries to sleep, a server-wide vote is initiated
- **🔔 Interactive Voting**: Beautiful chat prompts with clickable YES/NO buttons
- **🌍 World-Specific**: Votes only affect the world they're initiated in
- **👻 Anti-Phantom Protection**: Resets phantom spawn timer for all players when night is skipped
- **🌐 Multi-language Support**: Available in English, Spanish, and French (easily expandable)
- **⚙️ Fully Configurable**: Adjust voting requirements, duration, and more

## 📋 Commands

| Command                  | Description                   | Permission             |
| ------------------------ | ----------------------------- | ---------------------- |
| `/sleepmanager` or `/sm` | Shows plugin help             | -                      |
| `/sm version`            | Shows the plugin version      | `sleepmanager.version` |
| `/sm revote`             | Starts a new sleep vote       | `sleepmanager.revote`  |
| `/sm yes`                | Vote yes to skip night        | `sleepmanager.vote`    |
| `/sm no`                 | Vote no to skip night         | `sleepmanager.vote`    |
| `/sm toggle [world]`     | Toggle sleep voting per world | `sleepmanager.toggle`  |

## 🔧 Configuration

SleepManager is highly configurable through its `config.yml` file:

```yaml
# Language setting (files located in the lang/ folder)
language: en_US

# Voting settings
voting:
  # Percentage of players required to vote YES (0-100)
  required_percentage: 50

  # Duration in seconds that a vote will remain active
  duration_seconds: 30

  # Cooldown in seconds before allowing a new vote
  revote_cooldown_seconds: 60
```

## 🌍 Language Support

SleepManager supports multiple languages through its language files. Currently included:

- 🇺🇸 English (en_US)
- 🇪🇸 Spanish (es_ES)
- 🇫🇷 French (fr_FR)

Want to contribute a new language? Create a pull request adding your translation!

## 📥 Installation

1. Download the latest `.jar` file from [Releases](https://github.com/tatayless/sleepmanager-remastered/releases)
2. Place it in your server's `plugins` folder
3. Restart your server
4. Edit the configuration in `plugins/SleepManager/config.yml` if desired
5. Use `/sm` in-game to see available commands

## 🏆 Usage Example

When a player tries to sleep at night:

1. A vote message appears in chat for all players in that world
2. Players can click YES or NO to vote
3. If enough players vote YES (configurable percentage)
4. The next player to enter a bed will skip the night
5. All players' phantom spawn timers are reset

## 📝 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 👥 Credits

- **Developer**: [tatayless](https://github.com/ajaparicio36)

## 📞 Support

Having issues or suggestions? Open an issue on [GitHub](https://github.com/tatayless/sleepmanager-remastered/issues). You are free to contribute through Pull Requests.
