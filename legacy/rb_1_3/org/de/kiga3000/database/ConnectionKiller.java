package org.de.kiga3000.database;

import java.util.TimerTask;


// Class to kill passive database connections which stayed open too long
// Prevent some buggy databases to quit connections which have not requested 
// new data for a long time

public class ConnectionKiller extends TimerTask{
	private ConnectionPool pool;
	public ConnectionKiller(ConnectionPool pool) {
		this.pool=pool;
	}

	public void run () {
		pool.closeAllConnections();
	}
}
