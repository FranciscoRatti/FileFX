package stage;

import entity.FileProperties;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import node.CenterNode;

import static main.Lib.*;
import static panel.MainPane.*;

public class PermissionsStage extends Stage {
    private final Button[] octetValues;
    private final Button[] charsButtons;

    public PermissionsStage() {
        setTitle("Cambiar permisos");
        setAlwaysOnTop(true);
        setResizable(false);

        // Caracteres
        HBox charsBox = new HBox();
        charsBox.setId("PermissionsChars_pane");

        HBox[] charsBoxes = new HBox[]{new HBox(),new HBox(),new HBox()};

        charsButtons = new Button[]{
                new Button("r"),new Button("w"),new Button("x"),
                new Button("r"),new Button("w"),new Button("x"),
                new Button("r"),new Button("w"),new Button("x")
        };
        for (int i = 0; i < 3; i++) {
            int index = i;

            charsButtons[i].setId("PermissionsChars_button");
            charsButtons[i].setOnAction(e -> {
                char character = (index % 3 == 0) ? 'r' : ((index-1) % 3 == 0) ? 'w' : 'x';
                if (charsButtons[index].getText().charAt(0) == character) charsButtons[index].setText("-");
                else charsButtons[index].setText(String.valueOf(character));

                setOwner(
                        charsButtons[0].getText() +
                        charsButtons[1].getText() +
                        charsButtons[2].getText()
                );
            });

            charsButtons[i+3].setId("PermissionsChars_button");
            charsButtons[i+3].setOnAction(e -> {
                char character = ((index+3) % 3 == 0) ? 'r' : ((index+3-1) % 3 == 0) ? 'w' : 'x';
                if (charsButtons[index+3].getText().charAt(0) == character) charsButtons[index+3].setText("-");
                else charsButtons[index+3].setText(String.valueOf(character));

                setGroup(
                        charsButtons[3].getText() +
                        charsButtons[4].getText() +
                        charsButtons[5].getText()
                );
            });

            charsButtons[i+6].setId("PermissionsChars_button");
            charsButtons[i+6].setOnAction(e -> {
                char character = ((index+6) % 3 == 0) ? 'r' : ((index+6-1) % 3 == 0) ? 'w' : 'x';
                if (charsButtons[index+6].getText().charAt(0) == character) charsButtons[index+6].setText("-");
                else charsButtons[index+6].setText(String.valueOf(character));

                setOther(
                        charsButtons[6].getText() +
                        charsButtons[7].getText() +
                        charsButtons[8].getText()
                );
            });

            charsBoxes[i].getChildren().add(charsButtons[i*3]);
            charsBoxes[i].getChildren().add(charsButtons[i*3+1]);
            charsBoxes[i].getChildren().add(charsButtons[i*3+2]);
        }
        charsBox.getChildren().addAll(charsBoxes);

        // Octal
        HBox octetBox = new HBox();
        octetBox.setId("PermissionsOctet_pane");

        Button[][] octetButtons = new Button[][]{
                {new Button("▲"),new Button("0"),new Button("▼")},
                {new Button("▲"),new Button("0"),new Button("▼")},
                {new Button("▲"),new Button("0"),new Button("▼")}
        };
        octetValues = new Button[] {octetButtons[0][1],octetButtons[1][1],octetButtons[2][1]};
        for (int i = 0; i < 3; i++) {
            int index = i;
            Button[] buttons = octetButtons[i];

            buttons[0].setId("PermissionsOctet_button");
            buttons[0].setOnAction(e -> {
                int value = Integer.parseInt(buttons[1].getText());
                if (value != 7) {
                    buttons[1].setText(String.valueOf(value+1));

                    switch (index) {
                        case 0 -> setOwner(value+1);
                        case 1 -> setGroup(value+1);
                        default -> setOther(value+1);
                    }
                }
            });

            buttons[1].setId("PermissionsOctet_value");
            buttons[1].setOnKeyPressed(e -> {
                KeyCode key = e.getCode();

                int value = -1;
                if (key == KeyCode.DIGIT0 || key == KeyCode.NUMPAD0) value = 0;
                else if (key == KeyCode.DIGIT1 || key == KeyCode.NUMPAD1) value = 1;
                else if (key == KeyCode.DIGIT2 || key == KeyCode.NUMPAD2) value = 2;
                else if (key == KeyCode.DIGIT3 || key == KeyCode.NUMPAD3) value = 3;
                else if (key == KeyCode.DIGIT4 || key == KeyCode.NUMPAD4) value = 4;
                else if (key == KeyCode.DIGIT5 || key == KeyCode.NUMPAD5) value = 5;
                else if (key == KeyCode.DIGIT6 || key == KeyCode.NUMPAD6) value = 6;
                else if (key == KeyCode.DIGIT7 || key == KeyCode.NUMPAD7) value = 7;

                if (value != -1) {
                    buttons[1].setText(String.valueOf(value));

                    switch (index) {
                        case 0 -> setOwner(value);
                        case 1 -> setGroup(value);
                        default -> setOther(value);
                    }
                }
            });

            buttons[2].setId("PermissionsOctet_button");
            buttons[2].setOnAction(e -> {
                int value = Integer.parseInt(buttons[1].getText());
                if (value != 0) {
                    buttons[1].setText(String.valueOf(value-1));

                    switch (index) {
                        case 0 -> setOwner(value-1);
                        case 1 -> setGroup(value-1);
                        default -> setOther(value-1);
                    }
                }
            });

            octetBox.getChildren().add(new VBox(buttons));
        }

        // Botones
        HBox buttonsBox = new HBox();
        buttonsBox.setId("PermissionsButtons_pane");

        Button applyButton = new Button("Aplicar");
        applyButton.setId("PermissionsButtons_button");
        applyButton.setOnAction(e -> {
            int exitValue = changePermission(octetValues[0].getText()+octetValues[1].getText()+octetValues[2].getText());
            if (exitValue == 0) close();

            String name = centerPane.selectionModel.getSelectedItem().getName();
            updateCenter();
            centerPane.select(name);
            updateRight();
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.setId("PermissionsButtons_button");
        cancelButton.setOnAction(e -> close());

        buttonsBox.getChildren().addAll(applyButton, cancelButton);

        // Panel
        VBox pane = new VBox(charsBox, octetBox, buttonsBox);
        pane.setId("PermissionsPane");

        // Escena
        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ESCAPE) close();});
        scene.getStylesheets().add("file://"+THEME_PATH);
        setScene(scene);
    }
    
    public void update() {
        CenterNode selectedItem = centerPane.selectionModel.getSelectedItem();
        if (selectedItem != null) {
            FileProperties properties = selectedItem.getFileProperties();
            
            String ownerChars = String.valueOf(properties.getOwnerPermissions());
            String groupChars = String.valueOf(properties.getGroupPermissions());
            String otherChars = String.valueOf(properties.getOtherPermissions());
            
            setCharacters(ownerChars, 0);
            setCharacters(groupChars, 3);
            setCharacters(otherChars, 6);
            
            setOwner(ownerChars);
            setGroup(groupChars);
            setOther(otherChars);
        }
    }

    private void setOwner(String value) {octetValues[0].setText(String.valueOf(charsToOctet(value)));}
    private void setOwner(int value) {setCharacters(octetToChars(value), 0);}

    private void setGroup(String value) {octetValues[1].setText(String.valueOf(charsToOctet(value)));}
    private void setGroup(int value) {setCharacters(octetToChars(value), 3);}

    private void setOther(String value) {octetValues[2].setText(String.valueOf(charsToOctet(value)));}
    private void setOther(int value) {setCharacters(octetToChars(value), 6);}

    private void setCharacters(String chars, int begin) {
        charsButtons[begin  ].setText(chars.substring(0,1));
        charsButtons[begin+1].setText(chars.substring(1,2));
        charsButtons[begin+2].setText(chars.substring(2));
    }

    private String octetToChars(int value) {
        switch (value) {
            case 0 -> {return "---";}
            case 1 -> {return "--x";}
            case 2 -> {return "-w-";}
            case 3 -> {return "-wx";}
            case 4 -> {return "r--";}
            case 5 -> {return "r-x";}
            case 6 -> {return "rw-";}
            case 7 -> {return "rwx";}
            default -> {return null;}
        }
    }
    private int charsToOctet(String value) {
        char[] chars = {value.charAt(0), value.charAt(1), value.charAt(2)};
        return (chars[0] == 'r' ? 4 : 0) + (chars[1] == 'w' ? 2 : 0) + (chars[2] == 'x' ? 1 : 0);
    }
}