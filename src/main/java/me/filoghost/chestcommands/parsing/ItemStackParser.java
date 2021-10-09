/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.parsing;

import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.fcommons.MaterialsHelper;
import me.filoghost.fcommons.Preconditions;
import me.filoghost.fcommons.Strings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackParser {

	private final Item material;
	private int amount = 1;
	private short durability = 0;
	private boolean hasExplicitDurability = false;

	/*
	 * Reads item in the format "material:durability, amount".
	 */
	public ItemStackParser(String input, boolean parseAmount) throws ParseException {
		Preconditions.notNull(input, "input");

		if (parseAmount) {
			// Read the optional amount
			String[] splitAmount = Strings.splitAndTrim(input, ",", 2);

			if (splitAmount.length > 1) {
				try {
					this.amount = NumberParser.getStrictlyPositiveInteger(splitAmount[1]);
				} catch (ParseException e) {
					throw new ParseException(Errors.Parsing.invalidAmount(splitAmount[1]), e);
				}

				// Only keep the first part as input
				input = splitAmount[0];
			}
		}


		// Read the optional durability
		String[] splitByColons = Strings.splitAndTrim(input, ":", 2);

		if (splitByColons.length > 1) {
			try {
				this.durability = NumberParser.getPositiveShort(splitByColons[1]);
			} catch (ParseException e) {
				throw new ParseException(Errors.Parsing.invalidDurability(splitByColons[1]), e);
			}

			this.hasExplicitDurability = true;

			// Only keep the first part as input
			input = splitByColons[0];
		}

		this.material = MaterialParser.parseMaterial(input);
	}

	public void checkNotAir() throws ParseException {
		if (MaterialsHelper.isAir(material)) {
			throw new ParseException(Errors.Parsing.materialCannotBeAir);
		}
	}

	public Item getMaterial() {
		return material;
	}

	public int getAmount() {
		return amount;
	}

	public short getDurability() {
		return durability;
	}

	public boolean hasExplicitDurability() {
		return hasExplicitDurability;
	}

	public ItemStack createStack() {
		ItemStack stack = new ItemStack(material, amount);
		stack.setDamage(durability);
		return stack;
	}
}