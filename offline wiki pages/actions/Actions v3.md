## Actions
Actions are what slot elements execute. There are many types of actions:
<details>
<summary>Types</summary>
* [[CloseAction v3]]
* [[CommandAction v3]]
* [[ConsumeAction v3]]
* [[EmptyAction v3]]
* [[GiveAction v3]]
* [[GroupAction v3]]
* [[MessageAction v3]]
* [[OpenGuiAction v3]]
* [[RequirementAction v3]]
* [[SendPropertyAction v3]]
* [[SoundAction v3]]
</details>

Actions look like:

```
{
  "type": "",
  "requiredIndex": 0,
  "requiredClickType": "",
  "requiredSlotActionType": "",
  "requiredGuiName": "",
  ...other fields...
}
```

## Type (required)
This is the type of the action. This is a string.
<details>
<summary>Types</summary>
* CloseGui
* Command
* Consume
* Empty
* Give
* Group
* Message
* OpenGui
* Require
* SendProperty
* Sound
</details>

## Required Index (optional)
When a slot element is clicked, only executes this action if the slot element is in this index. This is an integer.

## Required Click Type (optional)
When a slot element is clicked, only executes this action if the click was of this type. This is a string.
<details>
<summary>Types</summary>
* MOUSE_LEFT
* MOUSE_RIGHT
* MOUSE_LEFT_SHIFT
* MOUSE_RIGHT_SHIFT
* NUM_KEY_1
* NUM_KEY_2
* NUM_KEY_3
* NUM_KEY_4
* NUM_KEY_5
* NUM_KEY_6
* NUM_KEY_7
* NUM_KEY_8
* NUM_KEY_9
* MOUSE_MIDDLE
* DROP
* CTRL_DROP
* MOUSE_LEFT_OUTSIDE
* MOUSE_RIGHT_OUTSIDE
* MOUSE_LEFT_DRAG_START
* MOUSE_RIGHT_DRAG_START
* MOUSE_MIDDLE_DRAG_START
* MOUSE_LEFT_DRAG_ADD
* MOUSE_RIGHT_DRAG_ADD
* MOUSE_MIDDLE_DRAG_ADD
* MOUSE_LEFT_DRAG_END
* MOUSE_RIGHT_DRAG_END
* MOUSE_MIDDLE_DRAG_END
* MOUSE_DOUBLE_CLICK
* UNKNOWN
* OFFHAND_SWAP
</details>

## Required Slot Action Type (optional)
When a slot element is clicked, only executes this action if the click was of this action. This is a string.
<details>
<summary>Types</summary>
* PICKUP
* QUICK_MOVE
* SWAP
* CLONE
* THROW
* QUICK_CRAFT
* PICKUP_ALL
</details>

## Required Gui Name (optional)
When a slot element is clicked, only executes this action if the slot element is in a gui of this name. This is an identifier (see [[Home v3]] for creation).