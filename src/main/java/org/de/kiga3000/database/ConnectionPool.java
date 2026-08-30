package org.de.kiga3000.database;
 import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.messages.SystemMessenger;
 
 /**
  * \file ConnectionPool.java
  * 
  * \brief
  * Connection Pool manages the application's pool of JDBC database
  * connections.
  *
  * The connection pool manager
  * from http://www.WebDevelopersJournal.com/columns/connection_pool.html,
  * heavily modified by bjc. It was originally an inner class in DBConnectionPoolManager. 
  * I ultimately stripped it out and discarded the wrapper class to 
  * remove a level of dynamic binding that served no useful purpose 
  * and was confusing as well, and added exceptions for all configuration
  * errors.
  * @see http://www.WebDevelopersJournal.com/columns/connection_pool.html
  * 
  * $Id: ConnectionPool.java,v 1.1.4.2 2006/02/04 14:39:05 bdiemer Exp $
  * 
  * \author Brad Cox <bcox@virtualschool.edu>
  * \date   Sun Nov 11 10:49:20 EST 2001
  */
/*
	File was again modified by Johannes Nicolai for CryptCom
	Copyright 2002
	LICENSE: GPL
*/
/**
 * logger und internationlized messaging added by bdiemer 2005-11-28
 */
 public class ConnectionPool
 {
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
private boolean isInitialized = false;
   private final String url;
   private final String password;
   private final String user;
   private final int maxConnections;
   private final String driverList;
   private final LinkedList pooledConnections = new LinkedList();
 
   private int numberBusyConnections;
   private int numberAcquireCalls;
   private int numberReleaseCalls;

	private static Logger _logger = Logger.getLogger(ConnectionPool.class
			.getName());
   private SystemMessenger sysMessenger = new SystemMessenger();
 
   /**
    * Connection pool constructor. Notice that driverList installation is
    * deferred until the first connection is created. This was done so that
    * the constructor can be exception-free to simplify the client API and
    * to minimize constructor/destructor overhead.
    * driverList problems will be reported when the first connection is
    * created.
    * @param String url The JDBC url for the database
    * @param String user The database user, or null
    * @param String password The database user password, or null
    * @param int maxConnections The maximal number of connections, or 0
    * for unlimited
    * @param String driverList: one JDBC driver
    */
   public ConnectionPool(
     String url,
     String user,
     String password,
     int maxConnections,
     String driverList
   ) 
   {
     this.url = url;
     this.user = user;
     this.password = password;
     this.driverList = driverList;
     this.maxConnections = maxConnections;
     this.numberAcquireCalls = 0;
     this.numberReleaseCalls = 0;
     this.numberBusyConnections = 0;
     this.isInitialized = false;
   }
   /**
    * Checks out a connection from the pool. If no free connection
    * is available, a new connection is created unless the max
    * number of connections has been reached. If a free connection
    * has been closed by the database, it's removed from the pool
    * and this method is called again recursively.
    * <P>
    * If no connection is available and the max number has been 
    * reached, this method waits the specified time for one to be
    * checked in.
    *
    * @param timeout The timeout value in milliseconds
    */
   public synchronized Connection acquireConnection(long timeout) throws PoolException
   {
     long startTime = System.currentTimeMillis();
     Connection connection = null;
     while((connection = getConnection()) == null)
     {
       try { super.wait(timeout); }
       catch (InterruptedException e) { 
    	   System.out.println(e.getMessage()); 
    	   _logger.severe(sysMessenger.getMessage("KiGa.log.ConnPoolInt",e.getMessage()));
       } // debug
       if ((System.currentTimeMillis() - startTime) >= timeout){
	     _logger.severe(sysMessenger.getMessage("KiGa.log.ConnPoolTimeout", Long.toString(timeout)));
         throw new PoolException(sysMessenger.getMessage("KiGa.log.ConnPoolTimeout", Long.toString(timeout)));
       }
     }
     numberBusyConnections++;
     numberAcquireCalls++;
     return connection;
   }
   /**
    * Checks out a connection from the pool. If no free connection
    * is available, a new connection is created unless the max
    * number of connections has been reached. If a free connection
    * has been closed by the database, it's removed from the pool
    * and this method is called again recursively.
    * @return Connection the connection or null if no more available
    * @throws PoolException if the connection pool could not be initialized,
    * typically because of incorrect initialization parameters. 
    */
   private Connection getConnection() throws PoolException
   {
     Connection connection = null;
     while (pooledConnections.size() > 0 && connection == null)
     {
       connection = (Connection) pooledConnections.removeFirst();
       try 
       { 
         if (connection.isClosed()) connection = null; 
       }
       catch (SQLException e) // fixme
       { 
	     System.out.println(e.getMessage());
	     _logger.severe(sysMessenger.getMessage("KiGa.log.SQLExc", e.getMessage()));
         connection = null; 
       }
     }
     if (null == connection)
     {
       if (0 == maxConnections || numberBusyConnections < maxConnections)
         connection = addConnection();
     }
     return connection;
   }
   /**
    * Constructs a new connection for this pool. Initialization
    * is deferred from the contructor to here.
    * @return Connection
    * @throws PoolException
    */
   private Connection addConnection() throws PoolException
   {
     if (!isInitialized) initialize();
     Connection connection = null;
     try
     {
       if (null == user || null == password) 
         connection = DriverManager.getConnection(url);
       else 
         connection = DriverManager.getConnection(url, user, password);
 
       if (null == connection){
    	 _logger.severe(sysMessenger.getMessage("KiGa.log.DrvManag", url));
         throw new PoolException(sysMessenger.getMessage("KiGa.log.DrvManag", url));
       }   
       return connection;
     }catch (SQLException e){ 
    	 _logger.severe(sysMessenger.getMessage("KiGa.log.PoolExc", e.getMessage()));
    	 throw new PoolException(sysMessenger.getMessage("KiGa.log.PoolExc", e.getMessage())); 
     }
   }
   /**
    * Returns the maximum size of the pool as set by the constructor.
    * @return int maxConnections
    */
   public final int getMaxConnections() { return maxConnections; }
   /**
    * Return the number of times acquireConnection has been called.
    * @return int n:
    */
   public final int getNumberAcquireCalls() { return numberAcquireCalls; }
   /**
    * Return numberBusyConnections. This increments each acquireConnection() and
    * decrements for each returnConnection().
    * @return int n:
    */
   public final int getNumberBusyConnections() { return numberBusyConnections; }
   /**
    * Returns the size of the connection pool, e.g. the number of connections
    * returned by returnConnection().
    * @return int numberPooledConnections: the size() of the connection pool
    */
   public final int getNumberPooledConnections() { return pooledConnections.size(); }
   /**
    * Return the number of times returnConnection has been called. 
    * @return int n:
    */
   public final int getNumberReleaseCalls() { return numberReleaseCalls; }
   /**
    * Load and register JDBC drivers. 
    */
   private final void initialize() throws PoolException
   {
       try
       {
         Driver driver = (Driver) Class.forName(driverList).getDeclaredConstructor().newInstance();
         DriverManager.registerDriver(driver);
       }catch (Exception e){ 
    	   _logger.severe(sysMessenger.getMessage("KiGa.log.PoolNoReg", driverList));
    	   throw new PoolException(sysMessenger.getMessage("KiGa.log.PoolNoReg", driverList));
       }
     isInitialized = true;
   }
   /**
    * Release all pooled connections.
    */
   synchronized void releaseAllConnections() 
   {
	Iterator i = pooledConnections.iterator();
	while (i.hasNext()) 
     {
       Connection c = (Connection) i.next();
       try {
    	   c.close();
       }catch (SQLException ex){
    	   _logger.severe(sysMessenger.getMessage("KiGa.log.PoolNoClose", c.toString() , ex.getMessage()));
    	   System.out.println(sysMessenger.getMessage("KiGa.log.PoolNoClose", c.toString() , ex.getMessage()));
       }
	   i.remove();
     }
   }
   /**
    * Close all pooled connections.
    * Workaround for databases who have problems if a connection stays open a long time
    * while isn't used from a client and quits the connection.
    */
   public synchronized void closeAllConnections() 
   {
	Iterator i = pooledConnections.iterator();
	while (i.hasNext()) 
     {
       Connection c = (Connection) i.next();
       try {
    	   c.close();
       } catch (SQLException ex) {
    	   _logger.severe(sysMessenger.getMessage("KiGa.log.PoolNoClose", c.toString() , ex.getMessage()));
    	   System.out.println("Could not close :" + c+ " detail: "+ex.getMessage());
       }
     }
   }
   /**
    * Release connection to the pool for reuse. Notify other Threads that
    * may be waiting for a connection.
    * @param Connection connection: The connection to check in
    */
   public synchronized void releaseConnection(Connection connection)
   {
     pooledConnections.add(connection);
     numberReleaseCalls++;
     numberBusyConnections--;
     super.notifyAll();
   }
   public final String toString() { return "ConnectionPool:" + url; }
 }
