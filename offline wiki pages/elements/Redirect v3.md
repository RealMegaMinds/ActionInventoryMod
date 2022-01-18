## Redirect Slot Element
A redirect slot element is redirected to a slot in a different inventory (such as an enderchest). Clicks are redirected to that inventory and the displayed item is the one in that inventory.

It looks like:

```
{
  "type": "Redirect",
  "index": 3,
  "guiType": "",
  "name": "",
  "slotIndex": 0
}
```

## Type (required)
Specifies the type of the slot element. This should be "Redirect".

## Index (defaults to the index of the next open slot)
Which slot of the inventory the element should display in. Each slot element in an inventory should have a different index. This is an integer.

## Gui Type (defaults to PLAYER)
This is the type of gui to redirect to. This is a string. Supports:
<details>
<summary>Types</summary>
* PLAYER - a player's inventory
* ENDER_CHEST - a player's enderchest
</details>

## Name (defaults to the clicking player's UUID)
This is the UUID of the player who's enderchest or inventory is being redirected to. This is a UUID.

## Slot Index (defaults to 0)
This is the index of the slot in the inventory that is being redirected to. This is an integer.