package megaminds.actioninventory.api.actionobjects;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import megaminds.actioninventory.Helper;
import megaminds.actioninventory.api.helper.ObjectId;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a basic object used in an ActionInventory. For example: ActionItem, Requirement, Action, etc.<br>
 * ActionObject types must be registered with ActionObjectHandler to be able to be deserialized.
 */
public abstract class ActionObject {
	/**
	 * The id of this object. Should be unique.
	 */
	private ObjectId fullId;
	/**
	 * The id of this object's parent.
	 */
	@Nullable
	private ObjectId parent;
	/**
	 * The text the describes this object.
	 */
	private Text display;
	private boolean isDirty;
	private String fileName;
		
	public ActionObject() {
	}
	
	public ActionObject(Identifier id, Text display) {
		this.fullId = new ObjectId(getType(), id);
		this.display = display;
	}
	
	/**
	 * Returns the type of this object. Used for serialization. Should be the same for all instances of a class.
	 */
	protected abstract Identifier getType();
	/**
	 * Deletes the specified child from this object.
	 */
	public abstract void deleteChild(ObjectId child);
	/**
	 * Deletes all of this objects children.
	 */
	public abstract void deleteChildren();
	/**
	 * Returns this objects parent.
	 */
	/**
	 * Deletes this object from its parent.
	 */
	public void deleteSelf() {
		if (getParent()!=null) {
			ActionObjectHandler.getActionObject(getParent()).deleteChild(getFullId());
		}
	}
	public final String getFileName() {
		return fileName;
	}
	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Nullable
	public final ObjectId getParent() {
		return parent;
	}
	/**
	 * Sets this object's parent to the given one.
	 */
	public final void setParent(ObjectId parent) {
		this.parent = parent;
	}
	public final ObjectId getFullId() {
		return fullId;
	}
	public final void setId(Identifier id) {
		if (!this.fullId.id.equals(id)) markDirty();
		this.fullId.id = id;
	}
	public final Text getDisplay() {
		return display;
	}
	public final void setDisplay(Text display) {
		if (!this.display.equals(display)) {
			markDirty();
		}
		this.display = display;
	}
	public final void markDirty() {
		isDirty = true;
		Helper.ifNotNullDo(ActionObjectHandler.getActionObject(getParent()), p->p.markDirty());
	}
	public final boolean isDirty() {
		return isDirty;
	}
	/**
	 * Writes this objects data to a JsonObject. Most cases should call super.writeData() to write hierarchy data.
	 */
	public void writeData(JsonObject obj) {
		obj.addProperty("type", fullId.type.toString());
		obj.addProperty("id", fullId.id.toString());
		obj.add("display", Text.Serializer.toJsonTree(display));
	}
	/**
	 * Reads this object's data in from a JsonObject. Most cases should call super.readData() to read hierarchy data.<br>
	 * Should be able to read from the same JsonObject that was passed into {@link #writeData(JsonObject)}.
	 */
	public void readData(JsonObject obj) {
		this.fullId = new ObjectId(getType(), new Identifier(obj.get("id").getAsString()));
		this.display = Text.Serializer.fromJson(obj.get("display"));
	}
}