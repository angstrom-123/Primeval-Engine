package com.ang.peLib.threads;

/**
 * Runnable worker that periodically sends an update to its interface.
 */
public class PUpdateWorker implements Runnable {
	private int updateMs;
	private volatile boolean stop = false;
	private PThreadInterface ti;

	/**
	 * Constructs a new update worker with a time to sleep between updates.
	 * @param updateMs milliseconds to wait between updates
	 */
	public PUpdateWorker(int updateMs) {
		this.updateMs = updateMs;
	}

	/**
	 * Assigns an interface to send updates to.
	 * @param ti interface to send updates to
	 */
	public void setInterface(PThreadInterface ti) {
		this.ti = ti;
	}

	/**
	 * Instructs the worker to cease execution.
	 */
	public void doStop() {
		stop = true;
	}

	/**
	 * Periodically sends update events to its interface.
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (!stop) {
			try {
				ti.update();
				Thread.sleep(updateMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
