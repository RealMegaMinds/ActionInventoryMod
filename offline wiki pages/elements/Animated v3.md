## Animated Slot Element
An animated slot element cycles through the item stack which is on display and executes an action when clicked.

It looks like:

```
{
  "type": "Animated",
  "index": 3,
  "interval": 0,
  "random": false,
  "items": [],
  "action": {}
}
```

## Type (required)
Specifies the type of the slot element. This should be "Animated".

## Index (defaults to the index of the next open slot)
Which slot of the inventory the element should display in. Each slot element in an inventory should have a different index. This is an integer.

## Interval (defaults to 0)
Specifies how fast long an item stack is shown before changing. This is an integer.

## Random (defaults to false)
Whether the displayed stack should be chosen randomly or go in order. This is a boolean.

## Items (defaults to a single empty item stack)
These are the items that can be displayed. This is an array of objects. See [[Items v3]].

## Action (defaults to no action)
This is the action to execute when clicked. This is an object. See [[Actions v3]].