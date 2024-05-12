package pcd.part1.simengine_conc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ExecutorStepService {
	
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	private int numSteps;
	private ExecutorService executor;

	private long currentWallTime;
	
	private AbstractSimulation sim;
	private Flag stopFlag;
	private Semaphore done;
	private int nWorkers;
	
	public ExecutorStepService(AbstractSimulation sim, int nWorkers, int numSteps, Flag stopFlag, Semaphore done, boolean syncWithTime) {
		toBeInSyncWithWallTime = false;
		this.sim = sim;
		this.stopFlag = stopFlag;
		this.numSteps = numSteps;
		this.done = done;
		this.nWorkers = nWorkers;
		
		if (syncWithTime) {
			this.syncWithTime(25);
		}
	}

	public void compute() {
		
		log("booted");
		
		var simEnv = sim.getEnvironment();
		var simAgents = sim.getAgents();
		var nTasks = sim.getAgents().size();
		
		simEnv.init();
		for (var a: simAgents) {
			a.init(simEnv);
		}

		int t = sim.getInitialTime();
		int dt = sim.getTimeStep();
		
		sim.notifyReset(t, simAgents, simEnv);

		/*
		* Le varie barriere sono state rimosse dalla soluzione (Ass01) perche?
		* Abbiamo cavato
		* */
	//	Trigger canDoStep = new Trigger(nWorkers);
	//	CyclicBarrier jobDone = new CyclicBarrier(nWorkers + 1);
		
		log("creating workers...");
		
		int nAssignedAgentsPerWorker = simAgents.size()/nWorkers;

		int index = 0;

		log("starting the simulation loop.");

		int step = 0;
		currentWallTime = System.currentTimeMillis();

		try {
			while (!stopFlag.isSet() &&  step < numSteps) {
				executor = Executors.newFixedThreadPool(nWorkers);

				simEnv.step(dt);
				simEnv.cleanActions();

				/* trigger workers to do their work in this step */

				for (int i = 0; i < nTasks; i++) {
					executor.execute(new StepTask(simAgents.get(i), dt));
				}
				executor.shutdown();
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
				/* executed actions */
				simEnv.processActions();

				sim.notifyNewStep(t, simAgents, simEnv);
	
				if (toBeInSyncWithWallTime) {
					syncWithWallTime();
				}
				
				/* updating logic time */
				
				t += dt;
				step++;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		log("done");
		stopFlag.set();
		done.release();
	}

	private void syncWithTime(int nStepsPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nStepsPerSec;
	}

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			currentWallTime = System.currentTimeMillis();
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {}
		
	}
	
	private void log(String msg) {
		System.out.println("[EXECUTOR] " + msg);
	}
	
	
}
