/*
 * $Id$
 * Created on 03.05.2006
 * Copyright Paul Burlov 2001-2006
 */
package mou.core.res.colony.MaterialStorage;

import javax.swing.JLabel;
import mou.Main;
import mou.core.civilization.NaturalRessourcesStorageDB;
import mou.core.civilization.NaturalRessourcesStorageItem;
import mou.core.res.colony.DefaultBuildingUI;
import mou.gui.GUI;
import mou.storage.ser.ID;


/**
 * @author Dominik
 *
 */
public class MaterialStorageUI extends DefaultBuildingUI<MaterialStorage>
{

	private JLabel storageLabel = new JLabel();
	private JLabel storedLabel = new JLabel();
	protected NaturalRessourcesStorageDB storage = Main.instance().getMOUDB().getStorageDB();
	
	public MaterialStorageUI(MaterialStorage building)
	{
		super(building);
		addSliderPanel(building.getStorageData().size(), true, true);
		
		int i=0;
		NaturalRessourcesStorageItem item;
		for(ID id : building.getStorageData().keySet())
		{
			item = (NaturalRessourcesStorageItem) storage.getData(id);
			slider.setSliderLabel(i, item.getNaturalResource().getName());
			i++;
		}
		
		addField("Lagergröße: ", storageLabel);
		addField("Materialvorrat: ", storedLabel);
		JLabel label = new JLabel();
		label.setText(GUI.formatLong(building.computeCustomValue(2)));
		addField("Maximale Gebäudegröße: ", label);

	}

	@Override
	protected void refreshValuesIntern(MaterialStorage building)
	{
		storageLabel.setText(GUI.formatSmartDouble(building.computeTotalCapacity()/1E6)+"mio T");
		storedLabel.setText(GUI.formatSmartDouble(building.computeTotalStored()/1E6)+"mio T");
		storedLabel.setToolTipText(building.getMaterialStoredHTMLInfo());
	}
}
