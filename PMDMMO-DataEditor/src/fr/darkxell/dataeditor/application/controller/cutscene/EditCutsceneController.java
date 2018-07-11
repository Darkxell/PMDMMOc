package fr.darkxell.dataeditor.application.controller.cutscene;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.darkxell.client.mechanics.cutscene.Cutscene;
import com.darkxell.client.mechanics.cutscene.CutsceneEvent;

import fr.darkxell.dataeditor.application.data.Cutscenes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class EditCutsceneController implements Initializable
{

	@FXML
	public CutsceneCreationController cutsceneCreationController;
	@FXML
	private CutsceneEndController cutsceneEndController;
	@FXML
	private ListView<CutsceneEvent> eventList;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{}

	public void saveChanges()
	{
		Cutscene c = new Cutscene(CutscenesTabController.instance.currentCutscene.name, this.cutsceneCreationController.getCreation(),
				this.cutsceneEndController.getEnd(), new ArrayList<>(this.eventList.getItems()));
		CutscenesTabController.instance.currentCutscene = c;
		Cutscenes.update(c);
	}

	public void setupFor(Cutscene cutscene)
	{
		this.cutsceneCreationController.setupFor(cutscene);
		this.cutsceneEndController.setupFor(cutscene);
	}

}
