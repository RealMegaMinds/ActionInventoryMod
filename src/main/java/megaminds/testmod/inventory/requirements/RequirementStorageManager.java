package megaminds.testmod.inventory.requirements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import megaminds.testmod.TestMod;
import net.minecraft.server.network.ServerPlayerEntity;

public class RequirementStorageManager {
	private static final String fileName = "Paid Requirements.ser";
	private static Map<Requirement, Map<UUID, Object>> paidRequirements;
	
	public static Object getPayment(Requirement req, ServerPlayerEntity player, Object defaultObj) {
		Map<UUID, Object> map = paidRequirements.get(req);
		return map!=null ? map.get(player.getUuid()) : defaultObj;
	}
	public static Object setPayment(Requirement req, ServerPlayerEntity player, Object payment) {
		return paidRequirements.getOrDefault(req, new HashMap<>()).put(player.getUuid(), payment);
	}
	public static void removePayment(Requirement req, ServerPlayerEntity player) {
		Map<UUID, Object> map = paidRequirements.get(req);
		if (map!=null) map.remove(player.getUuid());
	}
	
	@SuppressWarnings("unchecked")
	public static void onStartup(Path inventoryFolder) {
		File f = new File(inventoryFolder.toFile(), fileName);
		try {
			boolean created = f.createNewFile();
			if (!created && f.isDirectory()) {
				TestMod.log(Level.WARN, f+" must be a file not a folder");
				if (paidRequirements==null) paidRequirements = new HashMap<>();
				return;
			}
		} catch (IOException e) {
			TestMod.log(Level.WARN, "Failed to create a file for paidRequirements");
			if (paidRequirements==null) paidRequirements = new HashMap<>();
			return;
		}
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
			paidRequirements = (Map<Requirement, Map<UUID, Object>>) in.readObject();
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			TestMod.log(Level.WARN, "Unable to read paidRequirements.");
			if (paidRequirements==null) paidRequirements = new HashMap<>();
			return;
		}
		if (paidRequirements==null) paidRequirements = new HashMap<>();
		TestMod.info("Loaded Stored Paid Requirements");
	}
	
	public static void onShutdown(Path inventoryFolder) {
		if (paidRequirements==null) return;

		File f = new File(inventoryFolder.toFile(), fileName);
		try {
			boolean created = f.createNewFile();
			if (!created && f.isDirectory()) {
				TestMod.log(Level.WARN, f+" must be a file not a folder");
				return;
			}
		} catch (IOException e) {
			TestMod.log(Level.WARN, "Failed to create a file for paidRequirements");
			return;
		}
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
			out.writeObject(paidRequirements);
		} catch (IOException e) {
			TestMod.log(Level.WARN, "Unable to save paidRequirements");
			return;
		}
		TestMod.info("Saved Paid Requirements");
	}
}