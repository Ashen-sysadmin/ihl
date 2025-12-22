package ihl.container;

import ic2.core.slot.SlotInvSlot;
import ihl.tile_entity.machines.CoilerTileEntity;
import ihl.tile_entity.machines.DetonationSprayingMachineTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class DetonationSprayingMachineContainer extends AbstractIHLContainer<DetonationSprayingMachineTileEntity> {


    public int lastFluidAmount = -1;
    public short lastProgress = -1;
    private final static int height=166;

	@Override
	public DetonationSprayingMachineTileEntity getTileEntity() {
		return this.tileEntity;
	}

    public DetonationSprayingMachineContainer(EntityPlayer entityPlayer, DetonationSprayingMachineTileEntity detonationSprayingMachineTileEntity){
        super(detonationSprayingMachineTileEntity);

        int col;
        for (col = 0; col < 3; ++col)
        {
            for (int col1 = 0; col1 < 9; ++col1)
            {
                this.addSlotToContainer(new Slot(entityPlayer.inventory, col1 + col * 9 + 9, 8 + col1 * 18, height + -82 + col * 18));
            }
        }
        for (col = 0; col < 9; ++col)
        {
            this.addSlotToContainer(new Slot(entityPlayer.inventory, col, 8 + col * 18, height + -24));
        }
            this.addSlotToContainer(new SlotInvSlot(detonationSprayingMachineTileEntity.input, 0, 10, 17));
            this.addSlotToContainer(new SlotInvSlot(detonationSprayingMachineTileEntity.input, 1, 98, 17));
            this.addSlotToContainer(new SlotInvSlot(detonationSprayingMachineTileEntity.input, 2, 117, 17));
    }

}
