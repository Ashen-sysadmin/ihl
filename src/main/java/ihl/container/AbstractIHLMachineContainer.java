package ihl.container;

import ihl.tile_entity.machines.BasicElectricMotorTileEntity;

public abstract class AbstractIHLMachineContainer<T extends BasicElectricMotorTileEntity> extends AbstractIHLContainer<T>{
	public AbstractIHLMachineContainer(T tileEntity1) {
		super(tileEntity1);
	}
	public T getTileEntity() {
		return this.tileEntity;
	}
}
