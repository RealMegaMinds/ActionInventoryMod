/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.parsing;

import me.filoghost.chestcommands.logging.Errors.Parsing;
import me.filoghost.fcommons.MaterialsHelper;
import net.minecraft.item.Item;

public class MaterialParser {

    public static Item parseMaterial(String materialName) throws ParseException {
        Item material = MaterialsHelper.matchMaterial(materialName);

        if (material == null) {
            throw new ParseException(Parsing.unknownMaterial(materialName));
        }
        return material;
    }

}
