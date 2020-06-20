package eigencraft.cpuArchMod.blocks;

import eigencraft.cpuArchMod.backend.simulation.SimulationIOManager;
import eigencraft.cpuArchMod.backend.simulation.SimulationMasterProvider;
import eigencraft.cpuArchMod.backend.simulation.SimulationWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DataPipeBlock extends Block {
    public DataPipeBlock(Settings settings) {
        super(settings);
    }


    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient){
            ((SimulationMasterProvider)world).getSimulationMaster().getIOManager().addSimulationTickRunnable(new SimulationIOManager.SimulationTickRunnable() {
                @Override
                public void run(SimulationWorld simulationWorld) {
                    simulationWorld.removePipe(pos);
                }
            });
        }
        super.onBlockRemoved(state, world, pos, newState, moved);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {

        if (!world.isClient){
            ((SimulationMasterProvider)world).getSimulationMaster().getIOManager().addSimulationTickRunnable(new SimulationIOManager.SimulationTickRunnable() {
                @Override
                public void run(SimulationWorld simulationWorld) {
                    simulationWorld.addPipe(pos);
                }
            });
        }
    }
}
