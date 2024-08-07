package dev.masterflomaster1.jfxc.gui.page.view;

import atlantafx.base.util.BBCodeParser;
import dev.masterflomaster1.jfxc.gui.page.SimplePage;
import dev.masterflomaster1.jfxc.gui.page.UIElementFactory;
import dev.masterflomaster1.jfxc.gui.page.viewmodel.CaesarViewModel;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class CaesarPage extends SimplePage {

    public static final String NAME = "Caesar Cipher";

    private final TextArea inputTextArea = UIElementFactory.createInputTextArea("Enter text to encrypt / decrypt");
    private final TextArea outputTextArea = UIElementFactory.createOuputTextArea("Result");
    private Spinner<Integer> shiftSpinner;

    private final CaesarViewModel viewModel = new CaesarViewModel();

    public CaesarPage() {
        super();
        addSection("Caesar Cipher", mainSection());
        bindComponents();

        viewModel.onInit();
    }

    private Node mainSection() {
        var description = BBCodeParser.createFormattedText(
                "Method in which each letter in the plaintext is replaced by a letter some fixed number of " +
                        "positions down the alphabet. The method is named after Julius Caesar, who used it in his " +
                        "private correspondence."
        );

        shiftSpinner = new Spinner<>(1, 26, 1);

        var encryptButton = new Button("Encrypt");
        var decryptButton = new Button("Decrypt");
        encryptButton.setOnAction(event -> viewModel.action(true));
        decryptButton.setOnAction(event -> viewModel.action(false));

        var controlsHBox = new HBox(
                20,
                shiftSpinner,
                encryptButton,
                decryptButton
        );

        var copyResultButton = UIElementFactory.createCopyButton(outputTextArea);
        var footerHBox = new HBox(
                20,
                copyResultButton,
                counterLabel
        );
        footerHBox.setAlignment(Pos.CENTER_LEFT);

        return new VBox(
                20,
                description,
                inputTextArea,
                controlsHBox,
                outputTextArea,
                footerHBox
        );
    }

    private void bindComponents() {
        inputTextArea.textProperty().bindBidirectional(viewModel.inputTextProperty());
        outputTextArea.textProperty().bindBidirectional(viewModel.outputTextProperty());
        shiftSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.shiftProperty().asObject());
        counterLabel.textProperty().bindBidirectional(viewModel.counterTextProperty());
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
