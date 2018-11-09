package rocks.zipcode.calcskin;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rocks.zipcode.calcskin.CalcEngine;

import java.util.HashMap;
import java.util.Map;

// a simple JavaFX calculator.
public class CalcSkin extends Application {

    public static void main(String[] args){
        launch(args);
    }
    private static final String[][] template = {
            { "7", "8", "9", "/", "x^2", "MC", "sin" },
            { "4", "5", "6", "*", "SQRT", "MR", "cos" },
            { "1", "2", "3", "-", "1/x", "M+", "tan" },
            { "0", "c", "=", "+", "x^y" }
    };

    private final Map<String, Button> accelerators = new HashMap<>();

    private DoubleProperty previousValue = new SimpleDoubleProperty();
    private DoubleProperty currentValue = new SimpleDoubleProperty();
    private CalcEngine calcEngine = new CalcEngine();
    private DoubleProperty memoryValue = new SimpleDoubleProperty();

    private enum Op { NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE, SQUARED, SQUAREROOT, INVERSE, EXPONENT }

    private Op curOp   = Op.NOOP;
    private Op stackOp = Op.NOOP;

    public static void launchCalc(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final TextField screen  = createScreen();
        final TilePane  buttons = createButtons();

        stage.setTitle("Calc");
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(new Scene(createLayout(screen, buttons)));
        stage.show();
    }

    private VBox createLayout(TextField screen, TilePane buttons) {
        final VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: silver; -fx-padding: 20; -fx-font-size: 20;");
        layout.getChildren().setAll(screen, buttons);
        handleAccelerators(layout);
        screen.prefWidthProperty().bind(buttons.widthProperty());
        return layout;
    }

    private void handleAccelerators(VBox layout) {
        layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                Button activated = accelerators.get(keyEvent.getText());
                if (activated != null) {
                    activated.fire();
                }
            }
        });
    }

    private TextField createScreen() {
        final TextField screen = new TextField();
        screen.setStyle("-fx-background-color: aquamarine;");
        screen.setAlignment(Pos.CENTER_LEFT);
        screen.setEditable(false);
        screen.textProperty().bind(Bindings.format("%.4f", currentValue));
        return screen;
    }

    private TilePane createButtons() {
        TilePane buttons = new TilePane();
        buttons.setVgap(7);
        buttons.setHgap(7);
        buttons.setPrefColumns(template[0].length);
        for (String[] r: template) {
            for (String s: r) {
                buttons.getChildren().add(createButton(s));
            }
        }
        return buttons;
    }

    private Button createButton(final String s) {
        Button button = makeStandardButton(s);

        if (s.matches("[0-9]")) {
            makeNumericButton(s, button);
        } else {
            final ObjectProperty<Op> triggerOp = determineOperand(s);
            if (triggerOp.get() != Op.NOOP) {
                makeOperandButton(button, triggerOp);
            } else if ("c".equals(s)) {
                makeClearButton(button);
            } else if ("=".equals(s)) {
                makeEqualsButton(button);
            } else if ("x^2".equals(s)) {
                makeSquaredButton(button);
            } else if ("SQRT".equals(s)){
                makeSqrtButton(button);
            } else if ("1/x".equals(s)){
                makeInverseButton(button);
            } else if ("M+".equals(s)){
                makeMemoryAddButton(button);
            } else if ("MR".equals(s)){
                makeMemoryRecallButton(button);
            } else if ("MC".equals(s)){
                makeMemoryClearButton(button);
            } else if ("sin".equals(s)){
                makeSinButton(button);
            } else if ("cos".equals(s)) {
                makeCosButton(button);
            } else if ("tan".equals(s)) {
                makeTanButton(button);
            }
            //make else ifs for the single numeric functions
            //call the new method for those functions
        }

        return button;
    }

    private ObjectProperty<Op> determineOperand(String s) {
        final ObjectProperty<Op> triggerOp = new SimpleObjectProperty<>(Op.NOOP);
        switch (s) {
            case "+": triggerOp.set(Op.ADD);      break;
            case "-": triggerOp.set(Op.SUBTRACT); break;
            case "*": triggerOp.set(Op.MULTIPLY); break;
            case "/": triggerOp.set(Op.DIVIDE);   break;
            //case "x^2": triggerOp.set(Op.SQUARED); break;
            //case "SQRT": triggerOp.set(Op.SQUAREROOT); break;
            //case "1/x": triggerOp.set(Op.INVERSE); break;
            case "x^y": triggerOp.set(Op.EXPONENT); break;
        }
        return triggerOp;
    }

    private void makeOperandButton(Button button, final ObjectProperty<Op> triggerOp) {
        button.setStyle("-fx-base: lightgray;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                curOp = triggerOp.get();
            }
        });
    }

    private Button makeStandardButton(String s) {
        Button button = new Button(s);
        button.setStyle("-fx-base: beige;");
        accelerators.put(s, button);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return button;
    }

    private void makeNumericButton(final String s, Button button) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (curOp == Op.NOOP) {
                    currentValue.set(currentValue.get() * 10 + Integer.parseInt(s));
                } else {
                    previousValue.set(currentValue.get());
                    currentValue.set(Integer.parseInt(s));
                    stackOp = curOp;
                    curOp = Op.NOOP;
                }
            }
        });
    }

    private void makeClearButton(Button button) {
        button.setStyle("-fx-base: mistyrose;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(0);
            }
        });
    }

    private void makeSquaredButton(Button button) {
        button.setStyle("-fx-base: darkgray");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(calcEngine.squared(currentValue.get()));
            }
        });
    }

    private void makeSqrtButton(Button button) {
        button.setStyle("-fx-base: darkgray;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(calcEngine.sqrt(currentValue.get()));
            }
        });
    }

    private void makeInverseButton(Button button) {
        button.setStyle("-fx-base: darkgray;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(calcEngine.inverse(currentValue.get()));
            }
        });
    }

    private void makeMemoryAddButton(Button button) {
        button.setStyle("-fx-base: gray");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                memoryValue.set(currentValue.get());
            }
        });
    }

    private void makeMemoryRecallButton(Button button) {
        button.setStyle("-fx-base: gray");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(memoryValue.get());
            }
        });
    }

    private void makeMemoryClearButton(Button button) {
        button.setStyle("-fx-base: gray");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                memoryValue.set(0);
            }
        });
    }

    private void makeSinButton(Button button) {
        button.setStyle("-fx-base: black");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(Math.sin(currentValue.get()));
            }
        });
    }

    private void makeCosButton(Button button) {
        button.setStyle("-fx-base: black");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(Math.cos(currentValue.get()));
            }
        });
    }

    private void makeTanButton(Button button) {
        button.setStyle("-fx-base: black");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                currentValue.set(Math.tan(currentValue.get()));
            }
        });
    }

    private void makeEqualsButton(Button button) {
        button.setStyle("-fx-base: white;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (stackOp) {
                    case ADD:      currentValue.set(calcEngine.add(previousValue.get(), currentValue.get())); break;
                    case SUBTRACT: currentValue.set(calcEngine.subtract(previousValue.get(), currentValue.get())); break;
                    case MULTIPLY: currentValue.set(calcEngine.multiply(previousValue.get(), currentValue.get())); break;
                    case DIVIDE:   currentValue.set(calcEngine.divide(previousValue.get(), currentValue.get())); break;
                    //case SQUARED:   currentValue.set(calcEngine.squared(currentValue.get())); break;
                    //case SQUAREROOT:   currentValue.set(calcEngine.sqrt(currentValue.get())); break;
                    //case INVERSE:   currentValue.set(calcEngine.inverse(currentValue.get())); break;
                    case EXPONENT:   currentValue.set(calcEngine.exponent(previousValue.get(), currentValue.get())); break;



                }
            }
        });
    }


}


