package com.pk.logic;

import java.util.TimerTask;

public class PackageRunner extends TimerTask {

	private final PackageWorker worker;
	
	public PackageRunner(PackageWorker worker) {
		this.worker = worker;
	}
	
	@Override
	public void run() {
		worker.printPackageInfo();
	}

}
