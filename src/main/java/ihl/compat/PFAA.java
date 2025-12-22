package ihl.compat;

import cpw.mods.fml.common.Loader;
import ihl.utils.IHLUtils;
import net.minecraft.item.ItemStack;

public final class PFAA {
	public static ItemStack LIMESTONE;
	public static ItemStack COBBLE_LIMESTONE;

	public static void init() {
		if (!Loader.isModLoaded("PFAAGeologica")){
			return;
		}

		LIMESTONE = IHLUtils.getOtherModItemStackWithDamage("PFAAGeologica", "mediumStone", 0, 1);
		COBBLE_LIMESTONE = IHLUtils.getOtherModItemStackWithDamage("PFAAGeologica", "mediumCobble", 0, 1);
	}
}
