package ihl.items_blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class IHLItemBlock extends ItemBlock {
	public Map<Integer, String> nameMap = new HashMap<Integer, String>();

	public IHLItemBlock(Block block1) {
		super(block1);
		this.setFull3D();
		this.setHasSubtypes(true);
		this.setCreativeTab(IHLCreativeTab.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":sackItem");
	}

	public Block getBlockContained() {
		return this.field_150939_a;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return this.field_150939_a.getCreativeTabToDisplayOn();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (!nameMap.isEmpty() && nameMap.containsKey(stack.getItemDamage())) {
			return nameMap.get(stack.getItemDamage());
		} else {
			return this.field_150939_a.getUnlocalizedName();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean flag) {
		if (itemStack.stackTagCompound != null) {
			if (itemStack.stackTagCompound.hasKey("resultSuffix")) {
				String result_suffix = itemStack.stackTagCompound.getString("resultSuffix");
				if (StatCollector.canTranslate("ihl." + result_suffix)) {
					result_suffix = StatCollector.translateToLocal("ihl." + result_suffix);
				}
				info.add(StatCollector.translateToLocal("result_of_molding") + result_suffix);
				if (itemStack.stackTagCompound.hasKey("isContainStearin")
						&& itemStack.stackTagCompound.getBoolean("isContainStearin")) {
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 1: "
							+ StatCollector.translateToLocal("remove_wax_using_muffle_furnace"));
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 2: "
							+ StatCollector.translateToLocal("fill_from_top_with_molten_metal"));
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 3: "
							+ StatCollector.translateToLocal("wait_for_10_seconds"));
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 4: "
							+ StatCollector.translateToLocal("destroy_mold_to_get_results"));

				} else {
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 1: "
							+ StatCollector.translateToLocal("fill_from_top_with_molten_metal"));
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 2: "
							+ StatCollector.translateToLocal("wait_for_10_seconds"));
					info.add(StatCollector.translateToLocal("ihl.tooltip.step") + " 3: "
							+ StatCollector.translateToLocal("destroy_mold_to_get_results"));
				}

			}
			if (itemStack.stackTagCompound.hasKey("detonator_delay")) {
				info.add(StatCollector.translateToLocal("ihl.detonator_delay") + " "
						+ itemStack.stackTagCompound.getInteger("detonator_delay") + " "
						+ StatCollector.translateToLocal("ihl.seconds"));
			}
			if (itemStack.stackTagCompound.hasKey("explosionPower")) {
				info.add(StatCollector.translateToLocal("ihl.explosionPower") + " "
						+ itemStack.stackTagCompound.getInteger("explosionPower") + " "
						+ StatCollector.translateToLocal("ihl.mTNT"));
			}
		}
	}
}
