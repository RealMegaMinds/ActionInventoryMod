## Consume Action
Consume actions consume things called consumables.

They look like:
```
{
  ...common fields...
  "actions": [],
  "consumables": [],
  "singlePay": false,
  "requireFull": false
}
```
(common fields are specified in [[Actions v3]])

## Actions (defaults to no actions)
These are the actions that will be executed after all consumables have been consumed. This is an array of objects. See [[Actions v3]].

## Consumables (defaults to no consumables)
These are the consumables that get consumed. This is an array of objects. See [[Consumables v3]].

## Single Pay (defaults to false)
Whether people only need to pay once (true->pay first time, false->pay every time). This is a boolean.

## Require Full (defaults to false)
Whether the full amount needs to be paid at once. (true->nothing is consumed until everything can be, false->will consume as much as possible). Regardless of choice, no actions are executed until the full amount has been consumed. This is a boolean.