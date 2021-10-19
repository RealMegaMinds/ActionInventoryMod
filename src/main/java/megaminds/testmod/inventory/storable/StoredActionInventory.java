package megaminds.testmod.inventory.storable;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class StoredActionInventory {
	public final Text name;
	public final int rows;
	public final List<StoredItem> items;
	public final ImmutableList<StoredOpenChecker> openCheckers;
	
	public StoredActionInventory() {
		this.name = null;
		this.rows = -1;
		this.items = null;
		this.openCheckers = null;
	}
	
	public StoredActionInventory(Text name, int rows, List<StoredItem> items, List<StoredOpenChecker> openCheckers) {
		this.name = name;
		this.rows = rows;
		this.openCheckers = ImmutableList.copyOf(openCheckers);
		DefaultedList<StoredItem> list = DefaultedList.ofSize(rows*9);
		items.forEach(i->{if (i.slot>=0&&i.slot<list.size()) list.add(i.slot, i);});
		this.items = Collections.unmodifiableList(list);
	}
}