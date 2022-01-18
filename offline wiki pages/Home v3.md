## Action Inventory Builders

Sgui uses GuiBuilders to create guis. Action inventories are special guis. Action inventories created on demand by the builders that you provide. Action inventory builders are created using the json format. Check out [[Config v3]] for where to place them.

Builders are loaded when a server starts and when the command "/actioninventory reload" is used.

An action inventory builder looks like:
```
{
  "type": "",
  "name": "",
  "title": {},
  "includePlayer": false,
  "lockPlayerInventory": false,
  "elements": []
}
```
### Check out [[Common Types v3]] for some common objects.

## Type (required)
This controls how the inventory is displayed. This is an identifier of a screen handler type. Vanilla Minecraft has:
<details>
<summary>Types</summary>
* generic_9x1
* generic_9x2
* generic_9x3
* generic_9x4
* generic_9x5
* generic_9x6
* generic_3x3
* anvil
* beacon
* blast_furnace
* brewing_stand
* crafting
* enchantment
* furnace
* grindstone
* hopper
* lectern
* loom
* merchant
* shulker_box
* smithing
* smoker
* cartography_table
* stonecutter
</details>

## Name (required)
This is used to reference the builder from other places. This is an identifier. Names must be unique for each builder.

## Title (defaults to no text)
This is the display at the top of the inventory. This is a text object.

## Include Player (defaults to false)
Whether the player's inventory slots should be included in the gui. Including a player's slots allows for more elements, but the player won't be able to access any of their items while in the gui. This is a boolean.

## Lock Player Inventory (defaults to false)
Whether the player is able to move things in their inventory. This is automatically set to true if includePlayer is true. This is a boolean.

## Elements (defaults to no elements)
These are the elements of the inventory. These are what actually do stuff. This is an array of objects. See [[Elements v3]] for how to create them.