package pcd.part1.simengine_conc;

public class StepTask implements Runnable{
	
	private AbstractAgent agent;
	private int dt;

	public StepTask(AbstractAgent agent, int dt) {
		this.agent = agent;
		this.dt = dt;
	}
	
	public void run() {
		System.out.println("task parita");
						agent.step(dt);
	}
}
