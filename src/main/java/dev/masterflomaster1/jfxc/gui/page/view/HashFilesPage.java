package dev.masterflomaster1.jfxc.gui.page.view;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import dev.masterflomaster1.jfxc.JFXCrypto;
import dev.masterflomaster1.jfxc.gui.page.SimplePage;
import dev.masterflomaster1.jfxc.gui.page.UIElementFactory;
import dev.masterflomaster1.jfxc.gui.page.viewmodel.HashFilesViewModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public final class HashFilesPage extends SimplePage {

    public static final String NAME = "Hash Files";

    private final ComboBox<String> hashComboBox = new ComboBox<>();
    private final TextArea outputTextArea = UIElementFactory.createOuputTextArea("Result", 100);

    private final ToggleButton hexModeToggleBtn = new ToggleButton("Hex");
    private final ToggleButton b64ModeToggleBtn = new ToggleButton("Base64");

    private ToggleGroup toggleGroup;

    private final HashFilesViewModel viewModel = new HashFilesViewModel();

    public HashFilesPage() {
        super();
        addSection("Hash Files", mainSection());
        bindComponents();

        viewModel.onInit();
    }

    private Node mainSection() {
        var description = BBCodeParser.createFormattedText("""
            Calculation of hash function values for files using 60+ different algorithms. For large files operation may take some time.
            
            [ul]
            [li]For [code]HARAKA-256[/code] - input [color="-color-danger-fg"]must be exactly 32 bytes[/color].[/li]
            [li]For [code]HARAKA-512[/code] - input [color="-color-danger-fg"]must be exactly 64 bytes[/color].[/li]
            [/ul]"""
        );

        var fileInputTextField = new TextField();
        fileInputTextField.setMinWidth(534);
        var fileInputLabel = new Label("", new FontIcon(BootstrapIcons.FILE_EARMARK));
        var fileInputBrowseButton = new Button("Browse");
        var fileInputGroup = new InputGroup(fileInputLabel, fileInputTextField, fileInputBrowseButton);

        fileInputTextField.setPromptText("Select file to hash");
        FileChooser fileChooser = new FileChooser();
        fileInputBrowseButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(JFXCrypto.getStage());

            if (file == null)
                return;

            viewModel.setSelectedFile(file);
            fileInputTextField.setText(file.getAbsolutePath());
        });

        toggleGroup = new ToggleGroup();
        hexModeToggleBtn.setSelected(true);
        hexModeToggleBtn.setToggleGroup(toggleGroup);
        b64ModeToggleBtn.setToggleGroup(toggleGroup);
        hexModeToggleBtn.getStyleClass().add(Styles.LEFT_PILL);
        b64ModeToggleBtn.getStyleClass().add(Styles.RIGHT_PILL);

        var outputModeHBox = new HBox(hexModeToggleBtn, b64ModeToggleBtn);
        var copyResultButton = UIElementFactory.createCopyButton(outputTextArea);

        var runButton = new Button("Run");
        runButton.setOnAction(event -> viewModel.action());

        var controlsHBox = new HBox(
                20, hashComboBox, runButton, outputModeHBox
        );

        return new VBox(
                20,
                description,
                fileInputGroup,
                controlsHBox,
                new Separator(Orientation.HORIZONTAL),
                outputTextArea,
                copyResultButton
        );
    }

    private void bindComponents() {
        outputTextArea.textProperty().bindBidirectional(viewModel.outputTextProperty());
        hashComboBox.valueProperty().bindBidirectional(viewModel.hashComboBoxPropertyProperty());
        Bindings.bindContent(hashComboBox.getItems(), viewModel.hashAlgorithmsList());
        hexModeToggleBtn.selectedProperty().bindBidirectional(viewModel.hexModeToggleButtonPropertyProperty());
        b64ModeToggleBtn.selectedProperty().bindBidirectional(viewModel.b64ModeToggleButtonPropertyProperty());

        hexModeToggleBtn.setSelected(true);
        toggleGroup.selectedToggleProperty().addListener(viewModel::onToggleChanged);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onReset() {
        viewModel.onReset();
    }
}
