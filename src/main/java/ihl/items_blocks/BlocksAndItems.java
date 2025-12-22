package ihl.items_blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import ihl.IHLCreativeTab;
import ihl.IHLModInfo;
import ihl.crop_harvestors.RubberTreeBlock;
import ihl.crop_harvestors.SackBlock;
import ihl.crop_harvestors.SackTileEntity;
import ihl.enviroment.MirrorBlock;
import ihl.explosion.ExplosiveBlock;
import ihl.explosion.GroundRemoverItem;
import ihl.explosion.PileBlock;
import ihl.flexible_cable.AnchorBlock;
import ihl.flexible_cable.AnchorTileEntity;
import ihl.handpump.AdvancedHandPump;
import ihl.handpump.IHLHandPump;
import ihl.processing.chemistry.*;
import ihl.processing.metallurgy.Crucible;
import ihl.processing.metallurgy.ElectricEngineItem;
import ihl.processing.metallurgy.InjectionMoldBlock;
import ihl.servitor.BoneBlock;
import ihl.tile_entity.machines.ElectricEvaporatorTileEntity;
import ihl.tile_entity.machines.EvaporatorTileEntity;
import ihl.trans_dimensional_item_teleporter.TDITBlock;
import ihl.trans_dimensional_item_teleporter.TDITFrequencyTransmitter;
import ihl.trans_dimensional_item_teleporter.TDITTileEntity;
import ihl.worldgen.ores.BlockOre;
import ihl.worldgen.ores.DebugScannerBlock;
import ihl.worldgen.ores.DebugScannerTileEntity;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class BlocksAndItems {
	public static Block cableAnchorBlock;
	public static Block sackBlock;
	public static Block rubberTreeBlock;
	public static Block spruceTreeBlock;
	public static Block evaporatorBlock;
	public static Block electricEvaporatorBlock;
	public static Block boneBlock = (new BoneBlock(Material.coral)).setBlockName("boneBlock").setHardness(2.0F)
		.setResistance(2.0F);
	public static Item ic2_handpump;
	public static Item ic2_advanced_handpump;
	public static Item ihlSkull = (new Item()).setUnlocalizedName("skull").setFull3D()
		.setCreativeTab(CreativeTabs.tabMisc).setTextureName(IHLModInfo.MODID + ":skull");
	public static Block ic2Leaves;
	public static Block ic2Wood;
	public static Block tditBlock = (new TDITBlock(Material.glass)).setBlockName("tditBlock")
		.setBlockTextureName(IHLModInfo.MODID + ":tditRight").setHardness(0.5F).setResistance(0.5F);
	public static Item tditft;
	public static Block ds = new DebugScannerBlock(Material.ice).setBlockName("debugScanner")
		.setBlockTextureName(IHLModInfo.MODID + ":tditTop");
	public static Item crucible;


	public static void RegisterBlocksAndItems(){

		rubberTreeBlock = (new RubberTreeBlock(RubberTreeBlock.TreeType.RUBBERTREE)).setBlockName("rubberTreeBlock")
			.setBlockTextureName(IHLModInfo.MODID + ":blockRubWoodFront").setHardness(2.0F).setResistance(5.0F);
		spruceTreeBlock = (new RubberTreeBlock(RubberTreeBlock.TreeType.SPRUCE)).setBlockName("spruceBlock")
			.setBlockTextureName(IHLModInfo.MODID + ":blockSpruceFront").setHardness(2.0F).setResistance(5.0F)
			.setCreativeTab(IHLCreativeTab.tab);



		FiberItem.init();
		FlexiblePipeItem.init();
		InjectionMoldBlock.init();
		ElectricEngineItem.init();
		BlockOre.init();
		IHLFluid.init();

		ItemSubstance.init();
		BatteryItem.init();
		IHLTool.init();
		MirrorBlock.init();
		MachineBaseBlock.init();// other things must be first

		GameRegistry.registerBlock(ds, "debugScanner");
		GameRegistry.registerTileEntity(DebugScannerTileEntity.class, "DebugScanner");
		cableAnchorBlock = new AnchorBlock("cableAnchor");
		List<String> info1 = new ArrayList<String>();
		info1.add("non vulcanized rubber insulated");
		GroundRemoverItem.init();
		FlexibleCableItem.init();
		ExplosiveBlock.init();
		PileBlock.init();
		GameRegistry.registerTileEntity(AnchorTileEntity.class, "anchorTileEntity");

		crucible = new Crucible();

		ic2_handpump = new IHLHandPump().setUnlocalizedName("handpump");
		ic2_advanced_handpump = new AdvancedHandPump().setUnlocalizedName("advanced_handpump");
		tditft = new TDITFrequencyTransmitter().setUnlocalizedName("tditFrequencyTransmitter");

		evaporatorBlock = (new EvaporatorBlock(Material.iron)).setBlockName("evaporatorBlock")
			.setBlockTextureName(IHLModInfo.MODID + ":solidFuelEvaporatorFrontActive").setHardness(5.0F)
			.setResistance(5.0F);
		electricEvaporatorBlock = (new ElectricEvaporatorBlock(Material.iron)).setBlockName("electricEvaporatorBlock")
			.setBlockTextureName(IHLModInfo.MODID + ":electricEvaporatorFrontActive").setHardness(5.0F)
			.setResistance(5.0F);
		sackBlock = (new SackBlock(Material.iron)).setBlockName("sackBlock")
			.setBlockTextureName(IHLModInfo.MODID + ":sackItem").setHardness(0.5F).setResistance(0.5F);

		GameRegistry.registerBlock(boneBlock, "boneBlock");

		GameRegistry.registerItem(ic2_handpump, "Handpump");
		GameRegistry.registerItem(ic2_advanced_handpump, ic2_advanced_handpump.getUnlocalizedName());
		GameRegistry.registerItem(ihlSkull, "skull");

		GameRegistry.registerBlock(rubberTreeBlock, "rubberTreeBlock");
		GameRegistry.registerBlock(spruceTreeBlock, "spruceTreeBlock");

		GameRegistry.registerBlock(sackBlock, "sackBlock");
		GameRegistry.registerTileEntity(SackTileEntity.class, "sackTileEntity");

		GameRegistry.registerBlock(evaporatorBlock, "evaporatorBlock");
		GameRegistry.registerBlock(electricEvaporatorBlock, "electricEvaporatorBlock");
		GameRegistry.registerTileEntity(EvaporatorTileEntity.class, "evaporatorTileEntity");
		GameRegistry.registerTileEntity(ElectricEvaporatorTileEntity.class, "electricEvaporatorTileEntity");

		GameRegistry.registerBlock(tditBlock, "tditBlock");
		GameRegistry.registerTileEntity(TDITTileEntity.class, "tditTileEntity");

		GameRegistry.registerItem(tditft, "tditftItem");
	}
}
