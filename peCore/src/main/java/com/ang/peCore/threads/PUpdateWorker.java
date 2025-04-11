package com.ang.peCore.threads;

public class PUpdateWorker implements Runnable {
	private int frameMs;
	private boolean stop = false;
	private PThreadInterface ti;

	public PUpdateWorker(int frameMs) {
		this.frameMs = frameMs;
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
				Thread.sleep(frameMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
