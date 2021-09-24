/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package thito.nodeflow.internal.ui;

import com.sun.javafx.scene.control.*;
import com.sun.javafx.scene.control.skin.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.internal.language.*;

/**
 * NodeFlow: Modified from CustomColorDialog
 */
public class ColorChooserPane extends HBox {

    private ColorRectPane colorRectPane;
    private ControlsPane controlsPane;

    private ObjectProperty<Color> currentColorProperty = new SimpleObjectProperty<>(Color.WHITE);

    private WebColorField webField = null;

    private boolean showOpacitySlider = true;

    public ColorChooserPane() {
        getStyleClass().add("custom-color-dialog");
        buildUI();
    }

    private void buildUI() {
        colorRectPane = new ColorRectPane();
        controlsPane = new ControlsPane();
        setHgrow(controlsPane, Priority.ALWAYS);
        getChildren().setAll(colorRectPane, controlsPane);
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColorProperty.set(currentColor);
    }

    public ObjectProperty<Color> currentColorProperty() {
        return currentColorProperty;
    }

    public final Color getCurrentColor() {
        return currentColorProperty.get();
    }

    public void setShowOpacitySlider(boolean showOpacitySlider) {
        this.showOpacitySlider = showOpacitySlider;
        buildUI();
    }


    /* ------------------------------------------------------------------------*/

    private class ColorRectPane extends HBox {

        private Pane colorRect;
        private Pane colorBar;
        private Pane colorRectOverlayOne;
        private Pane colorRectOverlayTwo;
        private Region colorRectIndicator;
        private Region colorBarIndicator;

        private boolean changeIsLocal = false;
        private DoubleProperty hue = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private DoubleProperty sat = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private DoubleProperty bright = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private IntegerProperty red = new SimpleIntegerProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };

        private IntegerProperty green = new SimpleIntegerProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };

        private IntegerProperty blue = new SimpleIntegerProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };

        private DoubleProperty alpha = new SimpleDoubleProperty(100) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    setCurrentColor(new Color(
                            getCurrentColor().getRed(),
                            getCurrentColor().getGreen(),
                            getCurrentColor().getBlue(),
                            clamp(alpha.get() / 100)));
                    changeIsLocal = false;
                }
            }
        };

        private void updateRGBColor() {
            Color newColor = Color.rgb(red.get(), green.get(), blue.get(), clamp(alpha.get() / 100));
            hue.set(newColor.getHue());
            sat.set(newColor.getSaturation() * 100);
            bright.set(newColor.getBrightness() * 100);
            alpha.set(getCurrentColor().getOpacity() * 100);
            setCurrentColor(newColor);
        }

        private void updateHSBColor() {
            Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100),
                    clamp(bright.get() / 100), clamp(alpha.get() / 100));
            red.set(doubleToInt(newColor.getRed()));
            green.set(doubleToInt(newColor.getGreen()));
            blue.set(doubleToInt(newColor.getBlue()));
            alpha.set(getCurrentColor().getOpacity() * 100);
            setCurrentColor(newColor);
        }

        private void colorChanged() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                hue.set(getCurrentColor().getHue());
                sat.set(getCurrentColor().getSaturation() * 100);
                bright.set(getCurrentColor().getBrightness() * 100);
                red.set(doubleToInt(getCurrentColor().getRed()));
                green.set(doubleToInt(getCurrentColor().getGreen()));
                blue.set(doubleToInt(getCurrentColor().getBlue()));
                alpha.set(getCurrentColor().getOpacity() * 100);
                changeIsLocal = false;
            }
        }

        public ColorRectPane() {

            getStyleClass().add("color-rect-pane");

            currentColorProperty.addListener((ov, t, t1) -> {
                colorChanged();
            });

            colorRectIndicator = new Region();
            colorRectIndicator.setId("color-rect-indicator");
            colorRectIndicator.setManaged(false);
            colorRectIndicator.setMouseTransparent(true);
            colorRectIndicator.setCache(true);

            final Pane colorRectOpacityContainer = new StackPane();

            colorRect = new StackPane() {
                // This is an implementation of square control that chooses its
                // size to fill the available height
                @Override
                public Orientation getContentBias() {
                    return Orientation.VERTICAL;
                }

                @Override
                protected double computePrefWidth(double height) {
                    return height;
                }

                @Override
                protected double computeMaxWidth(double height) {
                    return height;
                }
            };
            colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

            Pane colorRectHue = new Pane();
            colorRectHue.backgroundProperty().bind(new ObjectBinding<>() {

                {
                    bind(hue);
                }

                @Override
                protected Background computeValue() {
                    return new Background(new BackgroundFill(
                            Color.hsb(hue.getValue(), 1.0, 1.0),
                            CornerRadii.EMPTY, Insets.EMPTY));
                }
            });

            colorRectOverlayOne = new Pane();
            colorRectOverlayOne.getStyleClass().add("color-rect");
            colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                    new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.rgb(255, 255, 255, 1)),
                            new Stop(1, Color.rgb(255, 255, 255, 0))),
                    CornerRadii.EMPTY, Insets.EMPTY)));

            EventHandler<MouseEvent> rectMouseHandler = event -> {
                final double x = event.getX();
                final double y = event.getY();
                sat.set(clamp(x / colorRect.getWidth()) * 100);
                bright.set(100 - (clamp(y / colorRect.getHeight()) * 100));
            };

            colorRectOverlayTwo = new Pane();
            colorRectOverlayTwo.getStyleClass().addAll("color-rect");
            colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                    CornerRadii.EMPTY, Insets.EMPTY)));
            colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
            colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

            Pane colorRectBlackBorder = new Pane();
            colorRectBlackBorder.setMouseTransparent(true);
            colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

            colorBar = new Pane();
            colorBar.getStyleClass().add("color-bar");
            colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                    CornerRadii.EMPTY, Insets.EMPTY)));

            colorBarIndicator = new Region();
            colorBarIndicator.setId("color-bar-indicator");
            colorBarIndicator.setMouseTransparent(true);
            colorBarIndicator.setCache(true);

            colorRectIndicator.layoutXProperty().bind(sat.divide(100).multiply(colorRect.widthProperty()));
            colorRectIndicator.layoutYProperty().bind(Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
            colorBarIndicator.layoutYProperty().bind(hue.divide(360).multiply(colorBar.heightProperty()));
            colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

            EventHandler<MouseEvent> barMouseHandler = event -> {
                final double y = event.getY();
                hue.set(clamp(y / colorRect.getHeight()) * 360);
            };

            colorBar.setOnMouseDragged(barMouseHandler);
            colorBar.setOnMousePressed(barMouseHandler);

            colorBar.getChildren().setAll(colorBarIndicator);
            colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
            colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
            HBox.setHgrow(colorRect, Priority.SOMETIMES);
            getChildren().addAll(colorRect, colorBar);
        }

        private void updateValues() {
            if (getCurrentColor() == null) {
                setCurrentColor(Color.TRANSPARENT);
            }
            changeIsLocal = true;
            //Initialize hue, sat, bright, color, red, green and blue
            hue.set(getCurrentColor().getHue());
            sat.set(getCurrentColor().getSaturation() * 100);
            bright.set(getCurrentColor().getBrightness() * 100);
            alpha.set(getCurrentColor().getOpacity() * 100);
            setCurrentColor(Color.hsb(hue.get(), clamp(sat.get() / 100), clamp(bright.get() / 100),
                    clamp(alpha.get() / 100)));
            red.set(doubleToInt(getCurrentColor().getRed()));
            green.set(doubleToInt(getCurrentColor().getGreen()));
            blue.set(doubleToInt(getCurrentColor().getBlue()));
            changeIsLocal = false;
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();

            // to maintain default size
            colorRectIndicator.autosize();
            // to maintain square size
            double size = Math.min(colorRect.getWidth(), colorRect.getHeight());
            colorRect.resize(size, size);
            colorBar.resize(colorBar.getWidth(), size);
        }
    }

    /* ------------------------------------------------------------------------*/

    private class ControlsPane extends VBox {

        private Region currentTransparent; // for opacity
        private Region currentNewColorBorder;
        private ToggleButton hsbButton;
        private ToggleButton rgbButton;
        private ToggleButton webButton;
        private HBox hBox;

        private Label labels[] = new Label[4];
        private Slider sliders[] = new Slider[4];
        private IntegerField fields[] = new IntegerField[4];
        private Label units[] = new Label[4];
        private HBox buttonBox;
        private Region whiteBox;

        private GridPane settingsPane = new GridPane();

        public ControlsPane() {
            getStyleClass().add("controls-pane");

            currentNewColorBorder = new Region();
            currentNewColorBorder.setId("current-new-color-border");

            currentTransparent = new Region();
            currentTransparent.getStyleClass().addAll("transparent-pattern");

            whiteBox = new Region();
            whiteBox.getStyleClass().add("customcolor-controls-background");

            hsbButton = new ToggleButton();
            hsbButton.textProperty().bind(I18n.$("color-chooser.hsb"));
            hsbButton.getStyleClass().add("left-pill");
            rgbButton = new ToggleButton();
            rgbButton.textProperty().bind(I18n.$("color-chooser.rgb"));
            rgbButton.getStyleClass().add("center-pill");
            webButton = new ToggleButton();
            webButton.textProperty().bind(I18n.$("color-chooser.web"));
            webButton.getStyleClass().add("right-pill");
            final ToggleGroup group = new ToggleGroup();

            hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(hsbButton, rgbButton, webButton);

            Region spacer1 = new Region();
            spacer1.setId("spacer1");
            Region spacer2 = new Region();
            spacer2.setId("spacer2");
            Region leftSpacer = new Region();
            leftSpacer.setId("spacer-side");
            Region rightSpacer = new Region();
            rightSpacer.setId("spacer-side");
            Region bottomSpacer = new Region();
            bottomSpacer.setId("spacer-bottom");

            settingsPane = new GridPane();
            settingsPane.setId("settings-pane");
            settingsPane.getColumnConstraints().addAll(new ColumnConstraints(),
                    new ColumnConstraints(), new ColumnConstraints(),
                    new ColumnConstraints(), new ColumnConstraints(),
                    new ColumnConstraints());
            settingsPane.getColumnConstraints().get(0).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);
            settingsPane.getColumnConstraints().get(3).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(4).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(5).setHgrow(Priority.NEVER);
            settingsPane.add(whiteBox, 0, 0, 6, 5);
            settingsPane.add(hBox, 0, 0, 6, 1);
            settingsPane.add(leftSpacer, 0, 0);
            settingsPane.add(rightSpacer, 5, 0);
            settingsPane.add(bottomSpacer, 0, 4);

            webField = new WebColorField();
            webField.getStyleClass().add("web-field");
            webField.setSkin(new WebColorFieldSkin(webField));
            webField.valueProperty().bindBidirectional(currentColorProperty);
            webField.visibleProperty().bind(group.selectedToggleProperty().isEqualTo(webButton));
            settingsPane.add(webField, 2, 1);

            // Color settings Grid Pane
            for (int i = 0; i < 4; i++) {
                labels[i] = new Label();
                labels[i].getStyleClass().add("settings-label");

                sliders[i] = new Slider();

                fields[i] = new IntegerField();
                fields[i].getStyleClass().add("color-input-field");
                fields[i].setSkin(new IntegerFieldSkin(fields[i]));

                units[i] = new Label(i == 0 ? "\u00B0" : "%");
                units[i].getStyleClass().add("settings-unit");

                if (i > 0 && i < 3) {
                    // first row and opacity labels are always visible
                    // second and third row labels are not visible in Web page
                    labels[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
                }
                if (i < 3) {
                    // sliders and fields shouldn't be visible in Web page
                    sliders[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
                    fields[i].visibleProperty().bind(group.selectedToggleProperty().isNotEqualTo(webButton));
                    units[i].visibleProperty().bind(group.selectedToggleProperty().isEqualTo(hsbButton));
                }
                int row = 1 + i;
                if (i == 3) {
                    // opacity row is shifted one gridPane row down
                    row++;
                }

                // JDK-8161449 - hide the opacity slider
                if (i == 3 && !showOpacitySlider) {
                    continue;
                }

                settingsPane.add(labels[i], 1, row);
                settingsPane.add(sliders[i], 2, row);
                settingsPane.add(fields[i], 3, row);
                settingsPane.add(units[i], 4, row);
            }

            set(3, I18n.$("color-chooser.opacity"), 100, colorRectPane.alpha);

            hsbButton.setToggleGroup(group);
            rgbButton.setToggleGroup(group);
            webButton.setToggleGroup(group);
            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    group.selectToggle(oldValue);
                } else {
                    if (newValue == hsbButton) {
                        showHSBSettings();
                    } else if (newValue == rgbButton) {
                        showRGBSettings();
                    } else {
                        showWebSettings();
                    }
                }
            });
            group.selectToggle(hsbButton);

            buttonBox = new HBox();
            buttonBox.setId("buttons-hbox");

            getChildren().addAll(settingsPane, buttonBox);
        }

        private void showHSBSettings() {
            set(0, I18n.$("color-chooser.hue"), 360, colorRectPane.hue);
            set(1, I18n.$("color-chooser.saturation"), 100, colorRectPane.sat);
            set(2, I18n.$("color-chooser.brightness"), 100, colorRectPane.bright);
        }

        private void showRGBSettings() {
            set(0, I18n.$("color-chooser.red"), 255, colorRectPane.red);
            set(1, I18n.$("color-chooser.green"), 255, colorRectPane.green);
            set(2, I18n.$("color-chooser.blue"), 255, colorRectPane.blue);
        }

        private void showWebSettings() {
            labels[0].textProperty().bind(I18n.$("color-chooser.web-color"));
        }

        private Property<Number>[] boundProperties = new Property[4];

        private void set(int row, ObservableValue<String> caption, int maxValue, Property<Number> prop) {
            labels[row].textProperty().bind(caption);
            if (boundProperties[row] != null) {
                sliders[row].valueProperty().unbindBidirectional(boundProperties[row]);
                fields[row].valueProperty().unbindBidirectional(boundProperties[row]);
            }
            sliders[row].setMax(maxValue);
            sliders[row].valueProperty().bindBidirectional(prop);
            labels[row].setLabelFor(sliders[row]);
            fields[row].setMaxValue(maxValue);
            fields[row].valueProperty().bindBidirectional(prop);
            boundProperties[row] = prop;
        }
    }

    static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int y = 0; y < 255; y++) {
            offset = 1 - (1.0 / 255) * y;
            int h = (int) ((y / 255.0) * 360);
            stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 1f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }

    private static int doubleToInt(double value) {
        return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
    }
}
