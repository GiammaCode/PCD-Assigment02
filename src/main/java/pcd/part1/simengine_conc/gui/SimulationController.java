package pcd.part1.simengine_conc.gui;

import pcd.part1.simengine_conc.*;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationController {

	private Flag stopFlag;
	private AbstractSimulation simulation;
	private SimulationGUI gui;
	private RoadSimView view;
	private RoadSimStatistics stat;
	private ExecutorService executor;


	 
	public SimulationController(AbstractSimulation simulation) {
		this.simulation = simulation;
		this.stopFlag = new Flag();
	}
	
	public void attach(SimulationGUI gui) {
		this.gui = gui;		
		view = new RoadSimView();
		stat = new RoadSimStatistics();
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);		
		gui.setController(this);
	}

	public void notifyStarted(int nSteps) {
		executor = Executors.newSingleThreadExecutor();

		executor.execute( new Thread(() -> {
			simulation.setup();			
			view.display();
		
			stopFlag.reset();
			simulation.run(nSteps, stopFlag, true);
			gui.reset();
			
		}));
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}

}
