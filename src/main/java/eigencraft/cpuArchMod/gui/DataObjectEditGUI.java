package eigencraft.cpuArchMod.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import eigencraft.cpuArchMod.CpuArchMod;
import eigencraft.cpuArchMod.backend.DataObject;
import eigencraft.cpuArchMod.util.GsonDataObjectDeserializer;
import eigencraft.cpuArchMod.util.GsonDataObjectSerializer;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class DataObjectEditGUI extends LightweightGuiDescription {
    private static Gson gson;

    public static DataObjectEditGUI fromExisting(DataObject dataObject){
        return new DataObjectEditGUI(dataObject);
    }
    public static DataObjectEditGUI fromNew(){
        return new DataObjectEditGUI(null);
    }

    private DataObjectEditGUI(DataObject dataObject){
        //Create one static gson instance first time opened.
        if (gson==null){
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(DataObject.class,new GsonDataObjectSerializer());
            gsonBuilder.registerTypeAdapter(DataObject.class,new GsonDataObjectDeserializer());
            gson = gsonBuilder.create();
        }

        //Root panel
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(198, 180);

        //Text field. Because it doesn't support multiline, there is a replacment in work, but not ready. see WBigTextWidget
        WTextField textField = new WTextField();
        root.add(textField,0,3,10,1);
        //Can't set infinite max length, set it high
        textField.setMaxLength(1000);

        //Add label with the type
        WLabel label = new WLabel(new LiteralText("new dataObject"), 0xFFFFFF);
        root.add(label, 0, 1, 4, 1);

        //Fill with data
        if (dataObject!=null){
            //Set type of dataObject
            label.setText(new LiteralText(dataObject.getType()));
            //Serialise to String
            String jsonDataObject = gson.toJson(dataObject);
            //Can't set infinite max length, set it high
            textField.setMaxLength(jsonDataObject.length()+1000);
            //Set text
            textField.setText(jsonDataObject);
        }

        //Add button to save
        WButton saveButton = new WButton(new TranslatableText(CpuArchMod.MODID+":gui.button.save_and_close"));
        //Runs when clicked
        saveButton.setOnClick(new Runnable() {
            @Override
            public void run() {
                try {
                    //Convert to DataObject
                    DataObject editedObject = gson.fromJson(textField.getText(), DataObject.class);
                    //If text is empty, null is returned from gson
                    if (editedObject==null) throw new GsonDataObjectDeserializer.InvalidDataObjectJsonStructureError();

                    //Send package
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                    passedData.writeCompoundTag(editedObject.getCompoundTag());
                    // Send packet to server
                    ClientSidePacketRegistry.INSTANCE.sendToServer(CpuArchMod.DATAOBJECT_GUI_SAVE_C2S_PACKET, passedData);
                    label.setText(new LiteralText(editedObject.getType()));

                } catch (JsonSyntaxException| GsonDataObjectDeserializer.InvalidDataObjectJsonStructureError e){
                    //Invalid json
                    label.setText(new TranslatableText(CpuArchMod.MODID+":dataobject.gui.invalid_json"));
                }
            }
        });
        root.add(saveButton,6,1,4,1);

        root.validate(this);
    }
}
