## Common Types
These are just a list of some common types used by action inventories.

## Identifier
This is a string. Identifiers are composed of a namespace and path split by a colon. ex: `"namespace:path"` The namespace and path must contain only lowercase letters ([a-z]), digits ([0-9]), or the characters '_', '.', and '-'. The path can also contain the standard path separator '/'. Identifiers that only have a path ex: `"path"` default to `"minecraft:path"`.

## UUID
This is a string. Dashes can be used but are not required.

## Text
Use a string for unformatted text. Use an object for formatted text. Visit [here](https://minecraft.tools/en/json_text.php) for help creating text.