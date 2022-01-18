## Normal Slot Element
A normal slot element displays a single item stack and executes an action when clicked.

It looks like:

```
{
  "type": "Normal",
  "index": 3,
  "item": {},
  "action": {}
}
```

## Type (required)
Specifies the type of the slot element. This should be "Normal".

## Index (defaults to the index of the next open slot)
Which slot of the inventory the element should display in. Each slot element in an inventory should have a different index. This is an integer.

## Item (defaults to an empty item stack)
This is the item that is displayed. This is an object. See [[Items v3]].

## Action (defaults to no action)
This is the action to execute when clicked. This is an object. See [[Actions v3]].