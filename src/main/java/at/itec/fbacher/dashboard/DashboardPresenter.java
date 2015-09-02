package at.itec.fbacher.dashboard;

/*
 * #%L
 * igniter
 * %%
 * Copyright (C) 2013 - 2014 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import at.itec.fbacher.dashboard.graph.GraphView;
import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.model.Scenario;
import at.itec.fbacher.flowsim.model.ScenarioFactory;
import at.itec.fbacher.flowsim.model.ImportedScenarioFactory;
import at.itec.fbacher.flowsim.sim.FormattedTime;
import at.itec.fbacher.flowsim.sim.Simulator;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author adam-bien.com
 */
public class DashboardPresenter implements Initializable, EventSubscriber {

    @FXML
    Pane lightsBox;

    @FXML
    Button launch;

    @Inject
    private String prefix;

    @Inject
    private String happyEnding;

    @Inject
    private LocalDate date;

    private String theVeryEnd;

    @FXML
    MenuButton selectScenario;

    @FXML
    ListView log;

    @FXML
    Label simTimeLabel;

    @FXML
    Label currentTimeLabel;

    @FXML
    Slider speedupSlider;

    @FXML
    Label speedupLabel;

    Scenario currentScenario;

    Thread updateSimTimeThread;

    Task updateSimTimeTask;
    private boolean simulationRunning;

    private List<Control> simControlElements = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        launch.setDisable(true);

        List<String> scenarios = new ArrayList<>();
        scenarios.add("HelloScenario");

        scenarios.forEach(scenarioName -> {
            MenuItem item = new MenuItem(scenarioName);
            item.setOnAction(event -> {
                selectScenario.setText(scenarioName);
                loadScenario(scenarioName);
            });
            selectScenario.getItems().addAll(item);
        });

        MenuItem importFileItem = new MenuItem("Import from file...");
        importFileItem.setOnAction(event -> importScenario());
        selectScenario.getItems().addAll(importFileItem);
        GraphView graphView = new GraphView();
        graphView.getView(lightsBox.getChildren()::add);

        EventPublisher.getInstance().register(this, LogUpdateEvent.class);
        EventPublisher.getInstance().register(this, SimulationFinishedEvent.class);

        simControlElements.add(simTimeLabel);
        simControlElements.add(currentTimeLabel);
        simControlElements.add(speedupSlider);
        simControlElements.add(speedupLabel);

        simControlElements.parallelStream().forEach(c -> c.setOpacity(0.0));

        speedupSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeSpeedup(newValue);
        });
    }

    private void importScenario() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DAT files (*.dat)", "*.dat");
        fileChooser.getExtensionFilters().add(extFilter);
        File scenarioFile = fileChooser.showOpenDialog(speedupLabel.getScene().getWindow());
        if (scenarioFile != null) {
            ImportedScenarioFactory scenarioFactory = new ImportedScenarioFactory();
            Scenario scenario = scenarioFactory.createScenario(scenarioFile);

            initializeScenario(scenario);
        }
    }

    private void loadScenario(String scenarioStr) {
        ScenarioFactory scenarioFactory = new ScenarioFactory();
        Scenario scenario = scenarioFactory.createScenario(scenarioStr);

        initializeScenario(scenario);

    }

    private void initializeScenario(Scenario scenario) {
        if (scenario != null) {
            EventPublisher.getInstance().publishEvent(new ScenarioSelectedEvent());
            launch.setDisable(false);
            currentScenario = scenario;
            scenario.initialize();

            updateSpeedupLabel();

            simControlElements.stream().forEach(c -> {
                FadeTransition ft = new FadeTransition(Duration.millis(300), c);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            });
        }
    }

    private void updateSpeedupLabel() {
        int tmp = (int) (speedupSlider.getValue() * 100);
        String speedupLabelText = "Speedup = " + tmp / 100.0 + " x Realtime";
        speedupLabel.setText(speedupLabelText);
    }

    public void launch() {
        Thread t = new Thread(() -> {
            currentScenario.run();
        });
        t.start();
        startUpdateSimTimeThread();
    }

    private void startUpdateSimTimeThread() {
        simulationRunning = true;
        updateSimTimeTask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (simulationRunning) {
                    Platform.runLater(() -> {
                        FormattedTime ctf = Simulator.getInstance().getCurrentTimeFormatted();
                        currentTimeLabel.setText(ctf.getSeconds() + ":" + ctf.getMilliSeconds());
                    });
                    Thread.sleep(20);
                }
                return null;
            }
        };
        updateSimTimeThread = new Thread(updateSimTimeTask);
        updateSimTimeThread.setDaemon(true);
        updateSimTimeThread.start();
        EventPublisher.getInstance().publishEvent(new ScenarioStartedEvent());
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof  LogUpdateEvent) {
            LogUpdateEvent logUpdateEvent = (LogUpdateEvent) evt;

            Platform.runLater(() -> log.getItems().addAll(logUpdateEvent.getMessage()));
        }
        else if (evt instanceof SimulationFinishedEvent) {
            simulationRunning = false;
            try {
                updateSimTimeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeSpeedup(Number newValue) {
        Simulator.getInstance().setSpeedupFactor(newValue.doubleValue());
        updateSpeedupLabel();
    }
}
