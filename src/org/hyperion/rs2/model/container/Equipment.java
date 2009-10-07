package org.hyperion.rs2.model.container;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.model.Item;

/**
 * Contains equipment utility methods.
 * @author Graham Edgecombe
 * @author Lothy
 *
 */
public class Equipment {
	
	/**
	 * The size of the equipment container.
	 */
	public static final int SIZE = 14;
	
	/**
	 * Items which are classified as capes.
	 */
	public static int CAPES[] = { 3781, 3783, 3785, 3787, 3789, 3777, 3779,
		3759, 3761, 3763, 3765, 6111, 6570, 6568, 1007, 1019, 1021, 1023,
		1027, 1029, 1031, 1052, 2412, 2413, 2414, 4304, 4315, 4317, 4319,
		4321, 4323, 4325, 4327, 4329, 4331, 4333, 4335, 4337, 4339, 4341,
		4343, 4345, 4347, 4349, 4351, 4353, 4355, 4357, 4359, 4361, 4363,
		4365, 4367, 4369, 4371, 4373, 4375, 4377, 4379, 4381, 4383, 4385,
		4387, 4389, 4391, 4393, 4395, 4397, 4399, 4401, 4403, 4405, 4407,
		4409, 4411, 4413, 4514, 4516, 6070, 6568, 6570 };

	/**
	 * Items which are classified as boots.
	 */
	public static int BOOTS[] = { 7596, 6619, 7159, 7991, 6666, 6061, 6106, 88,
		89, 626, 628, 630, 632, 634, 1061, 1837, 1846, 2577, 2579, 2894,
		2904, 2914, 2924, 2934, 3061, 3105, 3107, 3791, 4097, 4107, 4117,
		4119, 4121, 4123, 4125, 4127, 4129, 4131, 4310, 5064, 5345, 5557,
		6069, 6106, 6143, 6145, 6147, 6328, 6920, 6349, 6357, 3393 };

	/**
	 * Items which are classified as gloves.
	 */
	public static int GLOVES[] = { 7595, 6629, 2491, 1065, 2487, 2489, 3060,
		1495, 775, 777, 778, 6708, 1059, 1063, 1065, 1580, 2487, 2489,
		2491, 2902, 2912, 2922, 2932, 2942, 3060, 3799, 4095, 4105, 4115,
		4308, 5556, 6068, 6110, 6149, 6151, 6153, 6922, 7454, 7455, 7456,
		7457, 7458, 7459, 7460, 7461, 7462, 6330, 3391 };

	/**
	 * Items which are classified as shields.
	 */
	public static int SHIELDS[] = { 7342, 7348, 7354, 7360, 7334, 7340, 7347,
		7352, 7358, 7356, 7350, 7344, 7332, 7338, 7336, 7360, 1171, 1173,
		1175, 1177, 1179, 1181, 1183, 1185, 1187, 1189, 1191, 1193, 1195,
		1197, 1199, 1201, 1540, 2589, 2597, 2603, 2611, 2621, 2629, 2659,
		2667, 2675, 2890, 3122, 3488, 3758, 3839, 3840, 3841, 3842, 3843,
		3844, 4072, 4156, 4224, 4225, 4226, 4227, 4228, 4229, 4230, 4231,
		4232, 4233, 4234, 4302, 4507, 4512, 6215, 6217, 6219, 6221, 6223,
		6225, 6227, 6229, 6231, 6233, 6235, 6237, 6239, 6241, 6243, 6245,
		6247, 6249, 6251, 6253, 6255, 6257, 6259, 6261, 6263, 6265, 6267,
		6269, 6271, 6273, 6275, 6277, 6279, 6524, 6889 };

	/**
	 * Items which are classified as hats.
	 */
	public static int HATS[] = { 4041, 4042, 4502, 7319, 7321, 7323, 7325,
		7327, 1167, 8074, 4168, 1169, 6665, 6665, 7321, 6886, 6547, 6548,
		2645, 2647, 2649, 4856, 4857, 4858, 4859, 4880, 4881, 4882, 4883,
		4904, 4905, 4906, 4907, 4928, 4929, 4930, 4931, 4952, 4953, 4954,
		4955, 4976, 4977, 4978, 4979, 4732, 4753, 4611, 6188, 6182, 4511,
		4056, 4071, 4724, 2639, 2641, 2643, 2665, 6109, 5525, 5527, 5529,
		5531, 5533, 5535, 5537, 5539, 5541, 5543, 5545, 5547, 5549, 5551,
		74, 579, 656, 658, 660, 662, 664, 740, 1017, 1037, 1040, 1042,
		1044, 1046, 1048, 1050, 1053, 1055, 1057, 1137, 1139, 1141, 1143,
		1145, 1147, 1149, 1151, 1153, 1155, 1157, 1159, 1161, 1163, 1165,
		1506, 1949, 2422, 2581, 2587, 2595, 2605, 2613, 2619, 2627, 2631,
		2633, 2635, 2637, 2651, 2657, 2673, 2900, 2910, 2920, 2930, 2940,
		2978, 2979, 2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988,
		2989, 2990, 2991, 2992, 2993, 2994, 2995, 3057, 3385, 3486, 3748,
		3749, 3751, 3753, 3797, 4071, 4089, 3755, 4099, 4109, 4164, 4302,
		4506, 4511, 4513, 4515, 4551, 4567, 4708, 4716, 4724, 4745, 4753,
		4856, 4857, 4858, 4859, 4880, 4881, 4882, 4883, 4904, 4905, 4906,
		4907, 4952, 4953, 4954, 4955, 4976, 4977, 4978, 4979, 5013, 5014,
		5554, 5574, 6109, 6128, 6131, 6137, 6182, 6188, 6335, 6337, 6339,
		6345, 6355, 6365, 6375, 6382, 6392, 6400, 6918, 6656, 2581, 7539,
		7394, 7396, 7534, 5574, 6885, 6858, 6860, 6862, 6856, 6326, 6128,
		6137, 7400, 7323, 7325, 7327, 7003 };

	/**
	 * Items which are classified as amulets.
	 */
	public static int AMULETS[] = { 1654, 1656, 1658, 1660, 1662, 1664, 8081,
		8033, 7968, 6585, 86, 87, 295, 421, 552, 589, 1478, 1692, 1694,
		1696, 1698, 1700, 1702, 1704, 1706, 1708, 1710, 1712, 1725, 1727,
		1729, 1731, 4021, 4081, 4250, 4677, 6040, 6041, 6208, 1718, 1722,
		6859, 6863, 6857 };

	/**
	 * Items which are classified as arrows.
	 */
	public static int ARROWS[] = { 8052, 8065, 7919, 7906, 78, 598, 877, 878,
		879, 880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 890, 891,
		892, 893, 942, 2532, 2533, 2534, 2535, 2536, 2537, 2538, 2539,
		2540, 2541, 2866, 4160, 4172, 4173, 4174, 4175, 4740, 5616, 5617,
		5618, 5619, 5620, 5621, 5622, 5623, 5624, 5625, 5626, 5627, 6061,
		6062 };

	/**
	 * Items which are classified as rings.
	 */
	public static int RINGS[] = { 773, 1635, 1637, 1639, 1641, 1643, 1645,
		2550, 2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566, 2568, 2570,
		2572, 4202, 4657, 6465, 6737, 6731, 6735, 6735, 6583, 6733 };

	/**
	 * Items which are classified as body.
	 */
	public static int BODY[] = { 7362, 7364, 636, 638, 640, 642, 644, 426,
		1005, 1757, 7592, 6617, 7376, 544, 7372, 7370, 577, 3793, 3775,
		3773, 3771, 3769, 3767, 6139, 1135, 2499, 2501, 1035, 540, 5553,
		4757, 1833, 6388, 6384, 4111, 4101, 4091, 6186, 6184, 6180, 3058,
		4509, 4504, 4069, 4728, 4736, 4712, 6107, 2661, 3140, 1101, 1103,
		1105, 1107, 1109, 1111, 1113, 1115, 1117, 1119, 1121, 1123, 1125,
		1127, 1129, 1131, 1133, 2583, 2591, 2599, 2607, 2615, 2623, 2653,
		2669, 3481, 4712, 4720, 4728, 4749, 4892, 4893, 4894, 4895, 4916,
		4917, 4918, 4919, 4964, 4965, 4966, 4967, 6107, 6133, 6322, 6322,
		6129, 75, 6916, 6916, 4111, 6654, 6654, 75, 7399, 7390, 7374, 5575,
		2503, 6341, 6351, 3387, 5030, 5032, 5034, 5030, 5032, 5034, 7392,
		546 };

	/**
	 * Items which are classified as legs.
	 */
	public static int LEGS[] = { 7378, 7380, 7382, 7368, 7366, 7388, 646, 648,
		650, 652, 654, 428, 1097, 1095, 7593, 6625, 8020, 8015, 7384, 6141,
		1835, 538, 1033, 5555, 4759, 6386, 6390, 2497, 2495, 2493, 1099,
		4113, 4103, 4093, 6924, 6187, 6185, 6181, 3059, 4510, 4505, 4070,
		6108, 538, 542, 548, 1011, 1013, 1015, 1067, 1069, 1071, 1073,
		1075, 1077, 1079, 1081, 1083, 1085, 1087, 1089, 1091, 1093, 2585,
		2593, 2601, 2609, 2617, 2625, 2655, 2663, 2671, 3059, 3389, 3472,
		3473, 3474, 3475, 3476, 3477, 3478, 3479, 3480, 3483, 3485, 3795,
		4087, 4585, 4712, 4714, 4722, 4730, 4738, 4751, 4759, 4874, 4875,
		4876, 4877, 4898, 4899, 4900, 4901, 4922, 4923, 4924, 4925, 4946,
		4947, 4948, 4949, 4970, 4971, 4972, 4973, 4994, 4995, 4996, 4997,
		5048, 5050, 5052, 5576, 6107, 6130, 6187, 6390, 6386, 6390, 6396,
		6404, 6135, 6809, 6916, 4091, 4111, 6655, 6654, 7398, 7398, 7386,
		6324, 6343, 6353, 3387, 5036, 5038, 5040 };

	/**
	 * Items which are classified as platebody.
	 */
	public static int PLATEBODY[] = { 636, 638, 640, 642, 644, 426, 8031, 8027,
		6617, 544, 577, 3793, 3773, 3775, 3771, 3769, 3767, 6139, 1035,
		540, 5553, 4757, 1833, 1835, 6388, 6384, 4111, 4101, 4868, 4869,
		4870, 4871, 4892, 4893, 4894, 4895, 4916, 4917, 4918, 4919, 4940,
		4941, 4942, 4943, 4964, 4965, 4966, 4967, 4988, 4989, 4990,
		0x2f9a0eb, 6186, 6184, 6180, 3058, 4509, 4504, 4069, 4728, 4736,
		4712, 6107, 2661, 3140, 1115, 1117, 1119, 1121, 1123, 1125, 1127,
		2583, 2591, 2599, 2607, 2615, 6322, 2623, 2653, 2669, 3481, 4720,
		4728, 4749, 2661, 6129, 6916, 4091, 6654, 6133, 75, 7399, 7390,
		5575, 6341, 6351, 3387, 5030, 5032, 5034, 7392 };

	/**
	 * Items which are classified as full helmets.
	 */
	public static int FULL_HELM[] = { 4041, 4042, 1147, 3748, 6137, 6128, 3753,
		3755, 3749, 3751, 1149, 3751, 7594, 4708, 4716, 4745, 4732, 5554,
		4753, 4732, 4753, 6188, 4511, 4056, 4071, 4724, 6109, 2665, 1153,
		1155, 1157, 1159, 1161, 1163, 1165, 2587, 2595, 2605, 2613, 2619,
		2627, 2657, 2673, 3486, 6402, 6394, 6131, 74, 7539, 7539, 7534,
		5574, 6326 };

	/**
	 * Items which are classified as full masks.
	 */
	public static int FULL_MASK[] = { 4502, 6623, 7990, 7594, 1153, 1155, 1157,
		1159, 1161, 1163, 1165, 4732, 5554, 4753, 4611, 6188, 3507, 4511,
		4056, 4071, 4724, 2665, 1053, 1055, 1057 };
	
	/**
	 * The helmet slot.
	 */
	public static final int SLOT_HELM = 0;
	
	/**
	 * The cape slot.
	 */
	public static final int SLOT_CAPE = 1;
	
	/**
	 * The amulet slot.
	 */
	public static final int SLOT_AMULET = 2;
	
	/**
	 * The weapon slot.
	 */
	public static final int SLOT_WEAPON = 3;
	
	/**
	 * The chest slot.
	 */
	public static final int SLOT_CHEST = 4;
	
	/**
	 * The shield slot.
	 */
	public static final int SLOT_SHIELD = 5;
	
	/**
	 * The bottoms slot.
	 */
	public static final int SLOT_BOTTOMS = 7;
	
	/**
	 * The gloves slot.
	 */
	public static final int SLOT_GLOVES = 9;
	
	/**
	 * The boots slot.
	 */
	public static final int SLOT_BOOTS = 10;
	
	/**
	 * The rings slot.
	 */
	public static final int SLOT_RING = 12;
	
	/**
	 * The arrows slot.
	 */
	public static final int SLOT_ARROWS = 13;
	
	/**
	 * Equipment type map.
	 */
	private static final Map<Integer, EquipmentType> equipmentTypes = new HashMap<Integer, EquipmentType>();

	/**
	 * Equipment interface id.
	 */
	public static final int INTERFACE = 1688;
	
	/**
	 * Equipment type enum.
	 * @author Lothy
	 * @author Miss Silabsoft
	 *
	 */
	public enum EquipmentType {
		CAPE("Cape", Equipment.SLOT_CAPE),
		BOOTS("Boots", Equipment.SLOT_BOOTS),
		GLOVES("Gloves", Equipment.SLOT_GLOVES),
		SHIELD("Shield", Equipment.SLOT_SHIELD),
		HAT("Hat", Equipment.SLOT_HELM),
		AMULET("Amulet", Equipment.SLOT_AMULET),
		ARROWS("Arrows", Equipment.SLOT_ARROWS),
		RING("Ring", Equipment.SLOT_RING),
		BODY("Body", Equipment.SLOT_CHEST),
		LEGS("Legs", Equipment.SLOT_BOTTOMS),
		PLATEBODY("Plate body", Equipment.SLOT_CHEST),
		FULL_HELM("Full helm", Equipment.SLOT_HELM),
		FULL_MASK("Full mask", Equipment.SLOT_HELM),
		WEAPON("Weapon", Equipment.SLOT_WEAPON);
		
		/**
		 * The description.
		 */
		private String description;
		
		/**
		 * The slot.
		 */
		private int slot;
		
		/**
		 * Creates the equipment type.
		 * @param description The description.
		 * @param slot The slot.
		 */
		private EquipmentType(String description, int slot) {
			this.description = description;
			this.slot = slot;
		}
		
		/**
		 * Gets the description.
		 * @return The description.
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * Gets the slot.
		 * @return The slot.
		 */
		public int getSlot() {
			return slot;
		}
		
	}
	
	/**
	 * Static initializer block to populate the type map.
	 */
	static {
		for(int cape : CAPES) {
			equipmentTypes.put(cape, EquipmentType.CAPE);
		}
		for(int boots : BOOTS) {
			equipmentTypes.put(boots, EquipmentType.BOOTS);
		}
		for(int gloves : GLOVES) {
			equipmentTypes.put(gloves, EquipmentType.GLOVES);
		}
		for(int shield : SHIELDS) {
			equipmentTypes.put(shield, EquipmentType.SHIELD);
		}
		for(int hat : HATS) {
			equipmentTypes.put(hat, EquipmentType.HAT);
		}
		for(int amulet : AMULETS) {
			equipmentTypes.put(amulet, EquipmentType.AMULET);
		}
		for(int arrowType : ARROWS) {
			equipmentTypes.put(arrowType, EquipmentType.ARROWS);
		}		
		for(int ring : RINGS) {
			equipmentTypes.put(ring, EquipmentType.RING);
		}
		for(int body : BODY) {
			equipmentTypes.put(body, EquipmentType.BODY);
		}
		for(int legs : LEGS) {
			equipmentTypes.put(legs, EquipmentType.LEGS);
		}
		for(int plateBody : PLATEBODY) {
			equipmentTypes.put(plateBody, EquipmentType.PLATEBODY);
		}
		for(int fullHelm : FULL_HELM) {
			equipmentTypes.put(fullHelm, EquipmentType.FULL_HELM);
		}
		for(int fullMask : FULL_MASK) {
			equipmentTypes.put(fullMask, EquipmentType.FULL_MASK);
		}
	}
	
	/**
	 * Gets an equipment type.
	 * @param item The item.
	 * @return The equipment type.
	 */
	public static EquipmentType getType(Item item) {
		int id = item.getId();
		if(equipmentTypes.containsKey(id)) {
			return equipmentTypes.get(id);
		} else {
			return EquipmentType.WEAPON;
		}
	}
	
	/**
	 * Checks if an item is of a specific type.
	 * @param type The type.
	 * @param item The item.
	 * @return <code>true</code> if the types are the same, <code>false</code>
	 * if not.
	 */
	public static boolean is(EquipmentType type, Item item) {
		return getType(item).equals(type);
	}

}
