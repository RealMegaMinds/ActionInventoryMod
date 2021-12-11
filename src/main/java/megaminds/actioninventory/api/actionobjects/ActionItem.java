package megaminds.actioninventory.api.actionobjects;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import megaminds.actioninventory.ActionInventoryMod;
import megaminds.actioninventory.Helper;
import megaminds.actioninventory.api.helper.ObjectId;
import net.minecraft.util.Identifier;

public class ActionItem extends ActionObject {
	private static final Identifier TYPE =  new Identifier(ActionInventoryMod.MOD_ID, "ActionItem");
	
	private Requirement requirement;
	private List<Action> actions;
	
	@Override
	protected Identifier getType() {
		return TYPE;
	}
	
	public void setRequirement(Requirement req) {
		this.requirement = req;
	}
	
	public void addAction(Action action) {
		if (action!=null) actions.add(action);
	}

	@Override
	public void deleteChild(ObjectId child) {
		if (requirement!=null && child.equals(requirement.getFullId())) {
			requirement = null;
			return;
		}
		Helper.removeFirst(actions, a->child.equals(a.getFullId()));
	}

	@Override
	public void deleteChildren() {
		actions.clear();
		requirement = null;
	}

	@Override
	public void writeData(JsonObject obj) {
		super.writeData(obj);
		if (requirement!=null) {
			JsonObject req = new JsonObject();
			requirement.writeData(req);
			obj.add("requirement", req);
		}
		JsonArray arr = new JsonArray();
		for (Action a : actions) {
			JsonObject act = new JsonObject();
			a.writeData(act);
			arr.add(act);
		}
		obj.add("actions", arr);
	}

	@Override
	public void readData(JsonObject obj) {
		super.readData(obj);
		requirement = Helper.readTyped(obj.get("requirement").getAsJsonObject(), s->(Requirement)ActionObjectHandler.getActionObject(ObjectId.fromString(s)));
		JsonArray arr = obj.get("actions").getAsJsonArray();
		for (JsonElement e : arr) {
			actions.add(Helper.readTyped(e.getAsJsonObject(), s->(Action)ActionObjectHandler.getActionObject(ObjectId.fromString(s))));
		}
	}
}