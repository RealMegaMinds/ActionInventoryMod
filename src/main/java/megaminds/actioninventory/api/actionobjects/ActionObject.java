package megaminds.actioninventory.api.actionobjects;

import com.google.gson.JsonObject;

import megaminds.actioninventory.api.util.ObjectId;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a basic object used in an ActionInventory. For example: ActionItem, Requirement, Action, etc.<br>
 * ActionObject types must be registered with ActionObjectHandler to be able to be deserialized.
 */
public abstract class ActionObject {
	private ObjectId id;
	private Text display;
	private boolean isDirty;
	private String fileName;
		
	public ActionObject() {
	}
	
	public ActionObject(ObjectId id, Text display) {
		this.id = id;
		this.display = display;
		markDirty();
	}
	
	/**
	 * Deletes the specified child from this object.
	 */
	public abstract void deleteChild(ObjectId child);
	/**
	 * Deletes all of this objects children.
	 */
	public abstract void deleteChildren();
	/**
	 * Returns the name of the file where this ActionObject is currently stored. Files may store multiple ActionObjects.
	 */
	public final String getFileName() {
		return fileName;
	}
	/**
	 * Changes the name of the file where this ActionObject will be stored to the given name.
	 */
	public final void setFileName(String fileName) {
		if (!this.fileName.equals(fileName)) {
			this.fileName = fileName;
			markDirty();
		}
	}
	/**
	 * Returns this ActionObjects id.
	 */
	public final ObjectId getId() {
		return id;
	}
	/**
	 * Sets this ActionObjects id.
	 */
	public final void setId(Identifier id) {
		if (!this.id.id.equals(id)) {
			this.id.id = id;
			markDirty();
		}
	}
	/**
	 * Returns the Text of how this ActionObject is displayed.
	 */
	public final Text getDisplay() {
		return display;
	}
	/**
	 * Sets the Text of how this ActionObject is displayed.
	 */
	public final void setDisplay(Text display) {
		if (!this.display.equals(display)) {
			this.display = display;
			markDirty();
		}
	}
	/**
	 * Marks this AcionObject in need of saving.
	 */
	public final void markDirty() {
		isDirty = true;
	}
	/**
	 * Returns whether this ActionObject needs to be saved.
	 */
	public final boolean isDirty() {
		return isDirty;
	}
	/**
	 * Writes this objects data to a JsonObject. Most cases should call super.writeData() to write hierarchy data.
	 */
	public void writeData(JsonObject obj) {
		id.writeData(obj);
		obj.add("display", Text.Serializer.toJsonTree(display));
	}
	/**
	 * Reads this object's data in from a JsonObject. Most cases should call super.readData() to read hierarchy data.<br>
	 * Should be able to read from the same JsonObject that was passed into {@link #writeData(JsonObject)}.
	 */
	public void readData(JsonObject obj) {
		this.id = ObjectId.readData(obj);
		this.display = Text.Serializer.fromJson(obj.get("display"));
	}
	/**
	 * Writes the given ActionObject's data to a new JsonObject and returns the JsonObject.
	 */
	public static JsonObject writeData(ActionObject obj) {
		JsonObject jsonObj = new JsonObject();
		obj.writeData(jsonObj);
		return jsonObj;
	}
}