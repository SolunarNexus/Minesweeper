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
- `reveal` or `r` - reveals the board, it expects 2 additional numeric arguments, `reveal <row> <col>`
- `debug` or `d` - prints debug output (it reveals where are the mines, it should be used for development purposes)
- `exit` or `quit` - to leave a game
- `export` - exports the game in base64 format
- `import` - imports the game in the same base64 format (example: `import NSwxMAowLDYKMCw3CjEsMgoxLDUKMSw2CjIsMAoyLDMKMiw3CjMsMAozLDIK`)


Now for the game play: we want the first turn (reveal) to always be safe, so the board is generated after the first
reveal. Before that, the board is empty and not initialized.

If the player reveals a cell with a mine, it will explode and the player will lose. 
After all cells that are not mines are cleared, the player won the game.


## Further improvements

Since our game is very popular, the customers want it to be better.

Here is a list of requests from our customers:

### UX/CLI:
- ``help`` command - shows a message with available commands and their descriptions with examples
- Better UX (upser experience) - for example, sometimes if we provide invalid command, there is no error message
  - Better error handling - right now, the whole game fails if there is an exception
  - Better error messages
  - `import` should prompt user whether he wants to replace the current board (if it was already initialized)
- Allow `debug` only if the application is started with `--devel` command line option
- Add ability to set `seed`, number of `rows`, `cols` and `mines` by command line options

### Gameplay:
- Implement floodfill reveal - currently, we are revealing only one cell (description see below).
- **We need flags** - add ability to flag cells. If a cell is flagged, it cannot be revealed
- Export and import shoud support flags and saving of board state, we would like to be able to export and import a game with flags,
but also we would like to have stored, which cells were already revealed. 
**IMPORTANT**: the old format has to work for the new version, since our customer wants to play
their stored games on the new version!

## Illustrative Example


```shell
     0   1   2   3                       0   1   2   3
   +---+---+---+---+                   +---+---+---+---+
 0 |XXX|XXX|XXX|XXX|                 0 |XXX|XXX|XXX|XXX|
   +---+---+---+---+                   +---+---+---+---+
 1 |XXX|XXX|XXX|XXX|  Reveal [2,1]   1 | 1 | 1 | 2 |XXX|  Flag [1,3]
   +---+---+---+---+       ->          +---+---+---+---+       ->
 2 |XXX|XXX|XXX|XXX|   (floodfill)   2 |   |   | 1 | 1 |
   +---+---+---+---+                   +---+---+---+---+
 3 |XXX|XXX|XXX|XXX|                 3 |   |   |   |   |
   +---+---+---+---+                   +---+---+---+---+

     0   1   2   3                       0   1   2   3
   +---+---+---+---+                   +---+---+---+---+
 0 |XXX|XXX|XXX|XXX|                 0 |XXX| M |XXX|XXX|
   +---+---+---+---+                   +---+---+---+---+
 1 | 1 | 1 | 2 |_F_|  Reveal [0,1]   1 | 1 | 1 | 2 |_F_|
   +---+---+---+---+       ->          +---+---+---+---+  -> Lost by revealing a mine
 2 |   |   | 1 | 1 |                 2 |   |   | 1 | 1 |
   +---+---+---+---+                   +---+---+---+---+
 3 |   |   |   |   |                 3 |   |   |   |   |
   +---+---+---+---+                   +---+---+---+---+
```


## Details

Here are some additional details about some features


### Flood-fill implementation

When the player is revealing a cell, it can be either a mine, or it can be a digit, 
which indicates how many adjacent squares contain mines.
If no mines are adjacent, the square becomes blank, 
and all adjacent squares will be recursively revealed. 
The algorithm ends when the adjacent cell is a mine.
For the implementation, you can use either recursion, or queue or stack.

In simple terms:
You will start with the current cell, reveal it, if it is a mine end - you revealed the mine.
If not and there is any neighbour that is mine - stop with the revealing. Otherwise, add all other
adjacent cells that are not revealed to the stack and then continue 
(Recursively call on the neighbours).


### Flag implementation

When the player flags a cell it cannot be revealed. You can place as many flags as you want. 
There should be a command that toggles a flag on the cell.
After each valid turn, the game updates the indicator of how many mines remain unflagged - however, 
this number is only the difference between the number of mines in the game and the number of already placed flags, 
it does not tell the player if a cell with a flag contains a mine.
The number of flags should be displayed after each valid turn. 
The game shows the difference between the number of placed flags and the number of mines. This number can be negative.


**IMPORTANT**: Flood-fill also removes wrongly-placed flags. If a flagged cell is checked during flood-filling and does not contain a mine, it should be revealed and the flag should be removed.


[1]: https://en.wikipedia.org/wiki/Minesweeper_(video_game)