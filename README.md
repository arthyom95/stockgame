# stockgame
Java based game simulating a stock market

How to start the game:
1. Run the “GameServer” class to start the game server.
2. Open Putty and create a new connection with the Host Name set to “localhost”, the
port number set to 8888 and the connection type set to “Raw”. Optionally, the
connection details can be saved by inputting a custom name in the “Saved Sessions”
field and clicking on save.
3. Open the newly created connection.
4. The first player to connect to the server will have to specify the number of players
that will be joining the game. The input has to be an integer between 1 and 4, as the
number of players includes the first player to connect and it cannot exceed 4.
5. New Putty instances will have to be opened and the same connection as the first
player’s established to the server until the number of connections specified by the
first player is reached.
6. Once enough players have connected, the game will start. 

How to play the game:
The game rules have been implemented to strictly follow the instructions provided in
the assignment description. The following commands are available to the players for each
round:
	BUY <StockCharacter><Amount>: allows a player to buy a certain amount of
stocks. The player needs to use the stock characters (a through e, case insensitive)
to specify the stock they wish to buy, followed by a space and an
integer representing the amount of stock they wish to buy. e.g. buy C 2
	SELL <StockCharacter><Amount>: allows a player to sell a certain amount of
stocks. The player needs to use the stock characters (a through e, case insensitive)
to specify the stock they wish to sell, followed by a space and an
integer representing the amount of stock they wish to buy. e.g. sell C 2
	VOTE <StockCharacter><VoteValue>: allows a player to vote for one of the top
cards from the stock decks. The player needs to use the stock characters (a
through e, case-insensitive) to specify the deck they wish to, followed by either
yes, no, true or false (case-insensitive) to specify if they wish to give a positive or
negative vote.
e.g. vote A yes; vote B no; vote E true; vote D false
ENDROUND: ends the player’s current round.
GETCASH: displays the player’s current cash.
GETSHARES: displays the player’s current shares.
GETPRICES: displays the current prices of the stocks.
GETCARDS: displays the first cards from each stock’s deck.