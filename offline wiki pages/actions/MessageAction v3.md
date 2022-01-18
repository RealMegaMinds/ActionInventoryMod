## Message Action
Message actions send messages to players.

They look like:
```
{
  ...common fields...
  "message": {},
  "sender": "",
  "receivers": [],
  "messageType": ""
}
```
(common fields are specified in [[Actions v3]])

## Message (defaults to empty text)
This is the message that is sent. This is a text object.

## Sender (defaults to the player that clicked)
This is the person who the message is from. This is a UUID.

## Receivers (defaults to broadcast)
These are the people that will receive the message. If the array is empty, it will be broadcast to all players. A null in the array is replaced with the UUID of the player that clicked on it. Use the nil UUID for logging to the server. This is an array of UUIDs.

## Message Type (defaults to CHAT)
This is the type of the message. This is a string.
<details>
<summary>Types</summary>
* CHAT
* SYSTEM
* GAME_INFO
</details>