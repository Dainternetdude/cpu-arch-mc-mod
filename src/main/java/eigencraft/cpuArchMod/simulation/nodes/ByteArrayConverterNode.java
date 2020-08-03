package eigencraft.cpuArchMod.simulation.nodes;

import eigencraft.cpuArchMod.dataObject.DataObject;
import eigencraft.cpuArchMod.dataObject.DataObjectTypes;
import eigencraft.cpuArchMod.simulation.PipeMessage;
import eigencraft.cpuArchMod.simulation.SimulationIOManager;
import eigencraft.cpuArchMod.simulation.SimulationNode;
import net.minecraft.util.math.BlockPos;

public class ByteArrayConverterNode extends SimulationNode {
    //TODO new converter system
    public ByteArrayConverterNode(BlockPos position) {
        super(position);
    }

    @Override
    public void process(PipeMessage in, SimulationIOManager ioManager) {
        DataObject inMessages = in.getDataObject();
        DataObject dataObject = new DataObject(DataObjectTypes.BYTE_ARRAY_TYPE);
        if (inMessages.matchType(DataObjectTypes.INT_TYPE)){
            dataObject.setByteArray("data",new byte[]{(byte)inMessages.getInt("data")});
        } else if (inMessages.matchType(DataObjectTypes.BYTE_TYPE)){
            dataObject.setByteArray("data",new byte[]{inMessages.getByte("data")});
        } else if (inMessages.matchType(DataObjectTypes.BOOLEAN_TYPE)){
            dataObject.setIntArray("data",new int[]{inMessages.getByte("bool")});
        } else if (inMessages.matchType(DataObjectTypes.INT_ARRAY_TYPE)){
            int[] inData = inMessages.getIntArray("data");
            byte[] outData = new byte[inData.length];
            for (int i = 0; i < inData.length; i++) {
                outData[i]=(byte)inData[i];
            }
            dataObject.setByteArray("data",outData);
        } else if (inMessages.matchType(DataObjectTypes.BYTE_ARRAY_TYPE)){
            publish(new PipeMessage(inMessages,in.getLane()));
            return;
        }
        publish(new PipeMessage(dataObject,in.getLane()));
    }
}
