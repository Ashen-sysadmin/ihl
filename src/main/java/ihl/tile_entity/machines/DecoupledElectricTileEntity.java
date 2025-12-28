package ihl.tile_entity.machines;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.Access;
import ihl.IHLMod;
import ihl.flexible_cable.FlexibleCableHolderBaseTileEntity;
import ihl.flexible_cable.NodeEntity;
import ihl.processing.invslots.InvSlotUpgradeIHL;
import ihl.utils.IHLInvSlotDischarge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Iterator;
import java.util.List;

public abstract class DecoupledElectricTileEntity extends TileEntityInventory
		implements IHasGui, INetworkClientTileEntityEventListener, IEnergySink, ISidedInventory {

	public final IHLInvSlotDischarge dischargeSlot;
	public final InvSlotUpgradeIHL upgradeSlot;
	public short progress;
	protected short operationLength = 6000;
	protected double energyConsume = 1d;
	public double energy = 0d;
	public int maxStorage = 128;
	private boolean addedToEnergyNet = false;

	public DecoupledElectricTileEntity() {
		super();
		energyConsume = IHLMod.config.machineryEnergyConsume/100d;
		dischargeSlot = new IHLInvSlotDischarge(this, 1, Access.I, 4, InvSlot.InvSide.BOTTOM);
		upgradeSlot = new  InvSlotUpgradeIHL(this, 1, Access.I, 4, InvSlot.InvSide.BOTTOM);
	}

	@Override
	public String getInventoryName() {
		return "Lathe";
	}

	public boolean enableUpdateEntity() {
		return IC2.platform.isSimulating();
	}

	@Override
	public void onLoaded() {
		super.onLoaded();
		if (IC2.platform.isSimulating() && !this.addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			this.addedToEnergyNet = true;
		}
	}

	@Override
	public void onUnloaded() {
		if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			this.addedToEnergyNet = false;
		}

		super.onUnloaded();
	}


	@Override
	public void setFacing(short facing1) {
		super.setFacing(facing1);
	}


	@Override
	public double getDemandedEnergy() {
		if(this.getMaxStorage() - this.energy <= 1d)
		{
			return 0d;
		}
		return this.getMaxStorage() - this.energy;
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if (this.energy < this.getMaxStorage()) {
			this.energy += amount;
			return 0.0D;
		} else {
			return amount;
		}
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return this.getFacing() != (short) side;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("progress", this.progress);
		nbt.setDouble("energy", this.energy);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.progress = nbt.getShort("progress");
		this.energy = nbt.getDouble("energy");
	}

	@Override
	public void onNetworkEvent(EntityPlayer player, int event) {
	}

	public abstract void operate();

	@Override
	public void updateEntityServer() {
		if (this.getDemandedEnergy() > 1.0D) {
			double amount = this.dischargeSlot.discharge(this.getDemandedEnergy(), false);
			this.energy += amount;
		}

		if (this.canOperate() && this.energy >= this.energyConsume) {
			this.energy -= this.energyConsume * this.upgradeSlot.getPowerConsumtionMultiplier();
			if (this.progress == 0) {
				IC2.network.get().initiateTileEntityEvent(this, 0, true);
			}
			this.progress+=(short)(10*this.upgradeSlot.getProgressMultiplier());
			if (this.progress >= this.operationLength) {
				this.operate();
				this.progress = 0;
				IC2.network.get().initiateTileEntityEvent(this, 2, true);
			}
		} else {
			if (this.progress != 0 && this.getActive()) {
				IC2.network.get().initiateTileEntityEvent(this, 1, true);
			}
			if (!this.canOperate()) {
				this.progress = 0;
			}
		}

	}

	private double getMaxStorage() {
		return maxStorage+this.upgradeSlot.getAdditionalEnergyStorage();
	}

	public abstract List<?>[] getInput();

	public abstract boolean canOperate();

	@Override
	public void onGuiClosed(EntityPlayer arg0) {
	}

	public int getEnergy() {
		return (int) this.energy;
	}

	public int getGUIEnergy(int i) {
		if (this.energy < Float.MAX_VALUE) {
			return Math.round((float) (this.energy / this.getMaxStorage() * i));
		} else {
			return Math.round((float) (this.energy / this.getMaxStorage()) * i);
		}
	}

	public int gaugeProgressScaled(int i) {
		return this.progress * i / this.operationLength;
	}

	public void setEnergy(int value) {
		this.energy = value;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}


	public double drawEnergyToGrid(double amount) {
		return 0d;
	}


}
