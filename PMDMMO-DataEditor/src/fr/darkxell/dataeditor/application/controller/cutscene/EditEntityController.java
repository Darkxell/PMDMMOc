package fr.darkxell.dataeditor.application.controller.cutscene;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.darkxell.client.model.cutscene.CutsceneEntityModel;
import com.darkxell.client.model.cutscene.CutscenePokemonModel;
import com.darkxell.client.resources.image.pokemon.body.PokemonSpriteState;
import com.darkxell.common.pokemon.PokemonSpecies;
import com.darkxell.common.registry.Registries;
import com.darkxell.common.util.Direction;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class EditEntityController implements Initializable {

    @FXML
    private CheckBox animatedCheckbox;
    @FXML
    public Button cancelButton;
    @FXML
    private ComboBox<String> entityTypeCombobox;
    @FXML
    private ComboBox<Direction> facingCombobox;
    @FXML
    private TextField idTextfield;
    @FXML
    private TextField memberTextfield;
    @FXML
    private ComboBox<String> modeCombobox;
    @FXML
    public Button okButton;
    @FXML
    private ComboBox<PokemonSpecies> speciesCombobox;
    @FXML
    private ComboBox<PokemonSpriteState> stateCombobox;
    @FXML
    private TextField xposTextfield;
    @FXML
    private TextField yposTextfield;

    public CutsceneEntityModel getEntity() {
        CutsceneEntityModel e;
        if (this.entityTypeCombobox.getSelectionModel().getSelectedIndex() == 0)
            e = new CutsceneEntityModel(Double.valueOf(this.idTextfield.getText()).intValue(),
                    Double.valueOf(this.xposTextfield.getText()), Double.valueOf(this.yposTextfield.getText()));
        else {
            Integer team = null, pokemon = null;
            if (this.modeCombobox.getSelectionModel().getSelectedIndex() == 1)
                team = Double.valueOf(this.memberTextfield.getText()).intValue();
            else
                pokemon = this.speciesCombobox.getSelectionModel().getSelectedItem().getID();
            e = new CutscenePokemonModel(Double.valueOf(this.idTextfield.getText()).intValue(),
                    Double.valueOf(this.xposTextfield.getText()), Double.valueOf(this.yposTextfield.getText()), team,
                    pokemon, this.stateCombobox.getSelectionModel().getSelectedItem(),
                    this.facingCombobox.getSelectionModel().getSelectedItem(), this.animatedCheckbox.isSelected());
        }
        return e;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.entityTypeCombobox.getItems().addAll("Default", "Pokemon");
        this.entityTypeCombobox.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            modeCombobox.setDisable(newValue.equals(0));
            speciesCombobox.setDisable(newValue.equals(0));
            memberTextfield.setDisable(newValue.equals(0));
            stateCombobox.setDisable(newValue.equals(0));
            facingCombobox.setDisable(newValue.equals(0));
            animatedCheckbox.setDisable(newValue.equals(0));
        });
        this.entityTypeCombobox.getSelectionModel().select(0);

        this.modeCombobox.getItems().addAll("Pokemon species", "Team member");
        this.modeCombobox.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            speciesCombobox.setVisible(newValue.equals(0));
            memberTextfield.setVisible(newValue.equals(1));
        });
        this.modeCombobox.getSelectionModel().select(0);

        this.speciesCombobox.getItems().addAll(Registries.species().toList());
        this.speciesCombobox.getSelectionModel().select(1);

        this.stateCombobox.getItems().addAll(PokemonSpriteState.values());
        this.stateCombobox.getSelectionModel().select(0);

        this.facingCombobox.getItems().addAll(Direction.DIRECTIONS);
        this.facingCombobox.getSelectionModel().select(0);

        Pattern pattern = Pattern.compile("-?\\d*(\\.\\d*)?");
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        this.xposTextfield.setTextFormatter(formatter);
        formatter = new TextFormatter<>(change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        this.yposTextfield.setTextFormatter(formatter);
        formatter = new TextFormatter<>(change -> {
            return Pattern.compile("-?\\d*").matcher(change.getControlNewText()).matches() ? change : null;
        });
        this.idTextfield.setTextFormatter(formatter);
        formatter = new TextFormatter<>(change -> {
            return Pattern.compile("([0123])?").matcher(change.getControlNewText()).matches() ? change : null;
        });
        this.memberTextfield.setTextFormatter(formatter);
    }

    public void onCancel() {
        CutsceneCreationController.editEntityPopup.close();
    }

    public void onOk() {
        this.onCancel();
        CutscenesTabController.instance.editCutsceneController.cutsceneCreationController
                .onEntityEdited(this.getEntity());
    }

    public void setupFor(CutsceneEntityModel entity) {
        this.idTextfield.setText(Integer.toString(entity.getCutsceneID()));
        this.xposTextfield.setText(Double.toString(entity.getXPos()));
        this.yposTextfield.setText(Double.toString(entity.getYPos()));
        this.entityTypeCombobox.getSelectionModel().select(0);

        if (entity instanceof CutscenePokemonModel) {
            CutscenePokemonModel pokemon = (CutscenePokemonModel) entity;
            this.entityTypeCombobox.getSelectionModel().select(1);
            this.animatedCheckbox.setSelected(pokemon.getAnimated());
            this.facingCombobox.getSelectionModel().select(pokemon.getFacing());
            this.stateCombobox.getSelectionModel().select(pokemon.getState());
            if (pokemon.getTeamMember() != null) {
                this.modeCombobox.getSelectionModel().select(1);
                this.memberTextfield.setText(Integer.toString(pokemon.getTeamMember()));
            } else {
                this.modeCombobox.getSelectionModel().select(0);
                this.speciesCombobox.getSelectionModel().select(Registries.species().find(pokemon.getPokemonID()));
            }
        }
    }

}
