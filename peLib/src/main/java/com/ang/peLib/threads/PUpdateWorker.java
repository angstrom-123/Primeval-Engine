package com.ang.peLib.threads;

public class PUpdateWorker implements Runnable {
	private int updateMs;
	private volatile boolean stop = false;
	private PThreadInterface ti;

	public PUpdateWorker(int updateMs) {
		this.updateMs = updateMs;
	}

	public void setInterface(PThreadInterface ti) {
		this.ti = ti;
	}

	public void doStop() {
		stop = true;
	}

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
