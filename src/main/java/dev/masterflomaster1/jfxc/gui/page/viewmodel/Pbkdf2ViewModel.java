package dev.masterflomaster1.jfxc.gui.page.viewmodel;

import dev.masterflomaster1.jfxc.MemCache;
import dev.masterflomaster1.jfxc.crypto.PbeImpl;
import dev.masterflomaster1.jfxc.crypto.SecurityUtils;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;

import java.util.Base64;
import java.util.HexFormat;

public class Pbkdf2ViewModel extends AbstractViewModel {

    private final StringProperty passwordTextProperty = new SimpleStringProperty();
    private final StringProperty iterationsTextProperty = new SimpleStringProperty();
    private final StringProperty keyLengthTextProperty = new SimpleStringProperty();
    private final StringProperty saltTextProperty = new SimpleStringProperty();
    private final StringProperty outputText = new SimpleStringProperty();

    private final ObjectProperty<String> pbkdf2ComboBoxProperty = new SimpleObjectProperty<>();
    private final ObservableList<String> pbkdf2AlgorithmsList = FXCollections.observableArrayList();

    private final BooleanProperty hexModeToggleButtonProperty = new SimpleBooleanProperty();
    private final BooleanProperty b64ModeToggleButtonProperty = new SimpleBooleanProperty();

    private Timeline emptyPasswordAnimation;
    private Timeline emptyIterationsAnimation;
    private Timeline emptyKeyLengthAnimation;
    private Timeline emptySaltAnimation;

    public Pbkdf2ViewModel() {
        pbkdf2AlgorithmsList.setAll(SecurityUtils.getPbkdfs());
    }

    public StringProperty passwordTextProperty() {
        return passwordTextProperty;
    }

    public StringProperty iterationsTextProperty() {
        return iterationsTextProperty;
    }

    public StringProperty getKeyLengthTextProperty() {
        return keyLengthTextProperty;
    }

    public StringProperty saltTextProperty() {
        return saltTextProperty;
    }

    public StringProperty outputTextProperty() {
        return outputText;
    }

    public ObjectProperty<String> pbkdf2ComboBoxProperty() {
        return pbkdf2ComboBoxProperty;
    }

    public ObservableList<String> getPbkdf2AlgorithmsList() {
        return pbkdf2AlgorithmsList;
    }

    public BooleanProperty hexModeToggleButtonPropertyProperty() {
        return hexModeToggleButtonProperty;
    }

    public BooleanProperty b64ModeToggleButtonPropertyProperty() {
        return b64ModeToggleButtonProperty;
    }

    public void setEmptyPasswordAnimation(Timeline emptyPasswordAnimation) {
        this.emptyPasswordAnimation = emptyPasswordAnimation;
    }

    public void setEmptyIterationsAnimation(Timeline emptyIterationsAnimation) {
        this.emptyIterationsAnimation = emptyIterationsAnimation;
    }

    public void setEmptyKeyLengthAnimation(Timeline emptyKeyLengthAnimation) {
        this.emptyKeyLengthAnimation = emptyKeyLengthAnimation;
    }

    public void setEmptySaltAnimation(Timeline emptySaltAnimation) {
        this.emptySaltAnimation = emptySaltAnimation;
    }

    @SuppressWarnings("unused")
    public void onToggleChanged(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (newValue == null) {
            if (oldValue != null)
                oldValue.setSelected(true);
            return;
        }

        var val = passwordTextProperty.get();
        var selectedButton = (ToggleButton) newValue;

        // bypass unpredictable behavior of ToggleButtonProperty.get()
        if (selectedButton.getText().equalsIgnoreCase("Hex")) {
            hexModeToggleButtonProperty.set(true);
            b64ModeToggleButtonProperty.set(false);
        } else if (selectedButton.getText().equalsIgnoreCase("Base64")) {
            b64ModeToggleButtonProperty.set(true);
            hexModeToggleButtonProperty.set(false);
        }
    }

    public void onSaltShuffleAction(ActionEvent e) {
        saltTextProperty.set(HexFormat.of().formatHex(SecurityUtils.generateSalt()));
    }

    public void action() {
        if (passwordTextProperty.get().isEmpty()) {
            emptyPasswordAnimation.playFromStart();
            return;
        }

        if (iterationsTextProperty.get().isEmpty()) {
            emptyIterationsAnimation.playFromStart();
            return;
        }

        if (keyLengthTextProperty.get().isEmpty()) {
            emptyKeyLengthAnimation.playFromStart();
            return;
        }

        if (saltTextProperty.get().isEmpty()) {
            emptySaltAnimation.playFromStart();
            return;
        }

        var algo = pbkdf2ComboBoxProperty.get();
        var pass = passwordTextProperty.get().toCharArray();
        var salt = HexFormat.of().parseHex(saltTextProperty.get());
        var iter = Integer.parseInt(iterationsTextProperty.get());
        var lKey = Integer.parseInt(keyLengthTextProperty.get());

        var completableFuture = PbeImpl.asyncHash(algo, pass, salt, iter, lKey);
        completableFuture
                .thenAccept(bytes -> {
                    outputText.set(formatOutput(bytes));
                })
                .exceptionally(ex -> {
                    System.err.println(ex.getMessage());
                    return null;
                });

    }

    private String formatOutput(byte[] value) {
        if (hexModeToggleButtonProperty.get()) {
            return HexFormat.of().formatHex(value);
        } else if (b64ModeToggleButtonProperty.get()) {
            return Base64.getEncoder().encodeToString(value);
        }

        return "";
    }

    @Override
    public void onInit() {
        pbkdf2ComboBoxProperty.set(pbkdf2AlgorithmsList.get(MemCache.readInteger("pbkdf2.algo", 0)));
        passwordTextProperty.set(MemCache.readString("pbkdf2.password", ""));
        keyLengthTextProperty.set(MemCache.readString("pbkdf2.key.len", "128"));
        iterationsTextProperty.set(MemCache.readString("pbkdf2.iterations", "10000"));
        saltTextProperty.set(MemCache.readString("pbkdf2.salt", ""));
        outputText.set(MemCache.readString("pbkdf2.output", ""));
    }

    @Override
    public void onReset() {
        MemCache.writeInteger("pbkdf2.algo", pbkdf2AlgorithmsList.indexOf(pbkdf2ComboBoxProperty.get()));
        MemCache.writeString("pbkdf2.password", passwordTextProperty.get());
        MemCache.writeString("pbkdf2.key.len", keyLengthTextProperty.get());
        MemCache.writeString("pbkdf2.iterations", iterationsTextProperty.get());
        MemCache.writeString("pbkdf2.salt", saltTextProperty.get());
        MemCache.writeString("pbkdf2.output", outputText.get());
    }
}
