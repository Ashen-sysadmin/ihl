package ihl.container;

import ic2.core.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public abstract class AbstractIHLContainer<T extends IInventory> extends ContainerBase<T> {
	@Deprecated
	protected final T tileEntity;

	public AbstractIHLContainer(T tileEntity1) {
		super(tileEntity1);
		//Aliasing

		this.tileEntity = base;

	}

	public abstract T getTileEntity();

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.base.isUseableByPlayer(entityplayer);
	}



}
