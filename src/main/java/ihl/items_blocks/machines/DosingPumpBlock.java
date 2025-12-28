package ihl.items_blocks.machines;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.item.tool.ItemToolCutter;
import ihl.IHLCreativeTab;
import ihl.IHLMod;
import ihl.IHLModInfo;
import ihl.flexible_cable.BatterySwitchUnitTileEntity;
import ihl.flexible_cable.IronWorkbenchTileEntity;
import ihl.flexible_cable.RectifierTransformerUnitTileEntity;
import ihl.interfaces.IEnergyNetNode;
import ihl.interfaces.IMultiPowerCableHolder;
import ihl.items_blocks.IHLItemBlock;
import ihl.processing.metallurgy.LathePart2TileEntity;
import ihl.tile_entity.machines.*;
import ihl.utils.IHLUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import net.minecraft.block.BlockDispenser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DosingPumpBlock extends Block implements ITileEntityProvider {

	private static final String unlocalizedName = "dosingPump";

	@SideOnly(Side.CLIENT)
	IIcon dosingPumpBack, dosingPumpLeftSide, dosingPumpRightSide,
			dosingPumpTop, dosingPumpFront;

	public DosingPumpBlock() {
		super(Material.iron);
		this.setCreativeTab(IHLCreativeTab.tab);
		this.setBlockName(unlocalizedName);
		this.setHardness(2F);
		this.setResistance(1F);
	}

	@Override //TODO Rewrite
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
			int metadata = world.getBlockMetadata(x, y, z);
			boolean latch = (metadata & 8) != 0;

			if (isPowered && !latch ){
				world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
				world.setBlockMetadataWithNotify(x, y, z, metadata | 8, 4);
			} else if (!isPowered && latch) {
				world.setBlockMetadataWithNotify(x, y, z, metadata & -9, 4);
			}
		}

	}

	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof DosingPumpTileEntity) {
				DosingPumpTileEntity dpte = (DosingPumpTileEntity) te;
				dpte.trigger();
			}
		}
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null) {
				if (te instanceof IEnergyNetNode) {
					IEnergyNetNode ate = (IEnergyNetNode) te;
					ate.removeAttachedChains();
				}
				if (te instanceof IMultiPowerCableHolder) {
					IMultiPowerCableHolder ate = (IMultiPowerCableHolder) te;
					ate.removeAttachedChains();
				}
				if (te instanceof IronWorkbenchTileEntity) {
					IronWorkbenchTileEntity iwb = (IronWorkbenchTileEntity) te;
					iwb.dropContents();
				} else if (te instanceof IInventory) {
					IInventory inventory = (IInventory) te;
					for (int i = 0; i < inventory.getSizeInventory(); i++) {
						if (inventory.getStackInSlot(i) != null)
							world.spawnEntityInWorld(new EntityItem(world, x, y + 1, z, inventory.getStackInSlot(i)));
					}
				}
			}
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list,
			Entity entity) {
		float height = 1f;
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void init() {
		GameRegistry.registerBlock(new DosingPumpBlock(), IHLItemBlock.class, unlocalizedName);
		GameRegistry.registerTileEntity(DosingPumpTileEntity.class, unlocalizedName);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int flag) {
		super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, flag);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DosingPumpTileEntity();
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon(IHLModInfo.MODID + ":brickOvenSide");
		this.dosingPumpBack = par1IconRegister.registerIcon(IHLModInfo.MODID + ":dosingPumpBack");
		this.dosingPumpLeftSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":dosingPumpLeft");
		this.dosingPumpRightSide = par1IconRegister.registerIcon(IHLModInfo.MODID + ":dosingPumpRight");
		this.dosingPumpTop = par1IconRegister.registerIcon(IHLModInfo.MODID + ":dosingPumpTop");
		this.dosingPumpFront = par1IconRegister.registerIcon(IHLModInfo.MODID + ":dosingPumpFront");
		icons = new IIcon[]{this.dosingPumpFront, this.dosingPumpBack, this.dosingPumpBack, this.dosingPumpTop, this.dosingPumpRightSide, this.dosingPumpLeftSide};
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float pos_x,
			float pos_y, float pos_z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (IC2.platform.isSimulating()) {
			if (te instanceof IEnergyNetNode) {
				IEnergyNetNode node = (IEnergyNetNode) te;
				if (player.getCurrentEquippedItem() != null
						&& player.getCurrentEquippedItem().getItem() instanceof ItemToolCutter) {
					node.removeAttachedChains();
				}
			}
		}
		return te instanceof IHasGui && (!IC2.platform.isSimulating() || IC2.platform.launchGui(player, (IHasGui) te));
	}

	/**
	 * Called when the block is placed in the world.
	 */

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn)
	{
		if (!world.isRemote) {
			short l = (short) BlockPistonBase.determineOrientation(world, x, y, z, placer);
			TileEntity t = world.getTileEntity(x, y, z);
			if (t instanceof IWrenchable) {
				((IWrenchable) t).setFacing(
					l
				);
			}
		}
	}




	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		int k = meta & 7;
		return this.getIconFromFacing(k, side);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromFacing(int facing, int side) {
		int[] mask = {
			0, 1, 2, 3, 4, 5,
			1, 0, 3, 2, 4, 5,
			2, 3, 0, 1, 4, 5,
			2, 3, 1, 0, 4, 5,
			2, 3, 5, 4, 0, 1,
			2, 3, 4, 5, 1, 0 };
		int localFace = mask[facing * 6 + side];
		return icons[localFace];
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int metadata, int flag) {
		return true;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int metadata) {
		return this.isProvidingWeakPower(world, x, y, z, metadata);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getAdditionalIconsForBlockRenderer(int flag) {
		return this.blockIcon;
	}
}
