# falling-blocks

A skeleton project for building a Tetris clone. This uses a fork of the [Bocko](https://github.com/mfikes/bocko) library called [Bocko Fun](https://github.com/jasongilman/bocko-fun).

## Implementing the Game

The game is implemented enough so that there is one type of falling block, the long 4 part piece. You can control a falling block with the arrow keys and the space bar to rotate. There are TODOs with areas throughout the code that need to be completed. Here's a list of the main things to implement

* Implement more falling piece types.
* When a piece reaches the bottom of the board "merge" it with the board matrix and select the next falling piece
* Add a falling piece component which will display the next falling block type.

Here's a javascript version of the game: http://www.javascripter.net/games/tetris/game.htm
Note this has a different control scheme.


## Startup

Run `lein repl` in the project directory

Then run `(reset)` in the repl. A swing window will open and the game will start. 


## License

Copyright © 2015 Jason Gilman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
