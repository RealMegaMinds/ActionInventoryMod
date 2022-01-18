## Command Action
Command actions execute a command.

They look like:
```
{
  ...common fields...
  "command": "",
  "fromServer": "",
  "makeTempOp": false
}
```
(common fields are specified in [[Actions v3]])

## Command (defaults to "")
This is the command to execute. This is a string.

## From Server (defaults to false)
Whether the command is from the server (true) or the player (false). This is a boolean.

## Make Temp Op (defaults to false)
Whether the player should temporarily be opped. This is a boolean.

When true, if the player was not already an op, they made an op before executing the command and deopped after completing the command.

If false, commands will cause an error if the player is not an op.