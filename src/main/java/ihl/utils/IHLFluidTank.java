package ihl.utils;

import java.util.*;

import ihl.recipes.IRecipeInputFluid;
import ihl.worldgen.ores.IHLFluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.*;

public class IHLFluidTank implements IFluidTank {
	private final List<FluidStack> fluidList = new ArrayList<FluidStack>();
	private final int capacity;

	public IHLFluidTank(int capacity) {
		this.capacity = capacity;
	}

	public IHLFluidTank(int capacity, boolean isOpenVessel1) {
		this.capacity = capacity;
	}

	public IHLFluidTank readFromNBT(NBTTagCompound nbt) {
		if (!nbt.hasKey("Empty")) {
			NBTTagList fluidList1 = nbt.getTagList("fluids", 10);
			for (int i = 0; i < fluidList1.tagCount(); i++) {
				NBTTagCompound fluidNBT1 = fluidList1.getCompoundTagAt(i);
				FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidNBT1);
				if (fluid != null) {
					fluidList.add(fluid);
				}
			}
		}
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (!fluidList.isEmpty()) {
			NBTTagList fluids = new NBTTagList();
			for (FluidStack fluid : fluidList) {
				if (fluid != null) {
					NBTTagCompound fluidNBT1 = new NBTTagCompound();
					fluid.writeToNBT(fluidNBT1);
					fluids.appendTag(fluidNBT1);
				}
			}
			nbt.setTag("fluids", fluids);
		} else {
			nbt.setString("Empty", "");
		}
		return nbt;
	}

	/* IFluidTank */
	@Override
	public FluidStack getFluid() {
		if (this.fluidList.isEmpty()) {
			return null;
		}
		return this.fluidList.get(0);
	}

	public FluidStack getLightestFluid() {
		if (this.fluidList.isEmpty()) {
			return null;
		}
		return this.fluidList.get(this.fluidList.size() - 1);
	}

	@Override
	public int getFluidAmount() {
		int amount = 0;
		for (FluidStack fluid : fluidList) {
			if (fluid != null) {
				amount += fluid.amount;
			}
		}
		return amount;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource == null)
			return 0;
		int freeSpace = capacity - this.getFluidAmount();
		int amount1 = Math.min(freeSpace, resource.amount);
		if (resource.getFluid() == null || amount1<=0) {
			return 0;
		}
		if (!doFill) {
			return amount1;
		}
		FluidStack fluid = getFluidStackWithSameFluid(resource);
		if (fluid != null) {
			fluid.amount += amount1;
			return amount1;
		}
		fluid = copyWithSize(resource, amount1);
		fluidList.add(fluid);
		this.sortFluidsByDensity();
		return amount1;
	}

	public void fill(List<FluidStack> fluidOutputs, boolean doFill) {
		if (fluidOutputs != null && !fluidOutputs.isEmpty()) {
			for (FluidStack fluidOutput : fluidOutputs) {
				this.fill(fluidOutput, doFill);
			}
		}
	}



	public FluidStack drainLightest(int maxDrain, boolean doDrain) {
		if (fluidList.isEmpty()) {
			return null;
		}
		FluidStack fstack = this.getLightestFluid().copy();
		fstack.amount = maxDrain;
		return this.drain(fstack, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (fluidList.isEmpty()) {
			return null;
		}
		FluidStack fstack = this.getFluid().copy();
		fstack.amount = maxDrain;
		return this.drain(fstack, doDrain);
	}


	public FluidStack drain(FluidStack fluidStack, boolean doDrain) {
		if (fluidList.isEmpty()) {
			return null;
		}
		int drained = 0;
		if (fluidStack != null) {
			drained = fluidStack.amount;
		}
		FluidStack fluid = this.getFluidStackWithSameFluid(fluidStack);
		if (fluid == null) {
			return null;
		}
		if (fluid.amount < drained) {
			drained = fluid.amount;
		}
		FluidStack stack = copyWithSize(fluid, drained);
		if (doDrain) {
			fluid.amount -= drained;
			if (fluid.amount <= 0) {
				this.fluidList.remove(fluid);
			}
		}
		return stack;
	}

	public FluidStack drain(IRecipeInputFluid fluidStack, boolean doDrain) {
		if (fluidList.isEmpty()) {
			return null;
		}
		int drained = 0;
		if (fluidStack != null) {
			drained = fluidStack.getAmount();
		}
		FluidStack fluid = this.getFluidStackWithSameFluid(fluidStack);
		if (fluid == null) {
			return null;
		}
		if (fluid.amount < drained) {
			drained = fluid.amount;
		}
		FluidStack stack = copyWithSize(fluid, drained);
		if (doDrain) {
			fluid.amount -= drained;
			if (fluid.amount <= 0) {
				this.fluidList.remove(fluid);
			}
		}
		return stack;
	}

	public void drainStacks(List<FluidStack> fluidInputs, boolean doDrain){
		if (fluidInputs == null || fluidInputs.isEmpty()) return;
		for (FluidStack fluidInput : fluidInputs) {
			this.drain(fluidInput, doDrain);
		}
	}

	/**
	* @deprecated Reason: If I see another "Object fluidStack" there will be consequences
	 */
	@Deprecated
	public void drain(List<?> fluidInputs, boolean doDrain) {
		if (fluidInputs == null || fluidInputs.isEmpty()) return;

		for (Object fluidInput : fluidInputs) {
			if (fluidInput instanceof FluidStack) {
				this.drain((FluidStack) fluidInput, doDrain);
			}
			else if  (fluidInput instanceof IRecipeInputFluid) {
				this.drain((IRecipeInputFluid) fluidInput, doDrain);
			}
		}
	}

	public FluidStack drain(IRecipeInputFluid fluidStack, int amount, boolean doDrain) {
		if (fluidList.isEmpty()) {
			return null;
		}
		int drained = amount;
		FluidStack fluid = this.getFluidStackWithSameFluid(fluidStack);
		if (fluid == null) {
			return null;
		}
		if (fluid.amount < drained) {
			drained = fluid.amount;
		}
		FluidStack stack = copyWithSize(fluid, drained);
		if (doDrain) {
			fluid.amount -= drained;
			if (fluid.amount <= 0) {
				this.fluidList.remove(fluid);
			}
		}
		return stack;
	}

	/**
	 * Not at all safe to use during iteration, even though it's really tempting.
	* @return Returns the fluidstack drained, as standard for forge fluid API.
	 */
	public FluidStack drainByIndex(int amount, int index, boolean doDrain){
		int tankIndexAmount = this.getFluidAmount(index); //OOB returns 0.
		int amountDrained = Math.min(tankIndexAmount,amount);
		if (amountDrained <= 0) return null;
		//Everything is now not Null as a byproduct of the above ^

		FluidStack stack = new FluidStack(this.getFluidFromIndex(index), amountDrained);
		if (doDrain) {
			if (amountDrained == tankIndexAmount){
				this.fluidList.remove(index);
			} else {
				this.setFluidAmount((tankIndexAmount-amountDrained), index);
			}
		}
		return stack;
	}

	/**
	 * @param fluidStack the fluid stack to compare to.
	 * @return mutable fluidstack inside the tank, returns null if no match found
	 */
	public FluidStack getFluidStackWithSameFluid(FluidStack fluidStack) {
		for (FluidStack fluid : fluidList) {
			if (fluid != null && fluidStack != null) {
				if (fluid.isFluidEqual(fluidStack)) {
					return fluid;
				}
			}
		}
		return null;
	}

	/**
	 * @param fluidStack the IRecipeInputFluid stack to compare to.
	 * @return mutable fluidstack inside the tank, returns null if no match found
	 */
	public FluidStack getFluidStackWithSameFluid(IRecipeInputFluid fluidStack) {
		for (FluidStack fluid : fluidList) {
			if (fluid != null && fluidStack != null) {
				if (fluidStack.matches(fluid)) {
					return fluid;
				}
			}
		}
		return null;
	}

	public int getNumberOfFluids() {
		return this.fluidList.size();
	}



	private boolean setFluidAmount(int amount1, int index) {
		if (index < 0 || this.fluidList.size() <= index) {
			return false;
		}
		this.fluidList.get(index).amount = amount1;
		return true;
	}

	public int getFluidAmount(int index) {
		if (this.fluidList.size() <= index || this.fluidList.get(index) == null) {
			return 0;
		}
		return this.fluidList.get(index).amount;
	}

	public Fluid getFluidFromIndex(int index) {
		if (this.fluidList.size() <= index || this.fluidList.get(index) == null) {
			return null;
		}
		return this.fluidList.get(index).getFluid();
	}

	/**
	 * @deprecated Not only are fluid IDs deprecated by forge, but this is horrible
	 */
	@Deprecated
	public int getFluidID(int i) {
		if (this.fluidList.get(i) == null) {
			return -1;
		}
		return this.fluidList.get(i).getFluid().getID();
	}

	public void sortFluidsByDensity() {
		Map<Integer, FluidStack> sortMap = new HashMap<Integer, FluidStack>();
		int[] keysArray = new int[fluidList.size()];
		for (FluidStack fluid : fluidList) {
			if (fluid == null) {
				return;
			}
			int key = Math.round(IHLFluid.getRealDensity(fluid.getFluid()) * 100F);
			while (sortMap.containsKey(key)) {
				key++;
			}
			sortMap.put(key, fluid);
			keysArray[fluidList.indexOf(fluid)] = key;
		}
		Arrays.sort(keysArray);
		ArrayList<FluidStack> newFluidList = new ArrayList<FluidStack>();
		for (int i = keysArray.length - 1; i >= 0; i--) {
			newFluidList.add(sortMap.get(keysArray[i]));
		}
		this.fluidList.clear();
		this.fluidList.addAll(newFluidList);
	}

	public FluidStack getFluid(int i) {
		return this.fluidList.get(i);
	}

	public void setTag(String string, int t1_1) {
		if (this.getFluid().tag == null) {
			this.getFluid().tag = new NBTTagCompound();
		}
		this.getFluid().tag.setInteger(string, t1_1);
	}

	public void setEmpty() {
		this.fluidList.clear();
	}

	/**
	* @deprecated *please* don't use getters to bypass access controls - make something public if you want to allow public getters.
	 */
	@Deprecated
	public List<FluidStack> getFluidList() {
		return this.fluidList;
	}

	/**
	 * Still horrifically unsafe, but actually useful for something.
	 * @return Returns an Iterator<FluidStack>
	 */
	public Iterator<FluidStack> getFluidIterator() {
		return this.fluidList.iterator();
	}

	private FluidStack copyWithSize(FluidStack resource, int amount1) {
		FluidStack fluid = resource.copy();
		fluid.amount = amount1;
		if (resource.tag != null) {
			fluid.tag = (NBTTagCompound) resource.tag.copy();
		}
		return fluid;
	}

	public void checkCorrectState() {
		if (!this.fluidList.isEmpty()) {
			this.fluidList.removeIf(fs -> fs.amount <= 0);
		}
	}
}
