package ihl.interfaces;

import ihl.utils.IHLFluidTank;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

public interface IIHLFluidHandler extends IFluidHandler {

	int getNumberOfFluidsInTank();

	IHLFluidTank getFluidTank();

}
