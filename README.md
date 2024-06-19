# StrokkChat
Server version: Paper 1.20.4 <br>
-- WILL NOT WORK ON SPIGOT --

## Feature Overview
- Permission based chat formats
- MiniMessage formatting
- Define custom MiniMessage tags in a config!
- Build-in MiniMessage tags for PlaceholderAPI (ex.: `<papi:player_name>`)
- Build-in MiniMessage tags for LuckPerms (ex.: `<lp:prefix>`)
- Define custom chat placeholders in the format [placeholder] for players to use
- Build-in chat placeholder for item showcase (default: `[item]`)

## Planned Features
- Developer API to use defined custom MiniMessage placeholders in other plugins
- Ability to define GUIs for use in Config
- Add support for more APIs

## Contributing
- Contributions in general are appreciated! Since I have not used MiniMessage for long, my ways are most definitely 
flawed. So any performance or code improving pull-requests are greatly appreciated.
- If you want to add a feature, please consult with me first by opening up an Issue.

## API Usage
- Currently, there is no API. But once I implement it, you can find maven / gradle repository information and API
usage documentation here!

## Special Thanks
- ...to nobody, yet! If you provide a significant contribution you might get listed here~

## TODO List
- [x] Add the ability to load and parse custom defined MiniMessage placeholders
- [ ] Add the ability to load and parse custom defined chat placeholders
- [ ] Make MiniMessage tags be parsed inside of other MiniMessage tags (tag in tag)
- [ ] Different chat formats depending on a users permission
- [ ] Add a developer API
- [x] Add build-in `<player>`, `<lp>`, and `<papi>` tags
- [ ] Make the build-in chat placeholder [item] work correctly
- [ ] Define and use GUI-placeholders
- [ ] Add a way to distinguish self-closing and inserting MiniMessage tags (`<my_name>` and `<my_style>text</my_style>`)
- [ ] Add Vault API, Essentials API, and more, direct API support in tags (ex.: `<essentials:nick>` or `<vault:balance>`)