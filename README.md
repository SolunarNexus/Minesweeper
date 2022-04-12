# Mineswepper

Welcome to our ultimate minesweeper game. Our implementation is based on the [original minesweeper][1].
We would like to improve our implementation, our programming skills are not that great and adding a new features
and fixing customer bugs became nightmare.

## The game

When you start the game, you are greeted with logo, the board is printed out and the prompt awaits your commands:

```shell
███╗░░░███╗██╗███╗░░██╗███████╗░██████╗░██╗░░░░░░░██╗███████╗███████╗██████╗░███████╗██████╗░
████╗░████║██║████╗░██║██╔════╝██╔════╝░██║░░██╗░░██║██╔════╝██╔════╝██╔══██╗██╔════╝██╔══██╗
██╔████╔██║██║██╔██╗██║█████╗░░╚█████╗░░╚██╗████╗██╔╝█████╗░░█████╗░░██████╔╝█████╗░░██████╔╝
██║╚██╔╝██║██║██║╚████║██╔══╝░░░╚═══██╗░░████╔═████║░██╔══╝░░██╔══╝░░██╔═══╝░██╔══╝░░██╔══██╗
██║░╚═╝░██║██║██║░╚███║███████╗██████╔╝░░╚██╔╝░╚██╔╝░███████╗███████╗██║░░░░░███████╗██║░░██║
╚═╝░░░░░╚═╝╚═╝╚═╝░░╚══╝╚══════╝╚═════╝░░░░╚═╝░░░╚═╝░░╚══════╝╚══════╝╚═╝░░░░░╚══════╝╚═╝░░╚═╝

   00 01 02 03 04 05 06 07 08 09 
00  X  X  X  X  X  X  X  X  X  X 
01  X  X  X  X  X  X  X  X  X  X 
02  X  X  X  X  X  X  X  X  X  X 
03  X  X  X  X  X  X  X  X  X  X 
04  X  X  X  X  X  X  X  X  X  X 
>>>
```

Available commands:
- `reveal` or `r` - for the board revealing, it expects 2 additional numeric arguments, `reveal <row> <col>`
- `debug` or `d` - to print debug output (it reveals where are the mines, it should be used for devel. purposes)
- `exit` or `quit` - to leave a game
- `export` - to export the game in base64 format
- `import` - to import the game in the same base64 format (example: `import NSwxMAowLDYKMCw3CjEsMgoxLDUKMSw2CjIsMAoyLDMKMiw3CjMsMAozLDIK`)


Now for the game play: we wanted to have always-safe the first turn (reveal), so the board is generated after the first
reveal and before that, the board is empty and not initialized.

If the player reveals a cell with a mine, it will explode and player will lose. 
After all cells that are not mines are cleared, the player won the game.


## Additional work

Since our game is ultra popular, the customers wants it to be better.

Here is a list of some suggestions, requested by our customers:

### UX/CLI:
- ``help`` command - to show help with available commands and its description with examples
- Better UX - for example, sometimes if we provide invalid command, there is no error message
- Allow `debug` only if the application is started with `--devel` command line option
- Add ability to set `seed`, number of `rows`, `cols` and `mines` by command line options

### Gameplay:
- Implement floodfill reveal - currently, we are revealing only the single cell (desc. see bellow).
- **We need flags** - add ability to flag cells. If the cell is flagged, it cannot be revealed
- Export and import with support of flags, we would like to be able to export and import game with flags,
but also we would like to have stored, which cells were already revealed. 
**IMPORTANT**: the old format has to work for the new version, since our customer wants to play
their stored games on the new version!


[1]: https://en.wikipedia.org/wiki/Minesweeper_(video_game)